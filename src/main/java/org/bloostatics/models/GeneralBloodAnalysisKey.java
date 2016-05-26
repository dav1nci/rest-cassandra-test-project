package org.bloostatics.models;

import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by stdima on 20.05.16.
 */
@PrimaryKeyClass
public class GeneralBloodAnalysisKey implements Serializable{

    @PrimaryKeyColumn(name = "patient_email", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String patientEmail;
    @PrimaryKeyColumn(name = "event_time", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Date eventTime;

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public int hashCode() {
        return eventTime.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GeneralBloodAnalysisKey other = (GeneralBloodAnalysisKey) obj;
        if (patientEmail == null) {
            if (other.patientEmail != null)
                return false;
        } else if (!patientEmail.equals(other.getPatientEmail()))
            return false;
        return true;
    }


}
