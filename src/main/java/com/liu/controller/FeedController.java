package com.liu.controller;

import com.liu.model.EntityType;
import com.liu.util.JedisAdapter;
import com.liu.util.RedisKey;
import com.liu.model.Feed;
import com.liu.service.FeedService;
import com.liu.service.FollowService;
import com.liu.service.QuestionService;
import com.liu.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedController {
    @Autowired
    FollowService followService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    QuestionService questionService;
    @Autowired
    JedisAdapter jedisAdapter;
    @Autowired
    FeedService feedService;

    @RequestMapping(path = {"/pullfeeds"},method = {RequestMethod.GET,RequestMethod.POST})
    public String getPullFeeds(Model model)
    {
        int localUserId=hostHolder.getUser()!=null?hostHolder.getUser().getId():0;
        List<Integer> followees=new ArrayList<>();
        if(localUserId!=0)
        {
            followees=followService.getFollowees(localUserId, EntityType.ENTITY_USER,Integer.MAX_VALUE);
        }
        List<Feed> feeds=feedService.selectUserFeeds(Integer.MAX_VALUE,followees,10);
        model.addAttribute("feeds",feeds);
        return "feeds";
    }

    @RequestMapping(path = {"/pushfeeds"},method = {RequestMethod.GET,RequestMethod.POST})
    public String getPushFeeds(Model model)
    {
        int localUserId=hostHolder.getUser()!=null?hostHolder.getUser().getId():0;
        List<String> feedIds=jedisAdapter.lrange(RedisKey.getTimelineKey(localUserId),0,10);
        List<Feed> feeds=new ArrayList<>();
        for(String feedId:feedIds)
        {
            Feed feed=feedService.getById(Integer.parseInt(feedId));
            if(feed!=null)
            {
                feeds.add(feed);
            }
        }
        model.addAttribute("feeds",feeds);
        return "feeds";
    }


}
