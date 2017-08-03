package shag.com.shag.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import shag.com.shag.Adapters.PhoneContactsAdapter;
import shag.com.shag.Models.PhoneContact;
import shag.com.shag.Other.ContactFetcher;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 8/2/17.
 */

public class ContactsFragment extends Fragment  {
    ArrayList<PhoneContact> listContacts;
    ListView lvContacts;
    Button btSendInvitation;

    // A UI Fragment must inflate its View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout
        View v = inflater.inflate(R.layout.fragment_contacts_listview, container, false);
        // get all contacts
        listContacts = new ContactFetcher(getActivity()).fetchAll();

        // Gets the ListView from the View list of the parent activity
        lvContacts = (ListView) v.findViewById(R.id.lvContacts);
        btSendInvitation = (Button) v.findViewById(R.id.btSendInvitation);

        PhoneContactsAdapter adapterContacts = new PhoneContactsAdapter(getActivity(), listContacts);
        lvContacts.setAdapter(adapterContacts);

        // add listener for invite button
        btSendInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageToSelectedContacts();
            }
        });

        return v;
    }

    public void sendMessageToSelectedContacts() {
        ArrayList<PhoneContact> selectedContacts = new ArrayList<>();
        for (PhoneContact contact : listContacts) {
            if (contact.isSelected()) {
                selectedContacts.add(contact);
            }
        }



    }



}
