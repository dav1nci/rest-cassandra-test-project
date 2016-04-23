package org.bloostatics.controllers;

import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bloostatics.exceptions.*;
import org.bloostatics.models.*;
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
import java.util.concurrent.ThreadLocalRandom;

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
            if (device.getIsEmpty() == 0){
                System.out.println("Device is buisy");
                throw new NoSuchDeviceException();
            }
        if (patientRepository.findByEmail(patient.getEmail()) != null)
            throw new PatientAlreadyExistsException();
        else {
            patient.setRegistrationDate(new Date());
            deviceRepository.setDeviceIsBusy(patient.getDeviceId());
            Map<String, Double> diagnosis = new HashMap<>();
            List<String> analysis = new ArrayList<>();
            patient.setAnalyses(analysis);
            patient.setDiagnosis(diagnosis);
            patientRepository.save(patient);

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

    @RequestMapping(value = "/addGeneralAnalysisData", method = RequestMethod.POST)
    public GeneralBloodAnalysis addGeneralAnalysisData(@RequestBody GeneralBloodAnalysis analysis)
    {
        Patient patient = patientRepository.findByEmail(analysis.getPatient_email());
        if (patient == null)
            throw new NoSuchPatientException();
        else if (patient.getAnalyses().contains("general_blood_analysis")){
            Insert generalBloodAnalysis = QueryBuilder.insertInto("general_blood_analysis");
            generalBloodAnalysis.value("patient_email", analysis.getPatient_email());
            generalBloodAnalysis.value("event_time", analysis.getEventTime());
            generalBloodAnalysis.value("analyses", analysis.getAnalysis());
            cassandraOperations.execute(generalBloodAnalysis);
            return analysis;
        }else
            throw new NoSuchAnalysisException();
    }

    @RequestMapping(value = "/generateGeneralBloodAnalysisData")
    public String generateGeneralBloodAnalysisData()
    {
        long startTime = System.currentTimeMillis();
        int [][]ageCategories = {{0, 1}, {1, 2}, {2, 4}, {4, 6}, {6, 8}, {8, 9}, {9, 10}, {10, 13}, {13, 15}, {15, 17}, {17, 18}, {18, 45}, {45, 65}, {65, 150}};
        double [][]leukocytes = {{6e9, 17.5e9}, {6e9, 17e9}, {5.5e9, 15.5e9}, {5e9, 14.5e9}, {4.5e9, 13.5e9}, {4.5e9, 13.5e9}, {4.5e9, 13.5e9}, {4.5e9, 13e9}, {4.5e9, 13e9}, {4.5e9, 13e9}, {4e9, 10e9}, {4e9, 10e9}, {4e9, 10e9}, {4e9, 10e9}};
        double [][]erythrocytes = {{4.1e12, 5.3e12}, {4.0e12, 4.4e12}, {4.0e12, 4.4e12}, {4.1e12, 4.5e12}, {4.1e12, 4.5e12}, {4.1e12, 4.5e12}, {4.2e12, 4.6e12}, {4.2e12, 4.6e12}, {4.4e12, 4.8e12}, {3.9e12, 5.6e12}, {3.9e12, 5.6e12}, {3.5e12, 5.2e12}, {3.5e12, 5.2e12}, {3.5e12, 5.2e12}};
        double [][]hemoglobin = {{110.0, 131.0}, {110.0, 132.0}, {111.0, 134.0}, {113.0, 135.0}, {115.0, 138.0}, {115.0, 137.0}, {118.0, 138.0}, {115.0, 142.0}, {120.0, 145.0}, {130.0, 168.0}, {120.0, 148.0}, {132.0, 173.0}, {131.0, 172.0}, {126.0, 174.0}};
        double [][]hematocrit = {{33.0, 41.0}, {32.0, 40.0}, {32.0, 40.0}, {32.0, 42.0}, {33.0, 41.0}, {33.0, 41.0}, {34.0, 43.0}, {34.0, 43.0}, {35.0, 45.0}, {37.0, 48.0}, {37.0, 48.0}, {39.0, 49.0}, {39.0, 50.0}, {39.0, 50.0}};
        double [][]erythrocytesMedian = {{71, 112}, {73, 85}, {73, 85}, {75, 87}, {75, 87}, {75, 87}, {75, 87}, {76, 94}, {77, 94}, {79, 95}, {79, 95}, {80, 99}, {81, 101}, {81, 102}};
        double [][]hemoglobinInErythrocyte = {{31, 37}, {24, 33}, {24, 33}, {25, 33}, {25, 33}, {25, 33}, {25, 33}, {25, 33}, {26, 32}, {26, 32}, {26, 32}, {27, 31}, {27, 31}, {27, 31}};
        double [][]hemoglobinAverageInErythrocyte = {{290, 370}, {280, 380}, {280, 370}, {280, 360}, {280, 360}, {280, 360}, {280, 360}, {280, 360}, {330, 340}, {330, 340}, {330, 340}, {300, 380}, {300, 380}, {300, 380}};
        double [][]platelets = {{100e9, 400e9}, {150e9, 400e9}, {150e9, 400e9}, {170e9, 420e9}, {180e9, 450e9}, {180e9, 450e9}, {180e9, 450e9}, {150e9, 450e9}, {150e9, 450e9}, {180e9, 320e9}, {180e9, 320e9}, {180e9, 320e9}, {180e9, 320e9}, {180e9, 320e9}};

        List<GeneralBloodAnalysisTrainingSet> trainingSet = new ArrayList<>();
        for (int i = 0; i < ageCategories.length; ++i)
        {
            for (int j = 0; j < 1000; ++j)
            {
                GeneralBloodAnalysisTrainingSet buffer = new GeneralBloodAnalysisTrainingSet();
                buffer.setAgeCategory(ThreadLocalRandom.current().nextInt(ageCategories[i][0], ageCategories[i][1] + 1));
                buffer.setLeukocytes(ThreadLocalRandom.current().nextDouble(leukocytes[i][0], leukocytes[i][1] + 1));
                buffer.setErythrocytes(ThreadLocalRandom.current().nextDouble(erythrocytes[i][0], erythrocytes[i][1] + 1));
                buffer.setHemoglobin(ThreadLocalRandom.current().nextDouble(hemoglobin[i][0], hemoglobin[i][1] + 1));
                buffer.setHematocrit(ThreadLocalRandom.current().nextDouble(hematocrit[i][0], hematocrit[i][1] + 1));
                buffer.setErythrocytesMedian(ThreadLocalRandom.current().nextDouble(erythrocytesMedian[i][0], erythrocytesMedian[i][1] + 1));
                buffer.setHemoglobinInErythrocyte(ThreadLocalRandom.current().nextDouble(hemoglobinInErythrocyte[i][0], hemoglobinInErythrocyte[i][1] + 1));
                buffer.setHemoglobinAverageInErythrocyte(ThreadLocalRandom.current().nextDouble(hemoglobinAverageInErythrocyte[i][0], hemoglobinAverageInErythrocyte[i][1] + 1));
                buffer.setPlatelets(ThreadLocalRandom.current().nextDouble(platelets[i][0], platelets[i][1] + 1));
                trainingSet.add(buffer);
            }
        }
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(trainingSet);
        System.out.println("Total time: [" + (System.currentTimeMillis() - startTime) + "] ms");
        return json;
    }
}
