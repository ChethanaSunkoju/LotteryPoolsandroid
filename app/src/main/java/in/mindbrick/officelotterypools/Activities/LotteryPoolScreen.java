package in.mindbrick.officelotterypools.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.mindbrick.officelotterypools.R;

import static android.net.wifi.WifiConfiguration.Status.strings;

/**
 * Created by chethana on 2/11/2019.
 */

public class LotteryPoolScreen extends AppCompatActivity implements View.OnClickListener {
    TextView tv_scan,tv_elements;
    String result,data;

    String first,second,third;
    LinearLayout ll_firstRow,ll_secondRow,ll_thirdRow,ll_ticket;
    ImageView iv_first_row,iv_second_row,iv_third_row;
    TextView tv_first1,tv_first2,tv_first3,tv_first4,tv_first5,tv_first6,tv_second1,tv_second2,tv_second3,tv_second4,tv_second5,tv_second6,tv_third1,tv_third2,tv_third3,tv_third4,tv_third5,tv_third6;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lottery_pool_screen);
        tv_scan = findViewById(R.id.tv_scan);

        tv_scan.setOnClickListener(this);
        tv_elements = findViewById(R.id.tv_elements);
        ll_firstRow = findViewById(R.id.ll_firstRow);
        ll_secondRow = findViewById(R.id.ll_secondRow);
        ll_thirdRow = findViewById(R.id.ll_thirdRow);
        ll_ticket = findViewById(R.id.ll_ticket);

        iv_first_row = findViewById(R.id.iv_first_row);
        iv_first_row.setOnClickListener(this);
        iv_second_row = findViewById(R.id.iv_second_row);
        iv_second_row.setOnClickListener(this);
        iv_third_row = findViewById(R.id.iv_third_row);
        iv_third_row.setOnClickListener(this);

        tv_first1 = findViewById(R.id.tv_first1);
        tv_first2 = findViewById(R.id.tv_first2);
        tv_first3 = findViewById(R.id.tv_first3);
        tv_first4 = findViewById(R.id.tv_first4);
        tv_first5 = findViewById(R.id.tv_first5);
        tv_first6 = findViewById(R.id.tv_first6);

        tv_second1 = findViewById(R.id.tv_second1);
        tv_second2 = findViewById(R.id.tv_second2);
        tv_second3 = findViewById(R.id.tv_second3);
        tv_second4 = findViewById(R.id.tv_second4);
        tv_second5 = findViewById(R.id.tv_second5);
        tv_second6 = findViewById(R.id.tv_second6);

        tv_third1 = findViewById(R.id.tv_third1);
        tv_third2 = findViewById(R.id.tv_third2);
        tv_third3 = findViewById(R.id.tv_third3);
        tv_third4 = findViewById(R.id.tv_third4);
        tv_third5 = findViewById(R.id.tv_third5);
        tv_third6 = findViewById(R.id.tv_third6);



        data = getIntent().getStringExtra("data");
        Log.e("data",data);
        if(data.equalsIgnoreCase("fulldata")){

            result = getIntent().getStringExtra("result");
           // ll_ticket.setVisibility(View.VISIBLE);

            Log.d("result",result);

            String[] numberLine = result.split("/n");

            Log.e("numberLines", Arrays.toString(numberLine));

           /* List<String> stringList = new ArrayList<String>(Arrays.asList(numberLine));

            Log.e("StringList",""+stringList);*/

           /* List<String> mylist= new ArrayList<String>();
            for (int i = 0; i < numberLine.length; i++) {
                first = numberLine[0];
                second = numberLine[1];
                third = numberLine[2];

            }
            Log.e("ArrayData",first+"--"+second+"--"+third);*/


          /*  String strArray[] = result.split("/n");
            for(int i=0; i < strArray.length; i++){
                System.out.println(strArray[i]);
                Log.e("strArrayElements",strArray[i]);

                first = strArray[0];
                second = strArray[1];
                third = strArray[2];

                Log.e("strArray",first+"--"+second+"--"+third);

            }

            String firstRow[] = first.split("");
            for(int i=0;i<firstRow.length;i++){
              String first1 = firstRow[0];
              String first2 = firstRow[1];
              String first3 = firstRow[2];
              String first4 = firstRow[3];
              String first5 = firstRow[4];
              String first6 = firstRow[5];

              tv_first1.setText(first1);
              tv_first2.setText(first2);
              tv_first3.setText(first3);
              tv_third4.setText(first4);
              tv_first5.setText(first5);
              tv_first6.setText(first6);

              Log.e("firstRow",first1+"--"+first2+"--"+first3+"--"+first4+"--"+first5+"--"+first6);

            }

            String secondRow[] = second.split("");
            for (int i=0;i<secondRow.length;i++){
                String second1 = secondRow[0];
                String second2 = secondRow[1];
                String second3 = secondRow[2];
                String second4 = secondRow[3];
                String second5 = secondRow[4];
                String second6 = secondRow[5];

                tv_second1.setText(second1);
                tv_second2.setText(second2);
                tv_second3.setText(second3);
                tv_second4.setText(second4);
                tv_second5.setText(second5);
                tv_second6.setText(second6);
                Log.e("secondRow",second1+"--"+second2+"--"+second3+"--"+second4+"--"+second5+"--"+second6);
            }
            String thirdRow[] = third.split("");
            for(int i=0;i<thirdRow.length;i++){
                String third1 = thirdRow[0];
                String third2 = thirdRow[1];
                String third3 = thirdRow[2];
                String third4 = thirdRow[3];
                String third5 = thirdRow[4];
                String third6 = thirdRow[5];


                tv_third1.setText(third1);
                tv_third2.setText(third2);
                tv_third3.setText(third3);
                tv_third4.setText(third4);
                tv_third5.setText(third5);
                tv_third6.setText(third6);
                Log.e("thirdRow",third1+"--"+third2+"--"+third3+"--"+third4+"--"+third5+"--"+third6);
            }*/



           /* int matrixSize = (int) Math.sqrt(result.length());
            for(int i = 0; i<matrixSize; i++) {
                for(int j = 0; j<matrixSize; j++){
                    matrix[i][j] = input[j];
                    j++;
                }*/


            tv_elements.setText(result);

        }else {

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_scan:

                Intent i_scan = new Intent(LotteryPoolScreen.this,ScanningActivity.class);
                startActivity(i_scan);

                break;

            case R.id.iv_first_row:
                ll_firstRow.setVisibility(View.GONE);
                break;
            case R.id.iv_second_row:
                ll_secondRow.setVisibility(View.GONE);
                break;

            case R.id.iv_third_row:
                ll_thirdRow.setVisibility(View.GONE);
                break;
        }
    }
}
