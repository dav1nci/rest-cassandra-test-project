package org.bloostatics.controllers;

import org.bloostatics.exceptions.EmailAlreadyExistsException;
import org.bloostatics.models.Doctor;
import org.bloostatics.repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Created by stdima on 03.04.16.
 */
@RestController
@RequestMapping(value = "/doctor")
public class DoctorController {
    @Autowired
    private DoctorRepository doctorRepository;

    @RequestMapping(value = "/addNewDoctor", method = RequestMethod.POST)
    public Doctor addNewDoctor(@RequestBody Doctor doctor)
    {
        System.out.println("New doctor");
        if (doctorRepository.checkIfEmailExists(doctor.getEmail()) != null)
            throw new EmailAlreadyExistsException();
        else {
            doctor.setRegistrationDate(new Date());
            doctorRepository.save(doctor);
        }
        return doctor;
    }
}
