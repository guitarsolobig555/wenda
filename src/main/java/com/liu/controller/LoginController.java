package com.liu.controller;
import java.util.*;

import com.liu.model.*;
import com.liu.service.*;
import com.liu.util.HostHolder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController
{

    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;
    @Autowired
    FollowService followService;
    @Autowired
    CommentService commentService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    LikeService likeService;
    private static final Logger log= LoggerFactory.getLogger(LoginController.class);


    @RequestMapping(path = {"/"},method = {RequestMethod.POST,RequestMethod.GET})
    public String index(Model model,
                        @RequestParam(value="pop",defaultValue ="0") int pop)
    {
        List<ViewObject> vos=getQuestions(0,0,10);
        model.addAttribute("vos",vos);
        return "index";
    }
    public List<ViewObject> getQuestions(int userId,int offset,int limit)
    {
        List<Question> list=questionService.getLatestQuestions(userId,0,10);
        List<ViewObject> vos=new ArrayList<>();
        for(Question question:list)
        {
            ViewObject vo=new ViewObject();
            vo.set("question",question);
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            log.info(String.valueOf(followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId())));
            vo.set("user",userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }


    @RequestMapping(path ="/reg")
    public String register(Model model,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                           @RequestParam(value = "next",required = false) String next
                           )
    {
        try {
            Map<String,String> map=new HashMap<>();
            map=userService.register(username,password);
            if(map.containsKey("msg"))
            {
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
            if(StringUtils.isNotBlank(next))
            {
                return "redirect:"+next;
            }

            return "redirect:/";
        }catch (Exception e)
        {
            log.error("注册异常"+e.getMessage());
            return "login";
        }

    }
    @RequestMapping(path = {"/login"},method = {RequestMethod.POST})
    public String login(Model model,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rememberme",defaultValue = "false") String rememeberme,
                        @RequestParam(value = "next",required = false) String next,
                        HttpServletResponse response)
    {
        try {
            Map<String, String> map = new HashMap<>();
            map = userService.login(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                if(StringUtils.isNotBlank(next))
                {
                    return "redirect:"+ next;
                }
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        }catch (Exception e)
        {
            log.error("登陆异常"+e.getMessage());
            return "login";
        }

    }
    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String regloginPage(Model model,
                               @RequestParam(value = "next",required = false) String next
                               ) {
        model.addAttribute("next",next);
        return "login";
    }
    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }

    @RequestMapping(path = {"/user/{userId}"},method = {RequestMethod.GET,RequestMethod.POST})
    public String userindex(Model model, @PathVariable("userId") int userId)
    {
       model.addAttribute("vos",getQuestions(userId,0,10));

       User user=userService.getUser(userId);
       ViewObject vo=new ViewObject();
       vo.set("user",user);
       vo.set("commentCount",commentService.getUserCommentCount(userId));
       vo.set("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,userId));
       vo.set("followeeCount",followService.getFolloweeCount(userId,EntityType.ENTITY_USER));
       if(hostHolder.getUser()!=null)
        {
            vo.set("followed",followService.isFollwer(hostHolder.getUser().getId(),EntityType.ENTITY_USER,userId));
        }else
       {
           vo.set("followed",false);
       }
       List<Comment> comments=commentService.getCommentByUserId(userId);
       int count=0;
       for(Comment cmt:comments)
       {
          count+=likeService.getCount(cmt.getId());
       }
        vo.set("commetFollow",count);
       model.addAttribute("profileUser",vo);
       return "profile";
    }

}
