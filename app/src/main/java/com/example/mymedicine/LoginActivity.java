package com.example.mymedicine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CountDownLatch;

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    private TextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    public static final String MyMedicine = "MyMedicine";

    //*****************************************************************************************************
    //THIS CREATES AND CONNECTS THE UI
    //*****************************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //This is the UI layout, with buttons and text fields
        mUsernameView = (TextView) findViewById(R.id.username);
        mProgressView = findViewById(R.id.login_progress);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        Button mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }


    //*****************************************************************************************************
    //THIS IS CALLED ONCE THE LOGIN BUTTON IS CLICKED
    //*****************************************************************************************************
    private void attemptLogin() {
        //a loading screen is shown
        mProgressView.setVisibility(View.VISIBLE);
        //the database is accessed to check the details inputted by the user
        final DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {

                boolean usernameIsOk = false;
                String username = mUsernameView.getText().toString();
                String password = mPasswordView.getText().toString();

                //this loop goes through all the entries in the database
                for (DataSnapshot i : ds.getChildren()) {
                    //it checks each one of them if they match the username inputted by the user
                    if (i.getKey().equals(username)) {
                        usernameIsOk = true;
                        //if yes, it checks if also the password matches
                        if (ds.child(username).child("password").getValue().toString().equals(password)) {
                            //if the password is correct it saves the username for later usage
                            SharedPreferences.Editor editor = getSharedPreferences(MyMedicine, MODE_PRIVATE).edit();
                            editor.putString("username", username);
                            editor.putString("fullname", ds.child(username).child("fullname").getValue().toString());
                            editor.apply();
                            //and redirects to the correct homepage
                            if(ds.child(username).child("user-type").getValue().toString().equals("Patient")){
                                Intent intent = new Intent(LoginActivity.this, ElderlyHomepageActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                                startActivity(intent);
                            }
                            //if password is wrong it sets an error
                        } else {
                            mProgressView.setVisibility(View.GONE);
                            mPasswordView.setError("Password doesn't match");
                        }
                    }
                }
                //if the username is not in the database it sets an error
                if(!usernameIsOk){
                    mProgressView.setVisibility(View.GONE);
                    mUsernameView.setError("Username not recognised");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressView.setVisibility(View.GONE);
                System.out.println("DATABASE ERROR");
            }
        });

    }


    //*****************************************************************************************************
    //BORING STUFF FROM HERE ON
    //*****************************************************************************************************
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

