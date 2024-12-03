package com.example.firebaselogin;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.OutputStream;

/**
 * Class to export expenses to CSV and PDF from Firebase Firestore.
 */
public class ExpenseExporter {

    private final Context context;
    private final FirebaseFirestore db;

    public ExpenseExporter(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Exports expenses to a CSV file by pulling data from Firebase Firestore.
     *
     * @param groupId The ID of the group to filter expenses.
     */
    public void exportToCSV(String groupId) {
        db.collection("expenses")
                .whereEqualTo("group", groupId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        StringBuilder csvContent = new StringBuilder();
                        csvContent.append("Amount,Description,Date,Category\n"); // Updated header

                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String amount = document.getString("amount");
                            String description = document.getString("description");
                            String date = document.getString("date");
                            String category = document.getString("category");

                            csvContent.append(amount).append(",")
                                    .append(description).append(",")
                                    .append(date).append(",")
                                    .append(category).append("\n"); // Updated content
                        }

                        saveToDownloads("expenses.csv", "text/csv", csvContent.toString());
                    } else {
                        Toast.makeText(context, "No expenses found for this group", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to fetch expenses: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Exports expenses to a PDF file by pulling data from Firebase Firestore.
     *
     * @param groupId The ID of the group to filter expenses.
     */
    public void exportToPDF(String groupId) {
        db.collection("expenses")
                .whereEqualTo("group", groupId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        PdfDocument pdfDocument = new PdfDocument();
                        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
                        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                        Canvas canvas = page.getCanvas();
                        Paint paint = new Paint();

                        int y = 50;

                        // Title
                        paint.setTextSize(18);
                        paint.setColor(Color.BLACK);
                        canvas.drawText("Expense Report", 50, y, paint);
                        y += 30;

                        // Header
                        paint.setTextSize(14);
                        canvas.drawText("Amount | Description | Date | Category", 50, y, paint); // Updated header
                        y += 20;

                        // Content
                        paint.setTextSize(12);
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String amount = document.getString("amount");
                            String description = document.getString("description");
                            String date = document.getString("date");
                            String category = document.getString("category");

                            String line = amount + " | " +
                                    description + " | " +
                                    date + " | " +
                                    category; // Updated content
                            canvas.drawText(line, 50, y, paint);
                            y += 20;
                        }

                        pdfDocument.finishPage(page);

                        try (OutputStream outputStream = createOutputStream("expenses.pdf", "application/pdf")) {
                            if (outputStream != null) {
                                pdfDocument.writeTo(outputStream);
                                Toast.makeText(context, "PDF saved to Downloads", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "Failed to save PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        pdfDocument.close();
                    } else {
                        Toast.makeText(context, "No expenses found for this group", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to fetch expenses: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Helper method to save files to the Downloads folder.
     */
    private void saveToDownloads(String fileName, String mimeType, String content) {
        try (OutputStream outputStream = createOutputStream(fileName, mimeType)) {
            if (outputStream != null) {
                outputStream.write(content.getBytes());
                Toast.makeText(context, "File saved to Downloads", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Helper method to create an OutputStream using MediaStore.
     */
    private OutputStream createOutputStream(String fileName, String mimeType) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = resolver.insert(MediaStore.Files.getContentUri("external"), values);
        if (uri != null) {
            try {
                return resolver.openOutputStream(uri);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Error creating file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Error: File URI is null", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
