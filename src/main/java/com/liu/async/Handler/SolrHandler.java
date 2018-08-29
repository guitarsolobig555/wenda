package com.liu.async.Handler;

import com.liu.async.EventHandler;
import com.liu.async.EventModel;
import com.liu.async.EventType;
import com.liu.service.SearchService;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class SolrHandler implements EventHandler {
    private Logger logger= LoggerFactory.getLogger(SolrHandler.class);
    @Autowired
    SearchService searchService;
    @Override
    public void doHandle(EventModel eventModel) {
        try {
            searchService.indexQuestion(eventModel.getEntityId(),eventModel.getExts().get("title"),eventModel.getExts().get("content"));
        } catch (IOException e) {
            logger.error("增加问题索引失败"+e.getMessage());
        } catch (SolrServerException e) {
            logger.error("增加问题索引失败"+e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.ADD_QUESTION);
    }
}
