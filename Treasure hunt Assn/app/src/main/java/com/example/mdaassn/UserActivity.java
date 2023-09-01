package com.example.mdaassn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdaassn.modalclass.UserAdapter;
import com.example.mdaassn.modalclass.UserModalClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<UserModalClass> userModalClassArrayList;
    UserAdapter userAdapter;
    Button btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        recyclerView = findViewById(R.id.userView);
        btnLogOut = findViewById(R.id.userButton);

        userModalClassArrayList = new ArrayList<>();

        getFirebaseData();

        userAdapter = new UserAdapter(userModalClassArrayList, UserActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(UserActivity.this));
        recyclerView.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();

        btnLogOut.setOnClickListener(view ->{
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(UserActivity.this, "User logged out successfully", Toast.LENGTH_LONG).show();
            startActivity(new Intent(UserActivity.this, LoginActivity.class));
            UserActivity.this.finish();
        });
    }
    private void getFirebaseData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean itemsAdded = false;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<Object> subArray = (List<Object>) document.get("submitted");
                                boolean containsid = subArray.contains(uid);
                                if (!containsid) {
                                    userModalClassArrayList.add(new UserModalClass(document.getString("title"),document.getString("objectives"),document.getId()));
                                    itemsAdded = true;
                                }
                            }

                            if (!itemsAdded) {
                                userModalClassArrayList.add(new UserModalClass("No Objectives Found", "", "1"));
                            }
                            userAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(UserActivity.this, "Error loading data from Firebase.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

