package org.bloostatics.controllers;

import org.bloostatics.models.Device;
import org.bloostatics.models.Greeting;
import org.bloostatics.services.AsyncService;
import org.bloostatics.services.User;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * Created by dima on 21.02.16.
 */
@RestController
@EnableAsync
public class HomeController
{
    @Autowired
    private CassandraOperations cassandraOperations;
    @Autowired
    private AsyncService asyncService;

    @RequestMapping(value = "/generateSomeDevices", method = RequestMethod.GET)
    public List<Device> generateDevices()
    {
        List<Device> devices = new ArrayList<>(100);
        for (int i = 0; i < 30; ++i) {
            devices.add(new Device());
            cassandraOperations.insert(devices.get(i));
        }
        return devices;
    }

    @RequestMapping("/tryAsync")
    public List<User> getUser() throws Exception
    {
        Future<User> page1 = asyncService.findUser("PivotalSoftware");
        Future<User> page2 = asyncService.findUser("CloudFoundry");
        Future<User> page3 = asyncService.findUser("Spring-Projects");
        List<User> users = new ArrayList<>();
        users.add(page1.get());
        users.add(page2.get());
        users.add(page3.get());
        return users;
    }

    @RequestMapping(value = "/python", method = RequestMethod.GET)
    public String python(){
//        /*PythonInterpreter.initialize(System.getProperties(), System.getProperties(), new String[0]);
//        PythonInterpreter interpreter = new PythonInterpreter();
//        interpreter.execfile("/home/stdima/Documents/Projects/IdeaProjects/MongoTestProject/src/main/resources/pyt.py");
//        PyObject fun = interpreter.get("hello");
//        PyObject res = fun.__call__(new PyString("Hello from Java"));
//        String resStr = (String) res.__tojava__(String.class);*/
        return null;
    }

    @RequestMapping("/lab1")
    public List<String> lab1()
    {
        String enter = "Hello my Name IS Di123a     Here many spaCEQQ           some TaBs WITH LOVe!       after";
        int N = 10;
        List<String> result = new ArrayList<>();
        for(String i : enter.split(" "))
            if (!i.equals(""))
                result.add(i);
        List<String> totalResult = new ArrayList<>();
        String buffer = result.get(0);
        for (int i = 1; i < result.size(); ++i)
        {
            if ((buffer.length() + result.get(i).length() + 1) <= N)
            {
                buffer += (" " + result.get(i));
            }
            else {
                totalResult.add(buffer);
                buffer = result.get(i);
            }
        }
        return totalResult;
    }


}
