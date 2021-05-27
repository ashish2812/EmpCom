package com.educationhub.empcom;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.educationhub.empcom.SessionManager.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    TextView mAppNameTv, mSloganTv;
    Animation rightAnimation, middleAnimation;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        intialization();
        mAuth = FirebaseAuth.getInstance();
        int SPLASH_TIME_OUT = 1500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SessionManager sessionManager = new SessionManager(SplashScreen.this, SessionManager.SESSION_USERSESSION);
                if (mAuth.getCurrentUser() != null | sessionManager.checkLogin()) {
                    startActivity(new Intent(getApplicationContext(), HomeActivty.class));
                    finish();
                }else{
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }



            }
        }, SPLASH_TIME_OUT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void intialization() {

        //hooks
        mAppNameTv = findViewById(R.id.appNameTv);
        mSloganTv = findViewById(R.id.sloganTv);

        //animation
        rightAnimation = AnimationUtils.loadAnimation(this, R.anim.right);
        middleAnimation = AnimationUtils.loadAnimation(this, R.anim.middle_animation);

        mAppNameTv.setAnimation(rightAnimation);
        mSloganTv.setAnimation(middleAnimation);

    }
}