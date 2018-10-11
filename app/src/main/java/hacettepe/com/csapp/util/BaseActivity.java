package hacettepe.com.csapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import hacettepe.com.csapp.LocalData;

public class BaseActivity extends AppCompatActivity {


    public LocalData localDataBase;

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null)
            return false;

        boolean isConnected = networkInfo.isConnected();

        if (networkInfo != null && isConnected) {
            //"WIFI or MOBILE"
            String connectionType = networkInfo.getTypeName();
        }

        return isConnected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localDataBase = new LocalData(getApplicationContext());
    }
}