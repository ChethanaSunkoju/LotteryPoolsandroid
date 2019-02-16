package in.mindbrick.officelotterypools.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.mindbrick.officelotterypools.Helpers.FileUtils;
import in.mindbrick.officelotterypools.Helpers.SessionManagement;
import in.mindbrick.officelotterypools.Interface.FileUploadService;
import in.mindbrick.officelotterypools.Models.FileResponse;
import in.mindbrick.officelotterypools.MyLibrary.NetworkDialog;
import in.mindbrick.officelotterypools.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chethana on 12/9/2018.
 */

public class GiftScreenActivity extends AppCompatActivity implements View.OnClickListener{

    CardView cv_fromDate,cv_toDate;
    private int day,month,year;
    private Calendar calendar;
    private int start_day,start_month,start_year,end_day,end_month,end_year;
    String start_date,end_date,targetAmount,defaultAmount,PoolGID;
    TextView tv_end_date,tv_start_date,tv_verify;
    ImageView plus,imageview;
    private static final String IMAGE_DIRECTORY = "/poolimages";
    private int GALLERY = 1, CAMERA = 2;
    Uri photo;
    EditText et_target_amount,et_default_payment;
    LinearLayout ll_total;
    NetworkDialog networkDialog;
    BroadcastReceiver mConnReceiver;
    NetworkInfo currentNetworkInfo;
    SessionManagement sessionManagement;
    ProgressDialog progressDialog;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gift_screen);
        PoolGID = getIntent().getStringExtra("PoolGID");
        Log.e("gift_poolId",PoolGID);
        cv_fromDate = findViewById(R.id.cv_fromDate);
        cv_fromDate.setOnClickListener(this);
        cv_toDate = findViewById(R.id.cv_toDate);
        cv_toDate.setOnClickListener(this);
        tv_start_date = findViewById(R.id.tv_start_date);
        tv_end_date = findViewById(R.id.tv_end_date);
        tv_verify = findViewById(R.id.tv_verify);
        tv_verify.setOnClickListener(this);
        ll_total = findViewById(R.id.ll_total);
       imageview = findViewById(R.id.imageview);
       plus = findViewById(R.id.plus);
        plus.setOnClickListener(this);

        et_target_amount = findViewById(R.id.et_target_amount);
        et_default_payment = findViewById(R.id.et_default_payment);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        networkDialog = new NetworkDialog(this);
        networkDialog.setDialogTitle("No Internet Connection");
        networkDialog.setDialogMessage("Your offline please check your internet connection");


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message

        requestMultiplePermissions();

        checknetwork();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cv_fromDate:

                showDialog(999);
             break;

            case R.id.cv_toDate:
                showDialog(998);
                break;

            case R.id.tv_verify:
                targetAmount = et_target_amount.getText().toString().trim();
                defaultAmount = et_default_payment.getText().toString().trim();


                 if(isDateAfter(start_date,end_date)){
                 if( photo != null && !photo.equals(Uri.EMPTY) && !targetAmount.isEmpty() && !defaultAmount.isEmpty() && !start_date.isEmpty() && !end_date.isEmpty()){
                    submitGiftWithImage(photo);
                 }else {
                     submitGiftWithoutImage();
                 }}else {
                     Toast.makeText(getApplicationContext(),"Please pick the correct date", Toast.LENGTH_SHORT).show();
                 }

                /*Intent i_home = new Intent(GiftScreenActivity.this,HomeScreen.class);
                startActivity(i_home);*/
                break;

            case R.id.plus:

                showPictureDialog();

                break;
        }
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(GiftScreenActivity.this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                photo = contentURI;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageview.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            photo = data.getData();
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageview.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getApplicationContext(),
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(GiftScreenActivity.this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }


                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {

            startDate(year, month+1, day);
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);


        }

        if(id == 998){

            endDate(year,month+1,day);
            return new DatePickerDialog(this,myendDateListener,year,month,day);

        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    startDate(arg1, arg2+1, arg3);
                }
            };
    private DatePickerDialog.OnDateSetListener myendDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    endDate(arg1, arg2+1, arg3);
                }
            };
    private void startDate(int year, int month, int day) {
        tv_start_date.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));



        Log.e("months", String.valueOf(month));
        Log.e("date", String.valueOf(day));
        Log.e("year", String.valueOf(year));

        start_year = year;
        start_month = month;
        start_day = day;


        if (start_month<10 || start_day <10){
            String totalMonth = String.format("%02d", start_month);
            String totalDay = String.format("%02d",start_day);
            Log.e("totalMonth",totalMonth);
            start_date = String.valueOf(start_year)+"-"+String.valueOf(totalMonth)+"-"+String.valueOf(totalDay);
            Log.e("totalEndDate",start_date);
        }else {


            start_date = String.valueOf(start_year)+"-"+ String.valueOf(start_month)+"-"+ String.valueOf(start_day);

            //  tv_endDate.setText(end_date);

            Log.e("endDate", start_date);
        }







        Log.e("startDate",start_date);
        //  tv_startDate.setText(start_date);


    }
    private void endDate(int year, int month, int day) {
        tv_end_date.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));



        Log.e("months", String.valueOf(month));
        Log.e("date", String.valueOf(day));
        Log.e("year", String.valueOf(year));

        end_year = year;
        end_month = month;
        end_day = day;

        if (end_month<10 || end_day <10){
            String totalMonth = String.format("%02d", end_month);
            String totalDay = String.format("%02d",end_day);
            Log.e("totalMonth",totalMonth);
            end_date = String.valueOf(end_year)+"-"+String.valueOf(totalMonth)+"-"+String.valueOf(totalDay);
            Log.e("totalEndDate",end_date);
        }else {


            end_date = String.valueOf(end_year)+"-"+ String.valueOf(end_month)+"-"+ String.valueOf(end_day);

            //  tv_endDate.setText(end_date);

            Log.e("endDate", end_date);
        }





        //  tv_endDate.setText(end_date);

      //  Log.e("endDate",end_date);


    }

    public static boolean isDateAfter(String startDate, String endDate)
    {
        try
        {
            String myFormatString = "yyyy-M-dd"; // for example
            SimpleDateFormat df = new SimpleDateFormat(myFormatString);
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);

            if (date1.after(startingDate))
                return true;
            else
                return false;
        }
        catch (Exception e)
        {

            return false;
        }
    }

    private void submitGiftWithoutImage(){
        progressDialog.dismiss();
        String JSON_URL = getString(R.string.base_url)+ "updatecustompoolinfo";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                      //  createPoolResponse(response);

                        giftResponse(response);
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

                params.put("cpoolpic","");
                params.put("PoolGID",PoolGID);
                params.put("Amount",et_target_amount.getText().toString().trim());
                params.put("DefaultPaymentPermember",et_default_payment.getText().toString().trim());
                params.put("Fromdate",start_date);
                params.put("Todate",end_date);
                params.put("Freeze","false");



                return params;
            }
        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    private void giftResponse(String response){
        progressDialog.dismiss();
        try{
            JSONObject jsonObject = new JSONObject(response);
            String msg = jsonObject.getString("msg");

            if(msg.equals("success")){
                Intent i_home = new Intent(GiftScreenActivity.this,HomeScreen.class);
                startActivity(i_home);
            }

        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }



    private void submitGiftWithImage(Uri fileUri){
        progressDialog.show();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        FileUploadService client = retrofit.create(FileUploadService.class);




        Map<String, RequestBody> partmap = new HashMap<>();





        partmap.put("PoolGID",createPartFromString(PoolGID));
        partmap.put("Amount",createPartFromString(targetAmount));
        partmap.put("DefaultPaymentPermember",createPartFromString(defaultAmount));
        partmap.put("Fromdate",createPartFromString(start_date));
        partmap.put("Todate",createPartFromString(end_date));
        partmap.put("Freeze",createPartFromString("false"));






        Call<FileResponse> call = client.uploadPooldetailsWithPartMap(
                partmap,
                prepareFilePart("cpoolpic",fileUri)
        );


        call.enqueue(new Callback<FileResponse>() {


            @Override
            public void onResponse(Call<FileResponse> call, retrofit2.Response<FileResponse> response) {

                progressDialog.dismiss();

                response.body();

                String msg = response.body().getMsg();





                if(msg.equalsIgnoreCase("success")){

                    Intent i_home = new Intent(GiftScreenActivity.this,HomeScreen.class);
                    startActivity(i_home);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Uploading Failed", Toast.LENGTH_SHORT).show();
                }

                //    Toast.makeText(getActivity(),"response"+response,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<FileResponse> call, Throwable t) {
                // progressDialog.dismiss();

                Toast.makeText(getApplicationContext(),t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private RequestBody createPartFromString(String descriptionString){
        return RequestBody.create(MultipartBody.FORM,descriptionString);
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri){
        File file = FileUtils.getFile(GiftScreenActivity.this,fileUri);
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)),file);

        return MultipartBody.Part.createFormData(partName,file.getName(),requestFile);
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
