package fyp.com.charades.Views.TextToSign;
import androidx.appcompat.app.AppCompatActivity;
import fyp.com.charades.Api.CharadesClient;
import fyp.com.charades.Models.TextPrediction;
import fyp.com.charades.R;
import fyp.com.charades.Variables;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.opencsv.CSVReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class TextToSign extends AppCompatActivity implements TextPredictionView {

    TextPredictionPresenter presenter;

    EditText inputText;
    Button translate_btn,repeat_btn;
    VideoView sign_videoView;
    ProgressBar progressBar;
    private int count = 0;
    TextPrediction textPrediction;

    private String textstr = "no change";
    private String file = "my file";
    private String[] tokens = new String[30];
    private String[] poseids = new String[30];

    private String[] files = new String[30];
    //    int countVideo = Variables.getCount();
//    private String[]  fileline = new String[countVideo];
    private ArrayList<String> videoPath = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_sign);
        inputText=(EditText)findViewById(R.id.Input_EditText);
        translate_btn=(Button)findViewById(R.id.Convert_Button);
        repeat_btn=(Button)findViewById(R.id.Repeat_Button);
        repeat_btn.setEnabled(false);
        sign_videoView=(VideoView)findViewById(R.id.Sign_VideoView);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        presenter = new TextPredictionPresenter(this);
        repeat_btn.setEnabled(false);
        ButtonEvents();
    }

    private void ButtonEvents() {
        translate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputText.getText().toString();
                presenter.getPrediction(input);

            }
        });
        repeat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowVideo(textPrediction);
            }
        });
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setPrediction(TextPrediction prediction) {
        repeat_btn.setEnabled(true);
        textPrediction=prediction;
        ShowVideo(prediction);
    }

    private void ShowVideo(TextPrediction prediction){
        sign_videoView.setVideoURI(Uri.parse(prediction.getVideoUrl()));
        sign_videoView.start();
    }

    @Override
    public void onErrorLoading(String message) {
        Toast.makeText(TextToSign.this,"Error: "+message,Toast.LENGTH_SHORT).show();
    }


}