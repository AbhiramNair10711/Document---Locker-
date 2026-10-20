package com.example.doclocker;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {
    private Cursor cursor;
    private final boolean hideName;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(long id, String title, String category, String uri, String size, String date, String expiry);
    }

    public DocumentAdapter(Cursor cursor, boolean hideName, OnItemClickListener listener) {
        this.cursor = cursor;
        this.hideName = hideName;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOC_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOC_TITLE));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOC_CATEGORY));
        String uri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOC_URI));
        String size = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOC_SIZE));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOC_DATE));
        String expiry = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOC_EXPIRY));

        if (hideName) {
            holder.tvTitle.setText("Protected Secure Document ••••••");
        } else {
            holder.tvTitle.setText(title);
        }

        holder.tvCategory.setText(category);
        String dateText = "Added: " + date;
        if (expiry != null && !expiry.isEmpty() && !expiry.equals("Permanent")) {
            dateText += " • Expiry: " + expiry;
        } else if (expiry != null && expiry.equals("Permanent")) {
            dateText += " • Expiry: Permanent";
        }
        holder.tvDate.setText(dateText);
        holder.tvSize.setText(size);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(id, title, category, uri, size, date, expiry);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvDate, tvSize;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvDocTitle);
            tvCategory = itemView.findViewById(R.id.tvDocCategory);
            tvDate = itemView.findViewById(R.id.tvDocDate);
            tvSize = itemView.findViewById(R.id.tvDocSize);
        }
    }
}
