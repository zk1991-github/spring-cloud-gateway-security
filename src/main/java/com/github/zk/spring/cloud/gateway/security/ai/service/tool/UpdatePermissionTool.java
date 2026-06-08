package com.github.zk.spring.cloud.gateway.security.ai.service.tool;

import com.github.zk.spring.cloud.gateway.security.ai.facade.PermissionFacade;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UpdatePermissionTool implements AITool {

    private final PermissionFacade permissionFacade;

    public UpdatePermissionTool(PermissionFacade permissionFacade) {
        this.permissionFacade = permissionFacade;
    }

    @Override
    public String getName() {
        return "update_url_permission";
    }

    @Override
    public String getDescription() {
        return "修改已有URL权限的路径、显示名称或绑定的角色。通过URL查找要修改的权限。";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");

        Map<String, Object> properties = new HashMap<>();
        properties.put("url", Map.of("type", "string", "description", "当前URL路径，用于查找要修改的权限"));
        properties.put("new_url", Map.of("type", "string", "description", "新的URL路径（可选，不修改则省略）"));
        properties.put("new_url_name", Map.of("type", "string", "description", "新的权限显示名称（可选）"));
        properties.put("role_name", Map.of("type", "string", "description", "要绑定到此权限的角色名称（可选，不修改则省略）"));

        params.put("properties", properties);
        params.put("required", List.of("url"));
        return params;
    }

    @Override
    public String execute(Map<String, Object> args) {
        String url = (String) args.get("url");
        String newUrl = (String) args.get("new_url");
        String newUrlName = (String) args.get("new_url_name");
        String roleName = (String) args.get("role_name");

        if (url == null || url.isBlank()) {
            return "❌ 缺少URL参数";
        }

        return permissionFacade.updatePermission(url, newUrl, newUrlName, roleName);
    }
}
