package com.techai.shiftme.ui.customer.home.tabs.sendrequestlist;

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
import com.techai.shiftme.utils.Constants;

import java.util.ArrayList;

public class SendRequestsAdapter extends RecyclerView.Adapter<SendRequestsAdapter.ItemViewHolder> {

    public ArrayList<Request> items = new ArrayList<>();
    public AppCompatActivity activity;

    public SendRequestsAdapter(AppCompatActivity appCompatActivity) {
        activity = appCompatActivity;
    }

    public void setItemList(ArrayList<Request> list) {
        items = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRequestListBinding binding = ItemRequestListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(items.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemRequestListBinding binding;
        public AppCompatActivity activity;

        public ItemViewHolder(@NonNull ItemRequestListBinding itemRequestListBinding, AppCompatActivity appCompatActivity) {
            super(itemRequestListBinding.getRoot());
            this.binding = itemRequestListBinding;
            this.activity = appCompatActivity;
        }

        public void onBind(Request request) {
            binding.tvListOfItems.setText(String.format(binding.getRoot().getContext().getString(R.string.list_of_items_to_move), TextUtils.join(", ", request.getItemsToShift())));
            binding.tvDistance.setText(String.format(binding.getRoot().getContext().getString(R.string.estimated_distance), request.getDateAndTime()));
            binding.tvDateNTime.setText(String.format(binding.getRoot().getContext().getString(R.string.date_time_for_pick), request.getDateAndTime().split(Constants.DATE_TIME_SEPARATOR)[0], request.getDateAndTime().split(Constants.DATE_TIME_SEPARATOR)[1]));
            binding.tvStatus.setText(request.getStatus());
            if(request.getStatus().equals(Constants.APPROVED_REQUEST)){
                binding.tvRequestByUserName.setText(String.format(binding.getRoot().getContext().getString(R.string.approved_by), request.getAgencyDetails().getFullName()));
                binding.ivEmail.setVisibility(View.VISIBLE);
                binding.ivTrack.setVisibility(View.VISIBLE);
                binding.ivPhone.setVisibility(View.VISIBLE);
            }else{
                binding.tvRequestByUserName.setVisibility(View.GONE);
                binding.ivEmail.setVisibility(View.INVISIBLE);
                binding.ivPhone.setVisibility(View.INVISIBLE);
                binding.ivTrack.setVisibility(View.INVISIBLE);
            }
            binding.ivApprove.setVisibility(View.GONE);
            binding.ivReject.setVisibility(View.GONE);

            binding.ivEmail.setOnClickListener(view -> {
                if(request.getAgencyDetails()!=null){
                    openEmail(request.getAgencyDetails().getEmailId());
                }
            });

            binding.ivPhone.setOnClickListener(view -> {
                if(request.getAgencyDetails()!=null){
                    openCall(request.getAgencyDetails().getPhoneNumber());
                }
            });

        }

        public void openEmail(String email) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, email);
            activity.startActivity(intent);
        }

        public void openCall(String number) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            activity.startActivity(intent);
        }

    }

}
