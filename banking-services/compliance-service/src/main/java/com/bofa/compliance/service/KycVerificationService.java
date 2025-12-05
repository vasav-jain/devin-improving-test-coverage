package com.bofa.compliance.service;

import com.bofa.compliance.dto.KycDocument;
import com.bofa.compliance.dto.KycVerificationRequest;
import com.bofa.compliance.dto.KycVerificationResult;
import com.bofa.compliance.dto.UserAddress;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Locale;

@Service
public class KycVerificationService {

    public KycVerificationResult verifyKyc(KycVerificationRequest request) {
        boolean docValid = verifyDocumentOcr(request.getDocument());
        boolean faceMatch = matchFaceImage(request.getSelfie(), request.getIdPhoto());
        boolean addressValid = validateAddress(request.getAddress());

        if (docValid && faceMatch && addressValid) {
            return new KycVerificationResult(true, null);
        }
        String reason = !docValid ? "DOCUMENT_EXPIRED"
                : !faceMatch ? "FACE_MISMATCH"
                : "ADDRESS_INVALID";
        return new KycVerificationResult(false, reason);
    }

    public boolean verifyDocumentOcr(KycDocument doc) {
        if (doc.getExpiryDate() != null && doc.getExpiryDate().isBefore(LocalDate.now())) {
            return false;
        }
        return doc.getOcrConfidence() >= 75;
    }

    public boolean matchFaceImage(byte[] selfie, byte[] idPhoto) {
        if (selfie == null || idPhoto == null) {
            return false;
        }
        int diff = Math.abs(selfie.length - idPhoto.length);
        return diff < (0.1 * Math.max(selfie.length, 1));
    }

    public boolean validateAddress(UserAddress address) {
        return address.getPostalCode().matches("[0-9A-Z-]{5,}") &&
                !address.getState().isBlank() &&
                address.getCountry().toUpperCase(Locale.ROOT).length() == 2;
    }
}
