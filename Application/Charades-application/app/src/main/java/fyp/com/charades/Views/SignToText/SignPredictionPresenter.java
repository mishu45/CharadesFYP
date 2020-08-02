package fyp.com.charades.Views.SignToText;

import androidx.annotation.NonNull;
import fyp.com.charades.Models.TextPrediction;
import fyp.com.charades.Models.VideoPrediction;
import fyp.com.charades.Utils;
import fyp.com.charades.Views.TextToSign.TextPredictionView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignPredictionPresenter {
    SignPredictionView view;

    public SignPredictionPresenter(SignPredictionView view) {
        this.view = view;
    }

    void getPrediction(String inputVideoCode){
        view.showLoading();
        Call<VideoPrediction> predictionCall = Utils.getCharadesApi().getVideoPrediction(inputVideoCode);
        predictionCall.enqueue(new Callback<VideoPrediction>() {
            @Override
            public void onResponse(@NonNull Call<VideoPrediction> call, Response<VideoPrediction> response) {
                view.hideLoading();
                if(response.isSuccessful() && response.body() != null){
                    view.setPrediction(response.body());
                } else {
                    view.onErrorLoading(response.message());
                }

            }

            @Override
            public void onFailure(@NonNull Call<VideoPrediction> call, Throwable t) {
                view.hideLoading();
                view.onErrorLoading(t.getLocalizedMessage());
            }
        });

    }
}
