package com.liu.async;


import com.alibaba.fastjson.JSON;
import com.liu.util.JedisAdapter;
import com.liu.util.RedisKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware {

    @Autowired
    JedisAdapter jedisAdapter;

    //ApplicationContext 上下文可以利用这种方法在类初始化的时候拿到所有实现
    //EventHandler接口的类
 private static Logger logger= LoggerFactory.getLogger(EventConsumer.class);
 private Map<EventType,List<EventHandler>> config=new HashMap<>();
    private ApplicationContext applicationContext;
    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String,EventHandler> map=applicationContext.getBeansOfType(EventHandler.class);
        if(map!=null)
        {
            for(Map.Entry<String,EventHandler> entry:map.entrySet())
            {
                List<EventType> eventTypes=entry.getValue().getSupportEventType();
                for(EventType eventType:eventTypes)
                {
                    if(!config.containsKey(eventType))
                    {
                        logger.info(String.valueOf(eventType));
                        config.put(eventType,new ArrayList<>());
                    }
                    config.get(eventType).add(entry.getValue());
                }
            }
        }
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)
                {
                    String key= RedisKey.getEventQueueKey();
                    List<String> events=jedisAdapter.brpop(0,key);

                    for(String message:events)
                    {
                        if(message.equals(key))
                        {
                            continue;
                        }
                        logger.info(message);
                        EventModel eventModel= JSON.parseObject(message,EventModel.class);
                        logger.info(String.valueOf(eventModel.getType()));
                        if(!config.containsKey(eventModel.getType()))
                        {
                            logger.error("不能被识别的事件");
                            continue;
                        }
                        for(EventHandler eventHandler:config.get(eventModel.getType()))
                        {
                            eventHandler.doHandle(eventModel);
                        }
                    }
                }
            }
        }); thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
