package com.techai.shiftme.ui.customer.sendrequest;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techai.shiftme.databinding.ItemAddedBinding;

import java.util.ArrayList;
import java.util.List;


public class AddItemsAdapter extends RecyclerView.Adapter<AddItemsAdapter.ItemViewHolder> {
    private List<String> itemList = new ArrayList<>();

    @NonNull
    @Override
    public AddItemsAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAddedBinding binding = ItemAddedBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddItemsAdapter.ItemViewHolder holder, int position) {
        holder.binding.tvItemName.setText(itemList.get(position));
        holder.binding.ivClose.setOnClickListener(view -> onRemoveClick(itemList.get(position)));
    }

    public void setItemList(List<String> itemList){
        this.itemList=itemList;
         notifyDataSetChanged();
    }

    public void onRemoveClick(String item){
        if(!itemList.isEmpty()) {
            itemList.remove(item);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemAddedBinding binding;
        public ItemViewHolder(@NonNull ItemAddedBinding itemAddedBinding) {
            super(itemAddedBinding.getRoot());
            this.binding = itemAddedBinding;
        }
    }
}
