package com.techai.shiftme.ui.customer.home;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.techai.shiftme.ui.customer.home.tabs.sendrequestlist.SendRequestListFragment;
import com.techai.shiftme.ui.customer.home.tabs.sendrequest.SendRequestFragment;

public class CustomerRequestsPagerAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public CustomerRequestsPagerAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                SendRequestFragment sendRequestFragment = new SendRequestFragment();
                return sendRequestFragment;
            case 1:
                SendRequestListFragment sendRequestListFragment = new SendRequestListFragment();
                return sendRequestListFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
