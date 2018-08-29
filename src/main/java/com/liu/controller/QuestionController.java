package com.liu.controller;

import com.liu.async.EventModel;
import com.liu.async.EventType;
import com.liu.model.*;
import com.liu.service.*;
import com.liu.async.EventProducer;
import com.liu.util.HostHolder;
import com.liu.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;
    @Autowired
    FollowService followService;
    @Autowired
    EventProducer eventProducer;
    private static final Logger logger= LoggerFactory.getLogger(QuestionController.class);

    @RequestMapping(path = {"/question/add"},method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content)
    {
        try {
            Question question = new Question();
            question.setCreatedDate(new Date());
            question.setUserId(hostHolder.getUser().getId());
            question.setContent(content);
            question.setCommentCount(0);
            question.setTitle(title);
            int m=questionService.addQuestion(question);
            if(m>0)
            {
                eventProducer.fireEvent(new EventModel(EventType.ADD_QUESTION).setEntityId(question.getId()).
                        setEntityType(EntityType.ENTITY_QUESTION).setExt("title",title).setExt("content",content));
                return WendaUtil.getJSONString(0);
            }
        }catch (Exception e)
        {
            logger.error("发布帖子失败"+e.getMessage());
        }
        return WendaUtil.getJSONString(1,"失败");
    }
    @RequestMapping(path = {"/question/{qid}"},method = {RequestMethod.GET,RequestMethod.POST})
    public String questionDetail(Model model, @PathVariable("qid") int questionId)
    {
       Question question= questionService.selectById(questionId);
       User user=userService.getUser(question.getUserId());
       List<Comment> list=commentService.selectComment(questionId,1);
       List<ViewObject> vos=new ArrayList<>();
       model.addAttribute("question",question);
       for(Comment comment:list)
       {
           ViewObject vo=new ViewObject();
           vo.set("comment",comment);
           if(hostHolder.getUser()==null)
           {
               vo.set("liked",0);
           }else
           {
             vo.set("liked",likeService.getStatus(hostHolder.getUser().getId(),comment.getId()));
           }
           vo.set("likeCount",likeService.getCount(comment.getId()));
           vo.set("user",userService.getUser(comment.getUserId()));
           vos.add(vo);
       }
       model.addAttribute("comments",vos);
       List<ViewObject> followUsers=new ArrayList<>();
       List<Integer> users=followService.getFollowers(EntityType.ENTITY_QUESTION,questionId,20);
       for(Integer userId:users)
       {
           ViewObject vo=new ViewObject();
           User u=userService.getUser(userId);
           if(u==null)
           {
               continue;
           }
           vo.set("name",u.getName());
           vo.set("headurl",u.getHeadUrl());
           vo.set("id",u.getId());
           followUsers.add(vo);
       }
        model.addAttribute("followUsers", followUsers);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followed", followService.isFollwer(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId));
        } else {
            model.addAttribute("followed", false);
        }
       return "detail";
    }

}
