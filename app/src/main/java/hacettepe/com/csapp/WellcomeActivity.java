package hacettepe.com.csapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import hacettepe.com.csapp.util.BaseActivity;
import hacettepe.com.csapp.util.Constants;

public class WellcomeActivity extends BaseActivity {


    Button btnCont;
    EditText et_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellcome);

        btnCont = (Button) findViewById(R.id.button_cont);
        et_name = (EditText) findViewById(R.id.editText_name);

        btnCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = et_name.getText().toString().trim();

                if (name.isEmpty() || name.length() < 3) {
                    Toast.makeText(getApplicationContext(), R.string.enter_your_name, Toast.LENGTH_LONG).show();
                    return;
                }

                localDataBase.setStringValue(Constants.LOCALDATA_NAME, name);

                Intent intent = new Intent(WellcomeActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        });
    }
}
