package in.mindbrick.officelotterypools.Activities;

import android.Manifest;
import android.app.AlertDialog;
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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.PayPal;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
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
 * Created by chethana on 1/26/2019.
 */

public class AddPoolGiftScreen extends AppCompatActivity implements View.OnClickListener{

    ImageView imageview,plus,iv_back;
    private static final String IMAGE_DIRECTORY = "/poolimages";
    private int GALLERY = 1, CAMERA = 2;
    Uri photo;
    TextView tv_pool_name,tv_mem_num,tv_target_amount,tv_default_payment,tv_start_date,tv_end_date,tv_verify,tv_pay;
    Switch toggleButton;
    String PoolGID,StartDate,EndDate,adminId;
    LinearLayout ll_members,ll_total;
    private static final String SERVER_BASE = "http://13.233.223.167:3001"; // Replace with your own server
    private static final int REQUEST_CODE = Menu.FIRST;
    private AsyncHttpClient client = new AsyncHttpClient();
    private String clientToken,poolName;
    ProgressDialog progressDialog;
    NetworkDialog networkDialog;
    BroadcastReceiver mConnReceiver;
    NetworkInfo currentNetworkInfo;
    SessionManagement sessionManagement;
    File f;







    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_gift_screen);
        PoolGID = getIntent().getStringExtra("PoolGID");
        Log.e("PoolGID",PoolGID);
        sessionManagement = new SessionManagement(this);

        imageview = findViewById(R.id.imageview);
        plus = findViewById(R.id.plus);
        plus.setOnClickListener(this);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_pool_name = findViewById(R.id.tv_pool_name);
        tv_mem_num = findViewById(R.id.tv_mem_num);
        tv_target_amount = findViewById(R.id.tv_target_amount);
        tv_default_payment = findViewById(R.id.tv_default_payment);
        tv_start_date = findViewById(R.id.tv_start_date);
        tv_end_date = findViewById(R.id.tv_end_date);
        ll_members = findViewById(R.id.ll_members);
        ll_members.setOnClickListener(this);
        tv_verify = findViewById(R.id.tv_verify);
        tv_verify.setOnClickListener(this);
        ll_total = findViewById(R.id.ll_total);
        tv_pay = findViewById(R.id.tv_pay);
        tv_pay.setOnClickListener(this);


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message

        networkDialog = new NetworkDialog(this);
        networkDialog.setDialogTitle("No Internet Connection");
        networkDialog.setDialogMessage("Your offline please check your internet connection");

        getPoolInfo();
     //   getToken();

        checknetwork();

        requestMultiplePermissions();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.plus:
                showPictureDialog();

                break;

            case R.id.tv_verify:

                submitGiftWithImage(photo);
                break;
            case R.id.ll_members:
                Intent i_pool = new Intent(AddPoolGiftScreen.this,PoolMembersActivity.class);
                i_pool.putExtra("PoolGId",PoolGID);
                i_pool.putExtra("PooladminId",adminId);
                i_pool.putExtra("data","full");
                startActivity(i_pool);
                break;

            case R.id.tv_pay:

                //Toast.makeText(getApplicationContext(),"button is clicked",Toast.LENGTH_SHORT).show();
               /* Intent i_pay = new Intent(AddPoolGiftScreen.this,PaymentActivity.class);
                startActivity(i_pay);
*/


                break;

            case R.id.iv_back:
                Intent i_back = new Intent(AddPoolGiftScreen.this,HomeScreen.class);
                startActivity(i_back);
                break;
            case R.id.tv_done:

                break;


        }



    }


    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(AddPoolGiftScreen.this);
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
            Uri contentURI = (Uri) data.getData();

            //  photo = contentURI;

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
            f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getApplicationContext(),
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
           /* photo = Uri.fromFile(f);
            Log.e("photo",""+photo);*/
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }







    private void getPoolInfo(){
        progressDialog.show();
        String JSON_URL = getString(R.string.base_url)+ "Getcustompooldetails";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        customPoolResponse(response);
                        progressDialog.dismiss();

                       Toast.makeText(getApplicationContext(),"response"+response, Toast.LENGTH_SHORT).show();

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


                params.put("PoolGID",PoolGID);




                return params;
            }
        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    private void customPoolResponse(String response){
        progressDialog.dismiss();

        try{
            Log.e("poolGift","Hi i am in response");
            JSONObject jsonObject = new JSONObject(response);
            String PoolGID = jsonObject.getString("PoolGID");
            String Pooltype = jsonObject.getString("Pooltype");
            String PooladminId = jsonObject.getString("PooladminId");
            String Adminname = jsonObject.getString("Adminname");
            String Poolname = jsonObject.getString("Poolname");
            String Amount = jsonObject.getString("Amount");
            String DefaultPaymentPermember = jsonObject.getString("DefaultPaymentPermember");
            String PGpic = jsonObject.getString("PGpic");
            String Fromdate = jsonObject.getString("Fromdate");
            String Todate = jsonObject.getString("Todate");
            String CreatedDate = jsonObject.getString("CreatedDate");

            Log.e("gift_pool_details",Poolname+"--"+Amount+"--"+DefaultPaymentPermember+"--"+PGpic+"--"+Fromdate+"--"+Todate);
            JSONArray jsonArray = jsonObject.getJSONArray("Poolmembers");
           /* for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String _id = jsonObject1.getString("_id");
                String Sno = jsonObject1.getString("Sno");
                String Phone = jsonObject1.getString("Phone");
                String Firstname = jsonObject1.getString("Firstname");
                String Lastname = jsonObject1.getString("Lastname");
                String Email = jsonObject1.getString("Email");
                String ProfilePic = jsonObject1.getString("ProfilePic");
                String Status = jsonObject1.getString("Status");
                Log.e("gift_details",_id+"--"+Sno+"--"+Phone+"--"+Firstname+"--"+Lastname+"--"+Email+"--"+ProfilePic+"--"+Status);
            }*/

            String Status = jsonObject.getString("Status");

            sessionManagement.saveGroupId(PooladminId);

            StartDate = Fromdate;
            EndDate = Todate;
            poolName = Poolname;

            adminId = PooladminId;


            if(StartDate!=null) {

                String startString = StartDate;
                try {
                    String[] separated = startString.split("-");
                    String startYear = separated[0].trim(); // this will contain "Fruit"
                    String startmonth = separated[1].trim();
                    String startdate = separated[2].trim();
                    String fromDate = startYear + "-" + startmonth + "-" + startdate;
                    Log.e("startDate",fromDate);
                    tv_start_date.setText(fromDate);
                }catch (ArrayIndexOutOfBoundsException ex){
                    ex.printStackTrace();
                }



            }else {
                tv_start_date.setText("Start Date");
            }
            if(EndDate!=null) {
                String endString = EndDate;

                try {
                    String[] endseparated = endString.split("-");
                    String endYear = endseparated[0].trim(); // this will contain "Fruit"
                    String endmonth = endseparated[1].trim();
                    String enddate = endseparated[2].trim();

                    String toDate = endYear + "-" + endmonth + "-" + enddate;
                    Log.e("EndDate",toDate);
                    tv_end_date.setText(toDate);
                }catch (ArrayIndexOutOfBoundsException ex){
                    ex.printStackTrace();
                }
            }else {
                tv_end_date.setText("End Date");
            }

            //   Log.e("gift_pool_details",Poolname+"--"+Amount+"--"+DefaultPaymentPermember+"--"+PGpic+"--"+Fromdate+"--"+Todate);
            int memLength = jsonArray.length();
            tv_pool_name.setText(poolName);
            tv_mem_num.setText(String.valueOf(memLength));
            tv_target_amount.setText(Amount);
            tv_default_payment.setText(DefaultPaymentPermember);
            /*tv_start_date.setText(fromDate);
            tv_end_date.setText(toDate);*/
            Picasso.with(AddPoolGiftScreen.this)
                    .load( getString(R.string.base_url)+"custompoolimages/"+PGpic)
                    .resize(200, 200)
                    .into(imageview);




        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }


    private void submitGiftWithImage(Uri fileUri){
        //   progressDialog.show();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        FileUploadService client = retrofit.create(FileUploadService.class);




        Map<String, RequestBody> partmap = new HashMap<>();





        partmap.put("PoolGID",createPartFromString(PoolGID));








        Call<FileResponse> call = client.uploadFileWithPartMap(
                partmap,
                prepareFilePart("cpoolpic",fileUri)
        );


        call.enqueue(new Callback<FileResponse>() {


            @Override
            public void onResponse(Call<FileResponse> call, retrofit2.Response<FileResponse> response) {

                //progressDialog.dismiss();

                response.body();

                String msg = response.body().getMsg();





                if(msg.equalsIgnoreCase("success")){

                    Toast.makeText(getApplicationContext(),"Uploaded Successfully",Toast.LENGTH_SHORT).show();

                    Intent i_home = new Intent(AddPoolGiftScreen.this,HomeScreen.class);
                    startActivity(i_home);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Uploading Failed",Toast.LENGTH_SHORT).show();
                }

                //    Toast.makeText(getActivity(),"response"+response,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<FileResponse> call, Throwable t) {
                // progressDialog.dismiss();

                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }


    private RequestBody createPartFromString(String descriptionString){
        return RequestBody.create(MultipartBody.FORM,descriptionString);
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri){
        File file = FileUtils.getFile(AddPoolGiftScreen.this,fileUri);
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)),file);

        return MultipartBody.Part.createFormData(partName,file.getName(),requestFile);
    }




    private void  requestMultiplePermissions(){
        Dexter.withActivity(AddPoolGiftScreen.this)
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
