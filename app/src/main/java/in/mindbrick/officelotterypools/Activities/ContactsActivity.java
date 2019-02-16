package in.mindbrick.officelotterypools.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.view.View;
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
 * Created by chethana on 1/2/2019.
 */

public class ContactsActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    ImageView iv_back,iv_person,iv_share;
    TextView tv_tool_text,tv_contacts,tv_group,tv_share;

    SelectedAdapter contactsAdapter;
    ArrayList<Contact> contactsArrayList;
    LinearLayout ll_group,ll_total;
    ArrayList<String> phoneContactArrayList;
    String commaContacts,sendContacts;
    ArrayList<String> phonesArray = new ArrayList<>();
    ProgressDialog progressDialog;
    NetworkDialog networkDialog;
    BroadcastReceiver mConnReceiver;
    NetworkInfo currentNetworkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_screen);

        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForContactPermission();
        }*/

        phoneContactArrayList = new ArrayList<>();
        ll_total = findViewById(R.id.ll_total);


      //  readContacts();
        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message

        //getPhoneBook(getApplicationContext());

        toolbar = findViewById(R.id.toolbar);
        iv_back = toolbar.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        iv_person = findViewById(R.id.iv_person);
        iv_share = findViewById(R.id.iv_share);
        tv_tool_text = findViewById(R.id.tv_tool_text);
        tv_contacts = findViewById(R.id.tv_contacts);
        tv_group = findViewById(R.id.tv_group);
        tv_share = findViewById(R.id.tv_share);

        iv_share.setOnClickListener(this);
        ll_group = findViewById(R.id.ll_group);
        ll_group.setOnClickListener(this);
        contactsArrayList = new ArrayList<>();
      //  contactsAdapter = new SelectedAdapter(getApplicationContext(),contactsArrayList);


        networkDialog = new NetworkDialog(this);
        networkDialog.setDialogTitle("No Internet Connection");
        networkDialog.setDialogMessage("Your offline please check your internet connection");

      //  rv_contacts.setAdapter(contactsAdapter);

     //   sendContactsToServer();

        checknetwork();


     //   PoolList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_share:

                Uri contentUri = Uri.parse("android.resource://" + getPackageName() + "/drawable/" + "ic_launcher");

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
                        Toast.makeText(getApplicationContext(), "No App Available", Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case R.id.ll_group:
                Intent i_group = new Intent(ContactsActivity.this,AddnewPool.class);
                i_group.putExtra("newPool","noData");

                startActivity(i_group);

                break;

            case R.id.iv_back:
                Intent i_back = new Intent(ContactsActivity.this,HomeScreen.class);
                startActivity(i_back);

                break;

        }
    }

    public static final int PERMISSION_REQUEST_CONTACT = 99;

    public void askForContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ContactsActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) ContactsActivity.this,
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactsActivity.this);
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

                    ActivityCompat.requestPermissions(ContactsActivity.this,
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



    private void sendContactsToServer() {

        progressDialog.dismiss();


        String JSON_URL = getString(R.string.base_url)+ "GetFriendslist";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                     //   OtpVerificationResponse(response);

                        //  progressDialog.dismiss();

                        //ContactsResponse(response);
                        progressDialog.dismiss();


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
                    networkDialog.dismiss();
                    //drawer.setVisibility(View.VISIBLE);
                    ll_total.setVisibility(View.VISIBLE);

                    Log.e("network", "present");

                } else {

                    networkDialog.show();
                    //drawer.setVisibility(View.GONE);
                    ll_total.setVisibility(View.GONE);

                    Log.e("network", "absent");

                    networkDialog.setPositiveButton("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            networkDialog.dismiss();
                            checknetwork();
                            Log.e("network", "absent1");
                        }
                    });
                    networkDialog.setNegativeButton("Cancel", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG);
                            //  getTargetFragment().this.finish();
                            networkDialog.dismiss();
                            //drawer.setVisibility(View.GONE);
                        }
                    });

                }
            }
        };
        registerReceiver(this.mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

}
