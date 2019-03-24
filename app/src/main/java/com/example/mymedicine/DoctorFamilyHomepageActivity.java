package com.example.mymedicine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorFamilyHomepageActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private String currentUsername;
    private TextView text1;
    private TextView text2;
    private ListView mPatientsList;
    private ListView mSkippedPatientsList;
    private ArrayList<String> skippedPatients = new ArrayList<String>();
    private ArrayAdapter<String> adapterSk;
    private ArrayList<String> patients = new ArrayList<String>();
    private ArrayAdapter<String> adapter;


    //*****************************************************************************************************
    // SWITCH BETWEEN TABS ON BOTTOM NAVIGATION WIDGET
    //*****************************************************************************************************
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(DoctorFamilyHomepageActivity.this,DoctorFamilyHomepageActivity.class));

                    return true;
                case R.id.navigation_add:

                    startActivity(new Intent(DoctorFamilyHomepageActivity.this,AssignMedicineActivity.class));

                    return true;
                case R.id.navigation_delete:
                    startActivity(new Intent(DoctorFamilyHomepageActivity.this,DeleteMedicationActivity.class));
                    return true;
            }
            return false;
        }
    };

    //*****************************************************************************************************
    //THIS CREATES AND CONNECTS THE UI
    //*****************************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_family_homepage);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        getSupportActionBar().hide();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(this);

        mPatientsList = (ListView) findViewById(R.id.assigned_patients);
        mSkippedPatientsList = (ListView) findViewById(R.id.skipped_patients);
        text1 = (TextView) findViewById(R.id.textView5);
        text2 = (TextView) findViewById(R.id.textView6);

        //Get the logged in user's username
        SharedPreferences preferences = getSharedPreferences("MyMedicine", MODE_PRIVATE);
        currentUsername = preferences.getString("username", "");

        //Print a welcome message
        String message = "Hello, " + currentUsername + "!";
        toolbar.setTitle(message);

        //Print a list of medications taken
        showPatients();

        Button mAssignPatientButton = (Button) findViewById(R.id.assign_patient);
        mAssignPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When the "assign" button is clicked, go to a screen for assigning patient
                Intent intent = new Intent(DoctorFamilyHomepageActivity.this, AssignPatientActivity.class);
                startActivity(intent);
            }
        });
    }

    //*****************************************************************************************************
    // DISPLAY A LIST OF PATIENTS ASSIGNED TO A LOGGED IN DOCTOR/FAMILY MEMBER
    //*****************************************************************************************************
    private void showPatients() {
        final DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                if (ds.child(currentUsername).child("assignedPatients").child("0").exists()) {
                    int i=0;
                    while(ds.child(currentUsername).child("assignedPatients").child(Integer.toString(i)).exists()) {
                        String patient = ds.child(currentUsername).child("assignedPatients").child(Integer.toString(i)).getValue().toString();
                        patients.add(patient);
                        if(ds.child(patient).child("medicationsList").exists()){
                            for(DataSnapshot data : ds.child(patient).child("medicationsList").getChildren()) {
                                if (data.child("taken").toString().contains("nnn")){
                                    skippedPatients.add(patient);
                                }
                            }
                        }
                        i++;
                    }
                    adapter = new ArrayAdapter<>(DoctorFamilyHomepageActivity.this, R.layout.medication_list_item, patients);
                    mPatientsList.setAdapter(adapter);
                    adapterSk = new ArrayAdapter<>(DoctorFamilyHomepageActivity.this, R.layout.medication_list_item, skippedPatients);
                    mSkippedPatientsList.setAdapter(adapterSk);
                } else {
                    mPatientsList.setVisibility(View.GONE);
                    mSkippedPatientsList.setVisibility(View.GONE);
                    text2.setVisibility(View.GONE);
                    text1.setText("You don't have any patient assigned.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DATABASE ERROR");
            }
        });
    }

    //*****************************************************************************************************
    // THIS IS CALLED WHEN THE USER LOGS OUT
    //*****************************************************************************************************
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_button:
                SharedPreferences.Editor editor = getSharedPreferences("MyMedicine", MODE_PRIVATE).edit();
                editor.putString("username", "");
                editor.putString("fullname", "");
                editor.apply();
                Intent intent = new Intent(DoctorFamilyHomepageActivity.this, MainActivity.class);
                startActivity(intent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {



    }
}
