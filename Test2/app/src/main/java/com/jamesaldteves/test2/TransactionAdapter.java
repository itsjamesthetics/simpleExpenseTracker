package com.jamesaldteves.test2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/** @noinspection ALL*/
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<TransactionModel> transactionList;

    public TransactionAdapter(List<TransactionModel> transactionList) {
        this.transactionList = transactionList;
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        TransactionModel transaction = transactionList.get(position);
        holder.amount.setText(transaction.getAmount());
        holder.category.setText(transaction.getCategory());
        holder.date.setText(transaction.getDate());
        holder.note.setText(transaction.getNote());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView amount, category, date, note;

        TransactionViewHolder(View view) {
            super(view);
            amount = view.findViewById(R.id.tvAmount);
            category = view.findViewById(R.id.tvCategory);
            date = view.findViewById(R.id.tvDate);
            note = view.findViewById(R.id.tvNote);
        }
    }
}
