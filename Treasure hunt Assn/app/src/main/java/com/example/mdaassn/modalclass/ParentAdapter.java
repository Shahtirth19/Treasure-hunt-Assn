package com.example.mdaassn.modalclass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdaassn.R;

import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ViewHolder> {

    public ParentAdapter(List<ParentModalClass> parentModalClassList, Context context) {
        this.parentModalClassList = parentModalClassList;
        this.context = context;
    }

    List<ParentModalClass> parentModalClassList;
    Context context;


    @Override
    public ParentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.parent_rv_layout, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ParentAdapter.ViewHolder holder, int position) {
        holder.parentTitle.setText(parentModalClassList.get(position).title);

        ChildAdapter childAdapter;
        childAdapter = new ChildAdapter(parentModalClassList.get(position).childModalClassList,context,position,parentModalClassList);
        holder.rvChild.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false));
        holder.rvChild.setAdapter(childAdapter);
        childAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return parentModalClassList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView rvChild;
        TextView parentTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            rvChild = (RecyclerView) itemView.findViewById(R.id.rv_child);
            parentTitle = (TextView) itemView.findViewById(R.id.tv_parent_title);
        }
    }
}
