package com.example.mymedicine;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RegisterDoctorFamilyActivity extends AppCompatActivity{
    private TextView mUsernameView;
    private TextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;

    //*****************************************************************************************************
    //THIS CREATES AND CONNECTS THE UI
    //*****************************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_doctorfamily);

        //This is the UI layout, with buttons and text fields
        mProgressView = findViewById(R.id.docfam_progress);
        mUsernameView = (TextView) findViewById(R.id.username);
        mEmailView = (TextView) findViewById(R.id.email);
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


    //*****************************************************************************************************
    //THIS IS CALLED WHEN THE 'REGISTER' BUTTON IS CLICKED
    //*****************************************************************************************************
    private void attemptRegistration() {
        mProgressView.setVisibility(View.VISIBLE);

        //This gets the input values
        final String username = mUsernameView.getText().toString();
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        final DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();

        //This connects to the Firebase database
        try{
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot ds) {
                    boolean usernameAlreadyTaken = false;
                    //Search if the username is already used
                    for (DataSnapshot i : ds.getChildren()) {
                        if (i.getKey().equals(username)) {
                            usernameAlreadyTaken = true;
                        }
                    }
                    //If any of the text fields are empty, it prints an error message
                    if (password.equals("")) {
                        mProgressView.setVisibility(View.GONE);
                        mPasswordView.setError("Please type in your password");
                    }
                    if (email.equals("")) {
                        mProgressView.setVisibility(View.GONE);
                        mEmailView.setError("Please type in your email");
                    }
                    if (username.equals("")) {
                        mProgressView.setVisibility(View.GONE);
                        mUsernameView.setError("Please type in your username");
                    }
                    //If username is already taken, it prints an error message
                    else if (usernameAlreadyTaken) {
                        mProgressView.setVisibility(View.GONE);
                        mUsernameView.setError("This username has already been taken");
                    }
                    else{
                        //Otherwise it registers the user on the database
                        mDatabase.child(username).child("password").setValue(password);
                        mDatabase.child(username).child("email").setValue(email);
                        mDatabase.child(username).child("user-type").setValue("Doctor/Family");

                        // Store the username that a doctor/family registers with
                        SharedPreferences.Editor editor = getSharedPreferences("MyMedicine", MODE_PRIVATE).edit();
                        editor.putString("username", username);
                        editor.apply();

                        //It goes to a new page, if the registration is successful
                        Intent intent = new Intent(RegisterDoctorFamilyActivity.this, DoctorFamilyHomepageActivity.class);
                        startActivity(intent);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("DATABASE ERROR");
                }
            });
        }catch(Exception e) {
            mProgressView.setVisibility(View.GONE);
            Toast.makeText(RegisterDoctorFamilyActivity.this, "Sorry, something went wrong!", Toast.LENGTH_LONG).show();
            Log.e("MYMEDICINE", "exception", e);
        }
    }
}

