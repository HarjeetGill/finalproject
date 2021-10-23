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
import com.techai.shiftme.utils.Constants;

import java.util.ArrayList;

public class RequestsListAdapter extends RecyclerView.Adapter<RequestsListAdapter.ItemViewHolder> {

    public ArrayList<Request> items = new ArrayList<>();
    public AppCompatActivity activity;

    public RequestsListAdapter(AppCompatActivity appCompatActivity) {
        activity = appCompatActivity;
    }

    public void setItemList(ArrayList<Request> list) {
        items = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestsListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRequestListBinding binding = ItemRequestListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RequestsListAdapter.ItemViewHolder(binding, activity);
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

        public ItemViewHolder(@NonNull ItemRequestListBinding itemRequestListBinding, AppCompatActivity appCompatActivity) {
            super(itemRequestListBinding.getRoot());
            this.binding = itemRequestListBinding;
            this.activity = appCompatActivity;
        }

        public void onBind(Request request) {
            binding.tvListOfItems.setText(String.format(binding.getRoot().getContext().getString(R.string.list_of_items_to_move), TextUtils.join(", ", request.getItemsToShift())));
            binding.tvDistance.setText(String.format(binding.getRoot().getContext().getString(R.string.estimated_distance), request.getDateAndTime()));
            binding.tvDateNTime.setText(String.format(binding.getRoot().getContext().getString(R.string.date_time_for_pick), request.getDateAndTime().split(Constants.DATE_TIME_SEPARATOR)[0], request.getDateAndTime().split(Constants.DATE_TIME_SEPARATOR)[1]));
            binding.tvRequestByUserName.setText(String.format(binding.getRoot().getContext().getString(R.string.request_by), request.getDateAndTime()));
            binding.tvStatus.setText(request.getStatus());

            binding.ivEmail.setOnClickListener(view -> {
                openEmail("");
            });

            binding.ivPhone.setOnClickListener(view -> {
                openCall("");
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
