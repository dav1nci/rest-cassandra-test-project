package org.bloostatics.controllers;

import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.bloostatics.exceptions.NoSuchDeviceException;
import org.bloostatics.exceptions.NoSuchDoctorException;
import org.bloostatics.exceptions.PatientAlreadyExistsException;
import org.bloostatics.models.Device;
import org.bloostatics.models.Doctor;
import org.bloostatics.models.Patient;
import org.bloostatics.repositories.DeviceRepository;
import org.bloostatics.repositories.DoctorRepository;
import org.bloostatics.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    private CassandraOperations cassandraOperations;


    @RequestMapping(value = "/addNewPatient", method = RequestMethod.POST)
    public Patient addNewPatient(@RequestBody Patient patient)
    {
        Doctor doctor = doctorRepository.findById(patient.getDoctorId());
        if (doctor == null)
            throw new NoSuchDoctorException();
        Device device = deviceRepository.findById(patient.getDeviceId());
        if (device == null)
            throw new NoSuchDeviceException();
        else if (device != null)
            /*if (device.getIsEmpty() == 0){
                System.out.println("Device is buisy");
                throw new NoSuchDeviceException();
            }*/
        if (patientRepository.findByEmail(patient.getEmail()) != null)
            throw new PatientAlreadyExistsException();
        else {
            patient.setRegistrationDate(new Date());
            deviceRepository.setDeviceIsBusy(patient.getDeviceId());
            Map<String, Double> diagnosis = new HashMap<>();
            List<String> analysis = new ArrayList<>();
            patient.setAnalyses(analysis);
            patient.setDiagnosis(diagnosis);
            System.out.println("IN front of saving patient");
            patientRepository.save(patient);
            System.out.println("After save patient");
            Insert patientsByDoctor = QueryBuilder.insertInto("patients_by_doctor");
            patientsByDoctor.value("doctor_id", patient.getDoctorId());
            patientsByDoctor.value("surname", patient.getSurname());
            patientsByDoctor.value("name", patient.getName());
            patientsByDoctor.value("email", patient.getEmail());
            cassandraOperations.execute(patientsByDoctor);
            Insert generalBloodAnalysis = QueryBuilder.insertInto("general_blood_analysis");
            generalBloodAnalysis.value("patient_email", patient.getEmail());
            generalBloodAnalysis.value("day", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            generalBloodAnalysis.value("event_time", new Date().getTime());
            cassandraOperations.execute(generalBloodAnalysis);
        }
        return patient;
    }
}
