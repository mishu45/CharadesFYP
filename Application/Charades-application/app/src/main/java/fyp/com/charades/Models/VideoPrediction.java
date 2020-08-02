package fyp.com.charades.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoPrediction {
    @SerializedName("prediction")
    @Expose
    private String prediction;

    public String getPrediction() {
        return prediction;
    }
}
