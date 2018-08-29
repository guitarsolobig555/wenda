package com.liu.service;

import com.liu.dao.MessageDAO;
import com.liu.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService
{
    @Autowired
    MessageDAO messageDAO;
    private Logger logger= LoggerFactory.getLogger(MessageService.class);

    public List<Message> messageList(String conversationId)
    {
      return   messageDAO.messagedetail(conversationId,0,10);
    }
    public int getCount(int userId,String conversationId)
    {
       return messageDAO.getConvesationUnreadCount(userId,conversationId);
    }
    public List<Message> getList(int userId) {
        return messageDAO.messagelist(userId);
    }
    public int addMessage(Message message)
    {
        return messageDAO.addMessage(message);
    }
    public int getallCount(String conversationId) {
        return messageDAO.allcount(conversationId);
    }
    @Transactional
    public int updateHasRead(int id) {
        try {
            return messageDAO.updateHasread(id);
        } catch (Exception e) {
            logger.error("更新数据库发生错误" + e.getMessage());
            return -1;
        }
    }
}
