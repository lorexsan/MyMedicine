package com.example.mymedicine;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListMedicine extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    final ArrayList<String> array = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private Button addButton;
    private Button deleteButton;
    private ListView list;
    private Boolean itemSelected = false;
    private int selectedPosition = 0;

    //*****************************************************************************************************
    //THIS CREATES AND CONNECTS THE UI
    //*****************************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_list);
        getSupportActionBar().hide();

        //a top bar is created
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setTitle("Administrator Access");

        addButton = (Button) findViewById(R.id.button);
        deleteButton = (Button) findViewById(R.id.button2);
        list = (ListView) findViewById(R.id.list_data);
        adapter = new ArrayAdapter<>(this, R.layout.medication_list_item, array);
        list.setAdapter(adapter);

        //The database of all medications is displayed
        showMedications();

        //Makes the buttons and list interactive
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });
        list.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                selectedPosition = position;
                itemSelected = true;
            }
        });
        ;
    }

    //*****************************************************************************************************
    //THIS DELETES THE SELECTED MEDICINE FROM THE DATABASE
    //*****************************************************************************************************
    private void deleteItem() {
        if (itemSelected) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Are you sure you want to delete " + array.get(selectedPosition) + "?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot ds) {
                            //It removes the medicine from the online database
                            mDatabase.child("MEDICATIONS").child(String.valueOf(selectedPosition)).removeValue();
                            array.remove(selectedPosition);
                            //And displays the change in the UI
                            adapter.notifyDataSetChanged();
                            int i = selectedPosition+1;
                            while(ds.child("MEDICATIONS").child(Integer.toString(i)).exists()) {
                                String value = ds.child("MEDICATIONS").child(Integer.toString(i)).getValue().toString();
                                mDatabase.child("MEDICATIONS").child(Integer.toString(i-1)).setValue(value);
                                i++;
                            }
                            mDatabase.child("MEDICATIONS").child(String.valueOf(i-1)).removeValue();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("DATABASE ERROR");
                        }
                    });
                }
            });
            //When the cancel button is clicked the pop-up closes
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            //Dialog is shown
            builder.show();

        } else {
            Toast toast = Toast.makeText(this, "No medicine is selected", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    //*****************************************************************************************************
    //THIS DISPLAYS THE DATABASE OF MEDICATIONS IN A LIST IN THE UI
    //*****************************************************************************************************
    private void showMedications() {
        final DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot ds) {
                int i=0;
                while(ds.child("MEDICATIONS").child(Integer.toString(i)).exists()) {
                    String medicine = ds.child("MEDICATIONS").child(Integer.toString(i)).getValue().toString();
                    array.add(medicine);
                    adapter.notifyDataSetChanged();
                    i++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DATABASE ERROR");
            }
        });
    }

    //*****************************************************************************************************
    //THIS ADDS A NEW MEDICINE TO THE DATABASE
    //*****************************************************************************************************
    public void addItem() {
        final DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();
        final EditText input = new EditText(this);

        //A pop-up message is created to input the name of the medicine
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a medicine");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

        //when the 'add' button is clicked the medicine is added
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String inputMedicine = input.getText().toString();
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot ds) {
                        int i=0;
                        while(ds.child("MEDICATIONS").child(Integer.toString(i)).exists()) {
                            i++;
                        }
                        //Medicine is added to the Firebase database
                        mDatabase.child("MEDICATIONS").child(Integer.toString(i)).setValue(inputMedicine);
                        array.add(inputMedicine);
                        //Changes are displayed in the UI
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("DATABASE ERROR");
                    }
                });
            }
        });
        //When the cancel button is clicked the pop-up closes
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        //Dialog is shown
        builder.show();
    }

    //*****************************************************************************************************
    //THIS IS CALLED WHEN THE USER LOGS OUT
    //*****************************************************************************************************
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_button:
                SharedPreferences.Editor editor = getSharedPreferences("MyMedicine", MODE_PRIVATE).edit();
                editor.putString("username", "");
                editor.putString("fullname", "");
                editor.apply();
                Intent intent = new Intent(ListMedicine.this, MainActivity.class);
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
