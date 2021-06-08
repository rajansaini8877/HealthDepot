package com.myappcompany.rajan.healthdepot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EntryActivity extends AppCompatActivity {

    private Button mDoctorButton;
    private Button mUserButton;
    private Button mAdminButton;

    public static Intent newIntent(Context context) {
        return new Intent(context, EntryActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        mDoctorButton = findViewById(R.id.doctor_button);
        mUserButton = findViewById(R.id.user_button);
        mAdminButton = findViewById(R.id.admin_button);

        mDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(DoctorLoginActivity.newIntent(EntryActivity.this));
            }
        });

        mUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(UserLoginActivity.newIntent(EntryActivity.this));
            }
        });

        mAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AdminLoginActivity.newIntent(EntryActivity.this));
            }
        });
    }
}