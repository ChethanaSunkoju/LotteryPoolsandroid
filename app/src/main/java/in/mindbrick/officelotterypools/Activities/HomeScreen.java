package in.mindbrick.officelotterypools.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ss.bottomnavigation.BottomNavigation;
import com.ss.bottomnavigation.events.OnSelectedItemChangeListener;

import in.mindbrick.officelotterypools.Fragments.FriendsFragment;
import in.mindbrick.officelotterypools.Fragments.PoolFragment;
import in.mindbrick.officelotterypools.Fragments.TestFragment;
import in.mindbrick.officelotterypools.R;

/**
 * Created by chethana on 12/8/2018.
 */

public class HomeScreen extends AppCompatActivity implements View.OnClickListener {

    BottomNavigation bottom_navigation;
    private FragmentTransaction transaction;
    FloatingActionButton fab_icon;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

       /* toolbar = findViewById(R.id.toolbar);
        iv_back = toolbar.findViewById(R.id.iv_back);
        tv_tool_text = toolbar.findViewById(R.id.tv_tool_text);
        tv_addPool = toolbar.findViewById(R.id.tv_addPool);
        tv_addPool.setOnClickListener(this);*/

       fab_icon = findViewById(R.id.fab_icon);

       fab_icon.setOnClickListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_fragment_containers, new PoolFragment())
                .commit();

        bottom_navigation = findViewById(R.id.bottom_navigation);


        /*if(savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().replace(R.id.frame_fragment_containers,new PoolFragment()).commit();
        }*/
        bottom_navigation.setOnSelectedItemChangeListener(new OnSelectedItemChangeListener() {
            @Override
            public void onSelectedItemChanged(int itemId) {
                switch (itemId){


                    case R.id.tab_camera:
                        transaction=getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_fragment_containers,new FriendsFragment());
                        break;
                    case R.id.tab_products:
                        transaction=getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_fragment_containers,new PoolFragment());
                        break;
                    case R.id.tab_wallet:
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_fragment_containers,new TestFragment());
                        break;
                    case R.id.tab_more:
                        transaction=getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_fragment_containers,new TestFragment());
                        break;
                }
                transaction.commit();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_icon:
                Intent i_contact = new Intent(HomeScreen.this,ContactsActivity.class);
                startActivity(i_contact);
                break;

        }
    }
}

