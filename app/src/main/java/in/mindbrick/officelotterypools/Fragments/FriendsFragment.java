package in.mindbrick.officelotterypools.Fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import in.mindbrick.officelotterypools.Adapter.SelectedAdapter;
import in.mindbrick.officelotterypools.Models.AddressBookContact;
import in.mindbrick.officelotterypools.Models.Contact;
import in.mindbrick.officelotterypools.MyLibrary.NetworkDialog;
import in.mindbrick.officelotterypools.R;

/**
 * Created by chethana on 1/12/2019.
 */

public class FriendsFragment extends Fragment implements View.OnClickListener,SearchView.OnQueryTextListener{

    RecyclerView rv_contacts;
    SelectedAdapter contactsAdapter;
    ArrayList<Contact> contactsArrayList;
    Toolbar toolbar;
    ImageView iv_back;
    TextView tv_contacts;
    ArrayList<String> phoneContactArrayList;
    String commaContacts;
    ArrayList<String> phonesArray = new ArrayList<>();
    private SearchView searchView;
    BroadcastReceiver mConnReceiver;
    NetworkInfo currentNetworkInfo;
    NetworkDialog dialog;
    LinearLayout ll_main;
    ProgressDialog progressDialog;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_screen, container, false);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForContactPermission();
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message

        phoneContactArrayList = new ArrayList<>();
        ll_main = view.findViewById(R.id.ll_main);

        readContacts();

       // getPhoneBook(getActivity());

        toolbar = view.findViewById(R.id.toolbar);



        iv_back = view.findViewById(R.id.iv_back);
        tv_contacts = view.findViewById(R.id.tv_contacts);
        tv_contacts.setOnClickListener(this);


        rv_contacts = view.findViewById(R.id.rv_contacts);

        contactsArrayList = new ArrayList<>();
       // contactsAdapter = new ContactsAdapter(getContext(),contactsArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rv_contacts.setLayoutManager(mLayoutManager);
        rv_contacts.setItemAnimator(new DefaultItemAnimator());

       // rv_contacts.setAdapter(contactsAdapter);

       // PoolList();



        dialog = new NetworkDialog(getActivity());
        dialog.setDialogTitle("No Internet Connection");
        dialog.setDialogMessage("Your offline please check your internet connection");

        sendContactsToServer();

        checknetwork();

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.menu_main,menu);

            SearchManager searchManager;


        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
      //  searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search with number");

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                // adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // filter recycler view when text is changed
                /*final ArrayList<CustomerList> filtered = filter(data,newText);
              adapter.setfilter(filtered);
                return true;*/

                if ( TextUtils.isEmpty ( newText ) ) {
                    contactsAdapter.getFilter().filter("");
                } else {
                    contactsAdapter.getFilter().filter(newText.toString());
                }
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {


            case R.id.action_search:
                return true;



            case android.R.id.home:



                return true;




            // int id = item.getItemId();

            //noinspection SimplifiableIfStatement
           /* if (id == R.id.action_search) {
                return true;
            }*/
            default:return super.onOptionsItemSelected(item);


        }


    }




    public static final int PERMISSION_REQUEST_CONTACT = 99;

    public void askForContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getActivity(),
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                    ActivityCompat.requestPermissions(getActivity(),
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

                        Cursor cursor = getActivity().getContentResolver().query(photoUri,
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

                            String phone =  number.replaceAll("[^0-9+]","");
                            Log.e("phone",phone);


                            phoneContactArrayList.add(phone);
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


   /* private void PoolList(){
        Contacts contacts = new Contacts("Srinivas","4");
        contactsArrayList.add(contacts);

        Contacts contacts1 = new Contacts("Chethana","2");
        contactsArrayList.add(contacts1);

        Contacts contacts2 = new Contacts("Sravan","3");
        contactsArrayList.add(contacts2);

        Contacts contacts3 = new Contacts("Venkat","4");
        contactsArrayList.add(contacts3);

        Contacts contacts4 = new Contacts("Rajeshwari","2");
        contactsArrayList.add(contacts4);

        Contacts contacts5 = new Contacts("Lakshmi","3");
        contactsArrayList.add(contacts5);

       // contactsAdapter.notifyDataSetChanged();
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_contacts:
                Uri contentUri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/drawable/" + "ic_launcher");

                StringBuilder msg = new StringBuilder();
                msg.append("Hey, Download this awesome app!");
                msg.append("\n");
                msg.append("https://play.google.com/store/apps/details?id=com.india.polietik"); //example :com.package.name

                if (contentUri != null) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Pool App");// temp permission for receiving app to read this file
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg.toString());
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    try {
                        startActivity(Intent.createChooser(shareIntent, "Share via"));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(), "No App Available", Toast.LENGTH_SHORT).show();
                    }
                }
        }

    }

    private void sendContactsToServer() {

        progressDialog.show();


        String JSON_URL = getString(R.string.base_url)+ "GetFriendslist";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        //   OtpVerificationResponse(response);

                        //  progressDialog.dismiss();

                        ContactsResponse(response);
                        progressDialog.dismiss();


                   //     Toast.makeText(getActivity(),"response..."+response,Toast.LENGTH_LONG).show();


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse (VolleyError error){
                        //displaying the error in toast if occurrs
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    private void ContactsResponse(String response){
        progressDialog.dismiss();
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
                Log.e("Contact_details",_id+"---"+Sno+"---"+Phone+"---"+Firstname+"---"+Lastname+"---"+Email+"---"+ProfilePic+"---"+Status);
                Contact contact = new Contact(Sno,Phone,Firstname,Lastname,Email,ProfilePic,Status);
                contactsArrayList.add(contact);

            }

            contactsAdapter = new SelectedAdapter(getActivity(),contactsArrayList);
            rv_contacts.setAdapter(contactsAdapter);




        }catch (JSONException ex){
            ex.printStackTrace();
        }



    }

    private void readContacts() {
        progressDialog.show();
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
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        final int mimeTypeIdx = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
        final int idIdx = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
        final int nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        final int dataIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA);
        final int typeIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.TYPE);

        while (cursor.moveToNext()) {
            progressDialog.dismiss();
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







    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    private void checknetwork() {

        mConnReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
                boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

                currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

                if (currentNetworkInfo.isConnected()) {
                    //Toast.makeText(getApplicationContext(),"We are Detected the Internet Connection.. Please Wait",Toast.LENGTH_LONG);
                    dialog.dismiss();
                    //drawer.setVisibility(View.VISIBLE);
                    ll_main.setVisibility(View.VISIBLE);

                    Log.e("network", "present");

                } else {

                    dialog.show();
                    //drawer.setVisibility(View.GONE);
                    ll_main.setVisibility(View.GONE);

                    Log.e("network", "absent");

                    dialog.setPositiveButton("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            checknetwork();
                            Log.e("network", "absent1");
                        }
                    });
                    dialog.setNegativeButton("Cancel", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_LONG);
                            //  getTargetFragment().this.finish();
                            dialog.dismiss();
                            //drawer.setVisibility(View.GONE);
                        }
                    });

                }
            }
        };
        getContext().registerReceiver(this.mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    public void onStart() {
        super.onStart();
      //  readContacts();
    }
}
