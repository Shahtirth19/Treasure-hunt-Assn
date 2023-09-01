package com.example.mdaassn.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdaassn.R;
import com.example.mdaassn.modalclass.ChildModalClass;
import com.example.mdaassn.modalclass.ParentAdapter;
import com.example.mdaassn.modalclass.ParentModalClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ProgressFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ParentModalClass> parentModalClassArrayList;
    ParentAdapter parentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_progress, container, false);

        recyclerView = root.findViewById(R.id.rv_parent);

        parentModalClassArrayList = new ArrayList<>();

        getFirebaseData();

        parentAdapter = new ParentAdapter(parentModalClassArrayList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(parentAdapter);
        parentAdapter.notifyDataSetChanged();

        return root;
    }

    private void getFirebaseData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String temp = document.getString("title");
                                String docId = document.getId();
                                List<Object> subArray = (List<Object>) document.get("submitted");


                                db.collection("teams")
                                        .orderBy("teamName")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    ArrayList<ChildModalClass> childList = new ArrayList<>();

                                                    for (QueryDocumentSnapshot subdocument : task.getResult()) {
                                                        boolean containsid = subArray.contains(subdocument.getId());
                                                        if (containsid) {
                                                            childList.add(new ChildModalClass(subdocument.getString("teamName"),subdocument.getId(),"true"));
                                                        } else {
                                                            childList.add(new ChildModalClass(subdocument.getString("teamName"),subdocument.getId(),"false"));
                                                        }
                                                    }
                                                    parentModalClassArrayList.add(new ParentModalClass(temp,docId,childList));
                                                    parentAdapter.notifyDataSetChanged();
                                                } else {
                                                    Toast.makeText(getContext(), "Error loading data from Firebase.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(getContext(), "Error loading data from Firebase.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}