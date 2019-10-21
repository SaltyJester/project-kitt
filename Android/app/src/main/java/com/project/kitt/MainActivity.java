package com.project.kitt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
           Fragment selectedFragment = null;
           switch (menuItem.getItemId()){

               case R.id.nav_home:
                   selectedFragment = new HomeFragment();
                   break;

               case R.id.nav_today:
                   selectedFragment = new CalendarFragment();
                   break;

               case R.id.nav_settings:
                   selectedFragment = new SettingsFragment();
                   break;

           }
           getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
        return true;
        }
    };

    public void callAddInfo(View v){
        Intent intent = new Intent(MainActivity.this, AddInfo.class);
        startActivity(intent);
    }

}
