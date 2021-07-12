package com.mint.autumn.mybatisstudent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Classname UserController
 * @Description None
 * @Date 2019/6/25 17:51
 * @Created by Jay
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/list")
    public String list(){
        return "admin/admin_list";
    }
}
