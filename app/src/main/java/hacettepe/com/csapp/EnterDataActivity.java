package hacettepe.com.csapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ResponseCache;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import hacettepe.com.csapp.model.SingleObservation;
import hacettepe.com.csapp.util.BaseActivity;
import hacettepe.com.csapp.util.BaseBackActivity;

public class EnterDataActivity extends BaseActivity {

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    Button button_send;
    EditText editText_ph, editText_water_temp, editText_nitrat;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    Location mLastLocation;
    ProgressDialog progressDialog;


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;

                //Place current location marker
                //location.getLatitude()
                // location.getLongitude();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_data);


        button_send = (Button) findViewById(R.id.button_send);
        editText_nitrat = findViewById(R.id.et_nitrat_value);
        editText_water_temp = findViewById(R.id.et_water_temp_value);
        editText_ph = findViewById(R.id.et_ph_value);


        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (checkNetworkConnection()) {
                    getValuesAndSend();
                } else {
                    //TODO - Save to local db
                }

            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermission();

    }


    private void getValuesAndSend() {

        String water_text = editText_water_temp.getText().toString();
        String ph_text = editText_ph.getText().toString();
        String nitrat_text = editText_nitrat.getText().toString();


        double water_value = Double.parseDouble(water_text);
        double ph_value = Double.parseDouble(ph_text);
        double nitrat_value = Double.parseDouble(nitrat_text);


        double lat = 0;
        double lon = 0;
        String loc_desc = "";

        LocalData localData = new LocalData(getApplicationContext());
        String usercode = localData.UserCode();

        String requestCode = UUID.randomUUID().toString();
        Date now = new Date();

        SingleObservation water_Observation = new SingleObservation(requestCode, loc_desc, lat, lon, usercode, now);
        water_Observation.setCode(UUID.randomUUID().toString());
        water_Observation.setMeasurement(water_value);
        water_Observation.setMeasurement_text(water_text);
        water_Observation.setProperty("Water Temp");

        SingleObservation ph_Observation = new SingleObservation(requestCode, loc_desc, lat, lon, usercode, now);
        ph_Observation.setCode(UUID.randomUUID().toString());
        ph_Observation.setMeasurement(ph_value);
        ph_Observation.setMeasurement_text(ph_text);
        ph_Observation.setProperty("Ph");

        SingleObservation nitrat_Observation = new SingleObservation(requestCode, loc_desc, lat, lon, usercode, now);
        nitrat_Observation.setCode(UUID.randomUUID().toString());
        nitrat_Observation.setMeasurement(nitrat_value);
        nitrat_Observation.setMeasurement_text(nitrat_text);
        nitrat_Observation.setProperty("Nitrat");

        Gson gson = new Gson();
        String json = gson.toJson(new SingleObservation[]{water_Observation, nitrat_Observation, ph_Observation});

        ProgressDialog progress = new ProgressDialog(this);
        new HTTPAsyncTask(json).execute();
    }


    private class HTTPAsyncTask extends AsyncTask<String, Void, WebServiceResponse> {
        String data;
        WebServiceResponse webServiceResponse;

        public HTTPAsyncTask(String data) {
            this.data = data;
        }

        @Override
        protected WebServiceResponse doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.

            try {
                try {
                    webServiceResponse = HttpPost("https://hacettepe-cevre.herokuapp.com/api/observation/bulk", data);
                    return webServiceResponse;
                    //return "";
                } catch (JSONException e) {
                    e.printStackTrace();
                    webServiceResponse.error = "Error!";
                }
            } catch (IOException e) {
                webServiceResponse.error = "Unable to retrieve web page. URL may be invalid.";
            } finally {
                progressDialog.dismiss();
            }

            return webServiceResponse;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPreExecute() {
            //tvResult.setText(result);
            button_send.setEnabled(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(WebServiceResponse webServiceResponse) {
            progressDialog.dismiss();

            if (webServiceResponse.isSucces) {
                Toast.makeText(getApplicationContext(), R.string.data_sent, Toast.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                }, 2000);
            } else {
                Toast.makeText(getApplicationContext(), webServiceResponse.error, Toast.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        button_send.setEnabled(true);
                    }
                }, 2000);
            }

        }
    }

    private class WebServiceResponse {
        public String result;
        public String error;
        public String detail;
        public String[] rollback;
        public boolean isSucces;
    }

    private WebServiceResponse HttpPost(String myUrl, String json) throws IOException, JSONException {
        URL url = new URL(myUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        setPostRequestContent(conn, json);
        conn.connect();

        int responseCode = conn.getResponseCode();
        WebServiceResponse webServiceResponse = new WebServiceResponse();
        try {

            InputStream inputStream;
            if (200 <= responseCode && responseCode <= 299) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String currentLine;
            while ((currentLine = in.readLine()) != null)
                response.append(currentLine);
            in.close();

            String responseData = response.toString();
            Gson gson = new Gson();

            webServiceResponse = gson.fromJson(responseData, WebServiceResponse.class);

        } catch (Exception exp) {
            exp.printStackTrace();
            webServiceResponse.error = "Can not read response!";
            webServiceResponse.detail = exp.toString();
        }

        if (200 <= responseCode && responseCode <= 299)
            webServiceResponse.isSucces = true;

        return webServiceResponse;
    }

    private void setPostRequestContent(HttpURLConnection conn,
                                       String jsonString) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonString);
        //Log.i(MainActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }


    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }

    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(EnterDataActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);

                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
    }

    private JSONObject buidJsonObject() throws JSONException {

        JSONObject jsonMainObject = new JSONObject();

        JSONObject jsonObservation = new JSONObject();

        //jsonObservation.accumulate("name", etName.getText().toString());


        return jsonMainObject;
    }

}
