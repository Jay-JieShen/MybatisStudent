package com.mint.autumn.mybatisstudent.service;


import com.mint.autumn.mybatisstudent.domain.Attendance;
import com.mint.autumn.mybatisstudent.util.PageBean;

import java.util.Map;

/**
 * @Classname AttendanceService
 * @Description None
 * @Date 2019/7/1 15:47
 * @Created by Jay
 */
public interface AttendanceService {
    PageBean<Attendance> queryPage(Map<String, Object> paramMap);

    boolean isAttendance(Attendance attendance);

    int addAtendance(Attendance attendance);

    int deleteAttendance(Integer id);
}
