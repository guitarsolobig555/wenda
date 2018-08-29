package com.liu.service;

import com.liu.dao.QuestionDAO;
import com.liu.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService
{
    @Autowired
    QuestionDAO questionDAO;
    @Autowired
    SensitiveService sensitiveService;

    public List<Question> getLatestQuestions(int userId, int offset, int limit)
    {
        return questionDAO.selectLatestQuestions(userId,offset,limit);
    }
    public int addQuestion(Question question) {
        question.setContent(sensitiveService.filter(question.getContent()));
        question.setTitle(sensitiveService.filter(question.getTitle()));
        sensitiveService.filter(question.getTitle());
        sensitiveService.filter(question.getContent());
        return questionDAO.addQuestion(question) > 0 ? question.getId() : 0;
    }
    public Question selectById(int id)
    {
        return questionDAO.selectById(id);
    }


    public int updateComment(int id,int count)
    {
        return questionDAO.updateComment(id,count);
    }

}


