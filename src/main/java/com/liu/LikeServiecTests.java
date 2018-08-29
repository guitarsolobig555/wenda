package com.liu;

import com.liu.service.LikeService;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
public class LikeServiecTests {
    @Autowired
    LikeService likeService;
    @Test
    public void testLike()
    {
        likeService.disLike(12,8);
        Assert.assertEquals(-1,likeService.getStatus(12,8));
    }
}
