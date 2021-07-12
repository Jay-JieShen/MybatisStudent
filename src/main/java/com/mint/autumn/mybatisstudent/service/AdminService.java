package com.mint.autumn.mybatisstudent.service;


import com.mint.autumn.mybatisstudent.domain.Admin;

/**
 * @Classname AdminService
 * @Description None
 * @Date 2019/6/25 11:07
 * @Created by Jay
 */
public interface AdminService {

    Admin findByAdmin(Admin admin);


    int editPswdByAdmin(Admin admin);
}
