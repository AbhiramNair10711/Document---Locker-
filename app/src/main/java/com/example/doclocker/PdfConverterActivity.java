package com.example.doclocker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PdfConverterActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConversionToolAdapter adapter;
    private List<ConversionTool> toolList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_converter);

        ImageView ivBack = findViewById(R.id.ivBackConverter);
        ivBack.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewTools);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        toolList = new ArrayList<>();
        toolList.add(new ConversionTool("Merge PDF", "Combine multiple PDFs into one file.", "MERGE"));
        toolList.add(new ConversionTool("Split PDF", "Separate pages or extract pages.", "SPLIT"));
        toolList.add(new ConversionTool("Compress PDF", "Reduce file size with optimal quality.", "COMPRESS"));
        toolList.add(new ConversionTool("PDF to Word", "Convert PDF documents to editable DOCX.", "PDF_TO_WORD"));
        toolList.add(new ConversionTool("Word to PDF", "Turn DOC and DOCX files into PDF.", "WORD_TO_PDF"));
        toolList.add(new ConversionTool("PDF to PPT", "Convert PDF into PowerPoint slides.", "PDF_TO_PPT"));
        toolList.add(new ConversionTool("PDF to Excel", "Extract spreadsheet data from PDF.", "PDF_TO_EXCEL"));
        toolList.add(new ConversionTool("Edit PDF", "Add text, annotations and shapes.", "EDIT_PDF"));

        adapter = new ConversionToolAdapter(toolList, tool -> {
            Intent intent = new Intent(PdfConverterActivity.this, ConversionExecutionActivity.class);
            intent.putExtra("TOOL_TITLE", tool.getTitle());
            intent.putExtra("TOOL_DESC", tool.getDescription());
            intent.putExtra("TOOL_FORMAT", tool.getFormat());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }
}
