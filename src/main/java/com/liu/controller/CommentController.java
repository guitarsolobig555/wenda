package com.liu.controller;

import com.liu.model.Comment;
import com.liu.model.EntityType;
import com.liu.async.EventModel;
import com.liu.async.EventProducer;
import com.liu.async.EventType;
import com.liu.model.Question;
import com.liu.service.CommentService;
import com.liu.service.QuestionService;
import com.liu.util.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class CommentController
{
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;
    @Autowired
    EventProducer eventProducer;
    private static Logger logger= LoggerFactory.getLogger(CommentController.class);

    @RequestMapping(path = {"/addComment"},method = RequestMethod.POST)
   public String addComment(Model model,
                            @RequestParam("questionId") int questionId,
                            @RequestParam("content") String content
                            )
   {
       try {
           Comment comment = new Comment();
           comment.setContent(content);
           comment.setEntityId(questionId);
           Date date = new Date();
           comment.setCreatedDate(date);
           if (hostHolder.getUser() != null) {
               comment.setUserId(hostHolder.getUser().getId());
           }
           else
           {
               comment.setUserId(3);
           }
           comment.setEntityType(EntityType.ENTITY_QUESTION);
           comment.setStatus(1);
           commentService.addComment(comment);

           //更新评论里面的数量
           int count=commentService.getComment(comment.getEntityId(),comment.getEntityType())+1;
           questionService.updateComment(questionId,count);
           Question question=questionService.selectById(questionId);
           EventModel eventModel=new EventModel(EventType.COMMENT).setActorId(comment.getUserId()).
                   setEntityOwnerId(question.getUserId()).setEntityType(EntityType.ENTITY_QUESTION).
                   setEntityId(questionId);
           eventProducer.fireEvent(eventModel);
       }catch (Exception e)
       {
           logger.error("评论失败"+e.getMessage());
       }
       return "redirect:/question/" + String.valueOf(questionId);
   }

}
