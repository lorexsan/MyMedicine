package com.example.mymedicine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("MyMedicine", MODE_PRIVATE);
        String currentUsername = preferences.getString("username", "");
        String currentUserType = preferences.getString("user-type", "");

        if(!currentUsername.equals("") && currentUserType.equals("Patient")){
            Intent intent = new Intent(MainActivity.this, ElderlyHomepageActivity.class);
            startActivity(intent);
        } else if (!currentUsername.equals("")){
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
        }

        Button mLoginButton = (Button) findViewById(R.id.go_to_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        Button mRegisterButton = (Button) findViewById(R.id.go_to_register);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

}
