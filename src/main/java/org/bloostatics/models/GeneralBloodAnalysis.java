package org.bloostatics.models;

import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

import java.util.Map;
import java.util.UUID;

/**
 * Created by stdima on 10.04.16.
 */
@PrimaryKeyClass
public class GeneralBloodAnalysis
{
    @PrimaryKeyColumn(name = "patient_email", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String patient_email;
    @PrimaryKeyColumn(name = "day", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String day;
    @PrimaryKeyColumn(name = "event_time", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private long eventTime;
    @Column(value = "analysis")
    private Map<String, Double> analysis;

    public String getPatient_email() {
        return patient_email;
    }

    public void setPatient_email(String patient_email) {
        this.patient_email = patient_email;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public Map<String, Double> getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Map<String, Double> analysis) {
        this.analysis = analysis;
    }
}
