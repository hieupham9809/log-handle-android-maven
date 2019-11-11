package com.zingtv.logshowjava.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zingtv.logshowjava.R;

import java.util.List;

/**
 * Created by hieupm@vng.com.vn on 07,November,2019
 */
public class LogItemAdapter extends RecyclerView.Adapter<LogItemAdapter.LogViewHolder> {
    private List<Spanned> mDataset;

    private Context mContext;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class LogViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public LogViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public LogItemAdapter(List<Spanned> myDataset, Context context) {
        mContext = context;
        mDataset = myDataset;

    }
    public LogItemAdapter(Context context) {
        mContext = context;

    }
    public void setLog(List<Spanned> spannedList){
        mDataset = spannedList;
    }
    public void insertLog(List<Spanned> spannedList){
        mDataset.addAll(0, spannedList);
    }
    // Create new views (invoked by the layout manager)
    @Override
    public LogItemAdapter.LogViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(mContext)
                .inflate(R.layout.log_item, parent, false);

        LogViewHolder vh = new LogViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final LogViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (mDataset != null) {
            holder.textView.setText(mDataset.get(position));
            holder.textView.setTextIsSelectable(false);
            holder.textView.measure(-1, -1);//you can specific other values.
            holder.textView.setTextIsSelectable(true);
            holder.textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int startIndex = holder.textView.getSelectionStart();
                    int endIndex = holder.textView.getSelectionEnd();
                    if (startIndex > 0 && endIndex > 0) {
                        String copyText = holder.textView.getText().toString().substring(startIndex, endIndex);
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", copyText);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(mContext, "Copied: " + copyText, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(mContext, "Please select again", Toast.LENGTH_SHORT).show();

                    }

                    return true;
                }
            });
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mDataset != null) {
            return mDataset.size();
        } else return 0;
    }
}
