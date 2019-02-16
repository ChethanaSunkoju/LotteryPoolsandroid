package in.mindbrick.officelotterypools.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import in.mindbrick.officelotterypools.Elements.TypeWriter;
import in.mindbrick.officelotterypools.Helpers.SessionManagement;
import in.mindbrick.officelotterypools.R;


/**
 * Created by chethana on 12/3/2018.
 */

public class SplashActivity extends AppCompatActivity {

    TypeWriter name;
    SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        session = new SessionManagement(this);

        name = findViewById(R.id.name);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                name.setText("");
                name.setCharacterDelay(150);
                name.animateText("LOTTERY POOL");
            }
        }, 1000);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (session.isLoggedIn()) {

                    Intent mainIntent = new Intent(SplashActivity.this,HomeScreen.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                    overridePendingTransition(R.anim.enter, R.anim.exit);

                } else {
                    Intent mainIntent = new Intent(SplashActivity.this, LaunchingScreen.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        }, 4000);


    }
}
