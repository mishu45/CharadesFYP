package fyp.com.charades.Views.Initials;

import androidx.appcompat.app.AppCompatActivity;
import fyp.com.charades.R;
import fyp.com.charades.Views.Dashboard.DashboardPage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String FILE_NAME ="Charades";
    private static final String USER_LOGGED_KEY ="USER_LOGGED";
    private static int SPLASH_SCREEN_DELAY=2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ChangeActivity();
    }

    private void ChangeActivity(){
        SharedPreferences preferences =  getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        int logged = preferences.getInt(USER_LOGGED_KEY,0);
        if(logged==1){
            MoveToDashboard();
        }else{
            MoveToMainScreen();
        }
    }

    private void MoveToDashboard(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashScreenActivity.this, DashboardPage.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN_DELAY);
    }


    private void MoveToMainScreen(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN_DELAY);
    }
}
