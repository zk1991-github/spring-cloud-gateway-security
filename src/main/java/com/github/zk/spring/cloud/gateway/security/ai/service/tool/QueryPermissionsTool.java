package com.github.zk.spring.cloud.gateway.security.ai.service.tool;

import com.github.zk.spring.cloud.gateway.security.ai.facade.PermissionFacade;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QueryPermissionsTool implements AITool {

    private final PermissionFacade permissionFacade;

    public QueryPermissionsTool(PermissionFacade permissionFacade) {
        this.permissionFacade = permissionFacade;
    }

    @Override
    public String getName() {
        return "query_url_permissions";
    }

    @Override
    public String getDescription() {
        return "查询已有URL权限列表。可按关键字搜索URL或URL名称，也可按角色名称筛选。";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");

        Map<String, Object> properties = new HashMap<>();
        properties.put("keywords", Map.of("type", "string", "description", "搜索关键字（可选），匹配URL路径或权限名称"));
        properties.put("role_name", Map.of("type", "string", "description", "按角色名称筛选（可选）"));

        params.put("properties", properties);
        params.put("required", List.of());
        return params;
    }

    @Override
    public String execute(Map<String, Object> args) {
        String keywords = (String) args.get("keywords");
        String roleName = (String) args.get("role_name");

        return permissionFacade.queryPermissions(keywords, roleName);
    }
}
