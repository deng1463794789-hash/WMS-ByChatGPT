package com.wms.modules.system.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.exception.BusinessException;
import com.wms.modules.system.entity.*;
import com.wms.modules.system.mapper.*;
import com.wms.modules.system.service.SystemService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemServiceImpl implements SystemService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;
    private final SysSettingMapper sysSettingMapper;
    private final PasswordEncoder passwordEncoder;

    public SystemServiceImpl(SysUserMapper sysUserMapper, SysRoleMapper sysRoleMapper,
                             SysMenuMapper sysMenuMapper, SysSettingMapper sysSettingMapper,
                             PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysMenuMapper = sysMenuMapper;
        this.sysSettingMapper = sysSettingMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Map<String, Object>> getUserList(long pageNum, long pageSize, String keyword, String status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .orderByDesc(SysUser::getId);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword).or().like(SysUser::getRealName, keyword));
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(SysUser::getStatus, status);
        }
        Page<SysUser> page = sysUserMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return page.getRecords().stream().map(u -> {
            Map<String, Object> m = userToMap(u);
            if (u.getRoleId() != null) {
                SysRole role = sysRoleMapper.selectById(u.getRoleId());
                if (role != null) m.put("roleName", role.getName());
            }
            return m;
        }).collect(Collectors.toList());
    }

    @Override
    public long countUsers(String keyword, String status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword).or().like(SysUser::getRealName, keyword));
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(SysUser::getStatus, status);
        }
        return sysUserMapper.selectCount(wrapper);
    }

    @Override
    public Map<String, Object> getUser(Long id) {
        SysUser user = getUserOrThrow(id);
        Map<String, Object> m = userToMap(user);
        if (user.getRoleId() != null) {
            SysRole role = sysRoleMapper.selectById(user.getRoleId());
            if (role != null) m.put("roleName", role.getName());
        }
        return m;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(Map<String, Object> data) {
        SysUser user = new SysUser();
        user.setUsername((String) data.get("username"));
        user.setPassword(passwordEncoder.encode((String) data.get("password")));
        user.setRealName((String) data.get("realName"));
        user.setPhone((String) data.get("phone"));
        user.setEmail((String) data.get("email"));
        user.setRoleId(data.get("roleId") != null ? ((Number) data.get("roleId")).longValue() : null);
        user.setStatus((String) data.getOrDefault("status", "active"));
        sysUserMapper.insert(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long id, Map<String, Object> data) {
        SysUser user = getUserOrThrow(id);
        if (data.containsKey("password") && data.get("password") != null) {
            user.setPassword(passwordEncoder.encode((String) data.get("password")));
        }
        if (data.containsKey("realName")) user.setRealName((String) data.get("realName"));
        if (data.containsKey("phone")) user.setPhone((String) data.get("phone"));
        if (data.containsKey("email")) user.setEmail((String) data.get("email"));
        if (data.containsKey("roleId")) user.setRoleId(data.get("roleId") != null ? ((Number) data.get("roleId")).longValue() : null);
        if (data.containsKey("status")) user.setStatus((String) data.get("status"));
        sysUserMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        getUserOrThrow(id);
        sysUserMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id) {
        SysUser user = getUserOrThrow(id);
        user.setPassword(passwordEncoder.encode("123456"));
        sysUserMapper.updateById(user);
    }

    @Override
    public List<Map<String, Object>> getRoleList(long pageNum, long pageSize, String keyword) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .orderByDesc(SysRole::getId);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysRole::getCode, keyword).or().like(SysRole::getName, keyword));
        }
        Page<SysRole> page = sysRoleMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return page.getRecords().stream().map(this::roleToMap).collect(Collectors.toList());
    }

    @Override
    public long countRoles(String keyword) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysRole::getCode, keyword).or().like(SysRole::getName, keyword));
        }
        return sysRoleMapper.selectCount(wrapper);
    }

    @Override
    public Map<String, Object> getRole(Long id) {
        SysRole role = getRoleOrThrow(id);
        return roleToMap(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(Map<String, Object> data) {
        SysRole role = new SysRole();
        role.setCode((String) data.get("code"));
        role.setName((String) data.get("name"));
        role.setDescription((String) data.get("description"));
        role.setStatus((String) data.getOrDefault("status", "active"));
        sysRoleMapper.insert(role);
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long id, Map<String, Object> data) {
        SysRole role = getRoleOrThrow(id);
        if (data.containsKey("code")) role.setCode((String) data.get("code"));
        if (data.containsKey("name")) role.setName((String) data.get("name"));
        if (data.containsKey("description")) role.setDescription((String) data.get("description"));
        if (data.containsKey("status")) role.setStatus((String) data.get("status"));
        sysRoleMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        getRoleOrThrow(id);
        sysRoleMapper.deleteById(id);
    }

    @Override
    public List<Map<String, Object>> getAllRoles() {
        return sysRoleMapper.selectList(null).stream().map(this::roleToMap).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getPermissionTree() {
        List<SysMenu> all = sysMenuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, "active").orderByAsc(SysMenu::getSort));
        List<Map<String, Object>> roots = new ArrayList<>();
        for (SysMenu menu : all) {
            if (menu.getParentId() == null || menu.getParentId() == 0L) {
                Map<String, Object> m = menuToMap(menu);
                List<Map<String, Object>> children = all.stream()
                        .filter(c -> menu.getId().equals(c.getParentId()))
                        .map(this::menuToMap).collect(Collectors.toList());
                if (!children.isEmpty()) m.put("children", children);
                roots.add(m);
            }
        }
        return roots;
    }

    @Override
    public Map<String, Object> getSetting() {
        SysSetting setting = sysSettingMapper.selectOne(null);
        if (setting == null) return new LinkedHashMap<>();
        return settingToMap(setting);
    }

    @Override
    public void updateSetting(Map<String, Object> data) {
        SysSetting setting = sysSettingMapper.selectOne(null);
        if (setting == null) {
            setting = new SysSetting();
            setting.setId(1L);
        }
        if (data.containsKey("systemName")) setting.setSystemName((String) data.get("systemName"));
        if (data.containsKey("logo")) setting.setLogo((String) data.get("logo"));
        if (data.containsKey("passwordMinLength")) setting.setPasswordMinLength(((Number) data.get("passwordMinLength")).intValue());
        if (data.containsKey("loginMaxRetry")) setting.setLoginMaxRetry(((Number) data.get("loginMaxRetry")).intValue());
        if (data.containsKey("sessionTimeout")) setting.setSessionTimeout(((Number) data.get("sessionTimeout")).intValue());
        sysSettingMapper.insertOrUpdate(setting);
    }

    private SysUser getUserOrThrow(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) throw new BusinessException("user not found");
        return user;
    }

    private SysRole getRoleOrThrow(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) throw new BusinessException("role not found");
        return role;
    }

    private Map<String, Object> userToMap(SysUser u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getId());
        m.put("username", u.getUsername());
        m.put("realName", u.getRealName());
        m.put("phone", maskPhone(u.getPhone()));
        m.put("email", u.getEmail());
        m.put("avatar", u.getAvatar());
        m.put("roleId", u.getRoleId());
        m.put("status", u.getStatus());
        m.put("createdAt", u.getCreatedAt());
        return m;
    }

    private Map<String, Object> roleToMap(SysRole r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", r.getId());
        m.put("code", r.getCode());
        m.put("name", r.getName());
        m.put("description", r.getDescription());
        m.put("status", r.getStatus());
        m.put("createdAt", r.getCreatedAt());
        return m;
    }

    private Map<String, Object> menuToMap(SysMenu menu) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", menu.getId());
        m.put("parentId", menu.getParentId());
        m.put("name", menu.getName());
        m.put("path", menu.getPath());
        m.put("component", menu.getComponent());
        m.put("icon", menu.getIcon());
        m.put("sort", menu.getSort());
        m.put("type", menu.getType());
        m.put("permission", menu.getPermission());
        return m;
    }

    private Map<String, Object> settingToMap(SysSetting s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", s.getId());
        m.put("systemName", s.getSystemName());
        m.put("logo", s.getLogo());
        m.put("passwordMinLength", s.getPasswordMinLength());
        m.put("loginMaxRetry", s.getLoginMaxRetry());
        m.put("sessionTimeout", s.getSessionTimeout());
        return m;
    }

    private String maskPhone(String phone) {
        if (phone != null && phone.length() > 7) {
            return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
        }
        return phone;
    }
}
