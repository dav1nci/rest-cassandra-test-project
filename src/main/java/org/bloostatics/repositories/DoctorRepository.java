package org.bloostatics.repositories;

import org.bloostatics.models.Doctor;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.UUID;

/**
 * Created by stdima on 03.04.16.
 */
public interface DoctorRepository extends CassandraRepository<Doctor> {
    @Query("select * from doctor where email = ?0 and password = ?1")
    Doctor findByEmailAndPassword(String email, String password);
    @Query("select * from doctor where email = ?0")
    Doctor checkIfEmailExists(String email);
    @Query("select * from doctor where id = ?0 ALLOW FILTERING")
    Doctor findById(UUID id);
    @Query("select * from doctor where email = ?0")
    Doctor findByEmail(String email);
}
