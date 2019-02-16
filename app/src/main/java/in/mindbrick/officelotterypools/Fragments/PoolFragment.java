package in.mindbrick.officelotterypools.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import in.mindbrick.officelotterypools.Activities.AddnewPool;
import in.mindbrick.officelotterypools.Adapter.AdminPoolAdapter;
import in.mindbrick.officelotterypools.Adapter.PoolAdapter;
import in.mindbrick.officelotterypools.Helpers.SessionManagement;
import in.mindbrick.officelotterypools.Models.Pool;
import in.mindbrick.officelotterypools.MyLibrary.NetworkDialog;
import in.mindbrick.officelotterypools.R;

/**
 * Created by chethana on 12/8/2018.
 */

public class PoolFragment extends Fragment implements View.OnClickListener {

    Toolbar toolbar;
    ImageView iv_back;
    TextView tv_tool_text,tv_addPool,tv_no_data;
    RecyclerView rv_pool_list;
    PoolAdapter poolAdapter;
    ArrayList<Pool> poolArrayList;
    ArrayList<Pool> pool_ArrayList;
    String sno;
    SessionManagement sessionManagement;
    Pool pool;
    AdminPoolAdapter adminPoolAdapter;
    BroadcastReceiver mConnReceiver;
    NetworkInfo currentNetworkInfo;
    NetworkDialog dialog;
    LinearLayoutManager layoutManager;
    LinearLayout ll_main;
    ProgressDialog progressDialog;





    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pool_screen, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        iv_back = view.findViewById(R.id.iv_back);
        tv_tool_text = view.findViewById(R.id.tv_tool_text);
        tv_addPool = view.findViewById(R.id.tv_addPool);
        tv_addPool.setOnClickListener(this);
        rv_pool_list = view.findViewById(R.id.rv_pool_list);
        ll_main = view.findViewById(R.id.ll_main);
      //  poolArrayList = new ArrayList<>();
        tv_no_data = view.findViewById(R.id.tv_no_data);

        pool_ArrayList = new ArrayList<>();

        sessionManagement = new SessionManagement(getActivity());
        HashMap<String,String> user = sessionManagement.getUserDetails();

        sno = user.get(SessionManagement.KEY_SNO);
        Log.e("pool_sno",sno);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message




        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rv_pool_list.setLayoutManager(mLayoutManager);
        rv_pool_list.setItemAnimator(new DefaultItemAnimator());

        dialog = new NetworkDialog(getActivity());
        dialog.setDialogTitle("No Internet Connection");
        dialog.setDialogMessage("Your offline please check your internet connection");


        checknetwork();





       


        getAdminPools();



        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.tv_addPool:
                Intent i = new Intent(getContext(), AddnewPool.class);
                i.putExtra("newPool","emptydata");
                startActivity(i);
                break;

        }

    }



    private void getAdminPools(){
        progressDialog.show();
        String JSON_URL = getString(R.string.base_url)+ "GetadminCreatedpools";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        adminPoolResponse(response);
                        progressDialog.dismiss();




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

                params.put("userid",sno);




                return params;
            }
        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }



    private void adminPoolResponse(String response){
        progressDialog.dismiss();
        try{
            pool_ArrayList.clear();
            JSONArray jsonArray = new JSONArray(response);


            if(jsonArray.length()>0) {

                tv_no_data.setVisibility(View.GONE);
                rv_pool_list.setVisibility(View.VISIBLE);

                for (int i = 0; i < jsonArray.length(); i++) {

                    Log.e("HIII", "Entered into pool group");

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String PoolGID = jsonObject.getString("PoolGID");
                    String Pooltype = jsonObject.getString("Pooltype");
                    String PooladminId = jsonObject.getString("PooladminId");
                    String Adminname = jsonObject.getString("Adminname");
                    String Poolname = jsonObject.getString("Poolname");
                    String Amount = jsonObject.getString("Amount");
                    String PGpic = jsonObject.getString("PGpic");
                    String CreatedDate = jsonObject.getString("CreatedDate");
                /*JSONArray jsonArray1 = jsonObject.getJSONArray("Poolmembers");
                for(int j=0;j<jsonArray1.length();j++){

                    Log.e("ggggg","Entered into pool members");
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                    String _id = jsonObject1.getString("_id");
                    String Sno = jsonObject1.getString("Sno");
                    String Phone = jsonObject1.getString("Phone");
                    String Firstname = jsonObject1.getString("Firstname");
                    String Lastname = jsonObject1.getString("Lastname");
                    String Email = jsonObject1.getString("Email");
                    String ProfilePic = jsonObject1.getString("ProfilePic");
                    String Status = jsonObject1.getString("Status");
                }*/

                    Log.e("all_pools", PoolGID + "--" + Pooltype + "--" + PooladminId + "--" + Adminname + "--" + Poolname + "--" + Amount + "--" + PGpic + "--" + CreatedDate);
                    pool = new Pool(PoolGID, Pooltype, PooladminId, Adminname, Poolname, Amount, PGpic, CreatedDate);

                    Log.e("PoolData", pool.getPoolname() + "---" + pool.getAmount());
                    pool_ArrayList.add(pool);

                    Collections.reverse(pool_ArrayList);



                }

                poolAdapter = new PoolAdapter(getActivity(), pool_ArrayList);
                rv_pool_list.setAdapter(poolAdapter);
                poolAdapter.notifyDataSetChanged();

            }else {
                tv_no_data.setVisibility(View.VISIBLE);
                rv_pool_list.setVisibility(View.GONE);
            }

           /* adminPoolAdapter = new AdminPoolAdapter(getActivity(),pool_ArrayList);
            rv_pool_list.setAdapter(adminPoolAdapter);
            adminPoolAdapter.notifyDataSetChanged();*/

        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    public void onResume() {
        super.onResume();
      //  rv_pool_list.setAdapter(poolAdapter);

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



}
