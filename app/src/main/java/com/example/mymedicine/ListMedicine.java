package com.example.mymedicine;

import android.app.LoaderManager;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListMedicine extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_list);

        populateListView();
        registerClickCallback();
    }

    private void populateListView() {
        // Create list of items
        String[] myItems = {"Hydrocodone-Acetaminophen", "Simvastatin", "Prinivil (Lisinopril)", "ynthroid-Levothyroxine", "Norvasc-Amlodipine"};

        // Build Adapter
        // TODO: CHANGE THE [[ to a less than, ]] to greater than.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,     // Context for the activity.
                 R.layout.the_file,  // Layout to use (create)
                myItems);   // Items to be displayed

        // Configure the list view.
        ListView list = (ListView) findViewById(R.id.listViewMain);
        list.setAdapter(adapter);
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.listViewMain);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // TODO: CHANGE THE [[ to a less than, ]] to greater than.
            public void onItemClick(AdapterView<?> paret, View viewClicked, int position, long id) {
                TextView textView = (TextView) viewClicked;
                String message = "You clicked # " + position
                        + ", which is string: " + textView.getText().toString();
                Toast.makeText(ListMedicine.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}