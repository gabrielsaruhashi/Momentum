package shag.com.shag.Adapters;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
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

        Uri photoUri = getPhotoUri(contact.id);


        // Populate the data into the template view using the data object
        ImageView ivContactImage = (ImageView) view.findViewById(R.id.ivContactImage);

        if (photoUri != null) {
            Glide.with(context)
                    .load(photoUri)
                    .fitCenter()
                    .bitmapTransform(new RoundedCornersTransformation(context, 20, 10))
                    .into(ivContactImage);

        } else { // if null, replace it with a gmail style icon
            // get the first letter of list item
            String letter = String.valueOf(contact.name.charAt(0));
            // generate random color
            ColorGenerator generator = ColorGenerator.MATERIAL;

            // Create a new TextDrawable for our image's background
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(letter, generator.getRandomColor());

            ivContactImage.setImageDrawable(drawable);
        }


        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.cbContactCheck);
        tvName.setText(contact.name);
        tvPhone.setText("");

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

    /**
     * @return the photo URI
     */
    public Uri getPhotoUri(String contactId) {
        try {
            Cursor cur = this.context.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + contactId + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                .parseLong(contactId));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

}
