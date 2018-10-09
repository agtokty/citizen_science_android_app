package hacettepe.com.csapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import hacettepe.com.csapp.model.SingleObservation;
import hacettepe.com.csapp.model.WebServiceResponse;
import hacettepe.com.csapp.util.BaseBackActivity;
import hacettepe.com.csapp.util.Constants;

public class EnterSingleObservation extends BaseBackActivity {

    private long UPDATE_INTERVAL = 6 * 1000;  /* 6 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    Location mLastLocation;

    TextView tv_description;
    EditText et_value, et_location, et_additional_info;
    Button button_send;
    ProgressDialog progressDialog;
    private String observedPropertyName, observedPropertyDescription;
    private ConstraintLayout coordinatorLayout;

    private boolean locationFound = false;
    private Snackbar lookingForGPSSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_single_observation);

        Intent intent = getIntent();

        observedPropertyName = intent.getStringExtra(Constants.OBSERVED_PROPERTY_NAME);
        observedPropertyDescription = intent.getStringExtra(Constants.OBSERVED_PROPERTY_DESCRIPTION);
        //Check these values

        setTitle(observedPropertyName);

        coordinatorLayout = (ConstraintLayout) findViewById(R.id.layout_single_observation_send);
        lookingForGPSSnackBar = Snackbar.make(coordinatorLayout, R.string.looking_for_gps, Snackbar.LENGTH_SHORT);

        button_send = (Button) findViewById(R.id.button_send_single_observation);
        tv_description = (TextView) findViewById(R.id.tv_property_description);
        et_value = (EditText) findViewById(R.id.et_property_value);
        et_location = (EditText) findViewById(R.id.et_location);
        et_additional_info = (EditText) findViewById(R.id.et_additional_info);

        if (observedPropertyDescription != null && !observedPropertyDescription.isEmpty())
            tv_description.setText(observedPropertyDescription);


        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkNetworkConnection()) {
                    getValuesAndSend();
                } else {
                    //TODO - Save to local db
                    Snackbar.make(coordinatorLayout, R.string.check_internet_conn, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        //set focus on et_value
        et_value.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_value, InputMethodManager.SHOW_IMPLICIT);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        startLocationUpdates();

    }


    private void getValuesAndSend() {

        LocalData localData = new LocalData(getApplicationContext());

        String text_value = et_value.getText().toString().trim();

        if (text_value.isEmpty()) {
            Snackbar.make(coordinatorLayout, R.string.measurement_cannot_empty, Snackbar.LENGTH_SHORT).show();
            return;
        }

        String loc_desc = et_location.getText().toString().trim();
        String note = et_additional_info.getText().toString().trim();
        double dValue = Double.parseDouble(text_value);

        //TODO
        double lat = 0;
        double lon = 0;
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();
        }

        String requestCode = UUID.randomUUID().toString();
        Date now = new Date();

        SingleObservation singleObservation = new SingleObservation(requestCode, loc_desc, lat, lon, localData.UserCode(), now);
        singleObservation.setCode(UUID.randomUUID().toString());
        singleObservation.setMeasurement(dValue);
        singleObservation.setMeasurement_text(text_value);
        singleObservation.setProperty(observedPropertyName);
        singleObservation.setLoc_desc(loc_desc);
        singleObservation.setNote(note);

        Gson gson = new Gson();
        String json = gson.toJson(singleObservation);

        new HTTPAsyncTask(json).execute();
    }

    public class HTTPAsyncTask extends AsyncTask<String, Void, WebServiceResponse> {
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
                    webServiceResponse = HttpPost(Constants.POST_OBSERVATION_API_URL, data);
                    return webServiceResponse;
                    //return "";
                } catch (JSONException e) {
                    e.printStackTrace();
                    webServiceResponse.error = "Error!";
                    webServiceResponse.detail = e.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
                webServiceResponse.error = "Unable to make request.";
            } finally {
                progressDialog.dismiss();
            }

            return webServiceResponse;
        }

        @Override
        protected void onPreExecute() {
            button_send.setEnabled(false);
            progressDialog.setTitle(R.string.please_wait);
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
        writer.flush();
        writer.close();
        os.close();
    }


    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        if (locationFound == false)
            lookingForGPSSnackBar.show();

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
                //checkLocationPermission();
                Snackbar.make(coordinatorLayout, R.string.location_needed, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }

    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("Location", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                locationFound = true;
                lookingForGPSSnackBar.dismiss();
            }
        }
    };


}
