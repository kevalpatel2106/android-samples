package example.contactsread;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final int READ_CONTACT_PERMISSION_REQUEST_CODE = 100;

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check for the contact permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            //permission already granted. Load contacts from contact provider.
            loadContacts();
        } else {
            //Contact permission not available. Ask for that.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACT_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case READ_CONTACT_PERMISSION_REQUEST_CODE:

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted. Load contacts from contact provider.
                    loadContacts();
                } else {
                    //Permission declined.
                    Toast.makeText(this, "Contact permission declined. Cannot read contacts.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Load the contacts from the contact provider and read the contact details.
     */
    private void loadContacts() {
        HashMap<Long, ContactPojo> contactsHash = new HashMap<>();

        final String[] projections = new String[]{
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Contacts.Data.DATA1,
                ContactsContract.Data.MIMETYPE
        };

        Cursor c = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,  //Contact provider content uri
                projections,
                ContactsContract.Data.HAS_PHONE_NUMBER + "!=0 AND (" + ContactsContract.Data.MIMETYPE + "=? OR " + ContactsContract.Data.MIMETYPE + "=? OR " + ContactsContract.Data.MIMETYPE + "=?)",
                new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_TYPE},
                ContactsContract.Data.DISPLAY_NAME + " ASC");

        if (c != null) {
            while (c.moveToNext()) {
                long id = c.getLong(c.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                String phoneOrEmail = c.getString(c.getColumnIndex(ContactsContract.Contacts.Data.DATA1));

                ContactPojo contactPojo;
                if (contactsHash.containsKey(id)) {
                    contactPojo = contactsHash.get(id);

                    String mimeType = c.getString(c.getColumnIndex(ContactsContract.Data.MIMETYPE));
                    switch (mimeType) {
                        case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                            contactPojo.email.add(phoneOrEmail);
                            break;
                        case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                            contactPojo.phone.add(phoneOrEmail);
                            break;
                        case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE:
                            contactPojo.street = c.getString(c.getColumnIndex(ContactsContract.Contacts.Data.DATA4));
                            contactPojo.city = c.getString(c.getColumnIndex(ContactsContract.Contacts.Data.DATA7));
                            contactPojo.state = c.getString(c.getColumnIndex(ContactsContract.Contacts.Data.DATA8));
                            contactPojo.country = c.getString(c.getColumnIndex(ContactsContract.Contacts.Data.DATA10));
                            contactPojo.formattedAddress = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DATA1));
                            break;
                    }
                } else {
                    contactPojo = new ContactPojo();
                    contactPojo.id = id;
                    contactPojo.name = c.getString(c.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));

                    String mimeType = c.getString(c.getColumnIndex(ContactsContract.Data.MIMETYPE));
                    switch (mimeType) {
                        case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                            contactPojo.email.add(phoneOrEmail);
                            break;
                        case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                            contactPojo.phone.add(phoneOrEmail);
                            break;
                        case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE:
                            contactPojo.street = c.getString(c.getColumnIndex(ContactsContract.Contacts.Data.DATA4));
                            contactPojo.city = c.getString(c.getColumnIndex(ContactsContract.Contacts.Data.DATA7));
                            contactPojo.state = c.getString(c.getColumnIndex(ContactsContract.Contacts.Data.DATA8));
                            contactPojo.country = c.getString(c.getColumnIndex(ContactsContract.Contacts.Data.DATA10));
                            contactPojo.formattedAddress = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DATA1));
                            break;
                    }
                    contactsHash.put(id, contactPojo);
                }

                Log.d("contact pojo", contactPojo.toString());
            }

            Log.d("cursor count", contactsHash.size() + " ");
            c.close();
        }
    }

    public class ContactPojo {
        private String name;

        private ArrayList<String> phone = new ArrayList<>();

        private long id;

        private String street;

        private String city;

        private String state;

        private String country;

        private String formattedAddress;

        private ArrayList<String> email = new ArrayList<>();

        public ContactPojo() {
        }

        @Override
        public String toString() {
            return id + " " + name + " " + formattedAddress;
        }
    }
}
