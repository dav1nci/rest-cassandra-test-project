package org.bloostatics.controllers;

import org.bloostatics.exceptions.NoSuchDeviceException;
import org.bloostatics.exceptions.NoSuchDoctorException;
import org.bloostatics.exceptions.PatientAlreadyExistsException;
import org.bloostatics.models.Doctor;
import org.bloostatics.models.Patient;
import org.bloostatics.repositories.DeviceRepository;
import org.bloostatics.repositories.DoctorRepository;
import org.bloostatics.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stdima on 03.04.16.
 */
@RestController
@RequestMapping(value = "/patient")
public class PatientController {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DeviceRepository deviceRepository;

    @RequestMapping(value = "/addNewPatient", method = RequestMethod.POST)
    public Patient addNewPatient(@RequestBody Patient patient)
    {
        Doctor doctor = doctorRepository.findById(patient.getDoctorId());
        if (doctor == null)
            throw new NoSuchDoctorException();
        else if (deviceRepository.findById(patient.getDeviceId()) == null)
            throw new NoSuchDeviceException();
        else if (patientRepository.findByEmail(patient.getEmail()) != null)
            throw new PatientAlreadyExistsException();
        else {
            patient.setRegistrationDate(new Date());
            Map<String, Double> diagnosis = new HashMap<>();
            Map<String, Double> analysis = new HashMap<>();
            diagnosis.put("Diagnoz1" , 0.97);
            diagnosis.put("Diagnoz2", 0.85);
            analysis.put("Leikocyty", 192.0);
            analysis.put("Trombocyty", 15.0);
            patient.setAnalysis(analysis);
            patient.setDiagnosis(diagnosis);
            patientRepository.save(patient);
        }
        return patient;
    }
}
