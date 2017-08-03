package shag.com.shag.Activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import shag.com.shag.Adapters.PhoneContactsAdapter;
import shag.com.shag.Models.PhoneContact;
import shag.com.shag.Other.ContactFetcher;
import shag.com.shag.R;

public class InviteFriendsActivity extends AppCompatActivity {
    Toolbar myToolbar;
    TextView toolbarTextView;

    ArrayList<PhoneContact> listContacts;
    ListView lvContacts;
    Button btSendInvitation;
    // event info
    String eventId;
    String eventDescription;
    String eventOwnerName;
    ArrayList<String> participantsIds;

    PhoneContactsAdapter adapterContacts;

    // constants for permissions
    private final static int MY_PERMISSIONS_REQUEST_SEND_SMS = 23;
    private final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 24;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_contacts_listview);

        // unwrap info from chat activity
        eventId = getIntent().getStringExtra("eventId");
        eventDescription = getIntent().getStringExtra("eventDescription");
        eventOwnerName = getIntent().getStringExtra("eventOwnerName");
        participantsIds = getIntent().getStringArrayListExtra("eventParticipants");


        setupViews();

    }

    private void setupViews()  {
        // get all contacts
        listContacts = new ContactFetcher(this).fetchAll();

        // Gets the ListView from the View list of the parent activity
        lvContacts = (ListView) findViewById(R.id.lvContacts);
        btSendInvitation = (Button) findViewById(R.id.btSendInvitation);

        adapterContacts = new PhoneContactsAdapter(this, listContacts);
        lvContacts.setAdapter(adapterContacts);

        // add listener for invite button
        btSendInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageToSelectedContacts();
            }
        });


        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        toolbarTextView = (TextView) findViewById(R.id.tvToolbarText);
    }

    public void sendMessageToSelectedContacts() {
        ArrayList<PhoneContact> selectedContacts = getSelectedContacts();

        for (PhoneContact selectedContact : selectedContacts) {
            sendText(selectedContact);
        }

    }

    public ArrayList<PhoneContact> getSelectedContacts() {
        ArrayList<PhoneContact> selectedContacts = new ArrayList<>();
        for (PhoneContact contact : listContacts) {
            if (contact.isSelected()) {
                selectedContacts.add(contact);
            }
        }
        return selectedContacts;
    }

    // send text based on contact info and event info
    public void sendText(PhoneContact contact) {
        String SMS_SENT = "SMS_SENT";
        String SMS_DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

        // For when the SMS has been sent
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_SENT));

        // For when the SMS has been delivered
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_DELIVERED));
        String messageBody = "Hey " + contact.name + " ! " +
                eventOwnerName + " wants to know if you're down to " + eventDescription +
                ". If'd like to join, reply this text with the following code " + eventId;

        try {
            SmsManager.getDefault().sendTextMessage(contact.numbers.get(0).number, null, messageBody, sentPendingIntent, deliveredPendingIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to send invitation", Toast.LENGTH_SHORT).show();
        }

    }
}
