package hacettepe.com.csapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import hacettepe.com.csapp.util.BaseActivity;

public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();


                /*
                if (checkNetworkConnection()) {
                    Intent intent = new Intent(MainActivity.this, EnterDataActivity.class);
                    startActivity(intent);
                } else {
                    Snackbar.make(view, R.string.check_internet_conn, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
                */

                Intent intent = new Intent(MainActivity.this, EnterDataActivity.class);
                startActivity(intent);

            }
        });
    }
}
