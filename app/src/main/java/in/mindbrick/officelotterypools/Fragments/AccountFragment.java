package in.mindbrick.officelotterypools.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import in.mindbrick.officelotterypools.Activities.HomeScreen;
import in.mindbrick.officelotterypools.Helpers.FileUtils;
import in.mindbrick.officelotterypools.Helpers.PkDialog;
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
 * @author S.Shahini
 * @since 10/19/16
 */

public class AccountFragment extends Fragment {


    Typeface tf;
    private static final String IMAGE_DIRECTORY = "/poolimages";
    private int GALLERY = 1, CAMERA = 2;
    CircleImageView iv_profile;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String mobile,email,firstName,lastName,mobileNumber,countryCode;
    Bitmap bitmap,thumbnail;
    File f;
    Uri photo;
    private EditText et_fname,et_lname,et_email;
    SessionManagement sessionManagement;
    BroadcastReceiver mConnReceiver;
    NetworkInfo currentNetworkInfo;
    NetworkDialog dialog;
    ProgressDialog progressDialog;
    LinearLayout ll_main;
    PkDialog mDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_fragment, container, false);
       // tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Jaapokki_Regular.otf");
        TextView tv_profile = view.findViewById(R.id.tv_profile);
       // tv_profile.setTypeface(tf);
        TextView tv_text = view.findViewById(R.id.tv_text);

        sessionManagement = new SessionManagement(getActivity());

        sessionManagement = new SessionManagement(getContext());
        HashMap<String, String> user = sessionManagement.getUserDetails();

        mobile = user.get(SessionManagement.KEY_MOBILENO);

        Log.e("mobile_account",mobile);

        mobileNumber = getArguments().getString("mobile");
        countryCode = getArguments().getString("countryCode");

        Log.e("Country_account",mobileNumber+"---"+countryCode);

       // mobile = getArguments().getString("Mobilenumber");
      //  tv_text.setTypeface(tf);
        requestMultiplePermissions();
         et_fname = view.findViewById(R.id.et_fname);
         et_lname = view.findViewById(R.id.et_lname);
         et_email = view.findViewById(R.id.et_email);
        TextView tv_next = view.findViewById(R.id.tv_next);
         iv_profile = view.findViewById(R.id.iv_profile);
         ll_main = view.findViewById(R.id.ll_main);
        ImageView plus = view.findViewById(R.id.plus);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message

        mDialog = new PkDialog(getActivity());
        mDialog.setDialogTitle(getResources().getString(R.string.alert_nointernet));
        mDialog.setDialogMessage(getResources().getString(R.string.alert_nointernet_message));

        dialog = new NetworkDialog(getActivity());
        dialog.setDialogTitle("No Internet Connection");
        dialog.setDialogMessage("Your offline please check your internet connection");

     //   tv_next.setTypeface(tf);
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i_home = new Intent(getActivity(), HomeScreen.class);
                startActivity(i_home);*/

                email = et_email.getText().toString().trim();
                firstName = et_fname.getText().toString().trim();
                lastName = et_lname.getText().toString().trim();

                if(email.equalsIgnoreCase(null)|| email.equalsIgnoreCase("")){
                    et_email.setError("Email required");
                }else if(!isValidEmail(email)){
                    et_email.setError("Invalid Email");
                }else if(firstName.equalsIgnoreCase(null)|| firstName.equalsIgnoreCase("")){
                    et_fname.setError("FirstName required");
                }else if(lastName.equalsIgnoreCase(null) || lastName.equalsIgnoreCase("")){
                    et_lname.setError("LastName required");
                }else {
                  //  userProfile();
                    /*Intent i_new = new Intent(getContext(), HomeScreen.class);
                    startActivity(i_new);*/

                   uploadImage(photo);

                }



            }
        });

        checknetwork();

        return view;
    }

    private RequestBody createPartFromString(String descriptionString){
        return RequestBody.create(MultipartBody.FORM,descriptionString);
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri){
        File file = FileUtils.getFile(getActivity(),fileUri);
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContext().getContentResolver().getType(fileUri)),file);

        return MultipartBody.Part.createFormData(partName,file.getName(),requestFile);
    }


    private void uploadImage(Uri fileUri){
         progressDialog.show();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        FileUploadService client = retrofit.create(FileUploadService.class);




        Map<String, RequestBody> partmap = new HashMap<>();


        email = et_email.getText().toString().trim();
        firstName = et_fname.getText().toString().trim();
        lastName = et_lname.getText().toString().trim();


        partmap.put("ccode",createPartFromString("+"+countryCode));
        partmap.put("phone",createPartFromString(mobileNumber));
        partmap.put("fname",createPartFromString(et_fname.getText().toString()));
        partmap.put("lname",createPartFromString(et_lname.getText().toString()));
        partmap.put("email",createPartFromString(et_email.getText().toString()));






        Call<FileResponse> call = client.uploadFileWithPartMap(
                partmap,
                prepareFilePart("profpic",fileUri)
        );


        call.enqueue(new Callback<FileResponse>() {


            @Override
            public void onResponse(Call<FileResponse> call, retrofit2.Response<FileResponse> response) {

                progressDialog.dismiss();

                response.body();

                String Status = response.body().getMsg();


                Log.e("image_response",Status);


                if(Status.equalsIgnoreCase("success")){

                    String Sno = response.body().getSno();
                    String Phone = response.body().getPhone();
                    String Firstname = response.body().getFirstname();
                    String Lastname = response.body().getLastname();
                    String Email = response.body().getEmail();
                    String ProfilePic = response.body().getProfilePic();

                    Log.e("profileData",Sno+"--"+Phone+"--"+Firstname+"--"+Lastname+"--"+Email+"--"+ProfilePic);

                    sessionManagement.createLoginSession(Sno,Phone,Firstname,Lastname,Email,ProfilePic);


                    /*FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.replace(R.id.frameLayout, new PoolFragment());

                    fr.commit();*/


                   Intent i_new = new Intent(getActivity(), HomeScreen.class);
                    startActivity(i_new);
                }
                else{
                    Toast.makeText(getActivity(),"User registration failed", Toast.LENGTH_SHORT);
                }

             //    Toast.makeText(getActivity(),"response"+response,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<FileResponse> call, Throwable t) {
               // progressDialog.dismiss();

                Toast.makeText(getActivity(),t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }




    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
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
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                photo = contentURI;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(getContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    iv_profile.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            photo = data.getData();
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            iv_profile.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(getActivity(), "Image Saved!", Toast.LENGTH_SHORT).show();
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
            MediaScannerConnection.scanFile(getActivity(),
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
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getActivity(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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
