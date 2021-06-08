package com.myappcompany.rajan.healthdepot.ui_doctor.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myappcompany.rajan.healthdepot.CodeScannerActivity;
import com.myappcompany.rajan.healthdepot.MedicalRecordActivity;
import com.myappcompany.rajan.healthdepot.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    public static final int REQUEST_CODE =  2;

    private ImageView mScanQrImage;
    private RadioGroup mRadioGroup;
    private RadioButton mEmailRadioButton;
    private RadioButton mPhoneRadioButton;
    private EditText mCredentialsEditText;
    private Button mSearchButton;

    private FirebaseFirestore db;
    private String mAadharNumber = null;
    private String mUid = null;
    private String mName;
    private String mGender;
    private int mYob;
    private String mAddress;
    private ProgressDialog mProgressDialog;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mAadharNumber = data.getStringExtra(CodeScannerActivity.AADHAR_NUMBER);
            mName = data.getStringExtra(CodeScannerActivity.NAME);
            mGender = data.getStringExtra(CodeScannerActivity.GENDER);
            mYob = data.getIntExtra(CodeScannerActivity.YOB, -1);
            mAddress = data.getStringExtra(CodeScannerActivity.ADDRESS);

            checkByAadhar(mAadharNumber);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_doctor_home, container, false);

        mScanQrImage = root.findViewById(R.id.scan_qr_image);
        mRadioGroup = root.findViewById(R.id.radio_button_group);
        mEmailRadioButton = root.findViewById(R.id.email_radio_button);
        mPhoneRadioButton = root.findViewById(R.id.phone_radio_button);
        mCredentialsEditText = root.findViewById(R.id.credentials_edit_text);
        mSearchButton = root.findViewById(R.id.search_button);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        db = FirebaseFirestore.getInstance();

        mScanQrImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = CodeScannerActivity.newIntent(getActivity(), false);
                startActivityForResult(i, REQUEST_CODE);
            }
        });

        mEmailRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    mCredentialsEditText.setHint("Email");
                    mCredentialsEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                }
            }
        });

        mPhoneRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    mCredentialsEditText.setHint("Mobile number");
                    mCredentialsEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                }
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int checkedId = mRadioGroup.getCheckedRadioButtonId();

                if(checkedId<0) {
                    Toast.makeText(getActivity(), "Check some option first!", Toast.LENGTH_LONG).show();
                    return;
                }

                String input = mCredentialsEditText.getText().toString();

                if(TextUtils.isEmpty(input)) {
                    Toast.makeText(getActivity(), "Fill the required field first!", Toast.LENGTH_LONG).show();
                    return;
                }

                View view = mRadioGroup.findViewById(checkedId);
                if(view.equals(mEmailRadioButton)) {
                    checkByEmail(input);
                }
                if(view.equals(mPhoneRadioButton)) {
                    checkByPhone(input);
                }
            }
        });

        return root;
    }

    public void checkByAadhar(String aadharNumber) {

        db.collection("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots) {

                            if(document.get("aadhar_number").equals(mAadharNumber)) {
                                mUid = document.getId();
                                break;
                            }
                        }

                        if(mUid!=null) {
                            updateUI();
                        }
                        else {
                            createUser();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void createUser() {

        Map<String, Object> map = new HashMap<>();
        map.put("name", mName);
        map.put("email", "na");
        map.put("mobile_number", "na");
        map.put("aadhar_number", mAadharNumber);
        map.put("gender", mGender);
        map.put("yob", mYob);
        map.put("address", mAddress);
        map.put("approved", false);

        db.collection("users")
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }

                        mUid = documentReference.getId();
                        updateUI();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void checkByEmail(String email) {

        final String input = email;

        db.collection("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        for(QueryDocumentSnapshot document : queryDocumentSnapshots) {

                            if((boolean)document.get("approved") && document.get("email").toString().equals(input)) {

                                mUid = document.getId();
                                updateUI();
                                return;
                            }
                        }

                        Toast.makeText(getActivity(), "Email ID not found in records!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void checkByPhone(String phone) {

        final String input = phone;

        db.collection("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        for(QueryDocumentSnapshot document : queryDocumentSnapshots) {

                            if((boolean)document.get("approved") && document.get("mobile_number").toString().equals(input)) {

                                mUid = document.getId();
                                updateUI();
                                return;
                            }
                        }

                        Toast.makeText(getActivity(), "Phone not found in records!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void updateUI() {
        Intent i = MedicalRecordActivity.newIntent(getActivity(), mUid);
        startActivity(i);
    }
}