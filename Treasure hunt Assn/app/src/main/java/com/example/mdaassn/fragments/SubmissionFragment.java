package com.example.mdaassn.fragments;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mdaassn.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;


public class SubmissionFragment extends Fragment {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    ImageView selectedImage;
    TextInputLayout name,des;
    Button submitBtn,logout;
    String id;
    private Bitmap selectedBitmap;
    Boolean noData = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_submission, container, false);

        selectedImage = root.findViewById(R.id.cam);
        submitBtn = root.findViewById(R.id.submitBtn);
        name=root.findViewById(R.id.name);
        des=root.findViewById(R.id.desc);

//        logout = getActivity().findViewById(R.id.userButton);
//        logout.setVisibility(View.GONE);

        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getString("docId");
            name.getEditText().setText(bundle.getString("title"));
            des.getEditText().setText(bundle.getString("description"));
        }

        selectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noData == true){
                    submitData();
                }else{
                    Toast.makeText(getActivity(), "Please Add Image For Submission", Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            dispatchTakePictureIntent();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }else {
                Toast.makeText(getContext(), "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){

            if (data != null) {
                selectedBitmap = (Bitmap) data.getExtras().get("data");
                if (selectedBitmap != null) {
                    selectedImage.setImageBitmap(selectedBitmap);
                    noData = true;
                } else {
                    Toast.makeText(getContext(), "Error: Captured image is null.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Error: No data received from camera.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void submitData() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // Create a storage reference
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String imageFileName = System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageRef.child("images/" + imageFileName);
        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "Upload failed: " + exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Upload successful");
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                         addToFirebase(downloadUrl);
                    }
                });
            }
        });

    }

    private void addToFirebase(String downloadUrl) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> submissionData = new HashMap<>();
        submissionData.put("userId",uid );
        submissionData.put("submission",downloadUrl );

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(id)
                .collection("submissions")
                .add(submissionData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "Objectives Created", Toast.LENGTH_SHORT).show();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference docRef = db.collection("users").document(id);

                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        List<String> temparray = (List<String>) document.get("submitted");
                                        temparray.add(uid);
                                        docRef.update("submitted", temparray);
                                    }
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error adding document", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}