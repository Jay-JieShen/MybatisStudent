package com.mint.autumn.mybatisstudent.mapper;


import com.mint.autumn.mybatisstudent.domain.Teacher;

import java.util.List;
import java.util.Map;

/**
 * @Classname TeacherMapper
 * @Description None
 * @Date 2019/6/28 19:06
 * @Created by Jay
 */
public interface TeacherMapper {
    List<Teacher> queryList(Map<String, Object> paramMap);

    Integer queryCount(Map<String, Object> paramMap);

    int deleteTeacher(List<Integer> ids);

    int addTeacher(Teacher teacher);

    Teacher findById(Integer tid);

    int editTeacher(Teacher teacher);

    Teacher findByTeacher(Teacher teacher);

    int editPswdByTeacher(Teacher teacher);
}
