package com.github.sibmaks.spring.jfr.event;

import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("Spring Bean registered")
public class BeanRegisteredEvent extends Event {
    @Label("Bean name")
    private final String beanName;

    public BeanRegisteredEvent(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }
}
