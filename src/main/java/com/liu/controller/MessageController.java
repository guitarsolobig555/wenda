package com.liu.controller;

import com.liu.model.Message;
import com.liu.model.User;
import com.liu.model.ViewObject;
import com.liu.service.MessageService;
import com.liu.service.UserService;
import com.liu.util.HostHolder;
import com.liu.util.WendaUtil;
import org.apache.ibatis.annotations.Param;
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
public class MessageController {
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;

    private static Logger logger= LoggerFactory.getLogger(MessageController.class);

    @RequestMapping(path = {"/msg/addMessage"},method = RequestMethod.POST)
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                           @RequestParam("content") String content)
    {
       try {
           Message message=new Message();
           if(hostHolder.getUser()!=null) {
               message.setFromId(hostHolder.getUser().getId());
           }
           User user=userService.getUserByName(toName);
           if(user==null)
           {
               return WendaUtil.getJSONString(1,"用户不存在，请检查输入的用户名字是否正确");
           }
           message.setToId(user.getId());
           message.setCreatedDate(new Date());
           message.setContent(content);
           message.setHasRead(0);
           messageService.addMessage(message);
           return WendaUtil.getJSONString(0,"发送消息成功");
       }catch (Exception e)
       {
           logger.error("发送消息失败"+e.getMessage());
           return WendaUtil.getJSONString(1,"发送消息失败");
       }
    }
    @RequestMapping(path = {"/msg/list"},method = {RequestMethod.GET})
    public String getConversationlist(Model model)
    {

        try {
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<ViewObject>();
            List<Message> conversationList = messageService.getList(localUserId);
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                int targetId=msg.getFromId()==localUserId?msg.getToId():msg.getFromId();
                vo.set("user", userService.getUser(targetId));
                vo.set("unread",messageService.getCount(localUserId,msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letter";
    }
    @RequestMapping(path = {"/msg/detail"},method = {RequestMethod.GET})
    public String getConversationDetail(Model model,
                                        @Param("conversationId") String conversationId)
    {
        try
        {
            List<Message> list=messageService.messageList(conversationId);
           List<ViewObject> messages=new ArrayList<>();
           for(Message msg:list)
           {
               ViewObject vo=new ViewObject();
               vo.set("message",msg);
               User user=userService.getUser(msg.getFromId());
               if(hostHolder.getUser().getId()==msg.getToId())
               {
                   messageService.updateHasRead(msg.getId());
               }
               if(user==null)
               {
                   continue;
               }
               vo.set("user",user);
               messages.add(vo);
           }
           model.addAttribute("messages",messages);
        }catch (Exception e)
        {
             logger.error("显示消息详情错误"+e.getMessage());
        }
        return "letterDetail";
    }
}
