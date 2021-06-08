package com.myappcompany.rajan.healthdepot.model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myappcompany.rajan.healthdepot.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDoctorListItemAdapter extends RecyclerView.Adapter<MyDoctorListItemAdapter.MyViewHolder> {

    private final List<DoctorItem> mValues;
    private Context mContext;
    private FirebaseFirestore db;

    public MyDoctorListItemAdapter(List<DoctorItem> items, Context context) {
        mValues = items;
        mContext = context;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_doctor_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameTextView.setText(mValues.get(position).getName());
        holder.mAadharNumberTextView.setText(holder.mAadharNumberTextView.getText() + mValues.get(position).getAadharNumber());
        holder.mImrNumberTextView.setText(holder.mImrNumberTextView.getText() + mValues.get(position).getImrNumber());

        holder.mApproveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approve(mValues.get(position));
            }
        });

        holder.mRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reject(mValues.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameTextView;
        public final TextView mAadharNumberTextView;
        public final TextView mImrNumberTextView;
        public final Button mApproveButton;
        public final Button mRejectButton;
        public DoctorItem mItem;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            mNameTextView = view.findViewById(R.id.name_text_view);
            mAadharNumberTextView = view.findViewById(R.id.aadhar_text_view);
            mImrNumberTextView = view.findViewById(R.id.imr_text_view);
            mApproveButton = view.findViewById(R.id.approve_button);
            mRejectButton = view.findViewById(R.id.reject_button);
        }
    }

    public void approve(DoctorItem doctorItem) {

        Map<String, Object> map = new HashMap<>();
        map.put("approved", true);

        db.collection("doctors")
                .document(doctorItem.getUid())
                .update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        sendEmail(doctorItem, true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    public void reject(DoctorItem doctorItem) {
        sendEmail(doctorItem, false);
    }

    public void sendEmail(DoctorItem doctorItem, boolean approved) {

        String to = doctorItem.getEmail();
        String subject = "HeathDepot Doctor Profile Approval";
        String message = null;

        if(approved) {
            message = "Greetings!\n\n" +
                    "Your profile on the HealthDepot has been approved, you can now use the services by logging in to the application.\n\n" +
                    "Thank you.";
        }
        else {
            message = "Greetings!\n\n" +
                    "Your provided details on the HealthDepot didn't match with records. Hence your profile has been rejected.\n\n" +
                    "Thank you";
        }

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);

        //need this to prompts email client only
        email.setType("message/rfc822");

        mContext.startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }
}
