package com.example.mdaassn.modalclass;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mdaassn.R;
import com.example.mdaassn.ViewSubmissionActivity;

import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder> {

    List<ChildModalClass> childModalClassList;
    Context context;
    int parentPosition;
    List<ParentModalClass> parentModalClassList;

    public ChildAdapter(List<ChildModalClass> childModalClassList, Context context, int parentPosition, List<ParentModalClass> parentModalClassList) {
        this.childModalClassList = childModalClassList;
        this.context = context;
        this.parentPosition = parentPosition;
        this.parentModalClassList = parentModalClassList;
    }

    @Override
    public ChildAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.child_rv_layout, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChildAdapter.ViewHolder holder, int position) {

        if (childModalClassList.get(position).check.equals("true")){
            Drawable icon = context.getDrawable(R.drawable.ic_check);
            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
            holder.object.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
        }else{
            Drawable icon = context.getDrawable(R.drawable.ic_close);
            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
            holder.object.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
        }

        holder.object.setText(childModalClassList.get(position).teamName);

        holder.object.setOnClickListener(new View.OnClickListener() {
            int itemPosition = holder.getAdapterPosition();
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewSubmissionActivity.class);
                intent.putExtra("parentId", parentModalClassList.get(parentPosition).id);
                intent.putExtra("childId",childModalClassList.get(itemPosition).id );
                intent.putExtra("teamName",childModalClassList.get(itemPosition).teamName );
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return childModalClassList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView object;

        public ViewHolder(View itemView) {
            super(itemView);
            object = (TextView) itemView.findViewById(R.id.objData);
        }
    }
}
