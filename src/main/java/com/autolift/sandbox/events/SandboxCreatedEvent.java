package com.autolift.sandbox.events;

import org.springframework.context.ApplicationEvent;

public class SandboxCreatedEvent extends ApplicationEvent {

  private final String id;
  private final String name;

  public SandboxCreatedEvent(String id, String name) {
    super(id);
    this.id = id;
    this.name = name;
  }
}