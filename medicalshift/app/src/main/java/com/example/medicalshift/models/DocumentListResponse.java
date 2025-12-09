package com.example.medicalshift.models;

import java.util.List;

public class DocumentListResponse {
    private String message;
    private Integer count;
    private List<DocumentItem> documents;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
    public List<DocumentItem> getDocuments() { return documents; }
    public void setDocuments(List<DocumentItem> documents) { this.documents = documents; }

    public static class DocumentItem {
        private String id;
        private String gestionId;
        private String userId;
        private String fileName;
        private String originalName;
        private String contentType;
        private Long size;
        private String url;
        private Long uploadedAt;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getGestionId() { return gestionId; }
        public void setGestionId(String gestionId) { this.gestionId = gestionId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getOriginalName() { return originalName; }
        public void setOriginalName(String originalName) { this.originalName = originalName; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        public Long getSize() { return size; }
        public void setSize(Long size) { this.size = size; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public Long getUploadedAt() { return uploadedAt; }
        public void setUploadedAt(Long uploadedAt) { this.uploadedAt = uploadedAt; }
    }
}



