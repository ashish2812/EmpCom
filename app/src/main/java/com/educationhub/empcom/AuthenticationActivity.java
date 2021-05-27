package com.educationhub.empcom;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AuthenticationActivity extends AppCompatActivity {
    private final static String TAG = "AuthenticationActivity";
    TextView mName, mDescription;
    String nName, nPassword, nPhoneNumber, backOtp, enteredOtp, nUserName;
    PinView pinView;
    ProgressBar progressBar;


    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mName = findViewById(R.id.nameTv);
        mDescription = findViewById(R.id.description);
        pinView = findViewById(R.id.PinView);
        progressBar = findViewById(R.id.progress_circular);

        //Setting progress-bar visibility gone
        progressBar.setVisibility(View.GONE);


        //setting name and description in the text-fields
        mName.setText(getIntent().getStringExtra("FullName"));
        String description = "Enter the OTP sent to " + getIntent().getStringExtra("PhoneNumber");
        mDescription.setText(description);
        intentsExtra();
    }


    //getting value from last intents
    private void intentsExtra() {
        nName = getIntent().getStringExtra("FullName");
        nPassword = getIntent().getStringExtra("Password");
        nPhoneNumber = getIntent().getStringExtra("PhoneNumber");
        nUserName = getIntent().getStringExtra("Username");
        backOtp = getIntent().getStringExtra("backEndOtp");
    }


    //When continue button is pressed

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void continueButton(View view) {

        //Entered OTP
        enteredOtp = Objects.requireNonNull(pinView.getText()).toString().trim();


        Log.d("OTP", enteredOtp);

        //Checking if the pin-view is null or less than 6 digit
        if (enteredOtp.isEmpty() || enteredOtp.length() < 6) {
            Toast.makeText(AuthenticationActivity.this, "Code Error", Toast.LENGTH_SHORT).show();
            pinView.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        Log.d("OTP", enteredOtp);

        //Checking if the OTP send and Entered OTP are same
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                backOtp, enteredOtp);


        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Variefied", Toast.LENGTH_SHORT).show();
                FirebaseUser userId = mAuth.getCurrentUser();
                assert userId != null;

                DocumentReference documentReference = fstore.collection("Users").document(nPhoneNumber);
                Map<String, Object> user = new HashMap<>();
                user.put("FullName", nName);
                user.put("Password", nPassword);
                user.put("UserName", nUserName);
                user.put("PhoneNumber", nPhoneNumber);

                documentReference.set(user).addOnSuccessListener(aVoid -> Log.d(TAG, "On success: User is created with" + nPhoneNumber)
                ).addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));


                Intent intent = new Intent(getApplicationContext(), HomeActivty.class);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), userId.getUid(), Toast.LENGTH_SHORT).show();
                startActivity(intent);

                finish();
            } else {
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();

            }

        });

    }
}