package com.techai.shiftme.ui.agency.home;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.techai.shiftme.ui.agency.home.tabs.ApprovedRejectedTabFragment;
import com.techai.shiftme.ui.agency.home.tabs.NewRequestTabFragment;

public class RequestsPagerAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public RequestsPagerAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                NewRequestTabFragment newRequestTabFragment = new NewRequestTabFragment();
                return newRequestTabFragment;
            case 1:
                ApprovedRejectedTabFragment approvedRejectedTabFragment = new ApprovedRejectedTabFragment();
                return approvedRejectedTabFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
