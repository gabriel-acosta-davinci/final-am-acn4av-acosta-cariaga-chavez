package com.example.medicalshift.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.example.medicalshift.api.RetrofitClient;
import com.example.medicalshift.models.CreateGestionResponse;
import com.example.medicalshift.models.DocumentResponse;
import com.example.medicalshift.Gestion;
import com.example.medicalshift.models.GestionRequest;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionHelper {

    public interface GestionCallback {
        void onSuccess(String gestionId);
        void onError(String message);
    }

    public interface FileUploadCallback {
        void onSuccess();
        void onError(String message);
    }

    /**
     * Crea una gestión y opcionalmente sube un archivo
     */
    public static void crearGestionYSubirArchivo(
            Context context,
            String nombreGestion,
            String fechaAplicacion, // Puede ser null
            Uri attachedFileUri,
            GestionCallback callback) {

        TokenManager tokenManager = new TokenManager(context);
        String token = tokenManager.getToken();
        String userId = tokenManager.getUserId();

        if (token == null || userId == null) {
            callback.onError("Sesión expirada. Por favor, inicia sesión nuevamente.");
            return;
        }

        // Formatear fecha para el backend (en milisegundos como número)
        Long fechaTimestamp = fechaAplicacion != null ? 
            formatearFechaParaBackend(fechaAplicacion) : 
            System.currentTimeMillis();

        // Crear request de gestión
        GestionRequest gestionRequest = new GestionRequest(
            "pendiente",
            nombreGestion,
            String.valueOf(fechaTimestamp), // El modelo espera String pero el backend lo parseará
            userId
        );

        String authHeader = "Bearer " + token;

        // Paso 1: Crear la gestión
        android.util.Log.d("GestionHelper", "Creando gestión: " + nombreGestion + ", fecha: " + fechaTimestamp + ", userId: " + userId);
        RetrofitClient.getInstance().getApiService().createGestion(authHeader, gestionRequest)
                .enqueue(new Callback<CreateGestionResponse>() {
                    @Override
                    public void onResponse(Call<CreateGestionResponse> call, Response<CreateGestionResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            CreateGestionResponse gestionResponse = response.body();
                            Gestion gestion = gestionResponse.getGestion();

                            if (gestion == null) {
                                android.util.Log.e("GestionHelper", "Gestion es null en respuesta");
                                callback.onError("Error: No se pudo crear la gestión");
                                return;
                            }

                            String gestionId = gestion.getId();

                            if (gestionId == null || gestionId.isEmpty()) {
                                android.util.Log.e("GestionHelper", "Gestion creada pero sin ID en respuesta");
                                callback.onError("Error: No se pudo obtener el ID de la gestión");
                                return;
                            }

                            android.util.Log.d("GestionHelper", "Gestión creada exitosamente con ID: " + gestionId);

                            // Paso 2: Si hay archivo, subirlo
                            if (attachedFileUri != null) {
                                subirArchivo(context, authHeader, gestionId, attachedFileUri, new FileUploadCallback() {
                                    @Override
                                    public void onSuccess() {
                                        callback.onSuccess(gestionId);
                                    }

                                    @Override
                                    public void onError(String message) {
                                        // La gestión se creó pero falló la subida del archivo
                                        callback.onSuccess(gestionId); // Aún consideramos éxito
                                    }
                                });
                            } else {
                                // No hay archivo, solo éxito
                                callback.onSuccess(gestionId);
                            }
                        } else {
                            String errorMessage = "Error al crear la gestión";
                            try {
                                if (response.errorBody() != null) {
                                    String errorBody = response.errorBody().string();
                                    android.util.Log.e("GestionHelper", "Error response: " + errorBody);
                                    errorMessage = "Error: " + response.code() + " - " + errorBody;
                                }
                            } catch (Exception e) {
                                android.util.Log.e("GestionHelper", "Error leyendo error body", e);
                            }
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<CreateGestionResponse> call, Throwable t) {
                        android.util.Log.e("GestionHelper", "Error de conexión", t);
                        callback.onError("Error de conexión: " + t.getMessage());
                        t.printStackTrace();
                    }
                });
    }

    /**
     * Sube un archivo asociado a una gestión
     */
    public static void subirArchivo(
            Context context,
            String authHeader,
            String gestionId,
            Uri fileUri,
            FileUploadCallback callback) {

        try {
            android.util.Log.d("GestionHelper", "Iniciando subida de archivo. URI: " + fileUri.toString());
            
            File file = obtenerArchivoDesdeUri(context, fileUri);

            if (file == null || !file.exists()) {
                android.util.Log.e("GestionHelper", "Archivo no existe o es null. Path: " + (file != null ? file.getAbsolutePath() : "null"));
                callback.onError("Error: No se pudo acceder al archivo");
                return;
            }

            android.util.Log.d("GestionHelper", "Archivo encontrado: " + file.getAbsolutePath() + ", tamaño: " + file.length());

            // Obtener el tipo MIME del archivo
            String mimeType = getMimeType(context, fileUri);
            if (mimeType == null) {
                String fileName = file.getName();
                if (fileName.endsWith(".pdf")) {
                    mimeType = "application/pdf";
                } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    mimeType = "image/jpeg";
                } else if (fileName.endsWith(".png")) {
                    mimeType = "image/png";
                } else {
                    mimeType = "application/octet-stream";
                }
            }

            android.util.Log.d("GestionHelper", "Tipo MIME: " + mimeType);

            // Validar y crear MediaType de forma segura
            MediaType mediaType = null;
            try {
                if (mimeType != null && !mimeType.isEmpty()) {
                    mediaType = MediaType.parse(mimeType);
                }
            } catch (Exception e) {
                android.util.Log.w("GestionHelper", "Error parseando MIME type: " + mimeType, e);
            }
            
            if (mediaType == null) {
                // Usar un tipo por defecto seguro
                mediaType = MediaType.parse("application/octet-stream");
                android.util.Log.d("GestionHelper", "Usando MIME type por defecto: application/octet-stream");
            }
            
            // Leer el archivo como bytes - esto es más confiable que usar File directamente
            java.io.FileInputStream fileInputStream = null;
            RequestBody requestFile;
            try {
                // Validar que el archivo tenga contenido
                long fileSize = file.length();
                if (fileSize <= 0) {
                    android.util.Log.e("GestionHelper", "El archivo está vacío o no tiene contenido");
                    callback.onError("Error: El archivo está vacío");
                    return;
                }
                
                if (fileSize > 10 * 1024 * 1024) { // 10MB límite
                    android.util.Log.e("GestionHelper", "El archivo es demasiado grande: " + fileSize);
                    callback.onError("Error: El archivo es demasiado grande (máximo 10MB)");
                    return;
                }
                
                fileInputStream = new java.io.FileInputStream(file);
                byte[] fileBytes = new byte[(int) fileSize];
                int bytesRead = 0;
                int totalBytesRead = 0;
                
                // Leer todos los bytes del archivo
                while (totalBytesRead < fileBytes.length) {
                    bytesRead = fileInputStream.read(fileBytes, totalBytesRead, fileBytes.length - totalBytesRead);
                    if (bytesRead == -1) {
                        break;
                    }
                    totalBytesRead += bytesRead;
                }
                
                fileInputStream.close();
                fileInputStream = null;
                
                if (totalBytesRead != fileSize) {
                    android.util.Log.w("GestionHelper", "No se leyeron todos los bytes. Esperado: " + fileSize + ", Leído: " + totalBytesRead);
                    // Aún así continuar, puede que el archivo sea más pequeño
                }
                
                // Validar que mediaType no sea null
                if (mediaType == null) {
                    mediaType = MediaType.parse("application/octet-stream");
                }
                
                // Crear RequestBody desde bytes
                requestFile = RequestBody.create(mediaType, fileBytes);
                android.util.Log.d("GestionHelper", "RequestBody creado desde bytes. Tamaño: " + fileBytes.length + ", MediaType: " + mediaType.toString());
                
            } catch (java.io.FileNotFoundException e) {
                android.util.Log.e("GestionHelper", "Archivo no encontrado", e);
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (Exception ex) {
                        android.util.Log.e("GestionHelper", "Error cerrando FileInputStream", ex);
                    }
                }
                callback.onError("Error: Archivo no encontrado");
                return;
            } catch (java.io.IOException e) {
                android.util.Log.e("GestionHelper", "Error de IO leyendo archivo", e);
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (Exception ex) {
                        android.util.Log.e("GestionHelper", "Error cerrando FileInputStream", ex);
                    }
                }
                callback.onError("Error al leer archivo: " + e.getMessage());
                return;
            } catch (Exception e) {
                android.util.Log.e("GestionHelper", "Error inesperado leyendo archivo", e);
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (Exception ex) {
                        android.util.Log.e("GestionHelper", "Error cerrando FileInputStream", ex);
                    }
                }
                callback.onError("Error al leer archivo: " + e.getMessage());
                return;
            }
            
            // Usar un nombre de archivo seguro
            String safeFileName = file.getName();
            if (safeFileName == null || safeFileName.isEmpty()) {
                safeFileName = "archivo_" + System.currentTimeMillis();
                if (mimeType != null) {
                    if (mimeType.contains("pdf")) {
                        safeFileName += ".pdf";
                    } else if (mimeType.contains("jpeg") || mimeType.contains("jpg")) {
                        safeFileName += ".jpg";
                    } else if (mimeType.contains("png")) {
                        safeFileName += ".png";
                    }
                }
            }
            
            android.util.Log.d("GestionHelper", "Nombre de archivo: " + safeFileName + ", tamaño: " + file.length());
            
            // Crear MultipartBody.Part de forma segura
            MultipartBody.Part filePart;
            try {
                filePart = MultipartBody.Part.createFormData("file", safeFileName, requestFile);
                android.util.Log.d("GestionHelper", "MultipartBody.Part creado exitosamente");
            } catch (Exception e) {
                android.util.Log.e("GestionHelper", "Error creando MultipartBody.Part", e);
                android.util.Log.e("GestionHelper", "Stack trace:", e);
                callback.onError("Error al preparar archivo: " + e.getMessage());
                return;
            }

            android.util.Log.d("GestionHelper", "Subiendo archivo a gestión: " + gestionId);
            RetrofitClient.getInstance().getApiService().uploadDocument(authHeader, gestionId, filePart)
                    .enqueue(new Callback<DocumentResponse>() {
                        @Override
                        public void onResponse(Call<DocumentResponse> call, Response<DocumentResponse> response) {
                            if (response.isSuccessful()) {
                                android.util.Log.d("GestionHelper", "Archivo subido exitosamente");
                                callback.onSuccess();
                            } else {
                                String errorMessage = "Error al subir archivo";
                                try {
                                    if (response.errorBody() != null) {
                                        String errorBody = response.errorBody().string();
                                        android.util.Log.e("GestionHelper", "Error response: " + errorBody);
                                        errorMessage = "Error: " + response.code() + " - " + errorBody;
                                    }
                                } catch (Exception e) {
                                    android.util.Log.e("GestionHelper", "Error leyendo error body", e);
                                }
                                callback.onError(errorMessage);
                            }
                        }

                        @Override
                        public void onFailure(Call<DocumentResponse> call, Throwable t) {
                            android.util.Log.e("GestionHelper", "Error de conexión al subir archivo", t);
                            callback.onError("Error de conexión al subir archivo: " + t.getMessage());
                            t.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            android.util.Log.e("GestionHelper", "Excepción al preparar archivo", e);
            callback.onError("Error al preparar archivo para subir: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Long formatearFechaParaBackend(String fecha) {
        try {
            // La fecha viene en formato dd/MM/yyyy
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(fecha);

            if (date != null) {
                // Retornar en milisegundos (el backend espera milisegundos para Timestamp.fromMillis)
                return date.getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Si falla, usar timestamp actual en milisegundos
        return System.currentTimeMillis();
    }

    private static File obtenerArchivoDesdeUri(Context context, Uri uri) {
        android.util.Log.d("GestionHelper", "Obteniendo archivo desde URI. Scheme: " + uri.getScheme());
        
        try {
            if (uri.getScheme() != null && uri.getScheme().equals("file")) {
                String path = uri.getPath();
                android.util.Log.d("GestionHelper", "URI file://, path: " + path);
                if (path != null) {
                    File file = new File(path);
                    if (file.exists()) {
                        return file;
                    }
                }
            } else if (uri.getScheme() != null && uri.getScheme().equals("content")) {
                android.util.Log.d("GestionHelper", "URI content://, copiando archivo");
                return copyUriToFile(context, uri);
            } else {
                // Intentar obtener path directo
                String path = uri.getPath();
                android.util.Log.d("GestionHelper", "URI otro scheme, path: " + path);
                if (path != null) {
                    File file = new File(path);
                    if (file.exists()) {
                        return file;
                    }
                }
                // Si no funciona, intentar copiar como content://
                android.util.Log.d("GestionHelper", "Intentando copiar como content://");
                return copyUriToFile(context, uri);
            }
        } catch (Exception e) {
            android.util.Log.e("GestionHelper", "Error obteniendo archivo desde URI", e);
        }
        return null;
    }

    private static File copyUriToFile(Context context, Uri uri) {
        java.io.InputStream inputStream = null;
        java.io.FileOutputStream outputStream = null;
        
        try {
            android.util.Log.d("GestionHelper", "Copiando URI a archivo: " + uri.toString());
            
            inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                android.util.Log.e("GestionHelper", "No se pudo abrir InputStream desde URI");
                return null;
            }

            String fileName = "upload_" + System.currentTimeMillis();
            String mimeType = getMimeType(context, uri);
            android.util.Log.d("GestionHelper", "MIME type detectado: " + mimeType);
            
            if (mimeType != null) {
                if (mimeType.contains("pdf")) {
                    fileName += ".pdf";
                } else if (mimeType.contains("jpeg") || mimeType.contains("jpg")) {
                    fileName += ".jpg";
                } else if (mimeType.contains("png")) {
                    fileName += ".png";
                }
            } else {
                // Intentar obtener extensión desde el nombre del archivo en la URI
                String uriString = uri.toString();
                if (uriString.contains(".")) {
                    String extension = uriString.substring(uriString.lastIndexOf("."));
                    if (extension.length() <= 5) { // Extensiones válidas son cortas
                        fileName += extension;
                    }
                }
            }

            File file = new File(context.getCacheDir(), fileName);
            android.util.Log.d("GestionHelper", "Archivo temporal: " + file.getAbsolutePath());
            
            outputStream = new java.io.FileOutputStream(file);

            byte[] buffer = new byte[8192]; // Buffer más grande para mejor rendimiento
            int bytesRead;
            long totalBytes = 0;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }

            android.util.Log.d("GestionHelper", "Archivo copiado exitosamente. Tamaño: " + totalBytes + " bytes");
            
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            if (file.exists() && file.length() > 0) {
                return file;
            } else {
                android.util.Log.e("GestionHelper", "Archivo copiado pero no existe o está vacío");
                return null;
            }
        } catch (java.io.FileNotFoundException e) {
            android.util.Log.e("GestionHelper", "Archivo no encontrado", e);
            return null;
        } catch (java.io.IOException e) {
            android.util.Log.e("GestionHelper", "Error de IO al copiar archivo", e);
            return null;
        } catch (Exception e) {
            android.util.Log.e("GestionHelper", "Error inesperado al copiar archivo", e);
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                android.util.Log.e("GestionHelper", "Error cerrando streams", e);
            }
        }
    }

    private static String getMimeType(Context context, Uri uri) {
        String mimeType = null;
        try {
            if (uri != null && uri.getScheme() != null && uri.getScheme().equals("content")) {
                android.content.ContentResolver cr = context.getContentResolver();
                mimeType = cr.getType(uri);
            } else if (uri != null) {
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
                if (fileExtension != null && !fileExtension.isEmpty()) {
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
                }
            }
        } catch (Exception e) {
            android.util.Log.w("GestionHelper", "Error obteniendo MIME type", e);
        }
        return mimeType;
    }
}

