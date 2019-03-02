package com.example.mymedicine;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

public class RegisterElderlyActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final android.widget.Toast Toast = null;
    private static final android.util.Log Log = null;
    private TextView mUsernameView;
    private EditText mPasswordView;
    private TextView mFullNameView;

    /**
     This creates the UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_elderly);


        //This is the UI layout, with buttons and text fields
        mFullNameView = (TextView) findViewById(R.id.fullname);
        mUsernameView = (TextView) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    register();
                    return true;
                }
                return false;
            }
        });


        Button mRegisterButton = (Button) findViewById(R.id.register_elderly);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }


    /**
     This is called when the register button is clicked
     */
    private void register() {

        String fullName = mUsernameView.getText().toString();
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean error = false;

        try{
            InputStream is = this.getAssets().open("userdata.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line = br.readLine();
            String[] fields = new String[5];

            while (line != null) {
                fields = line.split(",");
                if (fields[1].equals(username)) {
                    error = true;
                }
                line = br.readLine();
            }
        }catch(Exception e) {
            Toast.makeText(RegisterElderlyActivity.this, "Sorry, something went wrong!", Toast.LENGTH_LONG).show();
            Log.e("MYAPP", "exception", e);
        }


        if (error) {
            mUsernameView.setError("This username has already been taken");
        } else {
            try{



            }catch(Exception e){
                Toast.makeText(RegisterElderlyActivity.this, "Sorry, something went wrong!", Toast.LENGTH_LONG).show();
                Log.e("MYAPP", "exception", e);
            }

        }
    }

    /**
     * Loads of boring stuff from here on
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) { }
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) { }
    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };
        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
}

