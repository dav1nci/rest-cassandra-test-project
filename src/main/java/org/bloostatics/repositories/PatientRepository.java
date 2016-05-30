package org.bloostatics.repositories;

import org.bloostatics.models.Patient;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;
import java.util.Map;
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
    @Query("select * from patient WHERE diagnosis CONTAINS -1 LIMIT 1")
    Patient findWithNoDiagnose();
    @Query("update patient set diagnosis = {?0 : ?1} where email = ?2 and password = ?3 and surname = ?4 and name = ?5")
    Patient setDiagnosis(String diagnosis, Double probability, String email, String password, String surname, String name);
}
