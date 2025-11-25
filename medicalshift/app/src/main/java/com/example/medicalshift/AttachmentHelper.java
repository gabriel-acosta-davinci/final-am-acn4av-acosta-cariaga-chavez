package com.example.medicalshift;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class AttachmentHelper {

    public interface AttachmentListener {
        void onFileAttached(String fileName, Uri fileUri);
    }

    private final Fragment fragment;
    private final AttachmentListener listener;
    private final ActivityResultLauncher<String> requestPermissionLauncher;
    private final ActivityResultLauncher<Uri> takePictureLauncher;
    private final ActivityResultLauncher<String[]> openDocumentLauncher;
    private Uri tempImageUri;

    public AttachmentHelper(Fragment fragment, AttachmentListener listener) {
        this.fragment = fragment;
        this.listener = listener;

        this.requestPermissionLauncher = fragment.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                launchCamera();
            }
        });

        this.takePictureLauncher = fragment.registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (success && tempImageUri != null) {
                listener.onFileAttached(getFileName(tempImageUri), tempImageUri);
            }
        });

        this.openDocumentLauncher = fragment.registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if (uri != null) {
                Uri permanentUri = copyFileToInternalStorage(uri, getFileName(uri));
                listener.onFileAttached(getFileName(uri), permanentUri);
            }
        });
    }

    public void dispatchTakePictureIntent() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    private void launchCamera(){
        tempImageUri = getTempImageUri();
        if (tempImageUri != null) {
            takePictureLauncher.launch(tempImageUri);
        }
    }

    public void dispatchOpenDocumentIntent() {
        openDocumentLauncher.launch(new String[]{"application/pdf", "image/*"});
    }

    private Uri getTempImageUri() {
        File imagePath = new File(fragment.requireContext().getCacheDir(), "images");
        if (!imagePath.exists()) {
            imagePath.mkdirs();
        }
        File newFile = new File(imagePath, "IMG_" + System.currentTimeMillis() + ".jpg");
        return FileProvider.getUriForFile(fragment.requireContext(), fragment.requireContext().getApplicationContext().getPackageName() + ".provider", newFile);
    }

    private String getFileName(Uri uri) {
        // Extraer el nombre del archivo de la Uri
        return "adjunto."+getFileExtension(uri);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = fragment.requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private Uri copyFileToInternalStorage(Uri uri, String newFileName) {
        try (InputStream inputStream = fragment.requireContext().getContentResolver().openInputStream(uri)) {
            File file = new File(fragment.requireContext().getFilesDir(), newFileName);
            try (OutputStream outputStream = new FileOutputStream(file)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }
            }
            return Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
