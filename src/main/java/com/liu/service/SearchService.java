package com.liu.service;

import com.liu.model.Question;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SearchService {
    private static final String SOLR_URL="http://127.0.0.1:8983/solr/wenda";
    private static final String QUESTION_TITLE="question_title";
    private static final String QUESTION_CONTENT="question_content";
    private HttpSolrClient client = new HttpSolrClient.Builder(SOLR_URL).build();
    public List<Question> search(String keyword, int offset, int count, String hlPre, String hlPos) throws IOException, SolrServerException {

      List<Question> questionList=new ArrayList<>();
        SolrQuery query=new SolrQuery(keyword);
        query.setRows(count);
        query.setStart(offset);
        query.setHighlight(true);
        query.setHighlightSimplePre(hlPre);
        query.setHighlightSimplePost(hlPos);
        query.set("hl.fl",QUESTION_CONTENT+","+QUESTION_TITLE);
        QueryResponse response=client.query(query);
        for(Map.Entry<String,Map<String,List<String>>> entry:response.getHighlighting().entrySet())
        {
            Question q=new Question();
            q.setId(Integer.parseInt(entry.getKey()));
            if(entry.getValue().containsKey(QUESTION_CONTENT))
            {
                List<String> contentList=entry.getValue().get(QUESTION_CONTENT);
                if(contentList.size()>0) {
                    q.setContent(contentList.get(0));
                }
            }
            if(entry.getValue().containsKey(QUESTION_TITLE))
            {
                List<String> titleList=entry.getValue().get(QUESTION_TITLE);
                if(titleList.size()>0)
                {
                    q.setTitle(titleList.get(0));
                }
            }
            questionList.add(q);
        }
        return questionList;
    }
    public boolean indexQuestion(int qid,String title,String content) throws IOException, SolrServerException {
        SolrInputDocument doc=new SolrInputDocument();
        doc.setField("id",qid);
        doc.setField(QUESTION_CONTENT,content);
        doc.setField(QUESTION_TITLE,title);
        UpdateResponse response=client.add(doc,1000);
        return response!=null && response.getStatus()==0;
    }
}
