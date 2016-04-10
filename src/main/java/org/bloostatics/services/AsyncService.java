package org.bloostatics.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Future;

/**
 * Created by stdima on 10.04.16.
 */
@Service
public class AsyncService {
    RestTemplate restTemplate = new RestTemplate();

    @Async
    public Future<User> findUser(String user) throws InterruptedException
    {
        System.out.println("Looking for user " + user);
        User results = restTemplate.getForObject("https://api.github.com/users/" + user, User.class);
        Thread.sleep(1000);
        return new AsyncResult<User>(results);
    }
}
