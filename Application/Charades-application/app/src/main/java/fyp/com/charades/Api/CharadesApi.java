package fyp.com.charades.Api;

import fyp.com.charades.Models.TextPrediction;
import fyp.com.charades.Models.VideoPrediction;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CharadesApi {
    @GET("predicttext/{sentence}")
    Call<TextPrediction> getTextPrediction(@Path("sentence") String inputSentence);

    @FormUrlEncoded
    @GET("predictvideo")
    Call<VideoPrediction> getVideoPrediction(@Field("video") String inputVideoCode);
}
