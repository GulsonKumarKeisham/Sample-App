package com.test.testapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class EmployeeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<String[]> employeeList;
    public int selectedItemPosition=-1;

    EmployeeListAdapter(Context context, ArrayList<String[]> employeeList) {
        this.context = context;
        this.employeeList = employeeList;
    }

    /**
     * method to notify for highlighting textView for search result
     * @param pos
     */

    void notify(int pos){
        selectedItemPosition = pos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.employee_item_layout, viewGroup, false);
        return new EmployeeItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        String[] currentEmployee = employeeList.get(i);
        EmployeeItemViewHolder currentHolder = (EmployeeItemViewHolder) viewHolder;
        currentHolder.employeeNameTv.setText(currentEmployee[0].trim());
        currentHolder.designationTv.setText(currentEmployee[1].trim());
        if (i==selectedItemPosition){
            currentHolder.employeeNameTv.setTextColor(Color.BLUE);
        }else {
            currentHolder.employeeNameTv.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return employeeList == null ? 0 : employeeList.size();
    }

    class EmployeeItemViewHolder extends RecyclerView.ViewHolder{
        private TextView employeeNameTv, designationTv;
        private ImageView img;
        EmployeeItemViewHolder(@NonNull View itemView) {
            super(itemView);
            employeeNameTv = itemView.findViewById(R.id.employeeNameTv);
            designationTv = itemView.findViewById(R.id.designationTv);
            img = itemView.findViewById(R.id.img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, EmployeeDetailsActivity.class);
                    intent.putExtra("employee_details", employeeList.get(getAdapterPosition()));
                    android.support.v4.util.Pair<View, String> pair1 = android.support.v4.util.Pair.create((View) employeeNameTv, context.getResources().getString(R.string.transition_emp_name));
                    android.support.v4.util.Pair<View, String> pair2 = Pair.create((View)img, context.getResources().getString(R.string.transition_dp));
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, pair1, pair2);
                    context.startActivity(intent, options.toBundle());
                }
            });
        }
    }
}
