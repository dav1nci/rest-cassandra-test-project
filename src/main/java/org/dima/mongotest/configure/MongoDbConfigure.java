/*
package org.dima.mongotest.configure;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoFactoryBean;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Arrays;

import static java.util.Collections.singletonList;

*/
/**
 * Created by dima on 21.02.16.
 *//*

@Configuration
@EnableMongoRepositories("org.dima.mongotest.configure")
@PropertySource("classpath:application.properties")
@EnableAutoConfiguration
public class MongoDbConfigure */
/*extends AbstractMongoConfiguration*//*

{
    private final Logger log = LoggerFactory.getLogger(MongoDbConfigure.class);

    */
/*@Value("${spring.data.mongodb.host}")
    private String host;
    @Value("${spring.data.mongodb.port}")
    private Integer port;
    @Value("${spring.data.mongodb.username}")
    private String username;
    @Value("${spring.data.mongodb.database}")
    private String database;
    @Value("${spring.data.mongodb.password}")
    private String password;*//*


    */
/*@Bean
    public ValidatingMongoEventListener validatingMongoEventListener() {
        return new ValidatingMongoEventListener(validator());
    }
    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }*//*


    */
/*@Override
    protected String getDatabaseName() {
        return database;
    }*//*


    */
/*@Override*//*

    */
/*@Bean
    public MongoClient mongo() throws Exception {
        return new MongoClient(Arrays.asList(new ServerAddress("localhost", 27017)),
                singletonList(MongoCredential.createCredential(username,database, password.toCharArray())));
    }*//*


}
*/
