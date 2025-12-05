package com.bofa.compliance.service;

import com.bofa.compliance.dto.AmlResult;
import com.bofa.compliance.dto.RiskChannel;
import com.bofa.compliance.dto.TransactionProfile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class AmlRuleEngine {

    private static final Set<String> HIGH_RISK_COUNTRIES = Set.of("IR", "KP", "SY", "RU");
    private static final BigDecimal DAILY_THRESHOLD = BigDecimal.valueOf(25000);
    private static final BigDecimal WEEKLY_THRESHOLD = BigDecimal.valueOf(75000);

    public AmlResult runRules(TransactionProfile profile) {
        List<String> reasons = new ArrayList<>();
        int score = 0;

        if (profile.getAmount().compareTo(profile.getAverageDailyAmount().multiply(BigDecimal.valueOf(4))) > 0) {
            reasons.add("Velocity spike exceeds 4x daily average");
            score += 30;
        }
        if (profile.getAverageDailyAmount().compareTo(DAILY_THRESHOLD) > 0 ||
                profile.getWeeklyVolume().compareTo(WEEKLY_THRESHOLD) > 0) {
            reasons.add("Volume exceeds configured threshold");
            score += 25;
        }
        if (HIGH_RISK_COUNTRIES.contains(profile.getCounterpartyCountry())) {
            reasons.add("Counterparty located in high-risk country");
            score += 30;
        }
        if (profile.getSanctionsMatches() > 0) {
            reasons.add("Sanctions list produced " + profile.getSanctionsMatches() + " hits");
            score += 40;
        }
        if (profile.getChannel() == RiskChannel.ONLINE && profile.getAmount().compareTo(BigDecimal.valueOf(10000)) > 0) {
            reasons.add("Large online transfer");
            score += 15;
        }
        boolean flagged = score >= 50 || !reasons.isEmpty();
        return new AmlResult(flagged, score, reasons);
    }
}
