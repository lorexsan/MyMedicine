package com.example.mymedicine;

import android.app.LoaderManager;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;


import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListMedicine extends AppCompatActivity {
    private ArrayList<String> myItems = new ArrayList<String>();
    private ArrayList<String> myItems2 = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    EditText editText;
    Button button;
    ListView listView;

    Button deleteButton;

    private Boolean itemSelected = false;
    private int selectedPosition = 0;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_list);

        editText = (EditText) findViewById(R.id.add_medicine);
        button = (Button) findViewById(R.id.button);
        listView = (ListView) findViewById(R.id.listViewMain);
        deleteButton = (Button) findViewById(R.id.button2);




        myItems = new ArrayList<String>();
        myItems2 = new ArrayList<String>();
        myItems.add("Hydrocodone-Acetaminophen");
        myItems.add("Simvastatin");



        adapter = new ArrayAdapter<String>(
                this,     // Context for the activity.
                R.layout.the_file,  // Layout to use (create)
                myItems);   // Items to be displayed

        ListView list = (ListView) findViewById(R.id.listViewMain);

        addChildEventListener();

        list.setAdapter(adapter);

        //user click a medicication from the list
        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
                        selectedPosition = position;
                        itemSelected = true;

                    }
                });
                addChildEventListener();



    }

    // show list data
    private void addChildEventListener() {
        ChildEventListener childListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int i=0;
                while(dataSnapshot.child("MEDICATIONS").child(String.valueOf(i)).exists()) {
                    adapter.add(dataSnapshot.child("MEDICATIONS").child(String.valueOf(i)).getValue().toString());
                    i++;
                }
                myItems2.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                int index = myItems2.indexOf(key);

                if (index != -1) {
                    myItems.remove(index);
                    myItems2.remove(index);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef.addChildEventListener(childListener);
    }

    // add medicine to database after user click the add button
    public void addItem(View view) {


        String medicine = editText.getText().toString();
        String key = dbRef.push().getKey();

        editText.setText("");
        dbRef.child(key).setValue(medicine);

        adapter.notifyDataSetChanged();
    }


    // delete medicine from database after selecting the medicine and clicking the delete button
    public void deleteItem(View view) {
        listView.setItemChecked(selectedPosition, false);
        dbRef.child(myItems2.get(selectedPosition)).removeValue();
    }


}
