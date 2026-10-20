package com.example.doclocker;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReadinessRadarActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout layoutStateSelection, layoutStateRequirements;
    private TextView tvActiveTitle, tvActiveProgress;
    private ProgressBar progressBarActive;
    private LinearLayout containerActiveRequirements;

    private static final Map<String, String[]> PREDEFINED_TEMPLATES = new HashMap<>();

    static {
        PREDEFINED_TEMPLATES.put("Visa Application", new String[]{"Valid passport", "Completed visa application form", "Photograph and supporting documents"});
        PREDEFINED_TEMPLATES.put("International Travel", new String[]{"Valid passport and required visa", "Flight tickets", "Accommodation details"});
        PREDEFINED_TEMPLATES.put("Passport Renewal", new String[]{"Existing passport", "Renewal application form", "Proof of identity"});
        PREDEFINED_TEMPLATES.put("Driving Licence Application", new String[]{"Identity and address proof", "Learner's licence if required", "Required application documents"});
        PREDEFINED_TEMPLATES.put("Driving Licence Renewal", new String[]{"Existing driving licence", "Proof of identity", "Renewal application"});
        PREDEFINED_TEMPLATES.put("Vehicle Registration and Insurance", new String[]{"Vehicle invoice and papers", "Insurance policy", "Identity proof"});
        PREDEFINED_TEMPLATES.put("College Admission", new String[]{"Academic marksheets", "Identity proof", "Completed admission form"});
        PREDEFINED_TEMPLATES.put("Scholarship Application", new String[]{"Academic records", "Identity and eligibility proof", "Completed scholarship application"});
        PREDEFINED_TEMPLATES.put("Internship Application", new String[]{"Resume", "Academic records", "Portfolio or relevant project work"});
        PREDEFINED_TEMPLATES.put("Examination Registration", new String[]{"Qualification certificates", "Identity proof", "Application form"});
        PREDEFINED_TEMPLATES.put("Job Application", new String[]{"Resume", "Educational certificates", "Relevant experience or portfolio"});
        PREDEFINED_TEMPLATES.put("Bank Account Opening", new String[]{"Identity proof", "Address proof", "Initial deposit proof"});
        PREDEFINED_TEMPLATES.put("Personal Loan Application", new String[]{"Identity proof", "Salary slips or income proof", "Bank statements"});
        PREDEFINED_TEMPLATES.put("Identity Document Application", new String[]{"Birth certificate", "Proof of address", "Application form"});
        PREDEFINED_TEMPLATES.put("Insurance Claim", new String[]{"Insurance policy details", "Claim form", "Supporting evidence or relevant bills"});
        PREDEFINED_TEMPLATES.put("Property Purchase Documentation", new String[]{"Sale deed or title", "Property tax receipts", "Identity proof"});
        PREDEFINED_TEMPLATES.put("Student Visa Application", new String[]{"Passport", "Admission letter", "Financial proof"});
        PREDEFINED_TEMPLATES.put("Work Visa Application", new String[]{"Passport", "Employment offer letter", "Resume/CV"});
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readiness_radar);

        dbHelper = new DatabaseHelper(this);

        ImageView ivBack = findViewById(R.id.ivBackRadar);
        if (ivBack != null) ivBack.setOnClickListener(v -> finish());

        layoutStateSelection = findViewById(R.id.layoutStateSelection);
        layoutStateRequirements = findViewById(R.id.layoutStateRequirements);
        tvActiveTitle = findViewById(R.id.tvActiveChecklistTitle);
        tvActiveProgress = findViewById(R.id.tvActiveChecklistProgress);
        progressBarActive = findViewById(R.id.progressBarActive);
        containerActiveRequirements = findViewById(R.id.containerActiveRequirements);
        com.google.android.material.button.MaterialButton btnBackToList = findViewById(R.id.btnBackToList);

        btnBackToList.setOnClickListener(v -> showSelectionState());

        RecyclerView recyclerView = findViewById(R.id.recyclerViewTemplatesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<RadarTemplateAdapter.TemplateModel> templateList = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : PREDEFINED_TEMPLATES.entrySet()) {
            String title = entry.getKey();
            String cat = getCategoryForTemplate(title);
            templateList.add(new RadarTemplateAdapter.TemplateModel(title, cat, entry.getValue()));
        }

        RadarTemplateAdapter adapter = new RadarTemplateAdapter(templateList, dbHelper, template -> {
            openRequirementsState(template);
        });
        recyclerView.setAdapter(adapter);

        showSelectionState();
    }

    private void showSelectionState() {
        if (layoutStateSelection != null) layoutStateSelection.setVisibility(View.VISIBLE);
        if (layoutStateRequirements != null) layoutStateRequirements.setVisibility(View.GONE);
    }

    private void openRequirementsState(RadarTemplateAdapter.TemplateModel template) {
        if (layoutStateSelection != null) layoutStateSelection.setVisibility(View.GONE);
        if (layoutStateRequirements != null) layoutStateRequirements.setVisibility(View.VISIBLE);

        tvActiveTitle.setText(template.title);
        loadActiveRequirements(template);
    }

    private void loadActiveRequirements(RadarTemplateAdapter.TemplateModel template) {
        containerActiveRequirements.removeAllViews();
        long chkId = dbHelper.getOrCreateRadarChecklist(template.title, template.category);

        Cursor cursor = dbHelper.getRadarItems(chkId);
        if (cursor == null || cursor.getCount() == 0) {
            if (cursor != null) cursor.close();
            for (String req : template.requirements) {
                dbHelper.addRadarItem(chkId, req, 0);
            }
            cursor = dbHelper.getRadarItems(chkId);
        }

        int total = 0;
        int completed = 0;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RITEM_ID));
                String itemTitle = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RITEM_TITLE));
                int isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RITEM_COMPLETED));

                total++;
                if (isCompleted == 1) completed++;

                CheckBox cb = new CheckBox(this);
                cb.setText(itemTitle);
                cb.setChecked(isCompleted == 1);
                cb.setTextSize(15);
                cb.setPadding(0, 8, 0, 8);
                cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    dbHelper.updateRadarItemCompleted(itemId, isChecked ? 1 : 0);
                    loadActiveRequirements(template);
                });
                containerActiveRequirements.addView(cb);
            } while (cursor.moveToNext());
            cursor.close();
        }

        int percent = total > 0 ? (completed * 100) / total : 0;
        tvActiveProgress.setText(String.format(Locale.getDefault(), "%d of %d completed (%d%%)", completed, total, percent));
        if (progressBarActive != null) {
            progressBarActive.setProgress(percent);
        }
    }

    private String getCategoryForTemplate(String templateName) {
        if (templateName.contains("Visa") || templateName.contains("Travel") || templateName.contains("Passport")) return "Travel & Immigration";
        if (templateName.contains("Driving") || templateName.contains("Vehicle")) return "Driving & Vehicles";
        if (templateName.contains("College") || templateName.contains("Scholarship") || templateName.contains("Internship") || templateName.contains("Examination")) return "Education";
        if (templateName.contains("Job") || templateName.contains("Bank") || templateName.contains("Loan")) return "Employment & Finance";
        return "Personal & Government";
    }
}
