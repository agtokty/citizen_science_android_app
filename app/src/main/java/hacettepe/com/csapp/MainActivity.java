package hacettepe.com.csapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hacettepe.com.csapp.adapter.ObservedPropertyAdapter;
import hacettepe.com.csapp.adapter.RecyclerTouchListener;
import hacettepe.com.csapp.model.ObservedProperty;
import hacettepe.com.csapp.util.BaseActivity;
import hacettepe.com.csapp.util.Constants;

public class MainActivity extends BaseActivity {

    private List<ObservedProperty> observedPropertyArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ObservedPropertyAdapter mAdapter;
    private Boolean checkIfLocationService = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new ObservedPropertyAdapter(observedPropertyArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ObservedProperty observedProperty = observedPropertyArrayList.get(position);

                Intent intent = new Intent(MainActivity.this, EnterSingleObservation.class);
                intent.putExtra(Constants.OBSERVED_PROPERTY_NAME, observedProperty.getName());
                intent.putExtra(Constants.OBSERVED_PROPERTY_DESCRIPTION, observedProperty.getDescription());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        prepareObservedPropertyData();
        checkLocationPermission();

        if (checkIfLocationService)
            checkIfLocationServiceEnabled();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkIfLocationService)
            checkIfLocationServiceEnabled();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void prepareObservedPropertyData() {
        observedPropertyArrayList.add(new ObservedProperty("Su Sıcaklığı", ""));
        observedPropertyArrayList.add(new ObservedProperty("Ph", ""));
        observedPropertyArrayList.add(new ObservedProperty("Nitrat", ""));
        observedPropertyArrayList.add(new ObservedProperty("Fosfat", ""));
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
                        .setTitle(R.string.location_needed)
                        .setMessage(R.string.location_needed_detail)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
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

    private AlertDialog.Builder dialog;
    private void checkIfLocationServiceEnabled() {
        Context context = getApplicationContext();
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!gps_enabled) {
                // notify user
                dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setMessage(R.string.open_location_service);
                dialog.setPositiveButton(R.string.open, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getApplicationContext().startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                    }
                });
                dialog.show();
            }else{
                checkIfLocationService = false;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
