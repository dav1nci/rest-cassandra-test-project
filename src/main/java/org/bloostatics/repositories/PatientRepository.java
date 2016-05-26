package org.bloostatics.repositories;

import org.bloostatics.models.Patient;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;
import java.util.UUID;

/**
 * Created by stdima on 03.04.16.
 */
public interface PatientRepository extends CassandraRepository<Patient> {

    @Query("select * from patient where email = ?0 and password = ?1")
    Patient findByEmailAndPassword(String email, String password);
    @Query("select * from patient where email = ?0")
    Patient findByEmail(String email);
    @Query("select * from patients_by_doctor where name = ?0 AND surname = ?1 ALLOW FILTERING")
    Patient findByNameAndSurname(String name, String surname);
}
