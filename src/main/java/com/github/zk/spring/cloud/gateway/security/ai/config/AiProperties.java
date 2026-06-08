package com.github.zk.spring.cloud.gateway.security.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("ai")
public class AiProperties {
    private boolean enabled = false;
    private String provider = "openai";
    private String apiKey;
    private String apiUrl = "https://api.openai.com/v1/chat/completions";
    private String model = "gpt-4o-mini";
    private String systemPrompt = "你是网关权限管理系统的AI助手。你可以帮助管理员管理URL访问权限。\n\n" +
            "你的工具包括：\n" +
            "1. add_url_permission - 为指定角色添加新的URL权限\n" +
            "2. update_url_permission - 修改已有URL权限的路径、名称或绑定的角色\n" +
            "3. query_url_permissions - 查询已有URL权限列表\n\n" +
            "操作规则：\n" +
            "- 添加权限时，权限为私有类型（需要角色绑定）\n" +
            "- 更新权限时，如果涉及角色变更，会重新绑定角色\n" +
            "- 如果用户没有指定角色名称，你需要先询问确认";

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getApiUrl() { return apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }
}
