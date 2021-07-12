package com.mint.autumn.mybatisstudent.service;


import com.mint.autumn.mybatisstudent.domain.Clazz;
import com.mint.autumn.mybatisstudent.util.PageBean;

import java.util.List;
import java.util.Map;

/**
 * @Classname ClazzService
 * @Description None
 * @Date 2019/6/26 10:14
 * @Created by Jay
 */
public interface ClazzService {
    PageBean<Clazz> queryPage(Map<String, Object> paramMap);

    int addClazz(Clazz clazz);

    int deleteClazz(List<Integer> ids);

    int editClazz(Clazz clazz);

    Clazz findByName(String clazzName);

}
