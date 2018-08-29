package com.liu.async;

import java.util.Arrays;
import java.util.List;

public class LoginHandler implements EventHandler {

    @Override
    public void doHandle(EventModel eventModel) {

    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.LOGIN);
    }
}
