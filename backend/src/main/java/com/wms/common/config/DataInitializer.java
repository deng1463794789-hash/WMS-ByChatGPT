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

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        SysUser admin = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "admin"));
        if (admin == null) {
            SysUser user = new SysUser();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setRealName("System Administrator");
            user.setPhone("13600136001");
            user.setEmail("admin@wms.com");
            user.setRoleId(1L);
            user.setStatus("active");
            sysUserMapper.insert(user);
            log.info("Default admin account initialized: admin / admin123");
        }
    }
}
