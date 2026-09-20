package com.blog.blogbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blog.blogbackend.common.Result;
import com.blog.blogbackend.entity.User;
import com.blog.blogbackend.mapper.UserMapper;
import com.blog.blogbackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserMapper userMapper;
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String,String> body) {
        String username=body.get("username");
        String password=body.get("password");
        User user=userMapper.selectOne(
                new QueryWrapper<User>().eq("username",username)
        );
        if (user==null||!user.getPassword().equals(password)){
            return Result.error("用户名或密码错误");
        }
        String token= JwtUtil.createToken(user.getId(), user.getUsername());
        Map<String,Object>data=new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        return Result.success(data);
    }

}
