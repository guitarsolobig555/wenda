package com.liu.service;

import com.liu.dao.FeedDAO;
import com.liu.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {
     @Autowired
     FeedDAO feedDAO;

     //拉资讯 从数据库里面取出自己关注的人的一些动态
     public List<Feed> selectUserFeeds(int maxId, List<Integer> userIds, int count)
     {
         return feedDAO.slectUsreFeeds(maxId,userIds,count);
     }
     public boolean addFeed(Feed feed)
     {
         feedDAO.addFeed(feed);
         return feed.getId()>0;
     }
     public Feed getById(int id) {
         return feedDAO.selectById(id);
     }
}
