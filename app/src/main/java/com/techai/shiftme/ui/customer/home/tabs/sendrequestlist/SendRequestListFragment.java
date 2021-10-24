package com.techai.shiftme.ui.customer.home.tabs.sendrequestlist;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techai.shiftme.data.model.Request;
import com.techai.shiftme.databinding.FragmentNewRequestsTabBinding;
import com.techai.shiftme.preferences.SharedPrefUtils;
import com.techai.shiftme.ui.agency.home.RequestsViewModel;
import com.techai.shiftme.ui.agency.home.tabs.RequestsListAdapter;
import com.techai.shiftme.ui.auth.login.LoginViewModelFactory;
import com.techai.shiftme.ui.customer.home.CustomerRequestsViewModel;
import com.techai.shiftme.ui.customer.home.tabs.sendrequest.SendRequestViewModel;
import com.techai.shiftme.utils.AppProgressUtil;
import com.techai.shiftme.utils.Constants;

import java.util.ArrayList;

public class SendRequestListFragment extends Fragment {

    private FragmentNewRequestsTabBinding binding;
    private SendRequestViewModel viewModel;
    private SendRequestsAdapter adapter = null;
    private ArrayList<Request> requestList = null;
    private CollectionReference collectionRef = null;
    private FirebaseFirestore db = null;
    private Request request = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewRequestsTabBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity(), new LoginViewModelFactory(getActivity().getApplication())).get(SendRequestViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db=FirebaseFirestore.getInstance();
        requestList = new ArrayList<>();
        setUpAdapter();
        getAllRequests();
        setUpObserver();
    }

    private void setUpObserver() {
        viewModel.requestSent.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    getAllRequests();
                }
            }
        });
    }

    private void getAllRequests() {
        AppProgressUtil.INSTANCE.showOldProgressDialog(requireContext());
        collectionRef = db.collection(Constants.REQUESTS);
        collectionRef
                .whereEqualTo("firebaseId", SharedPrefUtils.getStringData(requireContext(),Constants.FIREBASE_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        AppProgressUtil.INSTANCE.closeOldProgressDialog();
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                request = new Request();
                                request = document.toObject(Request.class);
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
        adapter = new SendRequestsAdapter((AppCompatActivity) requireActivity());
        binding.rvRequests.setAdapter(adapter);
    }
}
