package com.myappcompany.rajan.healthdepot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SignupFormFragment extends Fragment {

    private static final int REQUEST_CODE = 1;

    private static boolean login = true;
    private boolean isDoctor = false;

    private TextView mSwitchView;
    private ViewStub mViewStub;
    private TextView mHeaderTextView;
    private Button mSubmitButton;
    private EditText mMobileNumberEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mImrEditText;
    private TextView mForgotPasswordTextView;
    private ImageView mScanQrImage;
    private ProgressDialog mProgressDialog;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private String mAadharNumber = null;
    private String mName = null;
    private String mGender = null;
    private int mYob = -1;
    private String mAddress = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mAadharNumber = data.getStringExtra(CodeScannerActivity.AADHAR_NUMBER);
            mName = data.getStringExtra(CodeScannerActivity.NAME);
            mGender = data.getStringExtra(CodeScannerActivity.GENDER);
            mYob = data.getIntExtra(CodeScannerActivity.YOB, -1);
            mAddress = data.getStringExtra(CodeScannerActivity.ADDRESS);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        isDoctor = getArguments().getBoolean("isDoctor", false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        final View v = inflater.inflate(R.layout.fragment_signup_form, container, false);
        mViewStub = v.findViewById(R.id.student_view_stub);

        if(login) {
            mViewStub.setLayoutResource(R.layout.layout_login);
        }
        else {

            if(isDoctor) {
                mViewStub.setLayoutResource(R.layout.layout_doctor_register);
            }
            else {
                mViewStub.setLayoutResource(R.layout.layout_user_register);
            }
        }
        mViewStub.inflate();

        mSwitchView = v.findViewById(R.id.switch_view);
        mHeaderTextView = v.findViewById(R.id.header_text_view);
        mSubmitButton = v.findViewById(R.id.submit_button);

        if(isDoctor) {
            mHeaderTextView.setText("Doctor");
        }
        else {
            mHeaderTextView.setText("User");
        }

        if(login) {
            initLoginView(v);
        }
        else {
            if(isDoctor) {
                initDoctorRegisterView(v);
            }
            else {
                initUserRegisterView(v);
            }

            mScanQrImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = CodeScannerActivity.newIntent(getActivity(), true);
                    startActivityForResult(i, REQUEST_CODE);
                }
            });
        }

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isDoctor && login) {
                    doctorLogin();
                }
                else if(isDoctor && !login) {
                    doctorRegister();
                }
                else if(!isDoctor && login) {
                    userLogin();
                }
                else if(!isDoctor && !login) {
                    userRegister();
                }
            }
        });

        mSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(login) {
                    login = false;
                }
                else {
                    login = true;
                }
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment fragment = new SignupFormFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isDoctor", isDoctor);
                fragment.setArguments(bundle);
                ft.replace(R.id.fragment_container, fragment);
                ft.commit();
            }
        });
        return v;
    }

    private void initLoginView(View v) {
        mEmailEditText = v.findViewById(R.id.email_edit_text);
        mPasswordEditText = v.findViewById(R.id.password_edit_text);
        mForgotPasswordTextView = v.findViewById(R.id.forgot_password_text_view);
    }

    private void initUserRegisterView(View v) {
        mMobileNumberEditText = v.findViewById(R.id.mobile_number_edit_text);
        mEmailEditText = v.findViewById(R.id.email_edit_text);
        mPasswordEditText = v.findViewById(R.id.password_edit_text);
        mScanQrImage = v.findViewById(R.id.scan_qr_image);
    }

    private void initDoctorRegisterView(View v) {
        initUserRegisterView(v);
        mImrEditText = v.findViewById(R.id.imr_edit_text);
    }

    private void doctorLogin() {
        String path = "doctors";
        login(path);
    }

    private void doctorRegister() {
        String path = "doctors";
        register(path);
    }

    private void userLogin() {
        String path = "users";
        login(path);
    }

    private void userRegister() {
        String path = "users";
        register(path);
    }

    private void login(String path) {
        final String email = mEmailEditText.getText().toString();
        final String password = mPasswordEditText.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "Both fields are mandatory!", Toast.LENGTH_LONG).show();
            return;
        }

        mProgressDialog.show();

        db.collection(path)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }

                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                if(document.get("email").equals(email)) {

                                    if((boolean)document.get("approved")) {
                                        authorizeLogin(email, password);
                                    }
                                    else {
                                        Toast.makeText(getActivity(), "Profile not approved yet!", Toast.LENGTH_LONG).show();
                                    }
                                    return;
                                }
                            }
                            Toast.makeText(getActivity(), "Invalid Credentials!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void authorizeLogin(String email, String password) {

        mProgressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }

                        if(task.isSuccessful()) {
                            mUser = mAuth.getCurrentUser();
                            updateUI();
                        }
                        else {
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void register(String path) {

        final String location = path;
        final String mobileNumber = mMobileNumberEditText.getText().toString();

        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if(TextUtils.isEmpty(mobileNumber) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "All fields are mandatory!", Toast.LENGTH_LONG);
            return;
        }

        if(path.equals("doctors")) {
            final String imrNumber = mImrEditText.getText().toString();

            if(TextUtils.isEmpty(imrNumber)) {
                Toast.makeText(getActivity(), "All fields are mandatory!", Toast.LENGTH_LONG);
                return;
            }
        }

        if(TextUtils.isEmpty(mAadharNumber)) {
            Toast.makeText(getActivity(), "Please scan aadhar QR code!", Toast.LENGTH_LONG).show();
            return;
        }

        mProgressDialog.show();

        checkConstraints(location, email, password, mobileNumber);
    }

    private void checkConstraints(String path, String email, String password, String mobileNumber) {

        db.collection(path)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()) {

                            boolean flagUpdateOnly = false;
                            String documentUid = null;

                            for(QueryDocumentSnapshot document : task.getResult()) {

                                String fetchedEmail = (String)document.get("email");
                                String fetchedMobileNumber = (String)document.get("mobile_number");
                                String fetchedAadharNumber = (String)document.get("aadhar_number");
                                boolean isApproved = (boolean)document.get("approved");

                                if(fetchedEmail.equals(email)) {
                                    Toast.makeText(getActivity(), "Email already in use! Try another", Toast.LENGTH_LONG).show();
                                    if(mProgressDialog.isShowing()) {
                                        mProgressDialog.dismiss();
                                    }
                                    return;
                                }

                                if(fetchedMobileNumber.equals(mobileNumber)) {
                                    Toast.makeText(getActivity(), "Mobile number already in use! Try another", Toast.LENGTH_LONG).show();
                                    if(mProgressDialog.isShowing()) {
                                        mProgressDialog.dismiss();
                                    }
                                    return;
                                }

                                if(fetchedAadharNumber.equals(mAadharNumber)) {

                                    if(isApproved) {
                                        Toast.makeText(getActivity(), "Aadhar number already in use! Try another", Toast.LENGTH_LONG).show();
                                        if (mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                        }
                                        return;
                                    }
                                    else {
                                        flagUpdateOnly = true;
                                        documentUid = document.getId();
                                    }
                                }
                            }

                            authenticate(path, email, password, flagUpdateOnly, documentUid);
                        }


                    }
                });
    }

    private void authenticate(String path, String email, String password, boolean updateOnly, String documentUid) {

        final String location = path;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }

                        if(task.isSuccessful()) {
                            mUser = mAuth.getCurrentUser();
                            registerDetails(location, updateOnly, documentUid);
                            //updateUI();
                        }
                        else {
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void registerDetails(String path, boolean updateOnly, String documentUid) {

        final String email = mEmailEditText.getText().toString();
        final String mobileNumber = mMobileNumberEditText.getText().toString();


        Map<String, Object> map = new HashMap<>();
        map.put("name", mName);
        map.put("email", email);
        map.put("mobile_number", mobileNumber);
        map.put("aadhar_number", mAadharNumber);
        map.put("gender", mGender);
        map.put("yob", mYob);
        map.put("address", mAddress);



        if(path.equals("doctors")) {
            map.put("approved", false);
            map.put("imr_number", mImrEditText.getText().toString());
        }
        else {
            map.put("approved", true);
        }

        mProgressDialog.show();

        if(updateOnly) {
            db.collection(path)
                    .document(documentUid)
                    .set(map, SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }

                            Toast.makeText(getActivity(), "Account creation successful!", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            if(mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        else {
            db.collection(path)
                    .add(map)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            if (mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }

                            Toast.makeText(getActivity(), "Account creation successful!", Toast.LENGTH_LONG).show();

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
    }

    private void updateUI() {

        if(mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        Intent i;
        if(isDoctor) {
            i = DoctorHomeActivity.newIntent(getActivity());
            //Toast.makeText(getActivity(), "Doctor", Toast.LENGTH_LONG).show();
        }
        else {
            i = UserHomeActivity.newIntent(getActivity());
            //Toast.makeText(getActivity(), "User", Toast.LENGTH_LONG).show();
        }
        startActivity(i);
    }
}