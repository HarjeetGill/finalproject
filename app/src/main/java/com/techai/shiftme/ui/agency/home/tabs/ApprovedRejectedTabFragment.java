package com.techai.shiftme.ui.agency.home.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techai.shiftme.data.model.Request;
import com.techai.shiftme.databinding.FragmentNewRequestsTabBinding;
import com.techai.shiftme.preferences.SharedPrefUtils;
import com.techai.shiftme.ui.agency.home.RequestsViewModel;
import com.techai.shiftme.ui.auth.login.LoginViewModelFactory;
import com.techai.shiftme.utils.AppProgressUtil;
import com.techai.shiftme.utils.Constants;

import java.util.ArrayList;

public class ApprovedRejectedTabFragment extends Fragment implements IApproveRejectListener {

    private FragmentNewRequestsTabBinding binding;
    private DocumentReference docRef = null;
    private CollectionReference collectionRef = null;
    private FirebaseFirestore db = null;
    private RequestsListAdapter adapter = null;
    private ArrayList<Request> requestList = null;
    private ArrayList<String> filterStatusList = null;
    private Request request = null;
    private RequestsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewRequestsTabBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity(), new LoginViewModelFactory(getActivity().getApplication())).get(RequestsViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        requestList = new ArrayList<>();
        filterStatusList = new ArrayList<>();

        filterStatusList.add(Constants.APPROVED_REQUEST);
        filterStatusList.add(Constants.PENDING_REQUEST);

        setUpAdapter();
        getAllRequests();
        setUpObserver();
    }

    private void setUpObserver() {

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllRequests();
            }
        });

        viewModel.updated.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    getAllRequests();
                }
            }
        });
    }

    private void getAllRequests() {
        requestList.clear();
        AppProgressUtil.INSTANCE.showOldProgressDialog(requireContext());
        collectionRef = db.collection(Constants.REQUESTS);
        collectionRef
                .whereIn("status", filterStatusList)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        AppProgressUtil.INSTANCE.closeOldProgressDialog();
                        binding.swipeRefresh.setRefreshing(false);
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                request = new Request();
                                request = document.toObject(Request.class);
                                request.setRequestId(document.getId());
                                if (request.getAgencyFirebaseIds().contains(SharedPrefUtils.getStringData(requireContext(), Constants.FIREBASE_ID)) ||
                                        request.getAgencyFirebaseId().equals(SharedPrefUtils.getStringData(requireContext(), Constants.FIREBASE_ID))) {
                                    if (request.getStatus().equals(Constants.PENDING_REQUEST)) {
                                        request.setStatus(Constants.REJECTED_REQUEST);
                                    }
                                    requestList.add(request);
                                }
                            }
                            if (requestList.isEmpty()) {
                                binding.rvRequests.setVisibility(View.GONE);
                                binding.tvShowNoData.setVisibility(View.VISIBLE);
                            } else {
                                binding.rvRequests.setVisibility(View.VISIBLE);
                                binding.tvShowNoData.setVisibility(View.GONE);
                                adapter.setItemList(requestList);
                            }
                        }
                    }
                });
    }

    private void setUpAdapter() {
        binding.rvRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RequestsListAdapter((AppCompatActivity) requireActivity(), this);
        binding.rvRequests.setAdapter(adapter);
    }

    @Override
    public void updateStatus(Boolean isApproved, int position) {

    }
}
