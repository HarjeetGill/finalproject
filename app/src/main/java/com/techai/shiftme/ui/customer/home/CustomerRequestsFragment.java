package com.techai.shiftme.ui.customer.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techai.shiftme.databinding.FragmentRequestsBinding;
import com.techai.shiftme.ui.auth.login.LoginViewModelFactory;

public class CustomerRequestsFragment extends Fragment {

    private FragmentRequestsBinding binding;
    private CustomerRequestsViewModel viewModel;
    private FirebaseFirestore db = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRequestsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, new LoginViewModelFactory(getActivity().getApplication())).get(CustomerRequestsViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        setUpTabLayout();
        setPageChangedListener();
        setTabSelectedListener();
        setAdapter();
    }

    private void setAdapter() {
        final CustomerRequestsPagerAdapter adapter = new CustomerRequestsPagerAdapter(requireContext(), getChildFragmentManager(), binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(adapter);
    }

    private void setUpTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Send Request"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Request List"));
        binding.tabLayout.setTabGravity(binding.tabLayout.GRAVITY_FILL);
    }

    private void setPageChangedListener() {
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
    }

    private void setTabSelectedListener() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}
