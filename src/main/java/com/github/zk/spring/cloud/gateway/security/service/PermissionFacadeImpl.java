package com.github.zk.spring.cloud.gateway.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.zk.spring.cloud.gateway.security.ai.facade.PermissionFacade;
import com.github.zk.spring.cloud.gateway.security.dao.PermissionMapper;
import com.github.zk.spring.cloud.gateway.security.enums.IntfTypeEnum;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PermissionFacadeImpl implements PermissionFacade {

    private final IPermission iPermission;
    private final IRole iRole;
    private final PermissionMapper permissionMapper;

    public PermissionFacadeImpl(IPermission iPermission, IRole iRole, PermissionMapper permissionMapper) {
        this.iPermission = iPermission;
        this.iRole = iRole;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public String addPermission(String url, String urlName, String roleName) {
        List<RoleInfo> allRoles = iRole.queryAllRoles();
        RoleInfo targetRole = null;
        for (RoleInfo role : allRoles) {
            if (roleName.equals(role.getRoleName())) {
                targetRole = role;
                break;
            }
        }
        if (targetRole == null) {
            return "❌ 未找到角色「" + roleName + "」，可用角色：" +
                    allRoles.stream().map(RoleInfo::getRoleName).toList();
        }

        PermissionInfo permissionInfo = new PermissionInfo();
        permissionInfo.setUrl(url);
        permissionInfo.setUrlName(urlName);
        permissionInfo.setOpen(IntfTypeEnum.PRIVATE_PERMISSION.getIndex());
        permissionInfo.setGroupId(0L);
        permissionInfo.setRoleInfos(List.of(targetRole));

        int result = iPermission.addPermission(permissionInfo);
        if (result > 0) {
            return "✅ 已成功为角色「" + roleName + "」添加URL权限: " + url + "（显示名称: " + urlName + "）";
        } else {
            return "❌ 添加权限失败，请检查参数是否正确";
        }
    }

    @Override
    public String updatePermission(String url, String newUrl, String newUrlName, String roleName) {
        PermissionInfo existing = permissionMapper.selectOne(
                new QueryWrapper<PermissionInfo>().eq("url", url));
        if (existing == null) {
            return "❌ 未找到URL为「" + url + "」的权限";
        }

        if (!ObjectUtils.isEmpty(newUrl)) {
            existing.setUrl(newUrl);
        }
        if (!ObjectUtils.isEmpty(newUrlName)) {
            existing.setUrlName(newUrlName);
        }
        if (!ObjectUtils.isEmpty(roleName)) {
            List<RoleInfo> allRoles = iRole.queryAllRoles();
            RoleInfo targetRole = null;
            for (RoleInfo role : allRoles) {
                if (roleName.equals(role.getRoleName())) {
                    targetRole = role;
                    break;
                }
            }
            if (targetRole == null) {
                return "❌ 未找到角色「" + roleName + "」，可用角色：" +
                        allRoles.stream().map(RoleInfo::getRoleName).toList();
            }
            existing.setRoleInfos(List.of(targetRole));
        }

        int result = iPermission.updatePermission(existing);
        if (result > 0) {
            StringBuilder msg = new StringBuilder("✅ 已更新权限: ").append(url);
            if (!ObjectUtils.isEmpty(newUrl)) {
                msg.append(" → ").append(newUrl);
            }
            if (!ObjectUtils.isEmpty(newUrlName)) {
                msg.append("，名称: ").append(newUrlName);
            }
            if (!ObjectUtils.isEmpty(roleName)) {
                msg.append("，角色: ").append(roleName);
            }
            return msg.toString();
        } else {
            return "❌ 更新权限失败";
        }
    }

    @Override
    public String queryPermissions(String keywords, String roleName) {
        QueryWrapper<PermissionInfo> qw = new QueryWrapper<>();
        if (!ObjectUtils.isEmpty(keywords)) {
            qw.like("url", keywords)
                    .or().like("url_name", keywords);
        }
        qw.orderByDesc("create_time");
        List<PermissionInfo> permissions = permissionMapper.selectList(qw);

        if (!ObjectUtils.isEmpty(roleName)) {
            List<RoleInfo> allRoles = iRole.queryAllRoles();
            RoleInfo targetRole = null;
            for (RoleInfo role : allRoles) {
                if (roleName.equals(role.getRoleName())) {
                    targetRole = role;
                    break;
                }
            }
            if (targetRole == null) {
                return "未找到角色「" + roleName + "」，可用角色：" +
                        allRoles.stream().map(RoleInfo::getRoleName).toList();
            }
            List<PermissionInfo> rolePermissions = iRole.queryPermissionsByRoleId(targetRole.getId());
            if (rolePermissions == null) {
                return "角色「" + roleName + "」暂无权限";
            }
            Set<Long> permissionIds = rolePermissions.stream()
                    .map(PermissionInfo::getId).collect(Collectors.toSet());
            permissions = permissions.stream()
                    .filter(p -> permissionIds.contains(p.getId()))
                    .toList();
        }

        if (permissions.isEmpty()) {
            return "当前没有匹配的权限";
        }

        Map<Integer, String> typeNames = new HashMap<>();
        typeNames.put(0, "私有");
        typeNames.put(1, "公开");
        typeNames.put(2, "匿名");
        StringBuilder sb = new StringBuilder("查询到以下权限（共").append(permissions.size()).append("条）:\n");
        for (PermissionInfo p : permissions) {
            String typeName = typeNames.getOrDefault(p.getOpen(), "未知");
            sb.append("- [ID:").append(p.getId())
                    .append("] ").append(p.getUrlName())
                    .append(" (").append(p.getUrl()).append(")")
                    .append(" 类型:").append(typeName);
            if (p.getGroupId() != null && p.getGroupId() != 0) {
                sb.append(" 分组ID:").append(p.getGroupId());
            }
            sb.append("\n");
        }
        sb.append("如需查看某个权限详情，请指定更精确的搜索条件。");
        return sb.toString();
    }
}
