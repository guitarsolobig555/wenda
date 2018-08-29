package com.nowcoder;



import com.liu.WendaApplication;
import com.liu.dao.QuestionDAO;
import com.liu.dao.UserDAO;
import com.liu.model.Question;
import com.liu.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Date;
import java.util.Random;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@WebAppConfiguration
@Sql("/insertest.sql")
public class Tests {
    @Autowired
    UserDAO userDAO;
    @Autowired
    QuestionDAO questionDAO;
    @Test
    public void contextLoads()
    {
            Random random=new Random();
            for(int i=0;i<11;i++)
            {
                User user=new User();
                user.setName(String.format("USER%d",i));
                user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
                user.setSalt("213");
                user.setPassword("1234");
                userDAO.addUser(user);

                Question question = new Question();
                question.setCommentCount(i);
                Date date = new Date();
                date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
                question.setCreatedDate(date);
                question.setUserId(i+1);
                question.setTitle(String.format("TITLE{%d}", i));
                question.setContent(String.format("Balaababalalalal Content %d", i));
                questionDAO.addQuestion(question);
            }
    }
}
