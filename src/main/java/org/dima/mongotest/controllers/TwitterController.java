package org.dima.mongotest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dima on 21.02.16.
 */
@RestController
public class TwitterController
{
    @Autowired
    private Twitter twitter;

    @RequestMapping("/twitter")
    public SearchResults tweet()
    {
        //twitter.timelineOperations().updateStatus("#dontLeaveAccessTokenOnGithub Be careful =) From Ukraine with love");
        return twitter.searchOperations().search("#marta");
    }
}
