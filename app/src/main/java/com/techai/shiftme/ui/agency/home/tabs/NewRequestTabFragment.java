package com.techai.shiftme.ui.agency.home.tabs;

import android.content.Intent;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techai.shiftme.data.model.AgencyModel;
import com.techai.shiftme.data.model.Request;
import com.techai.shiftme.data.model.SignUpModel;
import com.techai.shiftme.databinding.FragmentNewRequestsTabBinding;
import com.techai.shiftme.preferences.SharedPrefUtils;
import com.techai.shiftme.ui.agency.home.RequestsViewModel;
import com.techai.shiftme.ui.agency.track.TrackActivity;
import com.techai.shiftme.ui.auth.login.LoginViewModelFactory;
import com.techai.shiftme.utils.AppProgressUtil;
import com.techai.shiftme.utils.Constants;
import com.techai.shiftme.utils.ToastUtils;

import java.util.ArrayList;

public class NewRequestTabFragment extends Fragment implements IApproveRejectListener {

    private FragmentNewRequestsTabBinding binding;
    private DocumentReference docRef = null;
    private CollectionReference collectionRef = null;
    private FirebaseFirestore db = null;
    private RequestsListAdapter adapter = null;
    private ArrayList<Request> requestList = null;
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
        collectionRef.whereEqualTo("status", Constants.PENDING_REQUEST).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                AppProgressUtil.INSTANCE.closeOldProgressDialog();
                binding.swipeRefresh.setRefreshing(false);
                if (task.isSuccessful() && task.getResult().size() >= 1) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        request = new Request();
                        request = document.toObject(Request.class);
                        request.setRequestId(document.getId());
                        if (!request.getAgencyFirebaseIds().contains(SharedPrefUtils.getStringData(requireContext(), Constants.FIREBASE_ID))) {
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
                } else {
                    ToastUtils.longCustomToast(getLayoutInflater(), binding.getRoot(), 0, "Could not fetch data. Probable reason: " + task.getException());
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
        if (isApproved) {
            onApprovingRequest(position);
        } else {
            onRejectingRequest(position);
        }
    }

    @Override
    public void sendToTracking(Request request) {

    }

    private void onRejectingRequest(int position) {
        AppProgressUtil.INSTANCE.showOldProgressDialog(requireContext());
        docRef = collectionRef.document(requestList.get(position).getRequestId());
        docRef.update("agencyUpdates", FieldValue.arrayUnion(new AgencyModel(SharedPrefUtils.getStringData(requireContext(), Constants.FIREBASE_ID), Constants.REJECTED_REQUEST)),
                "agencyFirebaseIds", FieldValue.arrayUnion(SharedPrefUtils.getStringData(requireContext(), Constants.FIREBASE_ID)))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AppProgressUtil.INSTANCE.closeOldProgressDialog();
                        if (task.isSuccessful()) {
                            viewModel.updated.postValue(true);
                            ToastUtils.longCustomToast(getLayoutInflater(), binding.getRoot(), 0, "Request rejected successfully.");
                        } else {
                            ToastUtils.longCustomToast(getLayoutInflater(), binding.getRoot(), 0, "Could not update request. Probable reason: " + task.getException());
                        }
                    }
                });
    }

    private void onApprovingRequest(int position) {
        AppProgressUtil.INSTANCE.showOldProgressDialog(requireContext());
        // Check if status isn't already approved.
        docRef = collectionRef.document(requestList.get(position).getRequestId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    request = new Request();
                    request = task.getResult().toObject(Request.class);
                    if (request.getAgencyFirebaseId().isEmpty()) {
                        docRef
                                .update("status", Constants.APPROVED_REQUEST, "agencyFirebaseId", SharedPrefUtils.getStringData(requireContext(), Constants.FIREBASE_ID), "agencyDetails", SharedPrefUtils.getObject(requireContext(), Constants.SIGN_UP_MODEL, SignUpModel.class))
                                .addOnCompleteListener(taskNew -> {
                                    AppProgressUtil.INSTANCE.closeOldProgressDialog();
                                    if (taskNew.isSuccessful()) {
                                        ToastUtils.longCustomToast(getLayoutInflater(), binding.getRoot(), 0, "Request approved successfully.");
                                        // Remove this item from the list and move it to approved list.
                                        viewModel.updated.postValue(true);
                                    } else {
                                        ToastUtils.longCustomToast(getLayoutInflater(), binding.getRoot(), 0, "Could not update request. Probable reason: " + task.getException());
                                    }
                                });
                    } else {
                        // Remove this item from the list.
                        getAllRequests();
                        ToastUtils.longCustomToast(getLayoutInflater(), binding.getRoot(), 0, "This request is no longer available.");
                    }
                } else {
                    ToastUtils.longCustomToast(getLayoutInflater(), binding.getRoot(), 0, "Could not fetch data. Probable reason: " + task.getException());
                }
            }
        });
    }

}
