package com.example.doclocker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ConversionToolAdapter extends RecyclerView.Adapter<ConversionToolAdapter.ToolViewHolder> {
    private final List<ConversionTool> toolList;
    private final OnToolClickListener listener;

    public interface OnToolClickListener {
        void onToolClick(ConversionTool tool);
    }

    public ConversionToolAdapter(List<ConversionTool> toolList, OnToolClickListener listener) {
        this.toolList = toolList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ToolViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversion_tool, parent, false);
        return new ToolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToolViewHolder holder, int position) {
        ConversionTool tool = toolList.get(position);
        holder.tvTitle.setText(tool.getTitle());
        holder.tvDesc.setText(tool.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToolClick(tool);
            }
        });
    }

    @Override
    public int getItemCount() {
        return toolList.size();
    }

    static class ToolViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;

        public ToolViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvToolName);
            tvDesc = itemView.findViewById(R.id.tvToolDesc);
        }
    }
}
