package in.mindbrick.officelotterypools.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import in.mindbrick.officelotterypools.Fragments.VerifyFragment;
import in.mindbrick.officelotterypools.MyLibrary.NetworkDialog;
import in.mindbrick.officelotterypools.R;

/**
 * Created by chethana on 12/11/2018.
 */

public class LoginActivity extends AppCompatActivity {
    TabLayout tabLayout;
    FrameLayout frameLayout;
    Fragment fragment = null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Typeface tf;
    BroadcastReceiver mConnReceiver;
    NetworkInfo currentNetworkInfo;
    NetworkDialog mDialog;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

       // tf = Typeface.createFromAsset(getAssets(), "fonts/Jaapokki_Regular.otf");

        tabLayout=(TabLayout)findViewById(R.id.tabLayout);
        frameLayout=(FrameLayout)findViewById(R.id.frameLayout);

        fragment = new VerifyFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        /*mDialog = new NetworkDialog(LoginActivity.this);
        mDialog.setDialogTitle("No Internet Connection");
        mDialog.setDialogMessage("Your offline please check your internet connection");*/

       // checknetwork();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Fragment fragment = null;

                 Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new VerifyFragment();
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.frameLayout, fragment);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.commit();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(),"Please complete the Login authentication", Toast.LENGTH_SHORT).show();
                        break;
                   /* case 2:

                        break;*/

                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
                    mDialog.dismiss();
                    //drawer.setVisibility(View.VISIBLE);
                    frameLayout.setVisibility(View.VISIBLE);

                    Log.e("network", "present");

                } else {

                    mDialog.show();
                    //drawer.setVisibility(View.GONE);
                    frameLayout.setVisibility(View.GONE);

                    Log.e("network", "absent");

                    mDialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            checknetwork();
                            Log.e("network", "absent1");
                        }
                    });
                    mDialog.setNegativeButton(getResources().getString(R.string.timer_label_alert_cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG);
                            LoginActivity.this.finish();
                            mDialog.dismiss();
                            //drawer.setVisibility(View.GONE);
                        }
                    });

                }
            }
        };
        this.registerReceiver(this.mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }




}
