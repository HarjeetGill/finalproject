package com.techai.shiftme.ui.agency.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techai.shiftme.databinding.FragmentRequestsBinding;
import com.techai.shiftme.ui.auth.login.LoginViewModelFactory;

public class RequestsFragment extends Fragment {

    private FragmentRequestsBinding binding;
    private RequestsViewModel viewModel;
    private DocumentReference docRef = null;
    private CollectionReference collectionRef = null;
    private FirebaseFirestore db = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRequestsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity(), new LoginViewModelFactory(getActivity().getApplication())).get(RequestsViewModel.class);
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
        final RequestsPagerAdapter adapter = new RequestsPagerAdapter(requireContext(), getChildFragmentManager(), binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(adapter);
    }

    private void setUpTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("New"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Approved / Rejected"));
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
