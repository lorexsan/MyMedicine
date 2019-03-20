package com.example.mymedicine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorFamilyHomepageActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private String currentUsername;
    private ListView mPatientsList;
    private ArrayList<String> patients = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_family_homepage);
        getSupportActionBar().hide();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(this);

        mPatientsList = (ListView) findViewById(R.id.assigned_patients);

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
                        i++;
                    }
                    adapter = new ArrayAdapter<>(DoctorFamilyHomepageActivity.this, R.layout.medication_list_item, patients);
                    mPatientsList.setAdapter(adapter);
                } else {
                    mPatientsList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DATABASE ERROR");
            }
        });
    }

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
}
