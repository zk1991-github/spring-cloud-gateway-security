package com.github.zk.spring.cloud.gateway.security.ai.facade;

public interface PermissionFacade {
    String addPermission(String url, String urlName, String roleName);
    String updatePermission(String url, String newUrl, String newUrlName, String roleName);
    String queryPermissions(String keywords, String roleName);
}
