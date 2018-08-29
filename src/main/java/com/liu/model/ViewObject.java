package com.liu.model;

import com.sun.javafx.collections.MappingChange;
import java.util.*;
public class ViewObject
{
    private Map<String,Object> objs=new HashMap<>();
    public void set(String key,Object value)
    {
        objs.put(key,value);
    }
    public Object get(String key)
    {
        return objs.get(key);
    }

}
