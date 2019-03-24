package com.example.mymedicine;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeleteMedicationActivity extends AppCompatActivity {

    private static final android.widget.Toast Toast = null;
    private static final android.util.Log Log = null;
    private String loggedInUsername;
    private EditText patientUsername;
    private EditText medicineName;
    private Button submitButton;
    private Boolean medicineSelected = false;
    private int selectedPosition = 0;
    private Boolean medicinePrescribed = true;

    final ArrayList<String> medicines = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    //*****************************************************************************************************
    // SWITCH BETWEEN TABS ON BOTTOM NAVIGATION WIDGET
    //*****************************************************************************************************
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(DeleteMedicationActivity.this,DoctorFamilyHomepageActivity.class));

                    return true;
                case R.id.navigation_add:

                    startActivity(new Intent(DeleteMedicationActivity.this,AssignMedicineActivity.class));

                    return true;
                case R.id.navigation_delete:
                    startActivity(new Intent(DeleteMedicationActivity.this,DeleteMedicationActivity.class));
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
        setContentView(R.layout.activity_delete_medication);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        getSupportActionBar().hide();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        //toolbar.setOnMenuItemClickListener(this);
        toolbar.setTitle("Delete patient's medicine");

        // Get a username of a doctor/family member that is logged in
        SharedPreferences preferences = getSharedPreferences("MyMedicine", MODE_PRIVATE);
        loggedInUsername = preferences.getString("username", "");

        patientUsername = (EditText) findViewById(R.id.patient_username);
        medicineName = (EditText) findViewById(R.id.medicine_name);

        medicineName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewMedicineList();
            }
        });

        submitButton = (Button) findViewById(R.id.delete_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionDeleteButton();
            }
        });
    }


    //*****************************************************************************************************
    // Pop up window with medicine list of a patient to select from.
    //*****************************************************************************************************
    public void viewMedicineList() {
        // Check the patient's username
        final String patient = patientUsername.getText().toString();
        final DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();

        DatabaseReference patientDR = mDatabase.child(loggedInUsername);
        patientDR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if a selected patient has medicine assigned to him/her
                if(dataSnapshot.child("assignedPatients").exists()){
                    for (DataSnapshot i : dataSnapshot.child("assignedPatients").getChildren()) {
                        if (i.getValue().equals(patient)) {
                            // creates an arrayList of medicines assigned to a patient from Firebase
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot ds) {
                                    medicines.removeAll(medicines);
                                    if(ds.child(patient).child("medicationsList").exists()) {
                                        // Store names of all medicine that is assigned to a patient
                                        for (DataSnapshot i : ds.child(patient).child("medicationsList").getChildren()) {
                                            medicines.add(i.getKey());
                                        }
                                    }
                                    else {
                                        // If a patient does not have any medicine assigned to him/her
                                        medicinePrescribed = false;
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    System.out.println("DATABASE ERROR");
                                }
                            });

                            // Create a pop up window
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DeleteMedicationActivity.this);
                            if(!medicinePrescribed) {
                                alertDialog.setTitle("No medicines are prescribed to this patient");
                                // Close a dialog window when "Cancel" button is clicked
                                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog dialog = alertDialog.create();
                                dialog.show();
                            }
                            else {
                                alertDialog.setTitle("Select medicine");
                                View row = getLayoutInflater().inflate(R.layout.row_item,null);
                                ListView list = (ListView)row.findViewById(R.id.list_view);

                                // Display a list with medicine names on pop up window
                                adapter = new ArrayAdapter<>(DeleteMedicationActivity.this, R.layout.medication_list2_item, medicines);
                                list.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                alertDialog.setView(row);

                                // Select a medicine on click
                                list.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        view.setSelected(true);
                                        selectedPosition = position;
                                        medicineSelected = true;
                                    }
                                });

                                alertDialog.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(medicineSelected){
                                            medicineName.setText(medicines.get(selectedPosition));
                                        }
                                        else{
                                            medicineName.setText(medicines.get(0));
                                        }
                                        dialog.cancel();
                                    }
                                });
                                // Close a dialog window when "Cancel" button is clicked
                                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog dialog = alertDialog.create();
                                dialog.show();

                            }

                        }
                    }
                }
                else {
                    medicineName.setError("Please select a patient that is assigned to you");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DATABASE ERROR");
            }
        });
    }


    //*****************************************************************************************************
    // WHEN DELETE BUTTON IS CLICKED, THE MEDICINE IS REMOVED FROM DATABASE OR ERROR IS DISPLAYED
    //*****************************************************************************************************
    private void actionDeleteButton() {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                boolean usernameAssigned = false;
                boolean medicineFound = false;
                String username = patientUsername.getText().toString();
                String medicine = medicineName.getText().toString();

                // Check if the patient is assigned to a logged in doctor/family member
                for (DataSnapshot i : ds.child(loggedInUsername).child("assignedPatients").getChildren()) {
                    if (i.getValue().equals(username)) {
                        usernameAssigned = true;
                    }
                }
                if (medicine.trim().isEmpty()){
                    medicineName.setError("Please select a medicine");
                }
                else if (!usernameAssigned) {
                    patientUsername.setError("This patient is not assigned to you");
                }
                else {
                    for (DataSnapshot i : ds.child(username).child("medicationsList").getChildren()) {
                        if(i.getKey().toString().equals(medicine)){
                            mDatabase.child(username).child("medicationsList").child(medicine).removeValue();
                            medicineFound = true;
                            // After the medicine is deleted go to the homepage
                            Intent intent = new Intent(DeleteMedicationActivity.this, DoctorFamilyHomepageActivity.class);
                            startActivity(intent);
                        }
                    }
                    if (!medicineFound) {
                        medicineName.setError("This medicine is not assigned to the patient");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DATABASE ERROR");
            }
        });
    }

    //*****************************************************************************************************
    //THIS IS CALLED WHEN THE USER LOGS OUT
    //*****************************************************************************************************
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_button:
                SharedPreferences.Editor editor = getSharedPreferences("MyMedicine", MODE_PRIVATE).edit();
                editor.putString("username", "");
                editor.putString("fullname", "");
                editor.apply();
                Intent intent = new Intent(DeleteMedicationActivity.this, MainActivity.class);
                startActivity(intent);
        }
        return true;
    }
}
