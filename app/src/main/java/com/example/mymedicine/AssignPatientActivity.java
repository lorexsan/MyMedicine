package com.example.mymedicine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AssignPatientActivity extends AppCompatActivity {
    private static final android.widget.Toast Toast = null;
    private static final android.util.Log Log = null;
    private String loggedInUsername;
    private TextView patientUsername;

    //*****************************************************************************************************
    //THIS CREATES AND CONNECTS THE UI
    //*****************************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_patient);
        patientUsername = (TextView) findViewById(R.id.patient_username);

        // Get the logged in user's username
        SharedPreferences preferences = getSharedPreferences("MyMedicine", MODE_PRIVATE);
        loggedInUsername = preferences.getString("username", "");

        // Assign patient on button click
        Button assignButton = (Button) findViewById(R.id.assign_button);
        assignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assign_patient();
            }
        });

    }

    //*****************************************************************************************************
    // ASSIGNS PATIENT TO DOCTOR/FAMILY MEMBER IF A PROPER USERNAME IS TYPED
    //*****************************************************************************************************
    private void assign_patient() {
        final String username = patientUsername.getText().toString();
        final DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();

        //Checks if the username is already taken (on FireBase database)
        try{
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot ds) {
                    boolean usernameAlreadyExists = false;
                    boolean patientAlreadyAssigned = false;
                    for (DataSnapshot i : ds.getChildren()) {
                        if (i.getKey().equals(username)) {
                            usernameAlreadyExists = true;
                        }
                    }
                    if(ds.child(loggedInUsername).child("assignedPatients").exists()) {
                        for (DataSnapshot i : ds.child(loggedInUsername).child("assignedPatients").getChildren()) {
                            if (i.getValue().equals(username)) {
                                patientAlreadyAssigned = true;
                            }
                        }
                    }

                    if(patientAlreadyAssigned) {
                        patientUsername.setError("This patient is already assigned to you");
                    }

                    else if (usernameAlreadyExists) {
                        DatabaseReference patient = mDatabase.child(username).child("user-type");
                        patient.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //boolean patientAlreadyAssigned = false;
                                String type = dataSnapshot.getValue(String.class);
                                System.out.println(type);
                                // Check if the username belongs to the "Patient" type
                                if(type.equals("Patient")) {
                                    // Assign a relevant doctor/family member to a patient in the database
                                    if (ds.child(username).child("assignedDoctorFamily").child("0").exists()) {
                                        int temp=0;
                                        while(ds.child(username).child("assignedDoctorFamily").child(Integer.toString(temp)).exists()) {
                                            temp++;
                                        }
                                        mDatabase.child(username).child("assignedDoctorFamily").child(Integer.toString(temp)).setValue(loggedInUsername);
                                    }
                                    else {
                                        mDatabase.child(username).child("assignedDoctorFamily").child("0").setValue(loggedInUsername);
                                    }

                                    // Assign a relevant patient to a doctor/family member in the database
                                    if (ds.child(loggedInUsername).child("assignedPatients").child("0").exists()) {
                                        int i=0;
                                        while(ds.child(loggedInUsername).child("assignedPatients").child(Integer.toString(i)).exists()) {
                                            i++;
                                        }
                                        mDatabase.child(loggedInUsername).child("assignedPatients").child(Integer.toString(i)).setValue(username);
                                    }
                                    else {
                                        mDatabase.child(loggedInUsername).child("assignedPatients").child("0").setValue(username);
                                    }

                                    // After the doctor/family is assigned, go back to the home screen
                                    Intent intent = new Intent(AssignPatientActivity.this, DoctorFamilyHomepageActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    patientUsername.setError("This user is not registered as a patient");
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.out.println("DATABASE ERROR");
                            }
                        });


                    }else{
                        patientUsername.setError("This username does not exist");
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("DATABASE ERROR");
                }
            });
        }catch(Exception e) {
            Toast.makeText(AssignPatientActivity.this, "Sorry, something went wrong!", Toast.LENGTH_LONG).show();
            Log.e("MYAPP", "exception", e);
        }
    }
}
