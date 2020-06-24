package com.xuecheng.framework.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by mrt on 2018/5/22.
 */
public class BCryptUtil {

    /**
     * 每次都不一样，结果应该包含了某些信息
     */
    public static String encode(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashPass = passwordEncoder.encode(password);
        return hashPass;
    }

    /**
     * 只要hashPass是用同一个密码生成的，那一定能对上
     */
    public static boolean matches(String password, String hashPass) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean f = passwordEncoder.matches(password, hashPass);
        return f;
    }

    public static void main(String[] args) {
        System.out.println(matches("sdfsdf", encode("sdfsdf")));//true
    }
}
