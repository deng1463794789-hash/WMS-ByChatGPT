package com.wms.modules.auth.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.exception.BusinessException;
import com.wms.common.security.CustomUserDetails;
import com.wms.common.security.JwtUtils;
import com.wms.modules.auth.dto.LoginRequest;
import com.wms.modules.auth.dto.LoginResponse;
import com.wms.modules.auth.service.AuthService;
import com.wms.modules.system.entity.SysMenu;
import com.wms.modules.system.entity.SysRole;
import com.wms.modules.system.entity.SysUser;
import com.wms.modules.system.mapper.SysMenuMapper;
import com.wms.modules.system.mapper.SysRoleMapper;
import com.wms.modules.system.mapper.SysUserMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(SysUserMapper sysUserMapper, SysRoleMapper sysRoleMapper,
                           SysMenuMapper sysMenuMapper, AuthenticationManager authenticationManager,
                           JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysMenuMapper = sysMenuMapper;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername()));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if ("inactive".equals(user.getStatus())) {
            throw new BusinessException("账户已被禁用");
        }
        if ("locked".equals(user.getStatus())) {
            throw new BusinessException("账户已被锁定，请联系管理员");
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BusinessException("用户名或密码错误");
        } catch (LockedException e) {
            throw new BusinessException("账户已被锁定");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(user.getId(), user.getUsername());

        Map<String, Object> userInfo = buildUserInfo(user);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setUserInfo(userInfo);
        response.setMenus(getMenus(user.getId()));
        return response;
    }

    @Override
    public void logout() {
    }

    @Override
    public Map<String, Object> getUserInfo(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return buildUserInfo(user);
    }

    @Override
    public List<Map<String, Object>> getMenus(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) return new ArrayList<>();

        List<Long> menuIds = new ArrayList<>();
        if (user.getRoleId() != null) {
            menuIds = sysRoleMapper.selectMenuIdsByRoleId(user.getRoleId());
        }

        List<SysMenu> all;
        if (menuIds == null || menuIds.isEmpty()) {
            all = sysMenuMapper.selectList(
                    new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, "active"));
        } else {
            all = sysMenuMapper.selectList(
                    new LambdaQueryWrapper<SysMenu>()
                            .eq(SysMenu::getStatus, "active")
                            .in(SysMenu::getId, menuIds));
        }

        List<Map<String, Object>> roots = new ArrayList<>();
        for (SysMenu menu : all) {
            if (menu.getParentId() == null || menu.getParentId() == 0L) {
                Map<String, Object> m = menuToMap(menu);
                List<Map<String, Object>> children = all.stream()
                        .filter(c -> menu.getId().equals(c.getParentId()))
                        .map(this::menuToMap)
                        .collect(Collectors.toList());
                if (!children.isEmpty()) m.put("children", children);
                roots.add(m);
            }
        }
        return roots;
    }

    private Map<String, Object> buildUserInfo(SysUser user) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("realName", user.getRealName());
        info.put("avatar", user.getAvatar());
        info.put("roleId", user.getRoleId());
        if (user.getRoleId() != null) {
            SysRole role = sysRoleMapper.selectById(user.getRoleId());
            if (role != null) {
                info.put("roleName", role.getName());
                info.put("roleCode", role.getCode());
            }
        }
        return info;
    }

    private Map<String, Object> menuToMap(SysMenu menu) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", menu.getId());
        map.put("parentId", menu.getParentId());
        map.put("name", menu.getName());
        map.put("path", menu.getPath());
        map.put("component", menu.getComponent());
        map.put("icon", menu.getIcon());
        map.put("sort", menu.getSort());
        map.put("type", menu.getType());
        map.put("permission", menu.getPermission());
        return map;
    }
}
