package com.example.doclocker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RadarTemplateAdapter extends RecyclerView.Adapter<RadarTemplateAdapter.RadarViewHolder> {

    private final List<TemplateModel> templateList;
    private final DatabaseHelper dbHelper;
    private final OnTemplateClickListener listener;

    public interface OnTemplateClickListener {
        void onTemplateClick(TemplateModel template);
    }

    public static class TemplateModel {
        String title;
        String category;
        String[] requirements;

        public TemplateModel(String title, String category, String[] requirements) {
            this.title = title;
            this.category = category;
            this.requirements = requirements;
        }
    }

    public RadarTemplateAdapter(List<TemplateModel> templateList, DatabaseHelper dbHelper, OnTemplateClickListener listener) {
        this.templateList = templateList;
        this.dbHelper = dbHelper;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RadarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_radar_template, parent, false);
        return new RadarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RadarViewHolder holder, int position) {
        TemplateModel template = templateList.get(position);
        holder.tvCategory.setText(template.category);
        holder.tvTitle.setText(template.title);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTemplateClick(template);
            }
        });
    }

    @Override
    public int getItemCount() {
        return templateList.size();
    }

    static class RadarViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvTitle;

        public RadarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvTemplateCategory);
            tvTitle = itemView.findViewById(R.id.tvTemplateTitle);
        }
    }
}
