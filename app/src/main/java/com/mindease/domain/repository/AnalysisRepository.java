package com.mindease.domain.repository;

import com.mindease.domain.model.AnalysisReport;

public interface AnalysisRepository {
    AnalysisReport generateReport(int days);
}
