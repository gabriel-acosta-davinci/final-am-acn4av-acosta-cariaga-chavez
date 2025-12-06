package com.example.medicalshift;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements InicioFragment.OnNavigationRequest {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private int lastSelectedTab = 0;
    private static final String MENU_FRAGMENT_TAG = "menu_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String loggedInUserId = getIntent().getStringExtra("LOGGED_IN_USER_ID");

        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Pasar el ID del usuario al adaptador
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle(), loggedInUserId);
        viewPager.setAdapter(adapter);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_inicio) {
                viewPager.setCurrentItem(0, false);
            } else if (itemId == R.id.nav_cartilla) {
                viewPager.setCurrentItem(1, false);
            } else if (itemId == R.id.nav_gestiones) {
                viewPager.setCurrentItem(2, false);
            } else if (itemId == R.id.nav_perfil) {
                viewPager.setCurrentItem(3, false);
            } else if (itemId == R.id.nav_menu) {
                toggleMenuFragment();
                return false; 
            }
            return true;
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position < 4) { 
                    bottomNavigationView.getMenu().getItem(position).setChecked(true);
                    lastSelectedTab = position;
                }
            }
        });
    }

    private void toggleMenuFragment() {
        Fragment menuFragment = getSupportFragmentManager().findFragmentByTag(MENU_FRAGMENT_TAG);
        if (menuFragment != null && menuFragment.isVisible()) {
            getSupportFragmentManager().popBackStack();
            bottomNavigationView.getMenu().getItem(lastSelectedTab).setChecked(true);
        } else {
            getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right)
                .add(R.id.menu_fragment_container, new MenuFragment(), MENU_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
        }
    }

    @Override
    public void navegarA(int posicion) {
        viewPager.setCurrentItem(posicion, false);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            bottomNavigationView.getMenu().getItem(lastSelectedTab).setChecked(true);
        } else {
            super.onBackPressed();
        }
    }
}
