package fyp.com.charades.Views.SignToText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import fyp.com.charades.Models.VideoPrediction;
import fyp.com.charades.Views.Dashboard.DashboardPage;
import fyp.com.charades.R;
import fyp.com.charades.Views.TextToSign.TextToSign;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SignToTextPage extends AppCompatActivity implements SignPredictionView {
    private static int VIDEO_REQUEST = 101;
    private final int SELECT_VIDEO_REQUEST=102;
    private Uri videoUri = null;

    String selectedVideoPath;
    String VideoCode=null;

    private final int GALLERY = 1, CAMERA = 2;

    Button backToDashboardBtn, convertToTextBtn;
    VideoView videoView;
    ProgressBar progressBar;
    TextView tv_Prediction;

    SignPredictionPresenter presenter;

    private static MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_to_text_page);
        presenter = new SignPredictionPresenter(this);
        mediaController = new MediaController(this);
        initializeComponents();
        onListeners();
    }

    private void initializeComponents()
    {
        backToDashboardBtn = findViewById(R.id.back_Btn);
        convertToTextBtn = findViewById(R.id.conversion_Btn);
        videoView = findViewById(R.id.videoView);
        progressBar = findViewById(R.id.progressBar);
        tv_Prediction = findViewById(R.id.tv_Prediction);
        setMediaController();
    }

    private void onListeners()
    {
        backToDashboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignToTextPage.this, DashboardPage.class);
                        startActivity(intent);
            }
        });

        convertToTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(VideoCode!=null){
                    presenter.getPrediction(VideoCode);
                }else{
                    Toast.makeText(SignToTextPage.this,"Please Select Video.",Toast.LENGTH_SHORT).show();
                }

            }
        });

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select video from gallery",
                "Record video from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                chooseVideoFromGallary();
                                //selectVideoFromGallery();
                                break;
                            case 1:
                                takeVideoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void chooseVideoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void setMediaController(){
        mediaController.setMediaPlayer(videoView);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
    }

    private void takeVideoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    public void captureVideo(View view)
    {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        Intent intent = new Intent(this, SignToTextPage.class);
        startActivity(intent);

        if(videoIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(videoIntent,VIDEO_REQUEST);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY && resultCode == RESULT_OK) {

            Uri videoUri = data.getData();

            if (videoUri  != null) {

                VideoCode = ConvertToString(videoUri);
                videoView.setVideoURI(videoUri);
                videoView.requestFocus();
                videoView.start();

            }

        } else if (requestCode == CAMERA) {
            Uri contentURI = data.getData();
            String recordedVideoPath = getPath(contentURI);
            VideoCode = ConvertToString(contentURI);
            videoView.setVideoURI(contentURI);
            videoView.requestFocus();
            videoView.start();
        }

    }

    public void selectVideoFromGallery()
    {
        Intent intent;
        if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        }
        else
        {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI);
        }
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(intent,SELECT_VIDEO_REQUEST);
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public String ConvertToString(Uri uri){
       String uriString = uri.toString();
       String videoCode="";
        try {
            InputStream in = getContentResolver().openInputStream(uri);
            byte[] bytes=getBytes(in);
            Log.d("data", "onActivityResult: bytes size="+bytes.length);
            Log.d("data", "onActivityResult: Base64string="+Base64.encodeToString(bytes,Base64.DEFAULT));
            String ansValue = Base64.encodeToString(bytes,Base64.DEFAULT);
            videoCode=Base64.encodeToString(bytes,Base64.DEFAULT);
            return videoCode ;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("error", "onActivityResult: " + e.toString());
        }
        return null;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
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
    public void setPrediction(VideoPrediction prediction) {
        tv_Prediction.setText(prediction.getPrediction().toString());
    }

    @Override
    public void onErrorLoading(String message) {
        Toast.makeText(SignToTextPage.this,"Error: "+message,Toast.LENGTH_SHORT).show();
    }
}