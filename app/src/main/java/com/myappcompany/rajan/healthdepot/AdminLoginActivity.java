package com.myappcompany.rajan.healthdepot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AdminLoginActivity extends AppCompatActivity {

    private ViewStub mViewStub;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private TextView mSwitchView;
    private Button mLoginButton;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;

    public static Intent newIntent(Context context) {
        return new Intent(context, AdminLoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        mViewStub = findViewById(R.id.login_view_stub);
        mViewStub.inflate();

        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(AdminLoginActivity.this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        mEmailEditText = findViewById(R.id.email_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mSwitchView = findViewById(R.id.switch_view);
        mSwitchView.setVisibility(View.GONE);
        mLoginButton = findViewById(R.id.submit_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

                    Toast.makeText(AdminLoginActivity.this, "All fields are required!", Toast.LENGTH_LONG).show();
                    return;
                }

                authorizeLogin(email, password);
            }
        });
    }

    private void authorizeLogin(String email, String password) {

        mProgressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(AdminLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }

                        if(task.isSuccessful()) {
                            updateUI();
                        }
                        else {
                            Toast.makeText(AdminLoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void updateUI() {
        Intent intent = AdminHomeActivity.newIntent(AdminLoginActivity.this);
        startActivity(intent);
    }
}