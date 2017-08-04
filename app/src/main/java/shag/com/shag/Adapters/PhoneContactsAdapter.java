package shag.com.shag.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import shag.com.shag.Models.PhoneContact;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 8/3/17.
 */

public class PhoneContactsAdapter extends ArrayAdapter<PhoneContact> {

    ArrayList<PhoneContact> localPhoneContacts;
    ArrayList<PhoneContact> supportLocalPhoneContacts;
    Context context;
    public PhoneContactsAdapter(Context context, ArrayList<PhoneContact> contacts) {
        super(context, 0, contacts);
        this.localPhoneContacts = contacts;
        this.supportLocalPhoneContacts = localPhoneContacts;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item
        PhoneContact contact = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.item_contacts_lv, parent, false);
        }
        // Populate the data into the template view using the data object
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        TextView tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.cbContactCheck);
        tvName.setText(contact.name);
        tvEmail.setText("");
        tvPhone.setText("");
        if (contact.emails.size() > 0 && contact.emails.get(0) != null) {
            tvEmail.setText(contact.emails.get(0).address);
        }
        if (contact.numbers.size() > 0 && contact.numbers.get(0) != null) {
            tvPhone.setText(contact.numbers.get(0).number);

        }
        // check if checkbox is selected or not
        checkBox.setChecked(getItem(position).isSelected());

        // add listener
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton vw,
                                         boolean isChecked) {
                getItem(position).setSelected(vw.isChecked());
            }
        });

        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        clear();
        if (charText.length() == 0) {
            addAll(localPhoneContacts);
        } else {
            for (PhoneContact contact : supportLocalPhoneContacts) {
                if (contact.name.toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                   add(contact);
                }
            }
        }

        notifyDataSetChanged();
    }

}
