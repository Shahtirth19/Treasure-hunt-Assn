package com.example.mdaassn;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mdaassn.fragments.AdminFragment;
import com.example.mdaassn.fragments.ProgressFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {
    public MyViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new ProgressFragment();
            case 0:
            default:
                return new AdminFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
