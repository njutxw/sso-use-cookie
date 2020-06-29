package com.sso.login.controller;

import com.sso.login.pojo.User;
import com.sso.login.utils.LoginCacheUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static Set<User> dbUsers;
    static{
        dbUsers = new HashSet<>();
        dbUsers.add(new User(0,"zhangsan","123456"));
        dbUsers.add(new User(1,"lisi","1234567"));
        dbUsers.add(new User(2,"wangwu","123"));
    }

    @PostMapping()
    public String doLogin(User user, HttpSession session, HttpServletResponse response){
        String target = (String) session.getAttribute("target");

        Optional<User> first = dbUsers.stream().filter(users -> users.getUsername().equals(user.getUsername()) &&
                users.getPassword().equals(user.getPassword())).findFirst();
        //判断用户是否登录
        if(first.isPresent()){
            //保存用户登录信息
            String token = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("TOKEN", token);
            cookie.setDomain("codeshop.com");
            response.addCookie(cookie);
            LoginCacheUtil.loginUser.put(token,first.get());
        }else{
            //登录失败
            session.setAttribute("msg","用户名或密码错误");
            return "login";
        }
        //重定向到target地址
        return "redirect:"+target;
    }

    @GetMapping("info")
    @ResponseBody
    public ResponseEntity<User> getUserInfo(String token){
        if(!StringUtils.isEmpty(token)){
            User user = LoginCacheUtil.loginUser.get(token);
            return ResponseEntity.ok(user);
        }else{
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

}



























