package com.example.skulfulharmony.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.skulfulharmony.fragments.FragmentCursosPopulares;
import com.example.skulfulharmony.fragments.FragmentInstrumentosPopulares;

public class EstadisticasPagerAdapter extends FragmentStateAdapter {

    public EstadisticasPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new FragmentCursosPopulares() : new FragmentInstrumentosPopulares();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
