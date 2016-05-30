package org.bloostatics.controllers;

import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
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

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

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
    private List<List<Double>> trainigSet = new ArrayList<>(80000);


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
            diagnosis.put("none", -1.0);
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

            String hackJson = "{\n" +
                    "    \"key\" : {\n" +
                    "        \"patientEmail\" : \"" + patient.getEmail() + "\"    \n" +
                    "    },\n" +
                    "    \"analysis\" : {\n" +
                    "        \"ageCategory\": 18,\n" +
                    "        \"leukocytes\": 8.5811964401304646E9,\n" +
                    "        \"erythrocytes\": 3.2793520121157227E12,\n" +
                    "        \"hemoglobin\": 125.41453757700343,\n" +
                    "        \"hematocrit\": 50.75135968224013,\n" +
                    "        \"erythrocytesMedian\": 93.07321262343845,\n" +
                    "        \"hemoglobinInErythrocyte\": 35.37189449096424,\n" +
                    "        \"hemoglobinAverageInErythrocyte\": 290.890230289122,\n" +
                    "        \"platelets\": 2.8670093272228476E11\n" +
                    "  }\n" +
                    "}";
            GeneralBloodAnalysis buffer = new Gson().fromJson(hackJson, GeneralBloodAnalysis.class);
            buffer.getKey().setEventTime(new Date());
            generalBloodAnalysisRepository.save(buffer);
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
        Patient patientFromDB = patientRepository.findByEmail(patient.getEmail());
        Doctor doctorFromDB = doctorRepository.findByEmail(patient.getEmail());
        if (patientFromDB == null) {
            if (doctorFromDB == null) {
                throw new NoSuchPatientException();
            }
        }
        if (doctorFromDB == null) {
            if (patientFromDB == null) {
                throw new NoSuchDoctorException();
            }
        }
        if (patientFromDB != null) {
            System.out.println("find patient in db");
            result.put("role", "PATIENT");
            List<GeneralBloodAnalysis> analysisFromDB = generalBloodAnalysisRepository.findByEmail(patient.getEmail());
            for (GeneralBloodAnalysis i : analysisFromDB) {
                JSONObject item = new JSONObject();
                item.put("event_time", i.getKey().getEventTime());
                /*if (i.getAnalysis() == null)
                    return null;*/
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
            result.put("diagnosis", patientFromDB.getDiagnosis());
        }

        if (doctorFromDB != null) {
            System.out.println("Doctor here");
            result.put("role", "DOCTOR");
            result.put("name", doctorFromDB.getName());
            result.put("surname", doctorFromDB.getSurname());
            result.put("id", doctorFromDB.getId());
        }
//      result.put("birthday", new Date());
        System.out.println("send result");
        return result.toString();
    }

    @RequestMapping(value = "/getPatient", method = RequestMethod.POST)
    public String getPatient(@RequestBody Map<String, String> request){
        Patient patientFromDB = patientRepository.findByEmail(request.get("email"));
        if (patientFromDB == null)
            throw new NoSuchPatientException();
        JSONObject result = new JSONObject();
        JSONArray analysisData = new JSONArray();
        List<GeneralBloodAnalysis> analysisFromDB = generalBloodAnalysisRepository.findByEmail(request.get("email"));
        for (GeneralBloodAnalysis i : analysisFromDB) {
            JSONObject item = new JSONObject();
            item.put("event_time", i.getKey().getEventTime());
            if (i.getAnalysis() == null)
                return null;
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
        result.put("diagnosis", patientFromDB.getDiagnosis());
        return result.toString();
    }

    @RequestMapping(value = "/find", method = RequestMethod.POST)
    public String findPatients(@RequestBody Map<String, String> request){
        System.out.println("Request on finding patients");
        Patient patient = patientRepository.findByNameAndSurname(request.get("name"), request.get("surname"));
        if (patient == null)
            throw new NoSuchPatientException();
        JSONObject item = new JSONObject();
        JSONArray responce = new JSONArray();
        item.put("name", patient.getName());
        item.put("surname", patient.getSurname());
        item.put("email", patient.getEmail());
        responce.put(item);
//        Select select = QueryBuilder.select().from("patients_by_doctor");
//        select.where(QueryBuilder.gt("surname", request.get("surname")));
//        System.out.println("id = " + request.get("doctor_id"));
//        System.out.println("surname = " + request.get("surname"));
        return responce.toString(); //patientRepository.findBySurname(UUID.fromString(request.get("doctor_id")),request.get("surname"));
    }

    /*@RequestMapping(value = "/generateGeneralBloodAnalysisData")*/
    public List<GeneralBloodAnalysisData> abstractPatient(double[] hem, double[]erythr, double[] ley, double[] tromb)
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
        /*for (int i = 0; i < ageCategories.length; ++i) // change 11 in code below to i
        {*/
        for (int j = 0; j < 6000; ++j)
        {
            GeneralBloodAnalysisData buffer = new GeneralBloodAnalysisData();
            buffer.setAgeCategory(ThreadLocalRandom.current().nextInt(ageCategories[11][0], ageCategories[11][1] + 1));
            buffer.setLeukocytes(ThreadLocalRandom.current().nextDouble(ley[0] / 21e9, (ley[1] + 0.1) / 21e9));
            buffer.setErythrocytes(ThreadLocalRandom.current().nextDouble(erythr[0] / 5.2e12, (erythr[1] + 0.1) / 5.2e12));
            buffer.setHemoglobin(ThreadLocalRandom.current().nextDouble(hem[0] / 173.0, (hem[1] + 0.1) / 173.0));
            buffer.setHematocrit(ThreadLocalRandom.current().nextDouble(hematocrit[11][0] / 49.0, (hematocrit[11][1] + 1) / 49.0));
            buffer.setErythrocytesMedian(ThreadLocalRandom.current().nextDouble(erythrocytesMedian[11][0] / 99.0, (erythrocytesMedian[11][1]) / 99.0));
            buffer.setHemoglobinInErythrocyte(ThreadLocalRandom.current().nextDouble(hemoglobinInErythrocyte[11][0] / 31, (hemoglobinInErythrocyte[11][1]) / 31));
            buffer.setHemoglobinAverageInErythrocyte(ThreadLocalRandom.current().nextDouble(hemoglobinAverageInErythrocyte[11][0] / 380.0, (hemoglobinAverageInErythrocyte[11][1] + 1) / 380.0));
            buffer.setPlatelets(ThreadLocalRandom.current().nextDouble(tromb[0] / 600e9, (tromb[1] + 0.1) / 600e9));
            trainingSet.add(buffer);
        }
        //}
        /*String json = new GsonBuilder().setPrettyPrinting().create().toJson(trainingSet);
        System.out.println("Total time: [" + (System.currentTimeMillis() - startTime) + "] ms");*/
        return trainingSet;
    }

    @RequestMapping("/trainigSet")
    public String genPoison(){
        List<GeneralBloodAnalysisData> result = new ArrayList<>(10000);
        result.addAll(abstractPatient(new double[]{130.0, 170.0}, new double[]{2.5e12, 3.1e12}, new double[]{13e9, 21e9}, new double[]{180e9, 320e9}));
        result.addAll(abstractPatient(new double[]{130.0, 170.0}, new double[]{3.0e12, 3.7e12}, new double[]{6.2e9, 6.8e9}, new double[]{500e9, 600e9}));
        result.addAll(abstractPatient(new double[]{80.0, 110.0}, new double[]{1.4e12, 2.0e12}, new double[]{6.2e9, 6.8e9}, new double[]{30e9, 39e9}));
        result.addAll(abstractPatient(new double[]{130.0, 170.0}, new double[]{4.35e12, 4.85e12}, new double[]{6.2e9, 6.8e9}, new double[]{370e9, 420e9}));
        result.addAll(abstractPatient(new double[]{130.0, 170.0}, new double[]{4.35e12, 4.85e12}, new double[]{12e9, 17e9}, new double[]{370e9, 420e9}));
        result.addAll(abstractPatient(new double[]{80.0, 110.0}, new double[]{1.4e12, 2.0e12}, new double[]{6.2e9, 6.8e9}, new double[]{180e9, 320e9}));
        result.addAll(abstractPatient(new double[]{130.0, 170.0}, new double[]{3.14e12, 3.74e12}, new double[]{10.0e9, 12e9}, new double[]{180e9, 320e9}));
        result.addAll(abstractPatient(new double[]{60.0, 80.0}, new double[]{1.4e12, 2.0e12}, new double[]{13e9, 21e9}, new double[]{370e9, 420e9}));
        result.addAll(abstractPatient(new double[]{130.0, 170.0}, new double[]{3.14e12, 3.74e12}, new double[]{6.2e9, 6.8e9}, new double[]{1.8e9, 3e9}));
        result.addAll(abstractPatient(new double[]{132.0, 173.0}, new double[]{3.5e12, 5.2e12}, new double[]{4e9, 10e9}, new double[]{180e9, 320e9}));

        for (GeneralBloodAnalysisData i : result){
            List<Double> item = new ArrayList<>(8);
            item.add(i.getLeukocytes());
            item.add(i.getErythrocytes());
            item.add(i.getHemoglobin());
            item.add(i.getHematocrit());
            item.add(i.getErythrocytesMedian());
            item.add(i.getHemoglobinInErythrocyte());
            item.add(i.getHemoglobinAverageInErythrocyte());
            item.add(i.getPlatelets());
            trainigSet.add(item);
        }
        JSONObject response = new JSONObject();
        response.put("x", trainigSet);
        return response.toString();
    }

    @RequestMapping(value = "/withNoDiagnose", method = RequestMethod.GET)
    public GeneralBloodAnalysis withNoDiagnose(){
        System.out.println("Getting data to analyse");
        String email = patientRepository.findWithNoDiagnose().getEmail();
        List<GeneralBloodAnalysis> generalBloodAnalysises = generalBloodAnalysisRepository.findByEmail(email);
        return generalBloodAnalysises.get(generalBloodAnalysises.size() - 1);
    }

    @RequestMapping(value = "/analyseReady", method = RequestMethod.POST)
    public String analyseReady(@RequestBody Map<String, String> request){
        System.out.println("Diagnosis data comes to server");
        System.out.println(request.get("diagnoses"));
        Patient patient = patientRepository.findByEmail(request.get("email"));
        System.out.println(request.get("email"));
        Map<String, Double> diagnosis = new HashMap<>(1);
        JSONArray diagnoses = new JSONArray(request.get("diagnoses"));
        List<Double> indicators = new ArrayList<>(10);
        for (int i = 0; i < 10; ++i){
            indicators.add(diagnoses.getDouble(i));
        }
        //patientRepository.setDiagnosis(request, )
        patientRepository.setDiagnosis(deseaseMapper(Integer.valueOf(request.get("most_probably"))), Collections.max(indicators), patient.getEmail(), patient.getPassword(), patient.getSurname(), patient.getName());
        return null;
    }

    private String deseaseMapper(int number){
        String[] deseases = {"poison", "cancer", "aplastical_anemiya", "trombos", "leykos", "hyperhydrotation", "bone_marrow_desease", "vich_spid", "tuberkulose", "nothing_founded"};
        return deseases[number];
    }
}
