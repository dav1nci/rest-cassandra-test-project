package org.bloostatics.controllers;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import org.bloostatics.exceptions.EmailAlreadyExistsException;
import org.bloostatics.models.Doctor;
import org.bloostatics.repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by stdima on 03.04.16.
 */
@RestController
public class AuthorizationController
{
    @Autowired
    private CassandraOperations cassandraOperations;

    @RequestMapping(value = "/generateSomeDoctors", method = RequestMethod.GET)
    public List<Doctor> generateSomeDoctors()
    {
        List<Doctor> doctors = new ArrayList<>(10);
        for (int i = 1; i < 11; ++i)
        {
            Doctor doctorsBuffer = new Doctor("doctor" + i + "@mail.com", "password" + i, "NameDoctor" + i, "SurnameDoctor" + i);
            doctorsBuffer.setRegistrationDate(new Date());
            doctors.add(doctorsBuffer);
            cassandraOperations.insert(doctorsBuffer);
        }
        return doctors;
    }

    @RequestMapping(value = "patient/addPatient")
    public String addPatient()
    {
        return null;
    }
}
