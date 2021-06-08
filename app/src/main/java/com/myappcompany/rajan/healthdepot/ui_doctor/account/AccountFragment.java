package com.myappcompany.rajan.healthdepot.ui_doctor.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myappcompany.rajan.healthdepot.EntryActivity;
import com.myappcompany.rajan.healthdepot.R;

public class AccountFragment extends Fragment {

    private TextView mEmailTextView;
    private Button mLogoutButton;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_doctor_account, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mEmailTextView = root.findViewById(R.id.email_text_view);
        mLogoutButton = root.findViewById(R.id.logout_button);

        mEmailTextView.setText(mUser.getEmail());

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), EntryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish(); // if the activity running has it's own context

            }
        });

        return root;
    }
}