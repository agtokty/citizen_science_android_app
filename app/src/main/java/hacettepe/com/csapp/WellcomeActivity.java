package hacettepe.com.csapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WellcomeActivity extends AppCompatActivity {


    Button btnCont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellcome);


        btnCont = (Button) findViewById(R.id.button_cont);

        btnCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WellcomeActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        });
    }
}
