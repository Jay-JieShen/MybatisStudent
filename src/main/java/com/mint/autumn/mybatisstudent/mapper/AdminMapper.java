package com.mint.autumn.mybatisstudent.mapper;


import com.mint.autumn.mybatisstudent.domain.Admin;
import org.springframework.stereotype.Repository;

/**
 * @Classname UserMapper
 * @Description None
 * @Date 2019/6/24 20:09
 * @Created by Jay
 */
@Repository
public interface AdminMapper {

    Admin findByAdmin(Admin admin);


    int editPswdByAdmin(Admin admin);
}
