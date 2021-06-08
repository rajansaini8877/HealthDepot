package com.myappcompany.rajan.healthdepot.ui_user.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myappcompany.rajan.healthdepot.R;
import com.myappcompany.rajan.healthdepot.model.MyRecordListItemAdapter;
import com.myappcompany.rajan.healthdepot.model.RecordComparator;
import com.myappcompany.rajan.healthdepot.model.RecordItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewStub mViewStub;
    private TextView mNameTextView;
    private TextView mGenderTextView;
    private TextView mAgeTextView;
    private TextView mAddressTextView;
    private Button mAddRecordButton;

    private RecyclerView mRecyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUid = null;

    private List<RecordItem> mRecords;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_user_home, container, false);

        mViewStub = root.findViewById(R.id.user_view_stub);
        mViewStub.inflate();

        initializeViews(root);

        fetchUserData();

        return root;
    }

    public void initializeViews(View v) {

        mNameTextView = v.findViewById(R.id.name_text_view);
        mGenderTextView = v.findViewById(R.id.gender_text_view);
        mAgeTextView = v.findViewById(R.id.age_text_view);
        mAddressTextView = v.findViewById(R.id.address_text_view);
        mAddRecordButton = v.findViewById(R.id.add_record_button);
        mAddRecordButton.setVisibility(View.GONE);

        mRecyclerView = v.findViewById(R.id.list);
        db = FirebaseFirestore.getInstance();
        mRecords = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    public void fetchUserData() {

        db.collection("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot document : queryDocumentSnapshots) {

                            if((boolean)document.get("approved") && document.get("email").toString().equals(mUser.getEmail())) {

                                mNameTextView.setText(mNameTextView.getText().toString()+ document.get("name").toString());
                                mGenderTextView.setText(mGenderTextView.getText().toString() + document.get("gender").toString());
                                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                                mAgeTextView.setText(mAgeTextView.getText().toString() + String.valueOf((long)currentYear - (long)document.get("yob")));
                                mAddressTextView.setText(mAddressTextView.getText().toString() + document.get("address").toString());

                                mUid = document.getId();
                                fetchRecord();
                                return;
                            }
                        }

                        Toast.makeText(getActivity(), "Some error occurred!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}