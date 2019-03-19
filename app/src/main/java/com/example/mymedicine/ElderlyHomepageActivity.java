package com.example.mymedicine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ElderlyHomepageActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private String currentUsername;
    private String currentFullName;
    private ArrayList<String> medications = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private ListView mMedicationsList;
    private TextView mMedicationsListTitle;
    private TextView mMedicationsListTitle2;

    //*****************************************************************************************************
    //THIS CREATES AND CONNECTS THE UI
    //*****************************************************************************************************
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_homepage);
        getSupportActionBar().hide();
        //creates the top bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.bringToFront();

        mMedicationsList = (ListView) findViewById(R.id.taken_medications);
        mMedicationsListTitle = (TextView) findViewById(R.id.taken_medications_title);
        mMedicationsListTitle2 = (TextView) findViewById(R.id.taken_medications_title2);

        //Get the logged-in user details
        SharedPreferences preferences = getSharedPreferences("MyMedicine", MODE_PRIVATE);
        currentFullName = preferences.getString("fullname", "");
        currentUsername = preferences.getString("username", "");

        //Print a welcome message in the top bar
        String message = "Hello, " + currentFullName + "!";
        toolbar.setTitle(message);

        //Print a list of medications taken
        showMedications();
    }

    //*****************************************************************************************************
    //THIS IS CALLED TO LOGOUT
    //*****************************************************************************************************
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_button:
                SharedPreferences.Editor editor = getSharedPreferences("MyMedicine", MODE_PRIVATE).edit();
                editor.putString("username", "");
                editor.putString("fullname", "");
                editor.apply();
                Intent intent = new Intent(ElderlyHomepageActivity.this, MainActivity.class);
                startActivity(intent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    //*****************************************************************************************************
    //THIS GETS THE MEDICATIONS FROM THE DATABASE AND SHOWS THEM IN A LIST
    //*****************************************************************************************************
    private void showMedications() {
        final DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                //retrieves medications for the current user
                if (ds.child(currentUsername).child("medications").child("0").exists()) {
                    int i=0;
                    //adds the medications to an arrayList
                    while(ds.child(currentUsername).child("medications").child(Integer.toString(i)).exists()) {
                        String medicine = ds.child(currentUsername).child("medications").child(Integer.toString(i)).child("name").getValue().toString();
                        medications.add(medicine);
                        i++;
                    }
                    //The content of the arrayList populates the list in the UI
                    adapter = new ArrayAdapter<>(ElderlyHomepageActivity.this, R.layout.medication_list_item, medications);
                    mMedicationsList.setAdapter(adapter);
                } else {
                    //if no medications are provided, the list is hidden and an error message is shown
                    mMedicationsList.setVisibility(View.GONE);
                    mMedicationsListTitle.setVisibility(View.GONE);
                    mMedicationsListTitle2.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DATABASE ERROR");
            }
        });
    }
}
