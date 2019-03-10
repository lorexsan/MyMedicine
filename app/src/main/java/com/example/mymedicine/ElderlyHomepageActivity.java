package com.example.mymedicine;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ElderlyHomepageActivity extends AppCompatActivity {

    private TextView mTitleMessage;
    private String currentUsername;
    private String currentFullName;
    private ArrayList<String> medications = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private ListView mMedicationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_homepage);
        mTitleMessage = (TextView) findViewById(R.id.elderly_home_title);
        mMedicationsList = (ListView) findViewById(R.id.taken_medications);

        //Get the logged in user's username
        SharedPreferences preferences = getSharedPreferences("MyMedicine", MODE_PRIVATE);
        currentFullName = preferences.getString("fullname", "");
        currentUsername = preferences.getString("username", "");

        //Print a welcome message
        String message = "Hello, " + currentFullName + "!";
        mTitleMessage.setText(message);

        //Print a list of medications taken
        showMedications();
    }

    private void showMedications() {
        final DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                if (ds.child(currentUsername).child("medications").exists()) {
                    int i=0;
                    while(ds.child(currentUsername).child("medications").child(Integer.toString(i)).exists()) {
                        String medicine = ds.child(currentUsername).child("medications").child(Integer.toString(i)).getValue().toString();
                        medications.add(medicine);
                        i++;
                    }
                    adapter = new ArrayAdapter<>(ElderlyHomepageActivity.this, R.layout.medication_list_item, medications);
                    mMedicationsList.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DATABASE ERROR");
            }
        });

    }
}
