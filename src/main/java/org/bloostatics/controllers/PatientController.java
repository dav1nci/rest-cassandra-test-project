package org.bloostatics.controllers;

import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import org.bloostatics.exceptions.*;
import org.bloostatics.models.*;
import org.bloostatics.repositories.DeviceRepository;
import org.bloostatics.repositories.DoctorRepository;
import org.bloostatics.repositories.GeneralBloodAnalysisRepository;
import org.bloostatics.repositories.PatientRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.hateoas.alps.Doc;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private GeneralBloodAnalysisRepository generalBloodAnalysisRepository;


    @RequestMapping(value = "/addNewPatient", method = RequestMethod.POST)
    public Patient addNewPatient(@RequestBody Patient patient)
    {
        System.out.println("Request on adding patient");
        Doctor doctor = doctorRepository.findById(patient.getDoctorId());
        if (doctor == null)
            throw new NoSuchDoctorException();
        /*Device device = deviceRepository.findById(patient.getDeviceId());
        if (device == null)
            throw new NoSuchDeviceException();
        else if (device != null)
            if (device.getIsEmpty() == 0){
                System.out.println("Device is busy");
                throw new NoSuchDeviceException();
            }*/
        if (patientRepository.findByEmail(patient.getEmail()) != null)
            throw new PatientAlreadyExistsException();
        else {
            patient.setRegistrationDate(new Date());
            //deviceRepository.setDeviceIsBusy(patient.getDeviceId());
            Map<String, Double> diagnosis = new HashMap<>();
            List<String> analysis = new ArrayList<>();
            analysis.add("general_blood_analysis");
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
            generalBloodAnalysis.value("event_time", new Date().getTime());
            generalBloodAnalysis.value("analysis", new HashMap<>());
            cassandraOperations.execute(generalBloodAnalysis);
        }
        return patient;
    }

    @RequestMapping(value = "/addGeneralAnalysisData", method = RequestMethod.POST)
    public GeneralBloodAnalysis addGeneralAnalysisData(@RequestBody GeneralBloodAnalysis analysis)
    {
        System.out.println("Request on add analysis data");
        Patient patient = patientRepository.findByEmail(analysis.getKey().getPatientEmail());
        if (patient == null)
            throw new NoSuchPatientException();
        else if (patient.getAnalyses().contains("general_blood_analysis")){
            analysis.getKey().setEventTime(new Date());
            /*Insert generalBloodAnalysis = QueryBuilder.insertInto("general_blood_analysis");
            generalBloodAnalysis.value("patient_email", analysis.getPatientEmail());
            generalBloodAnalysis.value("event_time", new Date().getTime());
            generalBloodAnalysis.value("analyses", analysis.getAnalysis());
            cassandraOperations.execute(generalBloodAnalysis);*/
            generalBloodAnalysisRepository.save(analysis);
            return analysis;
        }else
            throw new NoSuchAnalysisException();
    }

    @RequestMapping(value = "/getAnalyses", method = RequestMethod.POST)
    public String getAnalyses(@RequestBody Patient patient){
        System.out.println("Request on get analyses " + patient.getEmail());

        JSONObject result = new JSONObject();
        JSONArray analysisData = new JSONArray();
        Patient patientFromDB = patientRepository.findByEmailAndPassword(patient.getEmail(), patient.getPassword());
        Doctor doctorFromDB = doctorRepository.findByEmail(patient.getEmail());
        System.out.println("Doctor is " + doctorFromDB.getEmail());
        if (patientFromDB == null) {
            if (doctorFromDB == null) {
                throw new NoSuchPatientException();
            }
        }
        else if (doctorFromDB == null) {
            if (patientFromDB == null) {
                throw new NoSuchDoctorException();
            }
        }
        else if (patientFromDB != null) {
            result.put("role", "PATIENT");
            List<GeneralBloodAnalysis> analysisFromDB = generalBloodAnalysisRepository.findByEmail(patient.getEmail());
            for (GeneralBloodAnalysis i : analysisFromDB) {
                JSONObject item = new JSONObject();
                item.put("event_time", i.getKey().getEventTime());
                for (Map.Entry<String, Double> entry : i.getAnalysis().entrySet()) {
                    if (entry.getKey().equals("ageCategory"))
                        continue;
                    item.put(entry.getKey(), entry.getValue());
                }
                analysisData.put(item);
            }
            result.put("name", patientFromDB.getName());
            result.put("surname", patientFromDB.getSurname());
            result.put("analyses", analysisData);
        }

        else if (doctorFromDB != null) {
            System.out.println("Doctor here");
            result.put("role", "DOCTOR");
            result.put("name", doctorFromDB.getName());
            result.put("surname", doctorFromDB.getSurname());
            result.put("id", doctorFromDB.getId());
        }
//      result.put("birthday", new Date());
        return result.toString();
    }

    @RequestMapping(value = "/find", method = RequestMethod.POST)
    public String findPatients(@RequestBody Map<String, String> request){
        System.out.println("Request on finding patients");
        Patient patient = patientRepository.findByNameAndSurname(request.get("name"), request.get("surname"));
        if (patient == null)
            throw new NoSuchPatientException();
        JSONObject responce = new JSONObject();
        responce.put("name", patient.getName());
        responce.put("surname", patient.getSurname());
        responce.put("email", patient.getEmail());
//        Select select = QueryBuilder.select().from("patients_by_doctor");
//        select.where(QueryBuilder.gt("surname", request.get("surname")));
//        System.out.println("id = " + request.get("doctor_id"));
//        System.out.println("surname = " + request.get("surname"));
        return responce.toString(); //patientRepository.findBySurname(UUID.fromString(request.get("doctor_id")),request.get("surname"));
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

        List<GeneralBloodAnalysisData> trainingSet = new ArrayList<>();
        for (int i = 0; i < ageCategories.length; ++i)
        {
            for (int j = 0; j < 1000; ++j)
            {
                GeneralBloodAnalysisData buffer = new GeneralBloodAnalysisData();
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
