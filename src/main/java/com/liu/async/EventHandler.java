package com.liu.async;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface EventHandler
{
  void doHandle(EventModel eventModel);
  List<EventType> getSupportEventType();
}
