package com.techai.shiftme.ui.agency.home.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techai.shiftme.data.model.Request;
import com.techai.shiftme.data.model.SignUpModel;
import com.techai.shiftme.databinding.FragmentNewRequestsTabBinding;
import com.techai.shiftme.preferences.SharedPrefUtils;
import com.techai.shiftme.ui.customer.sendrequest.AddItemsAdapter;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewRequestsTabBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        requestList = new ArrayList<>();

        setUpAdapter();
        getAllRequests();
    }

    private void getAllRequests() {
        requestList.clear();
        AppProgressUtil.INSTANCE.showOldProgressDialog(requireContext());
        collectionRef = db.collection(Constants.REQUESTS);
        collectionRef
                .whereEqualTo("status", Constants.PENDING_REQUEST)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        AppProgressUtil.INSTANCE.closeOldProgressDialog();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                request = new Request();
                                request = document.toObject(Request.class);
                                request.setRequestId(document.getId());
                                requestList.add(request);
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
        if (isApproved) {
            // CHeck if status isn't already approved.
            docRef = collectionRef.document(requestList.get(position).getRequestId());
            docRef
                .update("status", Constants.APPROVED_REQUEST, "agencyFirebaseId", SharedPrefUtils.getStringData(requireContext(), Constants.FIREBASE_ID), "agencyDetails", SharedPrefUtils.getObject(requireContext(), Constants.SIGN_UP_MODEL, SignUpModel.class))
                .addOnCompleteListener(task -> {
                    AppProgressUtil.INSTANCE.closeOldProgressDialog();
                    if (task.isSuccessful()) {
                        ToastUtils.longCustomToast(getLayoutInflater(), binding.getRoot(), 0, "Could not fetch data. Probable reason: " + task.getException());
                    }
                });
        } else {

        }
    }
}
