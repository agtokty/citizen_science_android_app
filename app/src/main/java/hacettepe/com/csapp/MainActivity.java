package hacettepe.com.csapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EnterDataActivity.class);
                startActivity(intent);

            }
        });
        */


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

    }

    private void prepareObservedPropertyData() {
        observedPropertyArrayList.add(new ObservedProperty("Su Sıcaklığı", ""));
        observedPropertyArrayList.add(new ObservedProperty("Ph", ""));
        observedPropertyArrayList.add(new ObservedProperty("Nitrat", ""));
    }

}
