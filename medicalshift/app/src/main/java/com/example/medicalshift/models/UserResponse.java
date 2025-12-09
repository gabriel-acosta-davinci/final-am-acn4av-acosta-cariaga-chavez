package com.example.medicalshift.models;

public class UserResponse {
    private String uid;
    private String email;
    private Boolean emailVerified;
    private String fullName;
    private String documentNumber;
    private String phoneNumber;
    private String plan;
    private String associateNumber;
    private String cbu;
    private Object dateOfBirth; // Puede venir como Timestamp de Firestore o Date
    private String maritalStatus;
    private Address address;

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    public String getAssociateNumber() { return associateNumber; }
    public void setAssociateNumber(String associateNumber) { this.associateNumber = associateNumber; }
    public String getCbu() { return cbu; }
    public void setCbu(String cbu) { this.cbu = cbu; }
    public Object getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Object dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public static class Address {
        private String street;
        private Integer number;
        private String city;
        private String province;
        private String floor;
        private String apartment;

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        public Integer getNumber() { return number; }
        public void setNumber(Integer number) { this.number = number; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        public String getFloor() { return floor; }
        public void setFloor(String floor) { this.floor = floor; }
        public String getApartment() { return apartment; }
        public void setApartment(String apartment) { this.apartment = apartment; }
    }
}

