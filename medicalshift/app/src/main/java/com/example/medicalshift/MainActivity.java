package com.example.medicalshift;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements InicioFragment.OnNavigationRequest {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);

        // Navegación con la barra inferior
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
                viewPager.setCurrentItem(4, false);
            }
            return true;
        });

        // Sincronizar ViewPager con la barra de navegación
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });
    }

    @Override
    public void navegarA(int posicion) {
        viewPager.setCurrentItem(posicion, false);
    }
}
