import com.fasterxml.jackson.databind.ObjectMapper;
import org.bloostatics.Application;
import org.bloostatics.controllers.PatientController;
import org.bloostatics.models.GeneralBloodAnalysis;
import org.bloostatics.models.GeneralBloodAnalysisKey;
import org.bloostatics.models.Patient;
import org.bloostatics.repositories.DeviceRepository;
import org.bloostatics.repositories.DoctorRepository;
import org.bloostatics.repositories.GeneralBloodAnalysisRepository;
import org.bloostatics.repositories.PatientRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.cassandra.core.CassandraOperations;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import org.junit.Assert;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class PatientControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

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
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

//    final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void addNewPatientTest() throws Exception{
        Patient patient = new Patient();
        patient.setEmail("qweqqqqq");
        patient.setPassword("aaaa");
        patient.setDoctorId(UUID.fromString("3f273810-181a-11e6-930f-53bbf431623a"));
        patient.setSurname("Qsde");
        patient.setName("Dsda");
        patient.setDeviceId(UUID.fromString("36b309e0-17c8-11e6-a0ec-5dc0fedb0371"));
        mockMvc.perform(post("/patient/addNewPatient")
                .content(this.json(patient))
                .contentType(contentType))
                .andExpect(status().isConflict());

    }

    @Test
    public void addGeneralAnalysisDataTest() throws Exception{
        GeneralBloodAnalysis analysis = new GeneralBloodAnalysis();
        GeneralBloodAnalysisKey key = new GeneralBloodAnalysisKey();
        key.setEventTime(new Date());
        key.setPatientEmail("jack@gmail.com");
        analysis.setKey(key);
        Map<String, Double> analyses = new HashMap<>();
        analyses.put("ageCategory", 18.0);
        analyses.put("leukocytes", 8.5811964401304646E9);
        analyses.put("erythrocytes", 6.2793520121157227E12);
        analyses.put("hemoglobin", 140.41453757700343);
        analyses.put("hematocrit", 51.75135968224013);
        analyses.put("erythrocytesMedian", 91.07321262343845);
        analyses.put("hemoglobinInErythrocyte", 51.37189449096424);
        analyses.put("hemoglobinAverageInErythrocyte", 320.89023028912);
        analyses.put("platelets", 4.8670093272228476E11);
        analysis.setAnalysis(analyses);
        mockMvc.perform(post("/patient/addGeneralAnalysisData")
                .content(this.json(analysis))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.key.patientEmail", is("jack@gmail.com")));
    }

    @Test
    public void getPatient() throws Exception{
        Map<String, String> request = new HashMap<>();
        request.put("email", "jack@gmail.com");
        mockMvc.perform(post("/patient/getPatient")
        .content(this.json(request))
        .contentType(contentType))
                .andExpect(jsonPath("$.name", is("Jack")))
                .andExpect(jsonPath("$.surname", is("Porter")));
    }


    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
