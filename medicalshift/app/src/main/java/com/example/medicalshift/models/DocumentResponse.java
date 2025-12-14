package com.example.medicalshift.models;

public class DocumentResponse {
    private String message;
    private FileData file;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public FileData getFile() { return file; }
    public void setFile(FileData file) { this.file = file; }

    public static class FileData {
        private String id;
        private String url;
        private String fileName;
        private String originalName;
        private String gestionId;
        private Long size;
        private String contentType;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getOriginalName() { return originalName; }
        public void setOriginalName(String originalName) { this.originalName = originalName; }
        public String getGestionId() { return gestionId; }
        public void setGestionId(String gestionId) { this.gestionId = gestionId; }
        public Long getSize() { return size; }
        public void setSize(Long size) { this.size = size; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
    }
}




