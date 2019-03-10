package com.example.mymedicine;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
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
//database imports
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterElderlyActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    private static final android.widget.Toast Toast = null;
    private static final android.util.Log Log = null;
    private TextView mUsernameView;
    private EditText mPasswordView;
    private TextView mFullNameView;
    private View mProgressView;
    public static final String MyMedicine = "MyMedicine";

    //*****************************************************************************************************
    //THIS CREATES AND CONNECTS THE UI
    //*****************************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_elderly);
        mProgressView = findViewById(R.id.register_progress);
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


    //*****************************************************************************************************
    //THIS IS CALLED ONCE THE REGISTER BUTTON IS CLICKED
    //*****************************************************************************************************
    private void register() {
        mProgressView.setVisibility(View.VISIBLE);
        //Gets all the user inputs
        final String fullName = mFullNameView.getText().toString();
        final String username = mUsernameView.getText().toString();
        final String password = mPasswordView.getText().toString();
        final DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();

        //Checks if the username is already taken (on FireBase database)
        try{
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot ds) {
                    boolean usernameAlreadyExists = false;
                    for (DataSnapshot i : ds.getChildren()) {
                        if (i.getKey().equals(username)) {
                            usernameAlreadyExists = true;
                        }
                    }
                    //If username is already taken, it prints an error message
                    if (usernameAlreadyExists) {
                        mProgressView.setVisibility(View.GONE);
                        mUsernameView.setError("This username has already been taken");
                    }else{
                        //Otherwise it registers the user on the database
                        mDatabase.child(username).child("password").setValue(password);
                        mDatabase.child(username).child("fullname").setValue(fullName);
                        mDatabase.child(username).child("user-type").setValue("Patient");
                        //saves the username for later use
                        SharedPreferences.Editor editor = getSharedPreferences(MyMedicine, MODE_PRIVATE).edit();
                        editor.putString("username", username);
                        editor.putString("fullname", fullName);
                        editor.apply();
                        //It goes to a new page, if the registration is successful
                        Intent intent = new Intent(RegisterElderlyActivity.this, ElderlyHomepageActivity.class);
                        startActivity(intent);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mProgressView.setVisibility(View.GONE);
                    System.out.println("DATABASE ERROR");
                }
            });
        }catch(Exception e) {
            mProgressView.setVisibility(View.GONE);
            Toast.makeText(RegisterElderlyActivity.this, "Sorry, something went wrong!", Toast.LENGTH_LONG).show();
            Log.e("MYAPP", "exception", e);
        }
    }


    //*****************************************************************************************************
    //LOADS OF BORING STUFF FROM HERE ON
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

