package com.example.gul_h.letsrun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //<-----Find components in view----->//
        final EditText theUserName =  (EditText) findViewById(R.id.userName);
        final EditText thePassword =  (EditText) findViewById(R.id.passWord);
        final Button theLogin = (Button) findViewById(R.id.login);


        //<-----Method for performing login----->//
        theLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //<-----Click on button action----->//

                String userNameString = (String) theUserName.getText().toString();
                String passwordString = (String) thePassword.getText().toString();

                //<-----Debug actions----->//

                System.out.println("the username equals: " + userNameString);
                System.out.println("the password equals: " + passwordString);

                //<-----Bypass to next scene----->//

                startActivity(new Intent(MainActivity.this, Start.class));

                //<-----Error validation----->//

                boolean pass = false;
                if(userNameString.length() < 6 && passwordString.length() < 6){
                    System.out.println("username/password must be larger then 5 in size");
                }
                else{
                    pass = true;
                }

                //<-----Compare against AzureSQL----->//

                if(pass == true){

                    System.out.println("compare against AzureSQL");
                    //<-----Code here----->//
                }
            }
        });
    }
}
