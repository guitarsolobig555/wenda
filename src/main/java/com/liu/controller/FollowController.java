package com.liu.controller;

import com.liu.async.EventModel;
import com.liu.async.EventProducer;
import com.liu.async.EventType;
import com.liu.model.EntityType;
import com.liu.model.User;
import com.liu.model.ViewObject;
import com.liu.service.CommentService;
import com.liu.service.FollowService;
import com.liu.service.UserService;
import com.liu.util.HostHolder;
import com.liu.util.WendaUtil;
import com.liu.model.Question;
import com.liu.service.QuestionService;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.liu.model.*;

@Controller
public class FollowController {
    @Autowired
    FollowService followService;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;
    @Autowired
    EventProducer eventProducer;


    @RequestMapping(path = {"/followUser"},method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String follower(@RequestParam("userId") int userId)
    {
         if(hostHolder.getUser()==null)
         {
             return WendaUtil.getJSONString(999);
         }
         boolean ret=followService.addfollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER,userId);
         //返回关注的人数
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).setActorId(hostHolder.getUser().getId())
        .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));
        return WendaUtil.getJSONString(ret?0:1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }
    @RequestMapping(path = {"/unfollowUser"},method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String followeE(@RequestParam("userId") int userId)
    {
        if(hostHolder.getUser()==null)
        {
            return WendaUtil.getJSONString(999);
        }
        boolean ret=followService.unfollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER,userId);
        //返回关注的人数
        return WendaUtil.getJSONString(ret?0:1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }
    @RequestMapping(path = {"/followQuestion"},method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String followeQuestion(@RequestParam("questionId") int questionId)
    {
        if(hostHolder.getUser()==null)
        {
            return WendaUtil.getJSONString(999);
        }
        Question q=questionService.selectById(questionId);
        if(q==null)
        {
            return WendaUtil.getJSONString(1,"问题不存在");
        }
        boolean ret=followService.addfollower(hostHolder.getUser().getId(),EntityType.ENTITY_QUESTION,questionId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).setEntityType(EntityType.ENTITY_QUESTION).
                setActorId(hostHolder.getUser().getId()).
                setEntityId(questionId).setEntityOwnerId(q.getUserId()));
        Map<String,Object> info=new HashMap<>();
        info.put("headUrl",hostHolder.getUser().getHeadUrl());
        info.put("name",hostHolder.getUser().getName());
        info.put("id",hostHolder.getUser().getId());
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));
        return WendaUtil.getJSONString(ret?0:1,info);
    }
    @RequestMapping(path = {"/unfollowQuestion"},method = {RequestMethod.POST})
    @ResponseBody
    public String unfollower(@RequestParam("questionId") int quesitonId)
    {
        if(hostHolder.getUser()==null)
        {
            return WendaUtil.getJSONString(999);
        }
        Question q=questionService.selectById(quesitonId);
        if(q==null)
        {
            return WendaUtil.getJSONString(1,"问题不存在");
        }
        boolean ret=followService.unfollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION,quesitonId);
        Map<String,Object> info=new HashMap<>();
        info.put("id",hostHolder.getUser().getId());
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,quesitonId));
        return WendaUtil.getJSONString(ret?0:1,info);
    }
    @RequestMapping(path = {"/user/{uid}/followers"},method = {RequestMethod.GET})
    public String followers(Model model,
                            @PathVariable("uid") int uid)
    {
        List<Integer> followerIds =followService.getFollowers(EntityType.ENTITY_USER,uid,0,10);

        if(hostHolder.getUser()!=null)
        {
            model.addAttribute("followers",getUserInfo(hostHolder.getUser().getId(),followerIds));
        }else
        {
            model.addAttribute("followers",getUserInfo(0,followerIds));
        }
        model.addAttribute("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,uid));
        model.addAttribute("curUser",userService.getUser(uid));
        return "followers";
    }
    @RequestMapping(path = {"/user/{uid}/followees"},method = {RequestMethod.GET})
    public String followees(Model model,
                            @PathVariable("uid") int uid)
    {
        List<Integer> followerIds =followService.getFollowees(uid,EntityType.ENTITY_USER,0,10);

        if(hostHolder.getUser()!=null)
        {
            model.addAttribute("followees",getUserInfo(hostHolder.getUser().getId(),followerIds));
        }else
        {
            model.addAttribute("followees",getUserInfo(0,followerIds));
        }
        model.addAttribute("followeeCount",followService.getFolloweeCount(uid,EntityType.ENTITY_USER));
        model.addAttribute("curUser",userService.getUser(uid));
        return "followees";
    }

    public List<ViewObject> getUserInfo(int localUserId, List<Integer> userIds)
    {
        List<ViewObject> userInfo=new ArrayList<>();
        for(Integer uid:userIds)
        {
            User user=userService.getUser(uid);
            if(user==null)
            {
                continue;
            }
            ViewObject vo=new ViewObject();
            vo.set("user",user);
            vo.set("commentCount",commentService.getUserCommentCount(uid));
            vo.set("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,uid));
            vo.set("followeeCount",followService.getFolloweeCount(uid,EntityType.ENTITY_USER));
            if(localUserId!=0)
            {
                vo.set("followed",followService.isFollwer(localUserId,EntityType.ENTITY_USER,uid));
            }
            else
            {
                vo.set("followed",false);
            }
            userInfo.add(vo);
        }
        return userInfo;
    }


}
