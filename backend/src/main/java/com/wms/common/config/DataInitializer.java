package com.wms.common.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.modules.system.entity.SysUser;
import com.wms.modules.system.mapper.SysUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        SysUser admin = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, DEFAULT_ADMIN_USERNAME));
        if (admin == null) {
            SysUser user = new SysUser();
            user.setUsername(DEFAULT_ADMIN_USERNAME);
            user.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
            user.setRealName("System Administrator");
            user.setPhone("13600136001");
            user.setEmail("admin@wms.com");
            user.setRoleId(1L);
            user.setStatus("active");
            sysUserMapper.insert(user);
            log.info("Default admin account initialized: admin / admin123");
            return;
        }

        if (!passwordEncoder.matches(DEFAULT_ADMIN_PASSWORD, admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
            if (admin.getStatus() == null || admin.getStatus().isBlank()) {
                admin.setStatus("active");
            }
            sysUserMapper.updateById(admin);
            log.warn("Default admin password was invalid and has been reset to admin123");
        }
    }
}
