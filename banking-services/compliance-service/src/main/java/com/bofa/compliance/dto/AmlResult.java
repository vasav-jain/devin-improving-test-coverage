package com.bofa.compliance.dto;

import java.util.List;

public class AmlResult {
    private final boolean flagged;
    private final int score;
    private final List<String> reasons;

    public AmlResult(boolean flagged, int score, List<String> reasons) {
        this.flagged = flagged;
        this.score = score;
        this.reasons = reasons;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public int getScore() {
        return score;
    }

    public List<String> getReasons() {
        return reasons;
    }
}
