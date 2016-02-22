package org.dima.mongotest.controllers;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.dima.mongotest.models.Person;
import org.dima.mongotest.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by dima on 21.02.16.
 */
@RestController
public class HomeController
{
    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private PersonRepository personRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Person> home()
    {
        Person p = new Person("1", "Dima", "Stolpakov");
        MongoDatabase database = mongoClient.getDatabase("fooddb");
        /*database.createCollection("persons");*/
        /*database.getCollection("persons").insertOne(new Document("person",
                new Document()
                .append("name", "Dima")
                .append("surname", "Stolpakov")));*/
        /*for (int i = 0; i < 1000; ++i) {
            personRepository.save(p);
            p.setId(p.getId() + 1);
        }*/
        return personRepository.findAll();
    }
}
