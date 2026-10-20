package com.example.doclocker;

public class ConversionTool {
    private String title;
    private String description;
    private String format;

    public ConversionTool(String title, String description, String format) {
        this.title = title;
        this.description = description;
        this.format = format;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFormat() {
        return format;
    }
}
