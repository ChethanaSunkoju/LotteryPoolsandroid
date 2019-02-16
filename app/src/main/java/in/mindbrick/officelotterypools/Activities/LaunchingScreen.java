package in.mindbrick.officelotterypools.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import in.mindbrick.officelotterypools.R;

/**
 * Created by chethana on 12/3/2018.
 */

public class LaunchingScreen extends AppCompatActivity {

    TextView tv_started,tv_text,tv_return;
    Typeface tf;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launching_screen);
       // tf = Typeface.createFromAsset(getAssets(), "fonts/Jaapokki_Regular.otf");
        tv_started = findViewById(R.id.tv_started);
       // tv_started.setTypeface(tf);
        tv_text = findViewById(R.id.tv_text);
        //tv_text.setTypeface(tf);
        tv_return = findViewById(R.id.tv_return);
        tv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_home = new Intent(LaunchingScreen.this,LoginActivity.class);
                startActivity(i_home);
            }
        });
      //  tv_return.setTypeface(tf);
        tv_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_home = new Intent(LaunchingScreen.this,LoginActivity.class);
                startActivity(i_home);
            }
        });
    }
}
