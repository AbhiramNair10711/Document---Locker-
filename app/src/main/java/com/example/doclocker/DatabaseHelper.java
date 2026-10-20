package com.example.doclocker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DocLocker.db";
    private static final int DATABASE_VERSION = 6;

    // Table Documents
    public static final String TABLE_DOCUMENTS = "documents";
    public static final String COLUMN_DOC_ID = "id";
    public static final String COLUMN_DOC_TITLE = "title";
    public static final String COLUMN_DOC_CATEGORY = "category";
    public static final String COLUMN_DOC_URI = "file_uri";
    public static final String COLUMN_DOC_SIZE = "file_size";
    public static final String COLUMN_DOC_DATE = "date_added";
    public static final String COLUMN_DOC_EXPIRY = "expiry_date";
    public static final String COLUMN_DOC_TAGS = "tags";
    public static final String COLUMN_DOC_FOLDER = "folder_name";
    public static final String COLUMN_DOC_FAVOURITE = "is_favourite";

    // Table Conversion History
    public static final String TABLE_CONVERSIONS = "conversions";
    public static final String COLUMN_CONV_ID = "id";
    public static final String COLUMN_CONV_SOURCE = "source_name";
    public static final String COLUMN_CONV_TARGET = "target_format";
    public static final String COLUMN_CONV_STATUS = "status";
    public static final String COLUMN_CONV_DATE = "timestamp";

    // Table Readiness Radar Checklists
    public static final String TABLE_RADAR_CHK = "radar_checklists";
    public static final String COLUMN_RCHK_ID = "id";
    public static final String COLUMN_RCHK_NAME = "template_name";
    public static final String COLUMN_RCHK_CAT = "category";

    // Table Readiness Radar Items
    public static final String TABLE_RADAR_ITEMS = "radar_items";
    public static final String COLUMN_RITEM_ID = "id";
    public static final String COLUMN_RITEM_CHK_ID = "checklist_id";
    public static final String COLUMN_RITEM_TITLE = "item_title";
    public static final String COLUMN_RITEM_COMPLETED = "is_completed";
    public static final String COLUMN_ITEM_DOC_ID = "linked_doc_id";

    // Aliases for compatibility
    public static final String COLUMN_CHK_ID = COLUMN_RCHK_ID;
    public static final String COLUMN_CHK_NAME = COLUMN_RCHK_NAME;
    public static final String COLUMN_CHK_CATEGORY = COLUMN_RCHK_CAT;
    public static final String COLUMN_ITEM_ID = COLUMN_RITEM_ID;
    public static final String COLUMN_ITEM_CHK_ID = COLUMN_RITEM_CHK_ID;
    public static final String COLUMN_ITEM_TITLE = COLUMN_RITEM_TITLE;
    public static final String COLUMN_ITEM_COMPLETED = COLUMN_RITEM_COMPLETED;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDocumentsTable = "CREATE TABLE " + TABLE_DOCUMENTS + "("
                + COLUMN_DOC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DOC_TITLE + " TEXT, "
                + COLUMN_DOC_CATEGORY + " TEXT, "
                + COLUMN_DOC_URI + " TEXT, "
                + COLUMN_DOC_SIZE + " TEXT, "
                + COLUMN_DOC_DATE + " TEXT, "
                + COLUMN_DOC_EXPIRY + " TEXT, "
                + COLUMN_DOC_TAGS + " TEXT, "
                + COLUMN_DOC_FOLDER + " TEXT, "
                + COLUMN_DOC_FAVOURITE + " INTEGER DEFAULT 0)";
        db.execSQL(createDocumentsTable);

        String createConversionsTable = "CREATE TABLE " + TABLE_CONVERSIONS + "("
                + COLUMN_CONV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CONV_SOURCE + " TEXT, "
                + COLUMN_CONV_TARGET + " TEXT, "
                + COLUMN_CONV_STATUS + " TEXT, "
                + COLUMN_CONV_DATE + " TEXT)";
        db.execSQL(createConversionsTable);

        String createRadarChk = "CREATE TABLE " + TABLE_RADAR_CHK + "("
                + COLUMN_RCHK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_RCHK_NAME + " TEXT UNIQUE, "
                + COLUMN_RCHK_CAT + " TEXT)";
        db.execSQL(createRadarChk);

        String createRadarItems = "CREATE TABLE " + TABLE_RADAR_ITEMS + "("
                + COLUMN_RITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_RITEM_CHK_ID + " INTEGER, "
                + COLUMN_RITEM_TITLE + " TEXT, "
                + COLUMN_RITEM_COMPLETED + " INTEGER DEFAULT 0, "
                + COLUMN_ITEM_DOC_ID + " INTEGER DEFAULT -1)";
        db.execSQL(createRadarItems);

        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase directDb) {
        ContentValues cv1 = new ContentValues();
        cv1.put(COLUMN_DOC_TITLE, "Aadhaar Card");
        cv1.put(COLUMN_DOC_CATEGORY, "Identity Documents");
        cv1.put(COLUMN_DOC_URI, "content://sample/aadhaar.pdf");
        cv1.put(COLUMN_DOC_SIZE, "1.2 MB");
        cv1.put(COLUMN_DOC_DATE, "07 Jan 2022");
        cv1.put(COLUMN_DOC_EXPIRY, "Permanent");
        cv1.put(COLUMN_DOC_TAGS, "ID,Govt");
        cv1.put(COLUMN_DOC_FOLDER, "Personal IDs");
        cv1.put(COLUMN_DOC_FAVOURITE, 1);
        directDb.insert(TABLE_DOCUMENTS, null, cv1);

        ContentValues cv2 = new ContentValues();
        cv2.put(COLUMN_DOC_TITLE, "Updated Resume.pdf");
        cv2.put(COLUMN_DOC_CATEGORY, "Employment");
        cv2.put(COLUMN_DOC_URI, "content://sample/resume.pdf");
        cv2.put(COLUMN_DOC_SIZE, "850 KB");
        cv2.put(COLUMN_DOC_DATE, "19 Jan 2024");
        cv2.put(COLUMN_DOC_EXPIRY, "Permanent");
        cv2.put(COLUMN_DOC_TAGS, "Resume,Job");
        cv2.put(COLUMN_DOC_FOLDER, "Career");
        cv2.put(COLUMN_DOC_FAVOURITE, 1);
        directDb.insert(TABLE_DOCUMENTS, null, cv2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 6) {
            try {
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CONVERSIONS + "("
                        + COLUMN_CONV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_CONV_SOURCE + " TEXT, "
                        + COLUMN_CONV_TARGET + " TEXT, "
                        + COLUMN_CONV_STATUS + " TEXT, "
                        + COLUMN_CONV_DATE + " TEXT)");
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RADAR_CHK + "("
                        + COLUMN_RCHK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_RCHK_NAME + " TEXT UNIQUE, "
                        + COLUMN_RCHK_CAT + " TEXT)");
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RADAR_ITEMS + "("
                        + COLUMN_RITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_RITEM_CHK_ID + " INTEGER, "
                        + COLUMN_RITEM_TITLE + " TEXT, "
                        + COLUMN_RITEM_COMPLETED + " INTEGER DEFAULT 0, "
                        + COLUMN_ITEM_DOC_ID + " INTEGER DEFAULT -1)");
            } catch (Exception ignored) {}
        }
    }

    public long addDocument(String title, String category, String uri, String size, String date, String expiry) {
        return addDocument(title, category, uri, size, date, expiry, "General", "Unassigned", 0);
    }

    public long addDocument(String title, String category, String uri, String size, String date, String expiry, String tags, String folder, int favourite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DOC_TITLE, title);
        cv.put(COLUMN_DOC_CATEGORY, category);
        cv.put(COLUMN_DOC_URI, uri);
        cv.put(COLUMN_DOC_SIZE, size);
        cv.put(COLUMN_DOC_DATE, date);
        cv.put(COLUMN_DOC_EXPIRY, expiry);
        cv.put(COLUMN_DOC_TAGS, tags);
        cv.put(COLUMN_DOC_FOLDER, folder);
        cv.put(COLUMN_DOC_FAVOURITE, favourite);
        return db.insert(TABLE_DOCUMENTS, null, cv);
    }

    public void updateDocumentTitle(long id, String newTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DOC_TITLE, newTitle);
        db.update(TABLE_DOCUMENTS, cv, COLUMN_DOC_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void updateDocumentExpiry(long id, String expiry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DOC_EXPIRY, expiry);
        db.update(TABLE_DOCUMENTS, cv, COLUMN_DOC_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void updateDocumentUri(long id, String newUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DOC_URI, newUri);
        db.update(TABLE_DOCUMENTS, cv, COLUMN_DOC_ID + "=?", new String[]{String.valueOf(id)});
    }

    public Cursor getAllDocuments() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_DOCUMENTS + " ORDER BY " + COLUMN_DOC_ID + " DESC", null);
    }

    public Cursor searchDocuments(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            return db.rawQuery("SELECT * FROM " + TABLE_DOCUMENTS + " WHERE " + COLUMN_DOC_TITLE + " LIKE ? OR " + COLUMN_DOC_CATEGORY + " LIKE ?", new String[]{"%" + query + "%", "%" + query + "%"});
        } catch (Exception e) {
            return getAllDocuments();
        }
    }

    public void deleteDocument(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOCUMENTS, COLUMN_DOC_ID + "=?", new String[]{String.valueOf(id)});
    }

    public long addConversion(String source, String target, String status, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CONV_SOURCE, source);
        cv.put(COLUMN_CONV_TARGET, target);
        cv.put(COLUMN_CONV_STATUS, status);
        cv.put(COLUMN_CONV_DATE, date);
        return db.insert(TABLE_CONVERSIONS, null, cv);
    }

    // Checklists / Readiness Radar methods
    public Cursor getAllChecklists() {
        return getRadarChecklists();
    }

    public long findChecklistByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_RCHK_ID + " FROM " + TABLE_RADAR_CHK + " WHERE " + COLUMN_RCHK_NAME + " = ?", new String[]{name});
        long id = -1;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                id = cursor.getLong(0);
            }
            cursor.close();
        }
        return id;
    }

    public long addChecklist(String name, String category) {
        long existingId = findChecklistByName(name);
        if (existingId != -1) {
            return existingId;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RCHK_NAME, name);
        cv.put(COLUMN_RCHK_CAT, category);
        return db.insert(TABLE_RADAR_CHK, null, cv);
    }

    public void deleteChecklist(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RADAR_CHK, COLUMN_RCHK_ID + "=?", new String[]{String.valueOf(id)});
        db.delete(TABLE_RADAR_ITEMS, COLUMN_RITEM_CHK_ID + "=?", new String[]{String.valueOf(id)});
    }

    public Cursor getChecklistItems(long checklistId) {
        return getRadarItems(checklistId);
    }

    public long addItemToChecklist(long checklistId, String title, int completed, long docId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RITEM_CHK_ID, checklistId);
        cv.put(COLUMN_RITEM_TITLE, title);
        cv.put(COLUMN_RITEM_COMPLETED, completed);
        cv.put(COLUMN_ITEM_DOC_ID, docId);
        return db.insert(TABLE_RADAR_ITEMS, null, cv);
    }

    public void updateItemCompleted(long itemId, int completed) {
        updateRadarItemCompleted(itemId, completed);
    }

    public void updateItemLinkedDoc(long itemId, long docId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ITEM_DOC_ID, docId);
        db.update(TABLE_RADAR_ITEMS, cv, COLUMN_RITEM_ID + "=?", new String[]{String.valueOf(itemId)});
    }

    public void deleteChecklistItem(long itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RADAR_ITEMS, COLUMN_RITEM_ID + "=?", new String[]{String.valueOf(itemId)});
    }

    // Additional aliases for RadarTemplateAdapter
    public long getOrCreateRadarChecklist(String templateName, String category) {
        return addChecklist(templateName, category);
    }

    public Cursor getRadarItems(long checklistId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RADAR_ITEMS + " WHERE " + COLUMN_RITEM_CHK_ID + "=?", new String[]{String.valueOf(checklistId)});
    }

    public long addRadarItem(long checklistId, String title, int completed) {
        return addItemToChecklist(checklistId, title, completed, -1);
    }

    public void updateRadarItemCompleted(long itemId, int completed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RITEM_COMPLETED, completed);
        db.update(TABLE_RADAR_ITEMS, cv, COLUMN_RITEM_ID + "=?", new String[]{String.valueOf(itemId)});
    }

    public Cursor getRadarChecklists() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RADAR_CHK + " ORDER BY " + COLUMN_RCHK_ID + " ASC", null);
    }
}
