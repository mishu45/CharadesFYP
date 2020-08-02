package fyp.com.charades.Views.SignToText;

import fyp.com.charades.Models.VideoPrediction;

public interface SignPredictionView {
    void showLoading();
    void hideLoading();
    void setPrediction(VideoPrediction prediction);
    void onErrorLoading(String message);
}
