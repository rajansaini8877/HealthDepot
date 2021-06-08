package com.myappcompany.rajan.healthdepot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.myappcompany.rajan.healthdepot.model.MyRecordListItemAdapter;
import com.myappcompany.rajan.healthdepot.model.RecordComparator;
import com.myappcompany.rajan.healthdepot.model.RecordItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicalRecordActivity extends AppCompatActivity {

    private static final String UID = "uid";

    private TextView mNameTextView;
    private TextView mGenderTextView;
    private TextView mAgeTextView;
    private TextView mAddressTextView;
    private Button mAddRecordButton;

    private RecyclerView mRecyclerView;
    private FirebaseFirestore db;

    private String mUid = null;
    private List<RecordItem> mRecords;

    public static Intent newIntent(Context context, String uid) {
        Intent i = new Intent(context, MedicalRecordActivity.class);
        i.putExtra(UID, uid);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_record);

        mUid = getIntent().getStringExtra(UID);

        if(mUid==null) {
            finish();
        }

        initializeViews();

        fetchUserData();

        setListeners();
    }

    public void initializeViews() {

        mNameTextView = findViewById(R.id.name_text_view);
        mGenderTextView = findViewById(R.id.gender_text_view);
        mAgeTextView = findViewById(R.id.age_text_view);
        mAddressTextView = findViewById(R.id.address_text_view);
        mAddRecordButton = findViewById(R.id.add_record_button);

        mRecyclerView = findViewById(R.id.list);
        db = FirebaseFirestore.getInstance();
        mRecords = new ArrayList<>();
    }

    public void fetchUserData() {

        db.collection("users")
                .document(mUid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        mNameTextView.setText(mNameTextView.getText().toString()+ documentSnapshot.get("name").toString());
                        mGenderTextView.setText(mGenderTextView.getText().toString() + documentSnapshot.get("gender").toString());
                        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                        mAgeTextView.setText(mAgeTextView.getText().toString() + String.valueOf((long)currentYear - (long)documentSnapshot.get("yob")));
                        mAddressTextView.setText(mAddressTextView.getText().toString() + documentSnapshot.get("address").toString());

                        fetchRecord();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(MedicalRecordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    public void fetchRecord() {

        db.collection("users/"+mUid+"/record")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        mRecords.clear();

                        for(QueryDocumentSnapshot document : queryDocumentSnapshots) {

                            Timestamp timestamp = (Timestamp) document.get("timestamp");
                            String clinic = (String) document.get("clinic");
                            String disease = (String) document.get("disease");
                            String remarks = (String) document.get("remarks");

                            RecordItem recordItem = new RecordItem(timestamp, clinic, disease, remarks);
                            mRecords.add(recordItem);

                        }

                        Collections.sort(mRecords, new RecordComparator());
                        mRecyclerView.setAdapter(new MyRecordListItemAdapter(mRecords));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(MedicalRecordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    public void setListeners() {

        mAddRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MedicalRecordActivity.this);
                dialogBuilder.setMessage("Enter the required credentials and click submit");
                dialogBuilder.setCancelable(false);

                View view = View.inflate(MedicalRecordActivity.this, R.layout.dialog_add_record, null);
                final EditText clinicEditText = view.findViewById(R.id.clinic_edit_text);
                final EditText diseaseEditText = view.findViewById(R.id.disease_edit_text);
                final EditText remarksEditText = view.findViewById(R.id.remarks_edit_text);

                dialogBuilder.setView(view);
                dialogBuilder.setPositiveButton("Submit", null);
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String clinic = clinicEditText.getText().toString();
                        String disease = diseaseEditText.getText().toString();
                        String remarks = remarksEditText.getText().toString();

                        if(TextUtils.isEmpty(clinic) || TextUtils.isEmpty(disease) || TextUtils.isEmpty(remarks)) {
                            Toast.makeText(MedicalRecordActivity.this, "All fields are required!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        addRecord(dialog, clinic, disease, remarks);
                    }
                });
            }
        });
    }

    public void addRecord(AlertDialog dialog, String clinic, String disease, String remarks) {

        Map<String, Object> map = new HashMap<>();
        map.put("clinic", clinic);
        map.put("disease", disease);
        map.put("remarks", remarks);
        map.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users/"+mUid+"/record")
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Toast.makeText(MedicalRecordActivity.this, "Record added successfully!", Toast.LENGTH_LONG).show();
                        fetchRecord();
                        dialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {

                        Toast.makeText(MedicalRecordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}