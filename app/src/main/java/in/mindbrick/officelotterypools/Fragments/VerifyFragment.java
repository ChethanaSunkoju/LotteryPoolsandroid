package in.mindbrick.officelotterypools.Fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.mindbrick.officelotterypools.Helpers.PkDialog;
import in.mindbrick.officelotterypools.Helpers.SessionManagement;
import in.mindbrick.officelotterypools.MyLibrary.NetworkDialog;
import in.mindbrick.officelotterypools.R;

/**
 * Created by chethana on 12/11/2018.
 */

public class VerifyFragment extends Fragment {

    String mobile_number;
    Typeface tf;
    PkDialog mDialog;
    SessionManagement sessionManagement;
    CountryCodePicker ccp;
    String countryCodeAndroid = "91";
    BroadcastReceiver mConnReceiver;
    NetworkInfo currentNetworkInfo;
    FrameLayout ll_main;
    NetworkDialog dialog;
    ProgressDialog progressDialog;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.verify_fragment,container,false);
      //  tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Jaapokki_Regular.otf");

        ll_main = view.findViewById(R.id.ll_main);


        TextView tv_text_heading = view.findViewById(R.id.tv_text_heading);
       // tv_text_heading.setTypeface(tf);
        TextView tv_number = view.findViewById(R.id.tv_number);
      //  tv_number.setTypeface(tf);
        final EditText et_mobile = view.findViewById(R.id.et_mobile);
     //   et_mobile.addTextChangedListener(new PhoneNumberTextWatcher(et_mobile));
        TextView tv_verify = view.findViewById(R.id.tv_verify);



      //  tv_verify.setTypeface(tf);

        ccp = view.findViewById(R.id.ccp);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCodeAndroid = ccp.getSelectedCountryCode();
                Log.e("CountryCode", countryCodeAndroid);
            }
        });

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message


        dialog = new NetworkDialog(getActivity());
        dialog.setDialogTitle("No Internet Connection");
        dialog.setDialogMessage("You are offline please check your internet connection");



        checknetwork();


        tv_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mobile_number = et_mobile.getText().toString().trim();
                Log.e("mobile_number", mobile_number);


                if (mobile_number.equalsIgnoreCase("") || mobile_number.equalsIgnoreCase(null)) {
                    et_mobile.setError("Please enter the MobileNumber");
                } else if (mobile_number.length() < 10) {
                    et_mobile.setError("Invalid MobileNumber");
                } else {

                    mDialog = new PkDialog(getContext());
                    mDialog.setDialogTitle(getResources().getString(R.string.alert_nointernet));
                    mDialog.setDialogMessage("Is your phone number correct?" + mobile_number);

                    mDialog.show();
                    mDialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();


                            Log.e("network", "absent1");
                        }
                    });
                    mDialog.setNegativeButton(getResources().getString(R.string.timer_label_alert_cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //  Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_LONG);

                            mDialog.dismiss();


                            Login();

                       /* FragmentTransaction fr = getFragmentManager().beginTransaction();
                        fr.replace(R.id.frameLayout,new ActivationFragment());
                        fr.commit();*/
                            //drawer.setVisibility(View.GONE);
                        }
                    });

                /*FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.frameLayout,new ActivationFragment());
                fr.commit();*/
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public VerifyFragment() {
        // Required empty public constructor
    }


    private void Login() {

        progressDialog.show();

        String JSON_URL = getString(R.string.base_url)+ "usersignup";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        getLoginResponse(response);

                        progressDialog.dismiss();


                         Toast.makeText(getContext(),"response..."+response, Toast.LENGTH_LONG).show();


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse (VolleyError error){
                        //displaying the error in toast if occurrs
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                params.put("phone",mobile_number);
                params.put("ccode","+"+countryCodeAndroid);


                return params;
            }
        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    public void getLoginResponse(String response){

       progressDialog.dismiss();


        try{

            JSONObject jsonObject = new JSONObject(response);
            String Sno = jsonObject.getString("Sno");
            String Date = jsonObject.getString("Date");
            String Phone = jsonObject.getString("Phone");
            String Mcode = jsonObject.getString("Mcode");






            ActivationFragment af = new ActivationFragment();
            Bundle args = new Bundle();
            args.putString("Mobilenumber",mobile_number);
            args.putString("CountryCode",countryCodeAndroid);
            af.setArguments(args);
            getFragmentManager().beginTransaction().add(R.id.frameLayout,af).commit();




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
