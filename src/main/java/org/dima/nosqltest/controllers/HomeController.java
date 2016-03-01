package org.dima.nosqltest.controllers;

import org.dima.nosqltest.models.Greeting;
import org.dima.nosqltest.repositories.GreetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dima on 21.02.16.
 */
@RestController
public class HomeController
{
    @Autowired
    private GreetRepository greetRepository;

    @RequestMapping(value = "/greeting", method = RequestMethod.GET)
    public List<Greeting> greeting()
    {
        List<Greeting> response = new ArrayList<>();
        greetRepository.findAll().forEach(response::add);
        return response;
    }

    @RequestMapping(value = "/greeting",method = RequestMethod.POST)
    public Greeting saveGreeting(@RequestBody Greeting greeting) {
        greeting.setCreationDate(new Date());
        greetRepository.save(greeting);
        return greeting;
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
