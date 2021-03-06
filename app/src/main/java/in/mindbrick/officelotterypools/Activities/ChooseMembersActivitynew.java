package in.mindbrick.officelotterypools.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.ActionMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import in.mindbrick.officelotterypools.Adapter.ContactsAdapter;
import in.mindbrick.officelotterypools.Models.AddressBookContact;
import in.mindbrick.officelotterypools.Models.Contact;
import in.mindbrick.officelotterypools.R;

/**
 * Created by chethana on 2/3/2019.
 */

public class ChooseMembersActivitynew extends AppCompatActivity implements View.OnClickListener {

    RecyclerView rv_contact_mem,rv_choose_mem;
    ArrayList<String> phoneContactArrayList;
    String commaContacts,PoolGId;
    StringBuilder sb= null;
    StringBuilder sn = null;
    ArrayList<Contact> contactsArrayList;
    ContactsAdapter contactsAdapter;
    Toolbar toolbar;
    ImageView iv_back;
    TextView tv_tool_text,tv_next;
    ArrayList<Contact> multiselect_list = new ArrayList<>();
    boolean isMultiSelect = false;
    ActionMode mActionMode;
    private List<Contact> currentSelectedItems = new ArrayList<>();
    ArrayList<String> phonesArray = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_members_screen);
        PoolGId = getIntent().getStringExtra("PoolGId");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForContactPermission();
        }


        toolbar = findViewById(R.id.toolbar);
        iv_back = toolbar.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_tool_text = toolbar.findViewById(R.id.tv_tool_text);
        tv_next = toolbar.findViewById(R.id.tv_next);
        tv_next.setOnClickListener(this);
        rv_contact_mem = findViewById(R.id.rv_contact_mem);
        rv_choose_mem = findViewById(R.id.rv_choose_mem);
        phoneContactArrayList = new ArrayList<>();
        contactsArrayList = new ArrayList<>();

        readContacts();
        // getPhoneBook(getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_contact_mem.setLayoutManager(mLayoutManager);
        rv_contact_mem.setItemAnimator(new DefaultItemAnimator());
        sendContactsToServer();
    }

    public static final int PERMISSION_REQUEST_CONTACT = 99;

    public void askForContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ChooseMembersActivitynew.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) ChooseMembersActivitynew.this,
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChooseMembersActivitynew.this);
                    builder.setTitle("Contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Contacts access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}
                                    , PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(ChooseMembersActivitynew.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                //   getContact();
            }
        } else {
            //  getContact();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //   getContact();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    //   ToastMaster.showMessage(getActivity(),"No permission for contacts");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void getPhoneBook(Context context) {
        List<String> result = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor contacts = null;
        try {
            contacts = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (contacts.moveToFirst()) {
                do {
                    String contactId = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts._ID));

                    Cursor emails = null;
                    Cursor phones = null;
                    try {
                        emails = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID
                                + " = " + contactId, null, null);
                        while (emails.moveToNext()) {
                            String email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            // Add email to your phoneContact object
                        }

                        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(contactId));
                        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                        Cursor cursor = getContentResolver().query(photoUri,
                                new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
                        if (cursor == null) {

                        }
                        try {
                            if (cursor.moveToFirst()) {
                                byte[] data = cursor.getBlob(0);
                                if (data != null) {
                                    // image = new String(data);
                                }
                            }
                        } finally {
                            cursor.close();
                        }


                        phones = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                        while (phones.moveToNext()) {
                            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String displayName = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                            // Add others information into your phoneContact object
                            Log.e("contacts", number + "----" + displayName);
                            // PhoneContact phoneContact = new PhoneContact(displayName, number, image);

                            phoneContactArrayList.add(number);
                            HashSet<String> hashSet = new HashSet<String>();
                            hashSet.addAll(phoneContactArrayList);
                            phoneContactArrayList.clear();
                            phoneContactArrayList.addAll(hashSet);
                            commaContacts =  TextUtils.join(",",phoneContactArrayList);

                            Log.e("contact_list",commaContacts);
                            //  tv_tool_text.setText(String.valueOf(phoneContactArrayList.size()));


                        }
                    } finally {
                        if (emails != null) {
                            emails.close();
                        }
                        if (phones != null) {
                            phones.close();
                        }
                    }
                    // result.add(phoneContact);

                } while (contacts.moveToNext());
            }
        } finally {
            if (contacts != null) {
                contacts.close();
            }
        }

    }

    private void sendContactsToServer() {


        String JSON_URL = getString(R.string.base_url)+ "GetFriendslist";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        //   OtpVerificationResponse(response);

                        //  progressDialog.dismiss();

                        ContactsResponse(response);


                        Toast.makeText(getApplicationContext(),"response..."+response, Toast.LENGTH_LONG).show();


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse (VolleyError error){
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("mpnumbers",commaContacts);



                return params;
            }
        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    private void ContactsResponse(String response){
        try{
            JSONArray jsonArray = new JSONArray(response);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String _id = jsonObject.getString("_id");
                String Sno = jsonObject.getString("Sno");
                String Phone = jsonObject.getString("Phone");
                String Firstname = jsonObject.getString("Firstname");
                String Lastname = jsonObject.getString("Lastname");
                String Email = jsonObject.getString("Email");
                String ProfilePic = jsonObject.getString("ProfilePic");
                String Status = jsonObject.getString("Status");
                Log.e("Contact_member_details",_id+"---"+Sno+"---"+Phone+"---"+Firstname+"---"+Lastname+"---"+Email+"---"+ProfilePic+"---"+Status);
                Contact contact = new Contact(Sno,Phone,Firstname,Lastname,Email,ProfilePic,Status);
                contactsArrayList.add(contact);

            }

            contactsAdapter = new ContactsAdapter(getApplicationContext(),contactsArrayList);
            rv_contact_mem.setAdapter(contactsAdapter);
           /* contactsAdapter = new ContactsAdapter(getApplicationContext(), contactsArrayList, new ContactsAdapter.OnItemCheckListener() {
                @Override
                public void onItemCheck(Contact item) {
                    currentSelectedItems.add(item);
                    Toast.makeText(getApplicationContext(),"checked"+currentSelectedItems.size(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onItemUncheck(Contact item) {
                    currentSelectedItems.remove(item);
                }
            });

            rv_contact_mem.setAdapter(contactsAdapter);
*/




        }catch (JSONException ex){
            ex.printStackTrace();
        }



    }



    private void readContacts() {
        List<AddressBookContact> list = new LinkedList<AddressBookContact>();
        LongSparseArray<AddressBookContact> array = new LongSparseArray<AddressBookContact>();
        long start = System.currentTimeMillis();

        String[] projection = {
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Contactables.DATA,
                ContactsContract.CommonDataKinds.Contactables.TYPE,
        };
        String selection = ContactsContract.Data.MIMETYPE + " in (?, ?)";
        String[] selectionArgs = {
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
        };
        String sortOrder = ContactsContract.Contacts.SORT_KEY_ALTERNATIVE;

        Uri uri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI;
// we could also use Uri uri = ContactsContract.Data.CONTENT_URI;

// ok, let's work...
        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        final int mimeTypeIdx = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
        final int idIdx = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
        final int nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        final int dataIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA);
        final int typeIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.TYPE);

        while (cursor.moveToNext()) {
            long id = cursor.getLong(idIdx);
            AddressBookContact addressBookContact = array.get(id);
            if (addressBookContact == null) {
                addressBookContact = new AddressBookContact(id, cursor.getString(nameIdx), getResources());
                array.put(id, addressBookContact);
                list.add(addressBookContact);


            }
            int type = cursor.getInt(typeIdx);
            String data = cursor.getString(dataIdx);
            String mimeType = cursor.getString(mimeTypeIdx);
            if (mimeType.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                // mimeType == ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                addressBookContact.addEmail(type, data);
            } else {
                // mimeType == ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                addressBookContact.addPhone(type, data);
                String number = data;


                String phone =  number.replaceAll("[^0-9+]","");
                Log.e("phone",phone);
                phonesArray.add(phone);

                HashSet<String> hashSet = new HashSet<String>();
                hashSet.addAll(phonesArray);
                phonesArray.clear();
                phonesArray.addAll(hashSet);
                Log.e("arraySize","Length"+phonesArray.size());
                commaContacts =  TextUtils.join(",",phonesArray);
                Log.e("commaContacts",commaContacts);

            }
        }
        long ms = System.currentTimeMillis() - start;
        cursor.close();






// done!!! show the results...
       /* int i = 1;
        for (AddressBookContact addressBookContact : list) {
            Log.d(TAG, "AddressBookContact #" + i++ + ": " + addressBookContact.toString(true));
        }
        final String cOn = "<b><font color='#ff9900'>";
        final String cOff = "</font></b>";
        Spanned l1 = Html.fromHtml("got " + cOn + array.size() + cOff + " contacts<br/>");
        Spanned l2 = Html.fromHtml("query took " + cOn + ms / 1000f + cOff + " s (" + cOn + ms + cOff + " ms)");

        Log.d(TAG, "\n\n╔══════ query execution stats ═══════" );
        Log.d(TAG, "║    " + l1);
        Log.d(TAG, "║    " + l2);
        Log.d(TAG, "╚════════════════════════════════════" );
        SpannableStringBuilder msg = new SpannableStringBuilder().append(l1).append(l2);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        TextView tv = new TextView(this);
        tv.setTextSize(20);
        tv.setBackgroundColor(0xff000033);
        tv.setPadding(24, 8, 24, 24);
        tv.setText(msg);
        ll.addView(tv);
        ListView lv = new ListView(this);
        lv.setAdapter(new ArrayAdapter<AddressBookContact>(this, android.R.layout.simple_list_item_1, list));
        ll.addView(lv);
        new AlertDialog.Builder(this).setView(ll).setPositiveButton("close", null).create().show();
    }*/
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_next:
                sb = new StringBuilder();
                sn = new StringBuilder();
                for(Contact c : contactsAdapter.checkedContacts){
                    sb.append(c.getPhone());
                    sn.append(c.getFirstname());
                    sn.append(",");
                    sb.append(",");
                }

                if(contactsAdapter.checkedContacts.size()>0){
                    Toast.makeText(getApplicationContext(),sb.toString(), Toast.LENGTH_LONG).show();
                    Intent i_new = new Intent(ChooseMembersActivitynew.this,PoolMembersActivity.class);
                    i_new.putExtra("selectedContacts",sb.toString());
                    i_new.putExtra("selectedNames",sn.toString());
                    i_new.putExtra("data","empty");
                    i_new.putExtra("PoolGId",PoolGId);
                    startActivity(i_new);

                }else {
                    Toast.makeText(getApplicationContext(),"Please select Contact", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.iv_back:

                Intent i_back = new Intent(ChooseMembersActivitynew.this,PoolMembersActivity.class);
                i_back.putExtra("data","empty");
                startActivity(i_back);

                break;

        }
    }
}
