package com.myappcompany.rajan.healthdepot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myappcompany.rajan.healthdepot.model.DoctorItem;
import com.myappcompany.rajan.healthdepot.model.MyDoctorListItemAdapter;
import com.myappcompany.rajan.healthdepot.model.RecordItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FirebaseFirestore db;
    private List<DoctorItem> mRecords;

    public static Intent newIntent(Context context) {
        Intent i = new Intent(context, AdminHomeActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        mRecyclerView = findViewById(R.id.list);
        mRecords = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        //fetchData();
    }

    public void fetchData() {

        db.collection("doctors")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        mRecords.clear();

                        for(QueryDocumentSnapshot document : queryDocumentSnapshots) {

                            if(!(boolean)document.get("approved")) {

                                String name = document.get("name").toString();
                                String email = document.get("email").toString();
                                String aadharNumber = document.get("aadhar_number").toString();
                                String imrNumber = document.get("imr_number").toString();

                                DoctorItem doctorItem = new DoctorItem(document.getId(), name, email, aadharNumber, imrNumber);
                                mRecords.add(doctorItem);
                            }
                        }

                        mRecyclerView.setAdapter(new MyDoctorListItemAdapter(mRecords, AdminHomeActivity.this));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {

                        Toast.makeText(AdminHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
    }
}