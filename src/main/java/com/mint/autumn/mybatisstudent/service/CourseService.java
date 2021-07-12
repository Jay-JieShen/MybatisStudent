package com.mint.autumn.mybatisstudent.service;


import com.mint.autumn.mybatisstudent.domain.Course;
import com.mint.autumn.mybatisstudent.util.PageBean;

import java.util.List;
import java.util.Map;

/**
 * @Classname CourseService
 * @Description None
 * @Date 2019/6/29 20:09
 * @Created by Jay
 */
public interface CourseService {
    PageBean<Course> queryPage(Map<String, Object> paramMap);

    int addCourse(Course course);

    int editCourse(Course course);

    int deleteCourse(List<Integer> ids);

    List<Course> getCourseById(List<Integer> ids);

    int findByName(String name);
}
