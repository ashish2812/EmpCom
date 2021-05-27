package com.educationhub.empcom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.educationhub.empcom.Fragments.HomeFragement;
import com.educationhub.empcom.Fragments.PostFragment;
import com.educationhub.empcom.Fragments.ProfileFragement;
import com.educationhub.empcom.SessionManager.SessionManager;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomeActivty extends AppCompatActivity {
    ChipNavigationBar chipNavigationBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activty);
        chipNavigationBar = findViewById(R.id.chipNavigationBar);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_home,true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragement()).commit();
        bottomMenu();

    }

    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i){
                    case R.id.bottom_nav_home:
                        fragment = new HomeFragement();
                        break;
                    case R.id.bottom_nav_post:
                        fragment = new PostFragment();
                        break;
                    case R.id.bottom_nav_profile:
                        fragment = new ProfileFragement();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });
    }

    public void logout(View view) {
        SessionManager sessionManager = new SessionManager(HomeActivty.this,SessionManager.SESSION_USERSESSION);
        sessionManager.logout();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();

    }
}