package com.jamesaldteves.test2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends  RecyclerView.Adapter<CategoryAdapter.ViewHolder> {


    private final List<CategoryModel> categoryList;

    private OnCategoryClickListener clickListener;
    public interface OnCategoryClickListener {
        void onCategoryClicked(CategoryModel category);
    }


    public CategoryAdapter(List<CategoryModel> categoryList, OnCategoryClickListener clickListener) {
        this.categoryList = categoryList;
        this.clickListener = clickListener;
    }

    public CategoryAdapter(List<CategoryModel> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel category = categoryList.get(position);

        if (category != null) {
            holder.categoryName.setText(category.getName() != null ? category.getName() : "N/A");
            holder.dailyBudget.setText(category.getDailyBudget() != null ? category.getDailyBudget() : "N/A");
            holder.weeklyBudget.setText(category.getWeeklyBudget() != null ? category.getWeeklyBudget() : "N/A");
            holder.monthlyBudget.setText(category.getMonthlyBudget() != null ? category.getMonthlyBudget() : "N/A");
            holder.yearlyBudget.setText(category.getYearlyBudget() != null ? category.getYearlyBudget() : "N/A");
            holder.categoryNote.setText(category.getCategoryViewNote() != null ? category.getCategoryViewNote() : "N/A");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onCategoryClicked(category);
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName, dailyBudget, weeklyBudget, monthlyBudget, yearlyBudget,categoryNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            dailyBudget = itemView.findViewById(R.id.categoryDailyBudget);
            weeklyBudget = itemView.findViewById(R.id.categoryWeeklyBudget);
            monthlyBudget = itemView.findViewById(R.id.categoryMonthlyBudget);
            yearlyBudget = itemView.findViewById(R.id.categoryYearly);
            categoryNote = itemView.findViewById(R.id.categoryNote);
        }
    }
}
