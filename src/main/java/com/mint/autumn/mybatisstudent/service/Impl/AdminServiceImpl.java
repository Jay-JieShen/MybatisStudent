package com.mint.autumn.mybatisstudent.service.Impl;


import com.mint.autumn.mybatisstudent.domain.Admin;
import com.mint.autumn.mybatisstudent.mapper.AdminMapper;
import com.mint.autumn.mybatisstudent.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Classname UserServiceImpl
 * @Description None
 * @Date 2019/6/25 11:08
 * @Created by Jay
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin findByAdmin(Admin admin) {
        return adminMapper.findByAdmin(admin);
    }

    @Override
    public int editPswdByAdmin(Admin admin) {
        return adminMapper.editPswdByAdmin(admin);
    }

}
