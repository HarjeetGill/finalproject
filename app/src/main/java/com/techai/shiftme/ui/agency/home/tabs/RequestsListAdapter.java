package com.techai.shiftme.ui.agency.home.tabs;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.techai.shiftme.R;
import com.techai.shiftme.data.model.Request;
import com.techai.shiftme.databinding.ItemRequestListBinding;
import com.techai.shiftme.ui.agency.track.TrackActivity;
import com.techai.shiftme.utils.Constants;

import java.util.ArrayList;

public class RequestsListAdapter extends RecyclerView.Adapter<RequestsListAdapter.ItemViewHolder> {

    public ArrayList<Request> items = new ArrayList<>();
    public AppCompatActivity activity;
    public IApproveRejectListener iApproveRejectListener;

    public RequestsListAdapter(AppCompatActivity appCompatActivity, IApproveRejectListener iApproveRejectListener) {
        activity = appCompatActivity;
        this.iApproveRejectListener = iApproveRejectListener;
    }

    public void setItemList(ArrayList<Request> list) {
        items = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestsListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRequestListBinding binding = ItemRequestListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RequestsListAdapter.ItemViewHolder(binding, activity, iApproveRejectListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsListAdapter.ItemViewHolder holder, int position) {
        holder.onBind(items.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemRequestListBinding binding;
        public AppCompatActivity activity;
        public IApproveRejectListener iApproveRejectListener;

        public ItemViewHolder(@NonNull ItemRequestListBinding itemRequestListBinding, AppCompatActivity appCompatActivity, IApproveRejectListener iApproveRejectListener) {
            super(itemRequestListBinding.getRoot());
            this.binding = itemRequestListBinding;
            this.activity = appCompatActivity;
            this.iApproveRejectListener = iApproveRejectListener;
        }

        public void onBind(Request request) {
            binding.tvListOfItems.setText(String.format(binding.getRoot().getContext().getString(R.string.list_of_items_to_move), TextUtils.join(", ", request.getItemsToShift())));
            binding.tvDistance.setText(String.format(binding.getRoot().getContext().getString(R.string.estimated_distance), request.getDistance()));
            binding.tvDateNTime.setText(String.format(binding.getRoot().getContext().getString(R.string.date_time_for_pick), request.getDateAndTime().split(Constants.DATE_TIME_SEPARATOR)[0], request.getDateAndTime().split(Constants.DATE_TIME_SEPARATOR)[1]));
            binding.tvRequestByUserName.setText(String.format(binding.getRoot().getContext().getString(R.string.request_by), request.getUserDetails().getFullName()));
            binding.tvPickLocation.setText(String.format(binding.getRoot().getContext().getString(R.string.set_pick_location), request.getPickLocation()));
            binding.tvEstimatedCost.setText(String.format(binding.getRoot().getContext().getString(R.string.set_estimated_cost), request.getCostOfShifting()));
            binding.tvDestinationLocation.setText(String.format(binding.getRoot().getContext().getString(R.string.set_destination_location), request.getDestinationLocation()));
            binding.tvStatus.setText(request.getStatus());

            switch (request.getStatus()) {
                case Constants.PENDING_REQUEST: {
                    binding.ivReject.setVisibility(View.VISIBLE);
                    binding.ivApprove.setVisibility(View.VISIBLE);
                    binding.ivTrack.setVisibility(View.GONE);
                    break;
                }
                case Constants.APPROVED_REQUEST: {
                    binding.ivReject.setVisibility(View.GONE);
                    binding.ivApprove.setVisibility(View.GONE);
                    binding.ivTrack.setVisibility(View.VISIBLE);
                    break;
                }
                case Constants.REJECTED_REQUEST: {
                    binding.ivReject.setVisibility(View.GONE);
                    binding.ivApprove.setVisibility(View.GONE);
                    binding.ivTrack.setVisibility(View.GONE);
                    break;
                }
            }

            binding.ivEmail.setOnClickListener(view -> {
                if (!request.getStatus().equals(Constants.REJECTED_REQUEST)) {
                    openEmail(request.getUserDetails().getEmailId());
                }
            });

            binding.ivPhone.setOnClickListener(view -> {
                if (!request.getStatus().equals(Constants.REJECTED_REQUEST)) {
                    openCall(request.getUserDetails().getPhoneNumber());
                }
            });

            binding.ivTrack.setOnClickListener(view -> {
                if (!request.getStatus().equals(Constants.REJECTED_REQUEST)) {
                    openTrack(request);
                }
            });

            binding.ivApprove.setOnClickListener(view -> {
                iApproveRejectListener.updateStatus(true, getAdapterPosition());
            });

            binding.ivReject.setOnClickListener(view -> {
                iApproveRejectListener.updateStatus(false, getAdapterPosition());
            });

        }

        public void openEmail(String email) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, email);
            activity.startActivity(intent);
        }

        public void openTrack(Request request) {
            iApproveRejectListener.sendToTracking(request);
        }

        public void openCall(String number) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            activity.startActivity(intent);
        }

    }

}
