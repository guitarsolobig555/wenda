package com.liu.async;

import com.alibaba.fastjson.JSONObject;
import com.liu.util.JedisAdapter;
import com.liu.util.RedisKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {
    private static Logger logger= LoggerFactory.getLogger(EventModel.class);
    @Autowired
    JedisAdapter jedisAdapter;
    public boolean fireEvent(EventModel eventModel)
    {
        try{
          String json= JSONObject.toJSONString(eventModel);
          String key= RedisKey.getEventQueueKey();
          jedisAdapter.lpush(key,json);
          return true;
        }catch (Exception e)
        {
            logger.error("发生异常"+e.getMessage());
            return false;
        }

    }
}
