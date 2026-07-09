package com.github.zk.spring.cloud.gateway.security.ai.service.tool;

import com.github.zk.spring.cloud.gateway.security.ai.facade.PermissionFacade;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AddPermissionTool implements AITool {

    private final PermissionFacade permissionFacade;

    public AddPermissionTool(PermissionFacade permissionFacade) {
        this.permissionFacade = permissionFacade;
    }

    @Override
    public String getName() {
        return "add_url_permission";
    }

    @Override
    public String getDescription() {
        return "为指定角色添加新的URL访问权限。权限类型为私有（需要登录并有角色绑定才能访问）。";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");

        Map<String, Object> properties = new HashMap<>();
        properties.put("url", Map.of("type", "string", "description", "URL路径，如 /api/users/**"));
        properties.put("role_name", Map.of("type", "string", "description", "角色名称，如 管理员"));
        properties.put("url_name", Map.of("type", "string", "description", "权限显示名称（可选，默认使用url值）"));

        params.put("properties", properties);
        params.put("required", List.of("url", "role_name"));
        return params;
    }

    @Override
    public String execute(Map<String, Object> args) {
        String url = (String) args.get("url");
        String roleName = (String) args.get("role_name");
        String urlName = (String) args.getOrDefault("url_name", url);

        if (url == null || url.isBlank()) {
            return "❌ 缺少URL参数";
        }
        if (roleName == null || roleName.isBlank()) {
            return "❌ 缺少角色名称参数";
        }

        return permissionFacade.addPermission(url, urlName, roleName);
    }
}
