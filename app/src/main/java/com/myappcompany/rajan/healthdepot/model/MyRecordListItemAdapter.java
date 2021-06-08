package com.myappcompany.rajan.healthdepot.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.myappcompany.rajan.healthdepot.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRecordListItemAdapter extends RecyclerView.Adapter<MyRecordListItemAdapter.MyViewHolder> {

    private final List<RecordItem> mValues;

    public MyRecordListItemAdapter(List<RecordItem> items) {
        mValues = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_record_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mDiseaseTextView.setText(mValues.get(position).getDisease());
        Date date = mValues.get(position).getTimestamp().toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        holder.mTimestampTextView.setText(dateFormat.format(date));
        holder.mClinicTextView.setText(mValues.get(position).getClinic());
        holder.mRemarksTextView.setText(mValues.get(position).getRemarks());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDiseaseTextView;
        public final TextView mTimestampTextView;
        public final TextView mClinicTextView;
        public final TextView mRemarksTextView;
        public RecordItem mItem;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            mDiseaseTextView = view.findViewById(R.id.disease_text_view);
            mTimestampTextView = view.findViewById(R.id.timestamp_text_view);
            mClinicTextView = view.findViewById(R.id.clinic_text_view);
            mRemarksTextView = view.findViewById(R.id.remarks_text_view);
        }
    }
}