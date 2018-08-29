package com.liu.controller;
import com.liu.async.EventModel;
import com.liu.async.EventType;
import com.liu.model.Comment;
import com.liu.model.EntityType;
import com.liu.model.User;
import com.liu.service.CommentService;
import com.liu.async.EventProducer;
import com.liu.service.LikeService;
import com.liu.util.HostHolder;
import com.liu.util.WendaUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import java.util.HashMap;

@Controller
public class LikeController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    LikeService likeService;
    @Autowired
    CommentService commentService;
    @Autowired
    EventProducer eventProducer;
    @RequestMapping(path = {"/like"},method = {RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId)
    {
       User localhost=hostHolder.getUser();
       Comment comment=commentService.getCommentById(commentId);
       if(localhost==null) {
          return WendaUtil.getJSONString(999);
       }
       long count=0;
       if(likeService.getStatus(localhost.getId(),commentId)==1)
       {
           count= likeService.likedoubleClick(localhost.getId(),commentId);
       }
       else {
           count = likeService.addLike(localhost.getId(), commentId);
       }

       Map<String,String> map=new HashMap<>();


       eventProducer.fireEvent( new EventModel(EventType.LIKE).setActorId(localhost.getId()).setEntityId(commentId).setEntityOwnerId(comment.getUserId())
                  .setEntityType(EntityType.ENTITY_COMMENT)
                 .setExt("questionId",String.valueOf(comment.getEntityId())));
       return WendaUtil.getJSONString(0,String.valueOf(count));
    }
    @RequestMapping(path={"/dislike"},method = RequestMethod.POST)
    @ResponseBody
    public String dislike(@Param("commentId") int commentId)
    {
        User localhost=hostHolder.getUser();
        long count=0;
        if(likeService.getStatus(localhost.getId(),commentId)==-1)
        {
           count=likeService.dislikedoubleClick(localhost.getId(),commentId);
        }
        else {
            count = likeService.disLike(localhost.getId(), commentId);
        }
        return WendaUtil.getJSONString(0,String.valueOf(count));
    }


}
