package com.advertising;

import com.advertising.entity.User;
import com.advertising.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 初始化演示数据
 */
//@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 检查是否已有用户
        if (userMapper.selectByUsername("admin") == null) {
            System.out.println("Creating demo users...");
            
            // 创建管理员
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRealName("Administrator");
            admin.setEmail("admin@example.com");
            admin.setRole(User.ROLE_ADMIN);
            admin.setStatus(User.STATUS_ENABLED);
            userMapper.insert(admin);
            
            // 创建销售用户
            User sales = new User();
            sales.setUsername("sales");
            sales.setPassword(passwordEncoder.encode("sales123"));
            sales.setRealName("Sales Manager");
            sales.setEmail("sales@example.com");
            sales.setRole(User.ROLE_SALES);
            sales.setStatus(User.STATUS_ENABLED);
            userMapper.insert(sales);
            
            System.out.println("Demo users created successfully!");
        } else {
            System.out.println("Demo users already exist, skipping initialization.");
        }
    }
}
