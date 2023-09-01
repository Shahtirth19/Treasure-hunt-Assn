package com.example.mdaassn.modalclass;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdaassn.R;
import com.example.mdaassn.fragments.SubmissionFragment;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewHolder> {

    List<UserModalClass> userModalClassList;
    Context context;

    public UserAdapter(List<UserModalClass> userModalClassList, Context context) {
        this.userModalClassList = userModalClassList;
        this.context = context;
    }

    @Override
    public UserAdapter.viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_layout, null, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserAdapter.viewHolder holder, int position) {

        holder.title.setText(userModalClassList.get(position).title);
        holder.description.setText(userModalClassList.get(position).description);

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = holder.getAdapterPosition();

                SubmissionFragment submissionFragment = new SubmissionFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title", userModalClassList.get(itemPosition).title);
                bundle.putString("description",userModalClassList.get(itemPosition).description );
                bundle.putString("docId",userModalClassList.get(itemPosition).id );
                submissionFragment.setArguments(bundle);

                // Get the FragmentManager
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.user_Activity, submissionFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return userModalClassList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView title,description;

        public viewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.userTitle);
            description = (TextView) itemView.findViewById(R.id.userDesc);
        }
    }
}
