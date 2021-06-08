package com.myappcompany.rajan.healthdepot.ui_user.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.myappcompany.rajan.healthdepot.R;

public class DashboardFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_user_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);

        return root;
    }
}