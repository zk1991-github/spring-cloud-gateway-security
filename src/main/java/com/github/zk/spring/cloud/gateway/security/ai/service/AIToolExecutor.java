package com.github.zk.spring.cloud.gateway.security.ai.service;

import com.github.zk.spring.cloud.gateway.security.ai.service.tool.AITool;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AIToolExecutor {

    private final Map<String, AITool> toolMap = new LinkedHashMap<>();

    public AIToolExecutor(List<AITool> toolList) {
        for (AITool tool : toolList) {
            toolMap.put(tool.getName(), tool);
        }
    }

    public List<Map<String, Object>> getToolDefinitions() {
        List<Map<String, Object>> tools = new ArrayList<>();
        for (AITool tool : toolMap.values()) {
            Map<String, Object> function = new LinkedHashMap<>();
            function.put("name", tool.getName());
            function.put("description", tool.getDescription());
            function.put("parameters", tool.getParameters());

            Map<String, Object> toolDef = new LinkedHashMap<>();
            toolDef.put("type", "function");
            toolDef.put("function", function);

            tools.add(toolDef);
        }
        return tools;
    }

    public String execute(String name, Map<String, Object> args) {
        AITool tool = toolMap.get(name);
        if (tool == null) {
            return "❌ 未知工具: " + name;
        }
        return tool.execute(args);
    }
}
