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
import java.util.ArrayList;
import com.google.firebase.database.ChildEventListener;
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
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_list);
        getSupportActionBar().hide();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setTitle("Administrator Access");

        addButton = (Button) findViewById(R.id.button);
        deleteButton = (Button) findViewById(R.id.button2);
        list = (ListView) findViewById(R.id.listViewMain);

        adapter = new ArrayAdapter<>(this, R.layout.medication_list_item, array);
        list.setAdapter(adapter);
        showMedications();


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

        //user click a medication from the list
        list.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                selectedPosition = position;
                itemSelected = true;
            }
        });
    }

    private void deleteItem() {
        if (itemSelected) {
            final DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot ds) {
                    mDatabase.child("MEDICATIONS").child(String.valueOf(selectedPosition)).removeValue();
                    array.remove(selectedPosition);
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
    }

    private void showMedications() {
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
    }

    public void addItem() {
        final DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();
        final EditText input = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a medicine");

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

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
                        mDatabase.child("MEDICATIONS").child(Integer.toString(i)).setValue(inputMedicine);
                        array.add(inputMedicine);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("DATABASE ERROR");
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

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
