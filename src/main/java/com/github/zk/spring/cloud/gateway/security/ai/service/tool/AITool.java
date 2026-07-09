package com.github.zk.spring.cloud.gateway.security.ai.service.tool;

import java.util.Map;

public interface AITool {
    String getName();
    String getDescription();
    Map<String, Object> getParameters();
    String execute(Map<String, Object> args);
}
