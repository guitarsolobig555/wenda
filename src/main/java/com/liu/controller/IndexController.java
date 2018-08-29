package com.liu.controller;

import com.liu.service.WendaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by liujiahzuo on 2018/7/10.
 */

public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    WendaService wendaService;



    @RequestMapping(path = {"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("userId") int userId,
                          @PathVariable("groupId") String groupId,
                          @RequestParam(value = "type", defaultValue = "1") int type,
                          @RequestParam(value = "key", required = false) String key) {
        return String.format("Profile Page of %s / %d, t:%d k: %s", groupId, userId, type, key);
    }

    @RequestMapping(path = {"/vm"}, method = {RequestMethod.GET})
    public String template(Model model) {
        model.addAttribute("value1", "vvvvv1");
        List<String> colors = Arrays.asList(new String[]{"RED", "GREEN", "BLUE"});
        model.addAttribute("colors", colors);
        Map<String,String> map=new HashMap<>();
        return "home";
    }

    @RequestMapping(path = {"/request"}, method = {RequestMethod.GET})
    @ResponseBody
    public String request(Model model,
                           HttpServletResponse response,
                           HttpServletRequest request,
                           HttpSession httpSession,
                           @CookieValue("JSESSIONID") String sessionId) {
      StringBuilder sb=new StringBuilder();
      Enumeration<String> headerNames=request.getHeaderNames();
      while(headerNames.hasMoreElements())
      {
          String name=headerNames.nextElement();
          sb.append(name+":"+request.getHeader(name)+"<br>");
      }
        sb.append(request.getMethod() +"<br>");
        sb.append(request.getQueryString() +"<br>");
        sb.append(request.getPathInfo() +"<br>");
        sb.append(request.getRequestURI() +"<br>");
        if(request.getCookies()!=null)
        {
            for(Cookie cook:request.getCookies())
              sb.append("cookie"+": "+cook.getName()+": "+cook.getValue());
        }
        response.addHeader("liujiazhuo","hello");
        response.addCookie(new Cookie("username","liujiahzuo"));
        return sb.toString();

    }

    @RequestMapping(path = {"/redirect/{code}"}, method = {RequestMethod.GET})
    public RedirectView redirect(@PathVariable("code") int code,
                                 HttpSession httpSession) {
        httpSession.setAttribute("msg", "jump from redirect");
        RedirectView red = new RedirectView("/", true);
        if (code == 301) {
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return  red;
    }

    @RequestMapping(path = {"/admin"}, method = {RequestMethod.GET})
    @ResponseBody
    public String admin(@RequestParam("key") String key) {
        if ("admin".equals(key)) {
            return "hello admin";
        }
        throw  new IllegalArgumentException("参数不对");
    }

    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e) {
        return "error:" + e.getMessage();
    }

}