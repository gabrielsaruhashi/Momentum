package shag.com.shag.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;

import shag.com.shag.Models.Event;
import shag.com.shag.Models.Message;

/**
 * Created by gabesaruhashi on 8/3/17.
 */

public class SmsReceiver extends BroadcastReceiver {
    Context mContext;

    private String TAG = SmsReceiver.class.getSimpleName();
    ArrayList<String> participantsPhoneNumbers;

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // instantiate context
        mContext = context;

        // Get the data (SMS data) bound to intent
        Bundle bundle = intent.getExtras();

        SmsMessage[] msgs = null;

        String str = "";

        if (bundle != null) {
            // Retrieve the SMS Messages received
            msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            /*
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];*/

            // For every SMS message received
            //TODO test thsi
            for (int i=0; i < msgs.length; i++) {
                // Sender's phone number
                str += "SMS from " + msgs[i].getOriginatingAddress() + " : ";
                // Fetch the text message
                str += msgs[i].getMessageBody().toString();
                // Newline 🙂
                str += "\n";

                subscribeAction(msgs[i].getMessageBody().toString(), msgs[i].getOriginatingAddress());
            }

            // Display the entire SMS Message
            Log.d(TAG, str);


        }
    }

    // SMS from +16506445954 : Fkggog
    // phone code
    public void subscribeAction(final String rawMessage, final String phoneNumber) {

        final String potentialTextBody;
        final String potentialEventId;

        // check if there is a ':', it might be a message from a user that's already subscribed
        if (rawMessage.contains(":")) {
            // e.g. "86104: I'm down"
            potentialTextBody = rawMessage.substring(rawMessage.indexOf(":") + 1);
            potentialEventId = rawMessage.substring(0, rawMessage.indexOf(":"));
        } else {
            potentialEventId = rawMessage;
            // no text body
            potentialTextBody = null;
        }

        // query for potential event id
        ParseQuery<Event> parseQuery = ParseQuery.getQuery("Event");
        parseQuery.getInBackground(potentialEventId, new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                if (e == null) {
                    // get participants phone numbers array. If it doesn't exist, instantiate a new one
                    ArrayList<String> outSideParticipantsPhoneNumbers = (event.getOutsideParticipantsPhoneNumbers() != null) ? event.getOutsideParticipantsPhoneNumbers() : new ArrayList<String>();

                    // if user is already subscribed, notify user he's already in the chat group
                    if (outSideParticipantsPhoneNumbers.contains(phoneNumber)) {
                        // send user's message to channel
                        sendMessageToGroupChat(phoneNumber, potentialTextBody,potentialEventId);

                    } else { // else if user is subscribing now
                        outSideParticipantsPhoneNumbers.add(phoneNumber);
                        event.setOutsideParticipantsPhoneNumbers(outSideParticipantsPhoneNumbers);
                        event.saveInBackground();
                        // notify user that subscribed
                        sendMessage(phoneNumber, "Successfully subscribed to event! Download Shaggy at the Google Play store to access all the features");
                        // notify group chat that there is a new member in the group
                        sendJoinedMessage(potentialEventId, getContactName(phoneNumber,mContext));

                    }
                } else {
                    // notify the user it is not a valid command
                    sendMessage(phoneNumber, "Sorry, this is not a valid command. Please make sure you enter the right access code if you'd like to join the event");
                }
            }
        });

    }

    public void sendMessage(String phoneNumber, String messageBody)
    {
        try {
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, messageBody, null, null);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void sendMessageToGroupChat(String phoneNumber, String textBody, String eventId) {

        // using new `Message` Parse-backed model now
        final Message message = new Message();
        String senderName = getContactName(phoneNumber, mContext);

        // populate message
        message.setSenderName(senderName);
        message.setBody(textBody);
        // save User pointer
        message.put("outside_sender_phone_number", phoneNumber);
        message.setEventId(eventId);

    }

    public String getContactName(final String phoneNumber,Context context)
    {
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

    public void sendJoinedMessage(String eventId, String senderName) {
        Message m = new Message();
        m.setSenderId("InuSHuTqkn");
        m.setBody("Hey! " + senderName + " just joined the chat.");
        m.setEventId(eventId);
        m.setSenderName("Shaggy");
        try {
            m.save();
            HashMap<String, String> payload = new HashMap<>();
            payload.put("customData", senderName + " just joined the chat.");
            payload.put("title", "New message in channel");
            payload.put("channelID", eventId);
            payload.put("senderID", "InuSHuTqkn");
            payload.put("token", ""); //not being used rn
            ParseCloud.callFunctionInBackground("pushChannelTest", payload);
            //TODO: note, this message does not appear as last message in chats (we could fix that though)
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
