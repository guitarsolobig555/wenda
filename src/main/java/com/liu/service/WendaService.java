package com.liu.service;

import org.springframework.stereotype.Service;

/**
 * Created by liujiazhuo on 2018/7/10.
 */
@Service
public class WendaService {
    public String getMessage(int userId)
    {
        return "Hello Message:" + String.valueOf(userId);
    }
}
