package com.bofa.compliance.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class KycDocument {
    @NotBlank
    private String documentNumber;
    @NotBlank
    private String documentType;
    private LocalDate expiryDate;
    private String issuingCountry;
    private int ocrConfidence;

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getIssuingCountry() {
        return issuingCountry;
    }

    public void setIssuingCountry(String issuingCountry) {
        this.issuingCountry = issuingCountry;
    }

    public int getOcrConfidence() {
        return ocrConfidence;
    }

    public void setOcrConfidence(int ocrConfidence) {
        this.ocrConfidence = ocrConfidence;
    }
}
