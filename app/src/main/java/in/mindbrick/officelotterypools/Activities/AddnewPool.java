package in.mindbrick.officelotterypools.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
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

import in.mindbrick.officelotterypools.Helpers.SessionManagement;
import in.mindbrick.officelotterypools.Models.RadioButtonData;
import in.mindbrick.officelotterypools.MyLibrary.NetworkDialog;
import in.mindbrick.officelotterypools.R;

/**
 * Created by chethana on 12/9/2018.
 */

public class AddnewPool extends AppCompatActivity implements View.OnClickListener{

    TextView tv_save,tv_choose_member,tv_selected,tv_tool_text;
    RadioGroup radioGroup;
    RadioButton genderradioButton,radioLottery,radioCustom;
    Dialog dialog;
    ListView rv_list;
    String selectedContacts,newPool,selectedNames;
    EditText et_fname;
    LinearLayout ll_layout,ll_total;
    RadioButtonData radioButtonData;
    ArrayList<RadioButtonData> radioButtonDataArrayList = new ArrayList<>();
    int selectedId;
    String checkId,PoolName,sno,name;
    SessionManagement sessionManagement;
    Toolbar toolbar;
    ImageView iv_back;
    ProgressDialog progressDialog;
    NetworkDialog networkDialog;
    BroadcastReceiver mConnReceiver;
    NetworkInfo currentNetworkInfo;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_pool_new);
        newPool = getIntent().getStringExtra("newPool");

        sessionManagement = new SessionManagement(this);

        toolbar = findViewById(R.id.toolbar);
        iv_back = toolbar.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_tool_text = toolbar.findViewById(R.id.tv_tool_text);
        ll_total = findViewById(R.id.ll_total);


        HashMap<String,String> user = sessionManagement.getUserDetails();

        sno = user.get(SessionManagement.KEY_SNO);
        name = user.get(SessionManagement.KEY_FIRST_NAME);


        Log.e("sno",sno+"--"+name);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message


        if(newPool.equalsIgnoreCase("fulldata")) {
            selectedContacts = getIntent().getStringExtra("selectedContacts");
            selectedNames = getIntent().getStringExtra("selectedNames");
        }else {

        }
  //      Log.e("selectedContacts",selectedContacts);

        ll_layout = findViewById(R.id.ll_layout);

        poolTypes();
        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);
        et_fname = findViewById(R.id.et_fname);
        radioGroup = findViewById(R.id.radioGroup);
        tv_choose_member = findViewById(R.id.tv_choose_member);
        tv_choose_member.setOnClickListener(this);
        radioLottery = findViewById(R.id.radioLottery);

        radioCustom = findViewById(R.id.radioCustom);
        //tv_choose_member.setText("Participants"+" "+selectedContacts.si);

        tv_choose_member.setOnClickListener(this);
        tv_selected = findViewById(R.id.tv_selected);
        tv_selected.setText(selectedNames);
        networkDialog = new NetworkDialog(this);
        networkDialog.setDialogTitle("No Internet Connection");
        networkDialog.setDialogMessage("Your offline please check your internet connection");

        checknetwork();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_save:

                PoolName = et_fname.getText().toString().trim();

                if(checkId.equals("null")|| checkId.isEmpty()){
                    Toast.makeText(AddnewPool.this,"please select atleast one pool", Toast.LENGTH_SHORT).show();
                }else if(PoolName.equalsIgnoreCase("null" ) || PoolName.equalsIgnoreCase("")|| PoolName.isEmpty()){
                    et_fname.setError("Pool Name required");
                }else if(selectedContacts.isEmpty()|| selectedContacts.equals("null")){
                    Toast.makeText(AddnewPool.this,"Please select atleast two contacts", Toast.LENGTH_SHORT).show();
                }

                createPool();

                /*int selectedId = radioGroup.getCheckedRadioButtonId();
                genderradioButton = (RadioButton) findViewById(selectedId);
                if(selectedId==-1){
                    Toast.makeText(AddnewPool.this,"Nothing selected", Toast.LENGTH_SHORT).show();
                }
                else{
                    createPool();
                   *//* Toast.makeText(AddnewPool.this,genderradioButton.getText(), Toast.LENGTH_SHORT).show();
                    Log.e("selectedId",String.valueOf(selectedId));*//*
                }


                *//*Intent i_gift = new Intent(AddnewPool.this,GiftScreenActivity.class);
                startActivity(i_gift);*/

                break;

            case R.id.tv_choose_member:

                Intent i_invite = new Intent(AddnewPool.this,ChooseMembersActivity.class);
                startActivity(i_invite);
                /*SetUpDialog();
                dialog.show();*/

                break;

            case R.id.iv_back:;
            Intent i_conatcts = new Intent(AddnewPool.this,ContactsActivity.class);
            startActivity(i_conatcts);
            break;
        }
    }


    public  void SetUpDialog(){

        dialog = new Dialog(AddnewPool.this);
        dialog.setContentView(R.layout.choose_dialog);
        dialog.setTitle("");
        dialog.setCancelable(true);
        TextView textView = dialog.findViewById(R.id.text1);
         rv_list = dialog.findViewById(R.id.rv_list);
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        startManagingCursor(cursor);

        String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone._ID};

        int[] to = {android.R.id.text1,android.R.id.text2};
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getApplicationContext(),android.R.layout.simple_list_item_2,cursor,from,to);
        rv_list.setAdapter(simpleCursorAdapter);
        rv_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Button button = dialog.findViewById(R.id.but_email_verify);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               dialog.dismiss();


            }
        });

    }


    private void poolTypes(){
        progressDialog.show();

        String JSON_URL = getString(R.string.base_url)+ "pooltypes";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        getPoolTypeResponse(response);
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





                return params;
            }
        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    private void getPoolTypeResponse(String response){
        progressDialog.dismiss();
        try{

            JSONArray jsonArray = new JSONArray(response);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String _id = jsonObject.getString("_id");
                String Sno = jsonObject.getString("Sno");
                String Poolname = jsonObject.getString("Poolname");
                String Status = jsonObject.getString("Status");

                radioButtonData = new RadioButtonData(Sno,Poolname,Status);
                radioButtonDataArrayList.add(radioButtonData);

            }
            final RadioGroup rg = new RadioGroup(this); // create the RadioGroup
            rg.setOrientation(RadioGroup.VERTICAL);// or RadioGroup.VERTICAL
            for (int i = 0; i < radioButtonDataArrayList.size(); i++) {
                RadioButton rb = new RadioButton(this);
                rb.setText(radioButtonDataArrayList.get(i).getPoolname());
                rb.setId(Integer.valueOf(radioButtonDataArrayList.get(i).getSno()));
                String st = radioButtonDataArrayList.get(i).getStatus();
                if(st.equals("Active")){
                    rb.setSaveEnabled(true);
                }else {
                    rb.setSaveEnabled(false);
                }

                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                rg.addView(rb,params);
            }

            ll_layout.addView(rg);

            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                   selectedId = rg.getCheckedRadioButtonId();
                   checkId = String.valueOf(selectedId);
                    Log.e("SelectedId", String.valueOf(selectedId));
                }
            });




        }catch (JSONException ex){
            ex.printStackTrace();
        }

    }

    private void createPool() {

        progressDialog.show();



        String JSON_URL = getString(R.string.base_url)+ "poolcreate";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                         createPoolResponse(response);
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

                params.put("ptye", Integer.toString(selectedId));
                params.put("adminid",sno);
                params.put("adminname",name);
                params.put("poolname",et_fname.getText().toString());
                params.put("poolmemebers",selectedContacts);



                return params;
            }
        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }


    private void createPoolResponse(String response){
        progressDialog.dismiss();

        try {

            JSONObject jsonObject = new JSONObject(response);
            String msg = jsonObject.getString("msg");

            if(msg.equalsIgnoreCase("success")){

                String PoolGID = jsonObject.getString("PoolGID");
                String Pooltype = jsonObject.getString("Pooltype");


                if(Pooltype.equals("1")) {

                    Intent i_gift = new Intent(AddnewPool.this, GiftScreenActivity.class);
                    i_gift.putExtra("PoolGID",PoolGID);
                    startActivity(i_gift);

                }else {
                    Intent i_lottery = new Intent(AddnewPool.this, LotteryPoolScreen.class);
                    startActivity(i_lottery);
                }

            }else {
                Toast.makeText(getApplicationContext(),"Creation Failed", Toast.LENGTH_SHORT).show();
            }

        }catch (JSONException ex){
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
