package com.bofa.compliance.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class KycVerificationRequest {

    @NotBlank
    private String userId;

    @Valid
    @NotNull
    private KycDocument document;

    @Valid
    @NotNull
    private UserAddress address;

    @NotNull
    private byte[] selfie;

    @NotNull
    private byte[] idPhoto;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public KycDocument getDocument() {
        return document;
    }

    public void setDocument(KycDocument document) {
        this.document = document;
    }

    public UserAddress getAddress() {
        return address;
    }

    public void setAddress(UserAddress address) {
        this.address = address;
    }

    public byte[] getSelfie() {
        return selfie;
    }

    public void setSelfie(byte[] selfie) {
        this.selfie = selfie;
    }

    public byte[] getIdPhoto() {
        return idPhoto;
    }

    public void setIdPhoto(byte[] idPhoto) {
        this.idPhoto = idPhoto;
    }
}
