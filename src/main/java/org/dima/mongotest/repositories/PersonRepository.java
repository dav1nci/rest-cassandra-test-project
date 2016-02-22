package org.dima.mongotest.repositories;

import org.dima.mongotest.models.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by dima on 21.02.16.
 */
public interface PersonRepository extends MongoRepository<Person, String>
{
    List<Person> findByLastName(@Param("name") String name);
}
