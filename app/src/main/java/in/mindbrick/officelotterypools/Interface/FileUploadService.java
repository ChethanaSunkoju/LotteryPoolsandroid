package in.mindbrick.officelotterypools.Interface;

import java.util.Map;

import in.mindbrick.officelotterypools.Models.FileResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by chethana on 12/30/2018.
 */

public interface FileUploadService {


    @Multipart
    @POST("/updateuserdetails")
    Call<FileResponse> uploadFileWithPartMap(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part file);

    @POST("/updatecustompoolinfo")
    Call<FileResponse> uploadPooldetailsWithPartMap(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part file);


    @POST("/updatecustompoolimage")
    Call<FileResponse> uploadGiftWithPartMap(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part file);


}
