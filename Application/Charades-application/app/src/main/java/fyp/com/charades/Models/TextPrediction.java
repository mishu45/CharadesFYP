package fyp.com.charades.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TextPrediction {

@SerializedName("prediction")
@Expose
private String prediction;
@SerializedName("videoPath")
@Expose
private String videoPath;
@SerializedName("videoUrl")
@Expose
private String videoUrl;

public String getPrediction() {
return prediction;
}


public String getVideoPath() {
return videoPath;
}


public String getVideoUrl() {
return videoUrl;
}



}