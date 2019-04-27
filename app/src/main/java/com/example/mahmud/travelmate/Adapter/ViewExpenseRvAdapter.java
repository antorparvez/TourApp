package com.example.mahmud.travelmate.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mahmud.travelmate.POJO.Expense;
import com.example.mahmud.travelmate.R;

import java.util.ArrayList;

public class ViewExpenseRvAdapter extends RecyclerView.Adapter<ViewExpenseRvAdapter.AllExpenseVH>{
    private Context context;
    private ArrayList<Expense> expenses;

    public ViewExpenseRvAdapter(Context context, ArrayList<Expense> expenses) {
        this.context = context;
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public AllExpenseVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_expense_view_layout,viewGroup,false);
        return new AllExpenseVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AllExpenseVH allExpenseVH, int i) {
        allExpenseVH.mExpenseName.setText("Expense Name : "+expenses.get(i).getName());
        allExpenseVH.mExpense.setText(String.valueOf("Expense : "+expenses.get(i).getCost()));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }


    public class AllExpenseVH extends RecyclerView.ViewHolder{
        TextView mExpenseName,mExpense;
        public AllExpenseVH(@NonNull View itemView) {
            super(itemView);
            mExpenseName = itemView.findViewById(R.id.display_expense_name_single);
            mExpense = itemView.findViewById(R.id.display_expense_single);
        }
    }

}
