package org.bloostatics.repositories;

import org.bloostatics.models.Doctor;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

/**
 * Created by stdima on 03.04.16.
 */
public interface DoctorRepository extends CassandraRepository<Doctor> {
    @Query("select * from doctor where doctor.email = ?1 and doctor.password = ?2")
    Doctor findByEmailAndPassword(String email, String password);
}
