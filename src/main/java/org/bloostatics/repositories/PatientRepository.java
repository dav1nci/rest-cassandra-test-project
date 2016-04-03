package org.bloostatics.repositories;

import org.bloostatics.models.Patient;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

/**
 * Created by stdima on 03.04.16.
 */
public interface PatientRepository extends CassandraRepository<Patient> {

    @Query("select * from patient where email = ?0")
    Patient findByEmail(String email);
}
