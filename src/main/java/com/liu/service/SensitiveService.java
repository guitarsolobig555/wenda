package com.liu.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class SensitiveService implements InitializingBean{
    private Logger logger= LoggerFactory.getLogger(SensitiveService.class);
    public  TrieNode rootNode=new TrieNode();
    @Override
    public void afterPropertiesSet() throws Exception {
        try{
             InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read=new InputStreamReader(is);
            BufferedReader stream=new BufferedReader(read);
            String lineTxt;
            while((lineTxt=stream.readLine())!=null)
            {
                addWord(lineTxt);
            }
        }catch (Exception e)
        {
          logger.error("读取敏感词文件失败"+e.getMessage());
        }

    }
    //增加关键词
    private void addWord(String lineText){
        TrieNode tempNode=rootNode;
        for(int i=0;i<lineText.length();i++)
        {
            Character c=lineText.charAt(i);
            TrieNode node=tempNode.getSubNode(c);
            if(node==null)
            {
                node=new TrieNode();
                tempNode.addSubNode(c,node);
            }
            tempNode=node;
            if(i==lineText.length()-1){tempNode.setKeywordEnd(true);}
        }

    }
    public boolean isSymbol(char c){
        int n=(int) c;
        return !CharUtils.isAsciiAlphanumeric(c)&&(n<0x2E80||n>0x9FFF);
    }
    public String filter(String text )
    {
        if(StringUtils.isBlank(text)){return text;}
        String replcement="***";
        StringBuilder sb=new StringBuilder();
        TrieNode tempNode=rootNode;
        int begin=0;int position=0;
        while(position<text.length())
        {
            char c=text.charAt(position);
           if(isSymbol(c)){
               position++;
               continue;
           }
            tempNode=tempNode.getSubNode(c);
            if(tempNode==null){
                sb.append(text.charAt(begin));
                begin=begin+1;
                position=begin;
                tempNode=rootNode;
            }
            else if(tempNode!=null&&tempNode.isKeyWord()){
                sb.append(replcement);
                position=position+1;
                begin=position;
                tempNode=rootNode;
            }
            else
            {
                ++position;
            }
        }
        sb.append(text.substring(begin));
        return String.valueOf(sb);
    }
    private class TrieNode
    {
        //s是不是关键词的结尾
          private boolean end=false;
          //当前节点下所有的子节点
          private Map<Character,TrieNode> subNodes=new HashMap<>();
          //增加子节点
          public void addSubNode(Character key,TrieNode node)
          {
             subNodes.put(key,node);
          }
          TrieNode getSubNode(Character key) { return subNodes.get(key); }
          boolean isKeyWord() { return end; }
          void setKeywordEnd(boolean end){this.end=end;}
    }
    public static void main(String[] args){
        SensitiveService s=new SensitiveService();
        s.addWord("色情");
        s.addWord("赌博");
        System.out.println(s.filter("你好!色😀情sda**da"));
    }
}
