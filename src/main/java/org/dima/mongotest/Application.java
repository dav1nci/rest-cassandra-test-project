package org.dima.mongotest;

import org.dima.mongotest.configure.ApplicationConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by dima on 21.02.16.
 */
@SpringBootApplication
public class Application
{
    public static void main(String[] args)
    {
        SpringApplication.run(new Class<?>[]{Application.class, ApplicationConfiguration.class}, args);
    }
}
