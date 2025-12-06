package com.example.medicalshift;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {
    private final String userId;

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String userId) {
        super(fragmentManager, lifecycle);
        this.userId = userId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0: fragment = new InicioFragment(); break;
            case 1: fragment = new CartillaFragment(); break;
            case 2: fragment = new GestionesFragment(); break;
            case 3: fragment = new PerfilFragment(); break;
            default: fragment = new InicioFragment(); break;
        }
        
        Bundle args = new Bundle();
        args.putString("LOGGED_IN_USER_ID", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
