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

public class RegisterDoctorFamilyActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private TextView mUsernameView;
    private EditText mPasswordView;

    /**
     This creates the UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_doctorfamily);

        //This is the UI layout, with buttons and text fields

        mUsernameView = (TextView) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.register_carer);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });
    }


    /**
     This is called when the register button is clicked
     */
    private void attemptRegistration() {

        //TODO: Register the details on the txt file, if not set variable error to true
        String email = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean error = false;


        try{
            //TODO: access FireBase database
            //TODO: check if username already used, if yes set 'error' to true


        }catch(Exception e) {
            Toast.makeText(RegisterDoctorFamilyActivity.this, "Sorry, something went wrong!", Toast.LENGTH_LONG).show();
            Log.e("MYAPP", "exception", e);
        }


        if (error) {
            mUsernameView.setError("This username has already been taken");
        } else {
            try{
                //TODO: register new user


            }catch(Exception e){
                Toast.makeText(RegisterDoctorFamilyActivity.this, "Sorry, something went wrong!", Toast.LENGTH_LONG).show();
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

