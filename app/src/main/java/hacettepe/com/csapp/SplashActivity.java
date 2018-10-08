package hacettepe.com.csapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    LocalData localData = new LocalData(getApplicationContext());

                    Intent openstartingpoint = null;
                    if (localData.isFirstOpen()) {

                        String usercode = localData.UserCode();
                        openstartingpoint = new Intent(SplashActivity.this, WellcomeActivity.class);
                    } else {
                        openstartingpoint = new Intent(SplashActivity.this, MainActivity.class);
                    }

                    startActivity(openstartingpoint);
                    finish();
                }
            }
        };
        timer.start();


    }
}
