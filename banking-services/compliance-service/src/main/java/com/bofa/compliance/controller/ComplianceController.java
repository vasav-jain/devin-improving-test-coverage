package com.bofa.compliance.controller;

import com.bofa.compliance.dto.*;
import com.bofa.compliance.service.ComplianceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class ComplianceController {

    private final ComplianceService complianceService;

    public ComplianceController(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }

    @PostMapping("/aml/check")
    public AmlResult runAmlCheck(@Valid @RequestBody TransactionProfile profile) {
        return complianceService.runAmlCheck(profile);
    }

    @PostMapping("/kyc/verify")
    public KycVerificationResult verifyKyc(@Valid @RequestBody KycVerificationRequest request) {
        return complianceService.verifyKyc(request);
    }

    @PostMapping("/fraud/score")
    public int calculateFraudScore(@Valid @RequestBody FraudProfile profile) {
        return complianceService.calculateFraudScore(profile);
    }

    @GetMapping("/compliance/report/{userId}")
    public ComplianceReport generateComplianceReport(@PathVariable String userId) {
        return complianceService.generateComplianceReport(userId);
    }
}
