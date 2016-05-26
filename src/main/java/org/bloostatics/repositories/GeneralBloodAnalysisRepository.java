package org.bloostatics.repositories;

import org.bloostatics.models.GeneralBloodAnalysis;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

/**
 * Created by stdima on 20.05.16.
 */
public interface GeneralBloodAnalysisRepository extends CassandraRepository<GeneralBloodAnalysis> {
    @Query("select * from general_blood_analysis WHERE patient_email = ?0 LIMIT 14")
    List<GeneralBloodAnalysis> findByEmail(String email);
}
