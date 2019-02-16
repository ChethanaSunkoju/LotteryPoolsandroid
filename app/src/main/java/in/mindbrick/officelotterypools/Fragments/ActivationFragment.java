package in.mindbrick.officelotterypools.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.mindbrick.officelotterypools.Helpers.PkDialog;
import in.mindbrick.officelotterypools.Helpers.SessionManagement;
import in.mindbrick.officelotterypools.MyLibrary.NetworkDialog;
import in.mindbrick.officelotterypools.R;

/**
 * Created by chethana on 12/11/2018.
 */

public class ActivationFragment extends Fragment implements TextWatcher {

    Typeface tf;
    TextView tv_verify_heading,tv_resend;

    String otp,mobile_number,mobile,countryCode;
    private SmsVerifyCatcher smsVerifyCatcher;
    Context context;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    EditText et_otp1,et_otp2,et_otp3,et_otp4;
    SessionManagement sessionManagement;
    private TabLayout tabLayout;
    BroadcastReceiver mConnReceiver;
    NetworkInfo currentNetworkInfo;
    PkDialog mDialog;
    ProgressDialog progressDialog;
    FrameLayout ll_activation;
    NetworkDialog dialog;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.verify_activation,container,false);
       // tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Jaapokki_Regular.otf");
        sessionManagement = new SessionManagement(getContext());
        mobile = getArguments().getString("Mobilenumber");
        countryCode = getArguments().getString("CountryCode");
       Log.e("mobile",mobile+"---"+countryCode);
         tv_verify_heading = view.findViewById(R.id.tv_verify_heading);
      //  tv_verify_heading.setTypeface(tf);
       tv_verify_heading.setText("We have sent you an SMS with a 4-digit code to the number"+" "+mobile);
       tv_resend = view.findViewById(R.id.tv_resend);
      ll_activation = view.findViewById(R.id.ll_activation);
       tv_resend.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(mobile != null && countryCode != null){
                   Login();
               }
           }
       });
         et_otp1 = view.findViewById(R.id.et_otp1);
         et_otp2 = view.findViewById(R.id.et_otp2);
         et_otp3 = view.findViewById(R.id.et_otp3);
         et_otp4 = view.findViewById(R.id.et_otp4);


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message

       /* mDialog = new PkDialog(getActivity());
        mDialog.setDialogTitle(getResources().getString(R.string.alert_nointernet));
        mDialog.setDialogMessage(getResources().getString(R.string.alert_nointernet_message));
*/
        dialog = new NetworkDialog(getActivity());
        dialog.setDialogTitle("No Internet Connection");
        dialog.setDialogMessage("Your offline please check your internet connection");

        checknetwork();

        smsVerifyCatcher = new SmsVerifyCatcher(getActivity(), new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                Log.e("AgilanbuOTP", code);
              //  Toast.makeText(getActivity(), "AgilanbuOTP: " + code, Toast.LENGTH_LONG).show();
               // et_otp.setText(code);//set code in edit text
            }
        });

        et_otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Integer textlength1 = et_otp1.getText().toString().trim().length();

                if (textlength1 == 1) {
                    et_otp2.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Integer textlength2 = et_otp2.getText().toString().trim().length();

                if (textlength2 == 1) {
                    et_otp3.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Integer textlength3 = et_otp3.getText().toString().trim().length();

                if (textlength3 == 1) {
                    et_otp4.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        TextView tv_verify_activation = view.findViewById(R.id.tv_verify_activation);
       // tv_verify_activation.setTypeface(tf);
        tv_verify_activation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (checkAndRequestPermissions()) {
                    // carry on the normal flow, as the case of  permissions  granted.
                    otpVerification();



                }




            }
        });
        TextView tv_text = view.findViewById(R.id.tv_text);
      //  tv_text.setTypeface(tf);
        TextView tv_resend = view.findViewById(R.id.tv_resend);


        HashMap<String,String> user = sessionManagement.getUserDetails();

        mobile_number = user.get(SessionManagement.KEY_MOBILENO);

       // otp = et_otp1.getText().toString().trim()+et_otp2.getText().toString().trim()+et_otp3.getText().toString().trim()+et_otp4.getText().toString().trim();
      //  Log.e("details",mobile_number);

        // Inflate the layout for this fragment
        return view;
    }

    private String parseCode(String message) {

        Pattern p = Pattern.compile(":");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    private static String[] splitToNChar(String text, int size) {
        List<String> parts = new ArrayList<>();

        int length = text.length();
        for (int i = 0; i < length; i += size) {
            parts.add(text.substring(i, Math.min(length, i + size)));
        }
        return parts.toArray(new String[0]);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");

                Log.e("message",message);
                String[] separated = message.split(":");
                String s1 = separated[0];
                String s2 = separated[1].trim();
                String s3 = separated[2].trim();

                String firstString[] = splitToNChar(s3,1);

                String one = firstString[0].trim();
                String two = firstString[1].trim();
                String three = firstString[2].trim();
                String four = firstString[3].trim();



                et_otp1.setText(one);
                et_otp2.setText(two);
                et_otp3.setText(three);
                et_otp4.setText(four);



                Log.e("otp_splits",one+"---"+two+"---"+three+"---"+four);


               Log.e("otp_string",message+"---"+s2+"---"+s3);

//               Log.e("otp",otp);
            }
        }
    };

    public ActivationFragment() {

        // Required empty public constructor
    }

    private void otpVerification() {

        progressDialog.show();

        otp = et_otp1.getText().toString().trim()+et_otp2.getText().toString().trim()+et_otp3.getText().toString().trim()+et_otp4.getText().toString().trim();
        Log.e("otp",et_otp1.getText().toString().trim()+"---"+et_otp2.getText().toString().trim()+"---"+et_otp3.getText().toString().trim()+"---"+et_otp4.getText().toString().trim()+"---"+otp);

        String JSON_URL = getString(R.string.base_url)+ "verifyuser";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                      OtpVerificationResponse(response);

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
                params.put("ccode","+"+countryCode);
                params.put("phone",mobile);
                params.put("mcode",otp);


                return params;
            }
        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }
    private void OtpVerificationResponse(String response){
        progressDialog.dismiss();
        try{

            JSONObject jsonObject = new JSONObject(response);
            String msg = jsonObject.getString("msg");


            if(msg.equalsIgnoreCase("success") || msg.equalsIgnoreCase("exists") ) {
                String _id = jsonObject.getString("_id");
                String Sno = jsonObject.getString("Sno");
                String Phone = jsonObject.getString("Phone");
                String Firstname = jsonObject.getString("Firstname");
                String Lastname = jsonObject.getString("Lastname");
                String Email = jsonObject.getString("Email");
                String ProfilePic = jsonObject.getString("ProfilePic");
                String Status = jsonObject.getString("Status");



                sessionManagement.createSignUpSession(Phone);


                TabLayout tabhost = (TabLayout) getActivity().findViewById(R.id.tabLayout);
                tabhost.getTabAt(1).select();



                AccountFragment accountFragment = new AccountFragment();
                Bundle bundle = new Bundle();
                bundle.putString("mobile",mobile);
                bundle.putString("countryCode",countryCode);
                accountFragment.setArguments(bundle);
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.frameLayout, accountFragment);
                fr.commit();



            } else {
                Toast.makeText(getContext(),"Invalid OTP", Toast.LENGTH_SHORT).show();
            }


        }catch (JSONException ex){
            ex.printStackTrace();
        }



    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.SEND_SMS);
        int receiveSMS = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECEIVE_SMS);
        int readSMS = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(),
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void Login() {

        String JSON_URL = getString(R.string.base_url)+ "usersignup";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                      //  getLoginResponse(response);

                        //  progressDialog.dismiss();


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

                params.put("phone",mobile);
                params.put("ccode",countryCode);


                return params;
            }
        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        //adding the string request to request queue
        requestQueue.add(stringRequest);

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
                    ll_activation.setVisibility(View.VISIBLE);

                    Log.e("network", "present");

                } else {

                    dialog.show();
                    //drawer.setVisibility(View.GONE);
                    ll_activation.setVisibility(View.GONE);

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
        smsVerifyCatcher.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
