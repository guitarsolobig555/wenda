package com.liu.async;

import com.liu.model.Message;
import com.liu.model.User;
import com.liu.service.MessageService;
import com.liu.service.UserService;
import com.liu.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Override
    public void doHandle(EventModel eventModel) {
        Message message=new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(eventModel.getEntityOwnerId());
        User user=userService.getUser(eventModel.getEntityOwnerId());
        message.setContent("用户"+user.getName()+"赞了你的评论,链接为 http://localhost:8080/question/"+eventModel.getExts().get("questionId"));
        message.setHasRead(0);
        message.setCreatedDate(new Date());
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.LIKE);
    }
}
