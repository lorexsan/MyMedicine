package com.example.mymedicine;

import android.content.Intent;
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
        mProgressView.setVisibility(View.VISIBLE);
        final DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                boolean usernameIsOk = false;
                String username = mUsernameView.getText().toString();
                String password = mPasswordView.getText().toString();
                for (DataSnapshot i : ds.getChildren()) {
                    if (i.getKey().equals(username)) {
                        usernameIsOk = true;
                        if (ds.child(username).child("password").getValue().toString().equals(password)) {
                            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                            startActivity(intent);
                        } else {
                            mProgressView.setVisibility(View.GONE);
                            mPasswordView.setError("Password doesn't match");
                        }
                    }
                }
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

