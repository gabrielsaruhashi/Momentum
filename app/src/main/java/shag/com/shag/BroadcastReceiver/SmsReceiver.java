package shag.com.shag.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;

import shag.com.shag.Models.Event;

/**
 * Created by gabesaruhashi on 8/3/17.
 */

public class SmsReceiver extends BroadcastReceiver {

    private String TAG = SmsReceiver.class.getSimpleName();
    ArrayList<String> participantsPhoneNumbers;

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
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
    public void subscribeAction(String potentialEventId, final String phoneNumber) {

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
                        sendMessage(phoneNumber, "You already joined this event!");
                    } else { // else if user is subscribing now
                        outSideParticipantsPhoneNumbers.add(phoneNumber);
                        event.setOutsideParticipantsPhoneNumbers(outSideParticipantsPhoneNumbers);
                        event.saveInBackground();
                        sendMessage(phoneNumber, "Successfully subscribed to event! Download Shaggy at the Google Play store to access all the features");

                    }
                } else {
                    // notify the user it is not a valid command
                    sendMessage(phoneNumber, "Sorry, this is not a valid command. Please make sure you only type the access code if you'd like to join the event");
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

}
