package org.bloostatics.models;

import org.springframework.data.cassandra.mapping.*;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by stdima on 10.04.16.
 */
@Table(value = "general_blood_analysis")
public class GeneralBloodAnalysis implements Serializable
{
    @PrimaryKey
    private GeneralBloodAnalysisKey key;
    @Column(value = "analysis")
    private Map<String, Double> analysis;

    public GeneralBloodAnalysisKey getKey() {
        return key;
    }

    public void setKey(GeneralBloodAnalysisKey key) {
        this.key = key;
    }

    public Map<String, Double> getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Map<String, Double> analysis) {
        this.analysis = analysis;
    }
}
