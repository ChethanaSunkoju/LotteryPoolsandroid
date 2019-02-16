package in.mindbrick.officelotterypools.Activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.Map;

import in.mindbrick.officelotterypools.Adapter.SelectedAdapter;
import in.mindbrick.officelotterypools.Helpers.SessionManagement;
import in.mindbrick.officelotterypools.Models.Contact;
import in.mindbrick.officelotterypools.MyLibrary.NetworkDialog;
import in.mindbrick.officelotterypools.R;

/**
 * Created by chethana on 1/26/2019.
 */

public class PoolMembersActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    ImageView iv_back,iv_person,iv_share;
    TextView tv_tool_text,tv_contacts,tv_group,tv_share;
    RecyclerView rv_contacts;
    SelectedAdapter contactsAdapter;
    ArrayList<Contact> contactsArrayList;
    LinearLayout ll_group,ll_total;
    ArrayList<String> phoneContactArrayList;
    ArrayList<String> phonesArray = new ArrayList<>();
    String PoolGId,data,selectedContacts,selectedNames,adminId,sno,groupId;
    ProgressDialog progressDialog;
    NetworkDialog networkDialog;
    BroadcastReceiver mConnReceiver;
    NetworkInfo currentNetworkInfo;
    SessionManagement sessionManagement;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pool_members_screen);
        sessionManagement = new SessionManagement(this);

        toolbar = findViewById(R.id.toolbar);
        iv_back = toolbar.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_tool_text = toolbar.findViewById(R.id.tv_tool_text);
        tv_contacts = toolbar.findViewById(R.id.tv_contacts);
        ll_group = findViewById(R.id.ll_group);
        ll_group.setOnClickListener(this);
        tv_group = findViewById(R.id.tv_group);
        ll_total = findViewById(R.id.ll_total);
        tv_group.setText("Add Member");
        rv_contacts = findViewById(R.id.rv_contacts);


        contactsArrayList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_contacts.setLayoutManager(mLayoutManager);
        rv_contacts.setItemAnimator(new DefaultItemAnimator());


        data = getIntent().getStringExtra("data");
        if(data.equals("full")) {
            PoolGId = getIntent().getStringExtra("PoolGId");
            adminId = getIntent().getStringExtra("PooladminId");
            Log.e("memId",PoolGId);
            getpoolMembers();

        }else{

             selectedContacts = getIntent().getStringExtra("selectedContacts");
             selectedNames = getIntent().getStringExtra("selectedNames");
            PoolGId = getIntent().getStringExtra("PoolGId");
            addMembers();

        }

        HashMap<String,String> user = sessionManagement.getUserDetails();

        sno = user.get(SessionManagement.KEY_SNO);
        groupId = user.get(SessionManagement.KEY_GROUP_ID);

        Log.e("Sno",sno+"--"+groupId);

        /*if(sno.equals(adminId)){
            tv_group.setVisibility(View.VISIBLE);
        }else {
            tv_group.setVisibility(View.INVISIBLE);
        }*/



        networkDialog = new NetworkDialog(this);
        networkDialog.setDialogTitle("No Internet Connection");
        networkDialog.setDialogMessage("Your offline please check your internet connection");








    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_group:

                 Intent i_choose = new Intent(PoolMembersActivity.this,ChooseMembersActivitynew.class);
                 i_choose.putExtra("PoolGId",PoolGId);
                 startActivity(i_choose);
                break;

            case R.id.iv_back:

                Intent i_back = new Intent(PoolMembersActivity.this,AddPoolGiftScreen.class);
                i_back.putExtra("PoolGId",PoolGId);
                startActivity(i_back);

                break;


        }
    }

    private void getpoolMembers() {

        progressDialog.show();


        String JSON_URL = getString(R.string.base_url)+ "Getcustompoolmembersdetails";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        PoolMemberResponse(response);
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
                params.put("PoolGID",PoolGId);



                return params;
            }
        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    private void PoolMemberResponse(String response){
        progressDialog.dismiss();
        try{

            contactsArrayList.clear();

            JSONArray jsonArray = new JSONArray(response);

            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if(jsonObject.toString()!="" && jsonObject.toString().length()!=0) {

                    String _id = jsonObject.getString("_id");
                    String Sno = jsonObject.getString("Sno");
                    String Phone = jsonObject.getString("Phone");
                    String Firstname = jsonObject.getString("Firstname");
                    String Lastname = jsonObject.getString("Lastname");
                    String Email = jsonObject.getString("Email");
                    String ProfilePic = jsonObject.getString("ProfilePic");
                    String Status = jsonObject.getString("Status");

                    Log.e("memberDetails", Sno + "--" + Phone + "--" + Firstname + "--" + Lastname + "--" + Email + "--" + ProfilePic + "--" + Status);
                    Contact contact = new Contact(Sno, Phone, Firstname, Lastname, Email, ProfilePic, Status);
                    contactsArrayList.add(contact);

                    contactsAdapter = new SelectedAdapter(getApplicationContext(),contactsArrayList);
                    rv_contacts.setAdapter(contactsAdapter);
                    contactsAdapter.notifyDataSetChanged();
                   // tv_contacts.setText(String.valueOf(jsonArray.length()));
                }else{

                }
            }





        }catch(JSONException ex){
            ex.printStackTrace();
        }
    }

    private void addMembers() {

        progressDialog.show();


        String JSON_URL = getString(R.string.base_url)+ "addpoolmembers";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                      //  PoolMemberResponse(response);

                        addMemberResponse(response);
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
                params.put("PoolGID",PoolGId);
                params.put("poolmemebers",selectedContacts);


                return params;
            }
        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }


    private void addMemberResponse(String response){
        progressDialog.dismiss();
        try{
          JSONObject jsonObject = new JSONObject(response);
          String msg = jsonObject.getString("msg");

          if(msg.equalsIgnoreCase("success")) {

              String PoolGID1 = jsonObject.getString("PoolGID");
              String PooladminId = jsonObject.getString("PooladminId");


              PoolGId = PoolGID1;

              getpoolMembers();

          }








        }catch(JSONException ex){
            ex.printStackTrace();
        }
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
