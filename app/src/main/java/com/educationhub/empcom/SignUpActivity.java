package com.educationhub.empcom;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {
    private static String TAG = "SignUpActivity";
    ImageView backpressButton;
    TextView titleText;
    Button nextBtn, loginBtn;
    CountryCodePicker mCountryCodePicker;
    TextInputLayout mFullName, mUserName, mPassowrd, mPhoneNumber;
    ProgressBar mProgressBar;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        hooks();

    }


    //initialization of hooks
    private void hooks() {
        mCountryCodePicker = findViewById(R.id.SignUp_CountryCodePicker);
        backpressButton = findViewById(R.id.SignUp_back_btn);
        titleText = findViewById(R.id.SignUp_tile_text);
        nextBtn = findViewById(R.id.SignUp_next_btn);
        loginBtn = findViewById(R.id.SignUp_login_btn);
        mProgressBar = findViewById(R.id.progress_circular);


        //InputText Hooks


        mFullName = findViewById(R.id.EnterFullName);
        mUserName = findViewById(R.id.Username);
        mPassowrd = findViewById(R.id.Signup_Password);
        mPhoneNumber = findViewById(R.id.signup_PhoneNumber);


    }

    /*
     * Validation of phoneNumber, password,username, name
     */


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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Boolean validateName() {
        String val = Objects.requireNonNull(mFullName.getEditText()).getText().toString();
        if (val.isEmpty()) {
            mFullName.setError("Field cannot be Empty");
            return false;
        } else
            mFullName.setError(null);
        mFullName.setErrorEnabled(false);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Boolean validateUserName() {
        String val = Objects.requireNonNull(mUserName.getEditText()).getText().toString().trim();

        if (val.isEmpty()) {
            mUserName.setError("Field can not be empty");
            return false;
        } else if (val.length() > 20) {
            mUserName.setError("Username is too large!");
            return false;
        } else {
            mUserName.setError(null);
            mUserName.setErrorEnabled(false);
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Boolean validatePassword() {
        String val = Objects.requireNonNull(mPassowrd.getEditText()).getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (val.isEmpty()) {
            mPassowrd.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            mPassowrd.setError("Password is too weak");
            return false;
        } else {
            mPassowrd.setError(null);
            mPassowrd.setErrorEnabled(false);
            return true;
        }

    }


    //On Next Button Clicked
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void CallNextSignUpScreen(View view) {
        if (!validateName() | !validateUserName() | !validatePhoneNumber() | !validatePassword()) {
            return;
        }
        //Getting String From TextFields

        final String _FullName = Objects.requireNonNull(mFullName.getEditText()).getText().toString();
        final String _UserName = Objects.requireNonNull(mUserName.getEditText()).getText().toString();
        final String _Password = Objects.requireNonNull(mPassowrd.getEditText()).getText().toString();
        final String _PhoneNumber = Objects.requireNonNull(mPhoneNumber.getEditText()).getText().toString();
        final String _PhoneNumberWithCountryCode = "+" + mCountryCodePicker.getFullNumber() + _PhoneNumber;
        mProgressBar.setVisibility(View.VISIBLE);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(_PhoneNumberWithCountryCode)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                mProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                mProgressBar.setVisibility(View.GONE);
                                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
                                intent.putExtra("FullName", _FullName);
                                intent.putExtra("Username", _UserName);
                                intent.putExtra("Password", _Password);
                                intent.putExtra("PhoneNumber", _PhoneNumberWithCountryCode);

                                //Otp send
                                intent.putExtra("backEndOtp", s);
                                startActivity(intent);
                                finish();
                            }
                        })
                        // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);



    }
}