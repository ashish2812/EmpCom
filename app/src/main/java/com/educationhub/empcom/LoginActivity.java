package com.educationhub.empcom;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.educationhub.empcom.SessionManager.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static String TAG = "LoginActivity";
    ImageView backPressImage;
    TextInputLayout mPhoneNumber, mPassword;
    ProgressBar mProgressBar;
    TextInputEditText mPhoneNumberByRememberMe, mPasswordByRememberMe;

    CountryCodePicker countryCodePicker;
    CheckBox mRememberMe;
    FirebaseFirestore fstore;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        hooks();

        Log.d(TAG, "On create Method Started");

        //getting values form the field


        backPressImage.setOnClickListener(view -> LoginActivity.super.onBackPressed());
        //check whether the phone and password already saved or not
        SessionManager sessionManager = new SessionManager(LoginActivity.this, SessionManager.SESSION_REMEMBERME);

        if (sessionManager.checkRememberMe()) {
            HashMap<String, String> rememberMeDetails = sessionManager.getRememberSession();
            mPhoneNumberByRememberMe.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPHONENUMBER));
            mPasswordByRememberMe.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPASSWORD));

        }

    }


    //intialization of hooks
    private void hooks() {
        mPhoneNumberByRememberMe = findViewById(R.id.phoneNumberByRememberMe);
        mPasswordByRememberMe = findViewById(R.id.passwordByRememberMe);
        mPhoneNumber = findViewById(R.id.login_phone_number);
        mPassword = findViewById(R.id.login_password);
        mProgressBar = findViewById(R.id.progress_circular);
        mRememberMe = findViewById(R.id.rememberMe);
        countryCodePicker = findViewById(R.id.login_countryCodePicker);
        backPressImage = findViewById(R.id.login_back_btn);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }


    //checking if the password and phoneNumber are valid or not
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Boolean validatePassword() {
        String val = Objects.requireNonNull(mPassword.getEditText()).getText().toString();

        if (val.isEmpty()) {
            mPassword.setError("Field cannot be empty");
            return false;
        } else {
            mPassword.setError(null);
            mPassword.setErrorEnabled(false);
            return true;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Boolean validatePhoneNumber() {
        String val = Objects.requireNonNull(mPhoneNumber.getEditText()).getText().toString();
        if (val.isEmpty()) {
            mPhoneNumber.setError("Field cannot be Empty");
            return false;
        } else
            mPhoneNumber.setError(null);
        mPhoneNumber.setErrorEnabled(false);
        return true;

    }


    public void create_an_account(View view) {

        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void login(View view) {

        Log.d(TAG, "Login Button Is pressed");


        if (!validatePassword() | !validatePhoneNumber()) {
            return;
        }


        fstore = FirebaseFirestore.getInstance();
        Log.d(TAG, "fire-store instance created");

        mProgressBar.setVisibility(View.VISIBLE);
        String nPhoneNumber = "+" + countryCodePicker.getFullNumber() + Objects.requireNonNull(mPhoneNumber.getEditText()).getText().toString();
        Log.d(TAG, "Phone Number" + nPhoneNumber);


        String nPassword = Objects.requireNonNull(mPassword.getEditText()).getText().toString();
        Log.d(TAG, "Password" + nPassword);

        if (mRememberMe.isChecked()) {

            SessionManager sessionManager = new SessionManager(LoginActivity.this, SessionManager.SESSION_REMEMBERME);
            sessionManager.createRememberMeSession(mPhoneNumber.getEditText().getText().toString(), nPassword);


        }

        //Checking is the document exits in the firestore database

        DocumentReference docRef = fstore.collection("Users").document(nPhoneNumber);
        docRef.get().addOnSuccessListener(snapshot -> {


            if (snapshot.exists()) {

                if (nPassword.equals(snapshot.getString("Password"))) {
                    mProgressBar.setVisibility(View.GONE);


                    Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();

                    //storing the data in the session-manager

                    SessionManager sessionManager = new SessionManager(LoginActivity.this, SessionManager.SESSION_USERSESSION);
                    sessionManager.createLoginSession(snapshot.getString("FullName"),
                            snapshot.getString("UserName"), snapshot.getString("PhoneNumber"),
                            snapshot.getString("Password"));


                    Intent intent = new Intent(getApplicationContext(), HomeActivty.class);
                    startActivity(intent);
                    finish();
                } else {

                    mProgressBar.setVisibility(View.GONE);
                    mPassword.setError("Wrong Password");
                    mPassword.requestFocus();
                    Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                }

            } else {
                mPhoneNumber.setError("No such user found");
                mPhoneNumber.requestFocus();
                mProgressBar.setVisibility(View.GONE);
            }

        }).addOnFailureListener(e -> Log.d(TAG, "Failure in DocumentReference snapshot" + e));


    }
}