package com.liu.async.Handler;

import com.liu.async.EventHandler;
import com.liu.async.EventModel;
import com.liu.async.EventType;
import com.liu.model.EntityType;
import com.liu.model.Message;
import com.liu.model.User;
import com.liu.service.MessageService;
import com.liu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
@Component
public class FollowHandler implements EventHandler
{
    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;
    @Override
    public void doHandle(EventModel eventModel) {
        Message message=new Message();
        message.setCreatedDate(new Date());
        message.setHasRead(0);
        message.setFromId(3);
        message.setToId(eventModel.getEntityOwnerId());
        User user=userService.getUser(eventModel.getActorId());
        String username=user.getName();
        if(eventModel.getEntityType()== EntityType.ENTITY_QUESTION)
        {
            message.setContent("用户"+username+"关注了你的问题，http://127.0.0.1:8080/question/"+eventModel.getEntityId());
        }
        else if(eventModel.getEntityType()==EntityType.ENTITY_USER)
        {
            String content = "用户" + username + "关注了你,http://127.0.0.1:8080/user/"+eventModel.getActorId();
             message.setContent(content);
        }
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
