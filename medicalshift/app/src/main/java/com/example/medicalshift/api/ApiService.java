package com.example.medicalshift.api;

import com.example.medicalshift.models.LoginRequest;
import com.example.medicalshift.models.LoginResponse;
import com.example.medicalshift.Gestion;
import com.example.medicalshift.models.GestionRequest;
import com.example.medicalshift.models.GestionResponse;
import com.example.medicalshift.Factura;
import com.example.medicalshift.models.FacturaResponse;
import com.example.medicalshift.models.UserResponse;
import com.example.medicalshift.models.DocumentResponse;
import com.example.medicalshift.models.DocumentListResponse;
import com.example.medicalshift.models.UpdateUserRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    
    // Autenticación
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    
    @GET("auth/me")
    Call<UserResponse> getCurrentUser(@Header("Authorization") String token);
    
    // Gestiones
    @GET("gestiones")
    Call<GestionResponse> getGestiones(
        @Header("Authorization") String token,
        @Query("userId") String userId,
        @Query("estado") String estado,
        @Query("limit") Integer limit
    );
    
    @POST("gestiones")
    Call<com.example.medicalshift.models.CreateGestionResponse> createGestion(
        @Header("Authorization") String token,
        @Body GestionRequest request
    );
    
    // Facturas
    @GET("facturas")
    Call<FacturaResponse> getFacturas(
        @Header("Authorization") String token,
        @Query("estado") String estado,
        @Query("limit") Integer limit
    );
    
    // Storage/Documentos
    @Multipart
    @POST("storage/gestion/{gestionId}")
    Call<DocumentResponse> uploadDocument(
        @Header("Authorization") String token,
        @Path("gestionId") String gestionId,
        @Part MultipartBody.Part file
    );
    
    @GET("storage/documents")
    Call<DocumentListResponse> getDocuments(
        @Header("Authorization") String token,
        @Query("gestionId") String gestionId,
        @Query("limit") Integer limit
    );
    
    // Actualizar usuario autenticado
    @PUT("users/me")
    Call<UserResponse> updateCurrentUser(
        @Header("Authorization") String token,
        @Body com.example.medicalshift.models.UpdateUserRequest request
    );
}

