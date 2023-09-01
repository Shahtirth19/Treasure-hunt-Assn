package com.example.mdaassn.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mdaassn.LoginActivity;
import com.example.mdaassn.Objective;
import com.example.mdaassn.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdminFragment extends Fragment {

    LinearLayout layoutList;
    Button buttonAdd, buttonSubmitList, buttonSub,btnLogOut;
    TextView textView1;
    TextInputEditText textView2;
    int count = 0;
    ArrayList<Objective> objectiveArrayList = new ArrayList<>();
    Map<String, Object> user = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_admin, container, false);

        btnLogOut = root.findViewById(R.id.button);
        layoutList = root.findViewById(R.id.layout_list);
        buttonAdd = root.findViewById(R.id.button1);
        buttonSub = root.findViewById(R.id.button2);
        buttonSubmitList = root.findViewById(R.id.button_submit_list);
        textView1 = root.findViewById(R.id.textView1);
        textView2 = root.findViewById(R.id.textView2);

        textView2.setText(String.valueOf(count));


        btnLogOut.setOnClickListener(view ->{
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "User logged out successfully", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });
        buttonAdd.setOnClickListener(view ->{
            addView(++count);
            textView2.setText(String.valueOf(count));
        });
        buttonSubmitList.setOnClickListener(view ->{

            if(checkIfValid()){
                List<Object> blankArray = new ArrayList<>();

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                for (int i = 0; i < objectiveArrayList.size(); i++) {
                    Objective objective = objectiveArrayList.get(i);
                    user.put("date",System.currentTimeMillis());
                    user.put("title", objective.getTitle());
                    user.put("objectives", objective.getObj());
                    user.put("submitted", blankArray);

                    int temp = i;
                    db.collection("users")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    if(temp+1 == objectiveArrayList.size()){
                                        layoutList.removeAllViews(); // Clear all child views
                                        count = 0;
                                        Toast.makeText(getContext(), "Objectives Created", Toast.LENGTH_SHORT).show();
                                    }
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
        });

        return root;
    }

    private boolean checkIfValid() {

        objectiveArrayList.clear();
        boolean result = true;

        for(int i=0;i<layoutList.getChildCount();i++){

            View objView = layoutList.getChildAt(i);

            EditText editTextTitle = (EditText)objView.findViewById(R.id.formTitle);
            EditText editTextName = (EditText)objView.findViewById(R.id.objective);

            Objective objective = new Objective();

            if(!editTextName.getText().toString().equals("")){
                objective.setTitle(editTextTitle.getText().toString());
                objective.setObj(editTextName.getText().toString());
            }else {
                result = false;
                break;
            }


            objectiveArrayList.add(objective);
        }

        if(objectiveArrayList.size()==0){
            result = false;
            Toast.makeText(getContext(), "Add Objective First!", Toast.LENGTH_SHORT).show();
        }else if(!result){
            Toast.makeText(getContext(), "Enter All Details Correctly!", Toast.LENGTH_SHORT).show();
        }


        return result;
    }

    private void addView(int i) {
        final View formView = getLayoutInflater().inflate(R.layout.form_layout,null,false);

        EditText editText = (EditText)formView.findViewById(R.id.objective);
        TextView textView = (TextView) formView.findViewById(R.id.dispBlock);

        textView.setText("Objective "+i);
        Button delForm = (Button) formView.findViewById(R.id.btnDelete);


        delForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutList.removeView(formView);
                count--;
                textView2.setText(String.valueOf(count));
            }
        });

        buttonSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count == 1){
                    layoutList.removeAllViews(); // Clear all child views
                    count = 0;
                    textView2.setText(String.valueOf(count));
                }else{
                    layoutList.removeView(formView);
                    count--;
                    textView2.setText(String.valueOf(count));
                }
            }
        });

        layoutList.addView(formView);
    }

}