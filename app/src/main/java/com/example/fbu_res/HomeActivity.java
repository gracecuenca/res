package com.example.fbu_res;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fbu_res.fragments.GroupFragment;
import com.example.fbu_res.fragments.HomeFragment;
import com.example.fbu_res.fragments.ProfileFragment;
import com.example.fbu_res.fragments.SearchFragment;
import com.example.fbu_res.models.Business;
import com.example.fbu_res.models.Consumer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final FragmentManager fragmentManager = getSupportFragmentManager();

        if(((Consumer)ParseUser.getCurrentUser()).getType().equals("Consumer")){
            Toast.makeText(this, "signed in as a consumer", Toast.LENGTH_SHORT).show();
        }else if (((Consumer)ParseUser.getCurrentUser()).getType().equals("Business")){
            Toast.makeText(this, "signed in as a business", Toast.LENGTH_SHORT).show();
        }

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment fragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_search:
                        fragment = new SearchFragment();
                        break;
                    case R.id.action_groups:
                        fragment = new GroupFragment();
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection to home
        bottomNavigationView.setSelectedItemId(R.id.action_home);

    }
}
