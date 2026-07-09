package com.wms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class WmsApplicationTests {

    @Test
    void contextLoads() {
    }


    /**
     * 测试 admin123 是否匹配当前数据库里写死的 BCrypt 密文
     */
    @Test
    void testAdminPasswordMatches() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "admin123";

        String encodedPassword =
                "$2a$10$ESuCbYXaXvD54Eg6OMq5GeJcG1deK3e4T.dofWJZbxELfHZ8fH7pi";

        boolean matches = encoder.matches(rawPassword, encodedPassword);

        System.out.println("admin123 是否匹配数据库密文: " + matches);

        assertTrue(matches, "数据库中的 BCrypt 密文与 admin123 不匹配");
    }

    /**
     * 生成一个新的 admin123 BCrypt 密文
     */
    @Test
    void generateAdminPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("明文密码: " + rawPassword);
        System.out.println("BCrypt 密文: " + encodedPassword);
        System.out.println("校验结果: " + encoder.matches(rawPassword, encodedPassword));
    }
}
