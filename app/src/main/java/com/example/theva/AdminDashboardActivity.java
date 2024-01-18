package com.example.theva;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.theva.databinding.FragmentAdminHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.checkerframework.checker.nullness.qual.NonNull;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // I added this line to make the first fragment displayed be the home fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new AdminHomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    int itemId = item.getItemId();
                    if (itemId == R.id.home) {
                        selectedFragment = new AdminHomeFragment();
                    } else if (itemId == R.id.addCategory) {
                        selectedFragment = new AddCategoryFragment();
                    } else if (itemId == R.id.Product) {
                        selectedFragment = new ProductFragment();
                    } else if (itemId == R.id.order){
                        selectedFragment = new OrderFragment();
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };
}

