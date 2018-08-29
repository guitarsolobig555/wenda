package com.liu.async.Handler;


import com.alibaba.fastjson.JSONObject;
import com.liu.async.EventHandler;
import com.liu.async.EventModel;
import com.liu.async.EventType;
import com.liu.model.*;
import com.liu.service.FeedService;
import com.liu.service.FollowService;
import com.liu.service.QuestionService;
import com.liu.service.UserService;
import com.liu.util.JedisAdapter;
import com.liu.util.RedisKey;
import com.liu.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.*;


@Component
public class FeedHandler implements EventHandler {
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;
    @Autowired
    FeedService feedService;
    @Autowired
    JedisAdapter jedisAdapter;
    @Autowired
    QuestionService questionService;
    @Override
    public void doHandle(EventModel eventModel) {
        Feed feed=new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(eventModel.getType().getValue());
        feed.setUserId(eventModel.getActorId());
        feed.setData(buildFeedData(eventModel));
        if(feed.getData()==null)
        {
            return;
        }
        feedService.addFeed(feed);
        List<Integer> followers=followService.getFollowers(EntityType.ENTITY_USER,eventModel.getActorId(),Integer.MAX_VALUE);
        followers.add(0);
        for(int follower:followers)
        {
            String timelineKey= RedisKey.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey,String.valueOf(feed.getId()));
        }
    }

    public String buildFeedData(EventModel eventModel)
    {
        Map<String,String> map=new HashMap<>();
        User user=userService.getUser(eventModel.getActorId());
        if(user==null)
        {
            return null;
        }
        map.put("userId",String.valueOf(user.getId()));
        map.put("userHead",user.getHeadUrl());
        map.put("userName",user.getName());
        if(eventModel.getType()== EventType.COMMENT||(eventModel.getType()==EventType.FOLLOW&&eventModel.getEntityType()==EntityType.ENTITY_QUESTION))
        {
            Question question=questionService.selectById(eventModel.getEntityId());
            if(question==null){
                return null;
            }
            map.put("questionId",String.valueOf(question.getId())); //把问题d的链接
            map.put("questionTitle",question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(new EventType[]{EventType.COMMENT,EventType.FOLLOW});
    }
}
