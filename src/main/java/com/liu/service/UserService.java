package com.liu.service;


import com.liu.model.LoginTicket;
import com.liu.model.User;
import com.liu.util.WendaUtil;
import com.liu.dao.LoginTicketDAO;
import com.liu.dao.UserDAO;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService
{
    @Autowired
    private UserDAO userDao;
    @Autowired
    private LoginTicketDAO loginTicketDAO;

    private static final Logger logger= LoggerFactory.getLogger(UserService.class);

    public User getUser(int userid)
    {
        return userDao.selectById(userid);
    }
    public Map<String,String> register(String username,String password)
    {
        Map<String,String> map=new HashMap<>();
        if(StringUtils.isBlank(username))
        {
            map.put("msg","用户名为空");
            return map;
        }
        if(StringUtils.isBlank(password))
        {
            map.put("msg","密码不可以为空");
            return map;
        }
        if(password.length()<7)
        {
            map.put("msg","密码长度过短");
            return map;
        }
        User user= userDao.selectByname(username);
        if(user!=null)
        {
            map.put("msg","用户名已存在");
            return map;
        }
        user=new User();
        user.setName(username);
        Random random=new Random();
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        userDao.addUser(user);

        String ticket=addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }
    public Map<String,String> login(String username,String password)
    {
        Map<String,String> map=new HashMap<>();
        if(StringUtils.isBlank(username))
        {
            map.put("msg","用户名不可以为空");
            return map;
        }
        if(StringUtils.isBlank(password))
        {
            map.put("msg","密码不能为空");
            return map;
        }
        User user=userDao.selectByname(username);
        if(user==null)
        {
            map.put("msg","用户名不存在");
            return map;
        }
        if(!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword()))
        {
            map.put("msg","密码错误");
            return map;
        }
        map.put("ticket",addLoginTicket(user.getId()));
        return map;
    }
    private String addLoginTicket(int userId)
    {
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(userId);
        Date now=new Date();
        now.setTime(3600*24*1000+now.getTime());
        loginTicket.setExpired(now);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replace("-",""));
        String ticket=loginTicket.getTicket();
        loginTicketDAO.addTicket(loginTicket);

        return ticket;
    }

    public void logout(String ticket)
    {
        loginTicketDAO.updateStatus(1,ticket);
    }
    public User getUserByName(String username)
    {
        return userDao.selectByname(username);
    }

}
