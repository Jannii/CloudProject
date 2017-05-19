package com.example.gul_h.letsrun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //<-----Remember globals----->//
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private CheckBox checkBoxRemember;
    private EditText TheUserNameRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //<-----Find components in view----->//
        final EditText theUserName = (EditText) findViewById(R.id.userName);
        final Button theLogin = (Button) findViewById(R.id.login);
        final CheckBox theRemember = (CheckBox) findViewById(R.id.remember);

        //<-----Declare from local to Global----->//

        checkBoxRemember = theRemember;
        TheUserNameRemember = theUserName;

        //<-----Check if remember----->//
        initCheckBox();

        //<-----Method for performing login----->//
        theLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //<-----Click on button action----->//
                String userNameString = (String) theUserName.getText().toString();
                //<-----Debug actions----->//
                System.out.println("the username equals: " + userNameString);
                //<-----Bypass to next scene----->//
                //startActivity(new Intent(MainActivity.this, Start.class));

                //<-----Error validation----->//

                if (isValidEmail(userNameString) == false) {
                    System.out.println("Email must be valid");
                    createShortToastInvoke("Email must be valid");
                } else {
                    if (theRemember.isChecked()) {
                        loginPrefsEditor.putBoolean("saveLogin", true);
                        loginPrefsEditor.putString("email", userNameString);
                        loginPrefsEditor.commit();
                    } else {
                        loginPrefsEditor.clear();
                        loginPrefsEditor.commit();
                    }

                    System.out.println("Email IS valid");
                    Intent intent = new Intent(MainActivity.this, Start.class);
                    intent.putExtra("email", userNameString);
                    startActivity(intent);
                }

            }
        });

    }
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();

    }
    public void createShortToastInvoke(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void initCheckBox(){
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            //<-----DEBUG----->//
            System.out.println("PREFERENCES: " + loginPreferences.getString("email", ""));
            //<-----AUTOFILL----->//
            TheUserNameRemember.setText(loginPreferences.getString("email", ""));
            checkBoxRemember.setChecked(true);
        }

    }
}
