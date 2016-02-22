package org.dima.mongotest.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

/**
 * Created by dima on 21.02.16.
 */
@Configuration
public class TwitterConfiguration
{
    @Value("${oauth.consumerKey}")
    private String consumerKey; // The application's consumer key
    @Value("${oauth.consumerSecret}")
    private String consumerSecret; // The application's consumer secret
    @Value("${oauth.accessToken}")
    private String accessToken; // The access token granted after OAuth authorization
    @Value("${oauth.accessTokenSecret}")
    private String accessTokenSecret; // The access token secret granted after OAuth authorization

    @Bean
    public Twitter getTwitterTemplate()
    {
        return new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
    }
}
