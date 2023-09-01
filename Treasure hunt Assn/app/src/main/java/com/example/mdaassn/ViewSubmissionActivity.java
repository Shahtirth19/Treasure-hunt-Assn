package com.example.mdaassn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ViewSubmissionActivity extends AppCompatActivity {

    ImageView selectedImage;
    TextInputLayout name,des;
    Button submitBtn;
    String pid,cid,id;
    Boolean noData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_submission);

        selectedImage = findViewById(R.id.cam);
        submitBtn = findViewById(R.id.submitBtn);
        name=findViewById(R.id.name);
        des=findViewById(R.id.desc);

          pid = getIntent().getStringExtra("parentId");
          cid = getIntent().getStringExtra("childId");
          name.getEditText().setText(getIntent().getStringExtra("teamName"));

        getData();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noData){
                    submitData();
                }else{
                    Toast.makeText(ViewSubmissionActivity.this, "You Cannot Comment as submission not found", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getData() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(pid)
                .collection("submissions")
                .whereEqualTo("userId", cid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                des.getEditText().setText("Submission Not Found!");
                                noData = false;
                            }else{
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Glide.with(ViewSubmissionActivity.this)
                                            .load(document.getString("submission"))
                                            .centerCrop()
                                            .into(selectedImage);
                                    id = document.getId();
                                    des.getEditText().setText(document.getString("comment"));
//                                    Toast.makeText(ViewSubmissionActivity.this, id, Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else {
                            Toast.makeText(ViewSubmissionActivity.this, "Error loading data from Firebase.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void submitData() {

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("comment", des.getEditText().getText().toString());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(pid)
                .collection("submissions")
                .document(id)
                .update(updatedData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ViewSubmissionActivity.this, "Document updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewSubmissionActivity.this, "Error updating document", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}