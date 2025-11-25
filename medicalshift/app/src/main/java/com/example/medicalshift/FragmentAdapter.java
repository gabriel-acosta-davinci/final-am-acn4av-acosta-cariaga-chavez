package com.example.medicalshift;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new InicioFragment();
            case 1: return new CartillaFragment();
            case 2: return new GestionesFragment();
            case 3: return new PerfilFragment();
            case 4: return new MenuBottomSheetFragment();
            default: return new InicioFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
