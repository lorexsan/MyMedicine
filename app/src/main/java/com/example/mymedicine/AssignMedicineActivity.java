package com.example.mymedicine;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import java.util.Calendar;
import android.widget.TimePicker;
import android.app.TimePickerDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AssignMedicineActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener{
    private static final android.widget.Toast Toast = null;
    private static final android.util.Log Log = null;
    private String loggedInUsername;

    private TextView patientUsername;
    private TextView medicineName;
    private TextView startDate;
    private TextView endDate;
    private TextView dosage;
    private TextView time;
    private Button submitButton;
    //private View mProgressView;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    TimePickerDialog timePickerDialog;
    String amPm;


    private Boolean itemSelected = false;
    private int selectedPosition = 0;

    final ArrayList<String> array = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private static final String TAG = "MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_medicine);
        getSupportActionBar().hide();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setTitle("Assign medicine to a patient");



        SharedPreferences preferences = getSharedPreferences("MyMedicine", MODE_PRIVATE);
        loggedInUsername = preferences.getString("username", "");

        patientUsername = (TextView) findViewById(R.id.patient_username);


        // creates an arrayList of medicine name from firebase
        final DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot ds) {
                int i=0;
                while(ds.child("MEDICATIONS").child(Integer.toString(i)).exists()) {
                    String medicine = ds.child("MEDICATIONS").child(Integer.toString(i)).getValue().toString();
                    array.add(medicine);
                    i++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DATABASE ERROR");
            }
        });



        medicineName= (TextView) findViewById(R.id.medicine_name);
        medicineName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewMedicineList();
            }
        });


        startDate = (TextView) findViewById(R.id.start_date);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMedicineDate();
            }
        });


        endDate = (TextView) findViewById(R.id.end_date);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endMedicineDate();
            }
        });


        dosage = (TextView) findViewById(R.id.dosage);


        time = (TextView) findViewById(R.id.time);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                medicineTime();
            }
        });


        submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSubmit();

            }
        });

    }

    //*****************************************************************************************************
    //When submit checks if patient username is in the doctor's patient list then for that patient assign
    // medicine with all the prescription information (start date, end date, dosage, etc...). After that go
    // back to the doctor homepage
    //*****************************************************************************************************
    private void actionSubmit() {
        final String username = patientUsername.getText().toString();
        final String medicine = medicineName.getText().toString();
        final String sDate = startDate.getText().toString();
        final String eDate = endDate.getText().toString();
        final String dose = dosage.getText().toString();
        final String hourlyTime = time.getText().toString();

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {

                boolean usernameExists = false;
                for (DataSnapshot i : ds.getChildren()) {
                    if (i.getKey().equals(username)) {
                        usernameExists = true;
                        if (ds.child(username).child("assignedDoctorFamily").exists()) {
                            if (ds.child(username).child("assignedDoctorFamily").getValue().toString().equals(loggedInUsername)) {
                                mDatabase.child(username).child("medicine").setValue(medicine);
                                mDatabase.child(username).child("medicine").child("startDate").setValue(sDate);
                                mDatabase.child(username).child("medicine").child("endDate").setValue(eDate);
                                mDatabase.child(username).child("medicine").child("dosage").setValue(dose);
                                mDatabase.child(username).child("medicine").child("time").setValue(hourlyTime);

                                Intent intent = new Intent(AssignMedicineActivity.this, DoctorFamilyHomepageActivity.class);
                                startActivity(intent);


                            } else {
                                patientUsername.setError(" This user is not registered as a patient of " + loggedInUsername);
                            }
                        }
                        else{ patientUsername.setError(" This user is not registered as a patient of " + loggedInUsername);

                        }
                    }

                    else{
                        patientUsername.setError("This username does not exist");
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
    //pop up window with medicine list from database to select from.
    //*****************************************************************************************************
    public void viewMedicineList() {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Select medicine");
        View row = getLayoutInflater().inflate(R.layout.row_item,null);
        ListView list = (ListView)row.findViewById(R.id.list_view);



        adapter = new ArrayAdapter<>(this, R.layout.medication_list2_item, array);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        alertDialog.setView(row);





        list.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                selectedPosition = position;
                itemSelected = true;
            }
        });

        if(itemSelected){
            medicineName.setText(array.get(selectedPosition));

        }


        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });



        AlertDialog dialog = alertDialog.create();
        //Dialog is shown
        dialog.show();

    }


    //*****************************************************************************************************
    //pop up window to select the start time of the medicine
    //*****************************************************************************************************
    public void startMedicineDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                AssignMedicineActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date =  day + "-" +month + "-" + year;
                startDate.setText(date);
            }
        };

    }

    //*****************************************************************************************************
    //pop up window to select the end time of the medicine
    //*****************************************************************************************************
    public void endMedicineDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                AssignMedicineActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date =  day + "-" +month + "-" + year;
                endDate.setText(date);
            }
        };

    }

    //*****************************************************************************************************
    //pop up window to select the time patient needs to take the medicine
    //*****************************************************************************************************
    public void medicineTime(){

        Calendar cal= Calendar.getInstance();
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        int currentMinute = cal.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(AssignMedicineActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                if (hourOfDay >= 12) {
                    amPm = "PM";
                } else {
                    amPm = "AM";
                }
                time.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);
            }
        }, currentHour, currentMinute, false);

        timePickerDialog.show();

    }



    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_button:
                SharedPreferences.Editor editor = getSharedPreferences("MyMedicine", MODE_PRIVATE).edit();
                editor.putString("username", "");
                editor.putString("fullname", "");
                editor.apply();
                Intent intent = new Intent(AssignMedicineActivity.this, MainActivity.class);
                startActivity(intent);
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity(); // or finish();
    }


}

