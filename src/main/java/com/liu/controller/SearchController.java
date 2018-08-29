package com.liu.controller;

import com.liu.model.EntityType;
import com.liu.model.ViewObject;
import com.liu.service.UserService;
import com.liu.model.Question;
import com.liu.service.FollowService;
import com.liu.service.QuestionService;
import com.liu.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {
    @Autowired
    QuestionService questionService;
    @Autowired
    SearchService searchService;
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;
    private Logger logger= LoggerFactory.getLogger(SearchController.class);

    @RequestMapping(path= {"/search"},method = {RequestMethod.GET})
    public String search(Model model, @RequestParam("q") String keyword,
                         @RequestParam(value = "offset",defaultValue = "0") int offset,
                         @RequestParam(value = "count",defaultValue = "10") int count)  {
        try {
            List<Question> questionList = searchService.search(keyword, offset, count, "<em>", "</em>");
            if (questionList != null && questionList.size() > 0) {
                List<ViewObject> vos = new ArrayList<>();
                for (Question question : questionList) {
                    ViewObject vo = new ViewObject();
                    int id = question.getId();
                    Question q = questionService.selectById(id);
                    if (question.getContent() != null) {
                        q.setContent(question.getContent());
                    }
                    if (question.getTitle() != null) {
                        q.setTitle(question.getTitle());
                    }
                    vo.set("question", q);
                    vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, id));
                    vo.set("user", userService.getUser(q.getUserId()));
                    vos.add(vo);
                }
                model.addAttribute("vos", vos);
                model.addAttribute("keyword", keyword);
            }
        }catch (Exception e)
        {
            logger.error("搜索失败"+e.getMessage());
        }
        return "result";
    }
}
