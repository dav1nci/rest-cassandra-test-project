package org.bloostatics.models;

import com.datastax.driver.core.utils.UUIDs;
import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * Created by stdima on 02.04.16.
 */
@PrimaryKeyClass
public class Patient implements Serializable
{
    @PrimaryKeyColumn(name = "doctor_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID id = UUIDs.timeBased();
    @PrimaryKeyColumn(name = "email", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private String email;
    @PrimaryKeyColumn(name = "password", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private String password;
    @Column(value = "name")
    private String name;
    @Column(value = "surname")
    private String surname;
    @Column(value = "diagnosis")
    private Map<String, Double> diagnosis;
    @Column(value = "analysis")
    private Map<String, Double> analysis;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Map<String, Double> getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(Map<String, Double> diagnosis) {
        this.diagnosis = diagnosis;
    }

    public Map<String, Double> getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Map<String, Double> analysis) {
        this.analysis = analysis;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Patient other = (Patient) obj;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (surname == null) {
            if (other.surname != null)
                return false;
        } else if (!surname.equals(other.surname))
            return false;
        return true;
    }
}
