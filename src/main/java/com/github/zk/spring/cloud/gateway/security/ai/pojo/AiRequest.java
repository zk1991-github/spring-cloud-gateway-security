package com.github.zk.spring.cloud.gateway.security.ai.pojo;

import java.util.List;
import java.util.Map;

public class AiRequest {
    private String message;
    private List<Map<String, String>> history;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<Map<String, String>> getHistory() { return history; }
    public void setHistory(List<Map<String, String>> history) { this.history = history; }
}
