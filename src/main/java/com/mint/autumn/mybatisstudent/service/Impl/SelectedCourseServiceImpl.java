package com.mint.autumn.mybatisstudent.service.Impl;


import com.mint.autumn.mybatisstudent.domain.SelectedCourse;
import com.mint.autumn.mybatisstudent.mapper.CourseMapper;
import com.mint.autumn.mybatisstudent.mapper.SelectedCourseMapper;
import com.mint.autumn.mybatisstudent.service.SelectedCourseService;
import com.mint.autumn.mybatisstudent.util.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @Classname SelectedCourseServiceImpl
 * @Description None
 * @Date 2019/6/30 10:48
 * @Created by Jay
 */
@Service
public class SelectedCourseServiceImpl implements SelectedCourseService {

    @Autowired
    private SelectedCourseMapper selectedCourseMapper;
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public PageBean<SelectedCourse> queryPage(Map<String, Object> paramMap) {
        PageBean<SelectedCourse> pageBean = new PageBean<>((Integer) paramMap.get("pageno"),(Integer) paramMap.get("pagesize"));

        Integer startIndex = pageBean.getStartIndex();
        paramMap.put("startIndex",startIndex);
        List<SelectedCourse> datas = selectedCourseMapper.queryList(paramMap);
        pageBean.setDatas(datas);

        Integer totalsize = selectedCourseMapper.queryCount(paramMap);
        pageBean.setTotalsize(totalsize);
        return pageBean;
    }

    @Override
    @Transactional
    public int addSelectedCourse(SelectedCourse selectedCourse) {
        SelectedCourse s = selectedCourseMapper.findBySelectedCourse(selectedCourse);
        if(StringUtils.isEmpty(s)){
            int count = courseMapper.addStudentNum(selectedCourse.getCourseId());
            if(count == 1){
                selectedCourseMapper.addSelectedCourse(selectedCourse);
                return count;
            }
            if(count == 0){
                return count;
            }
        }else{
            return 2;
        }
        return 3;
    }

    @Override
    @Transactional
    public int deleteSelectedCourse(Integer id) {
        SelectedCourse selectedCourse = selectedCourseMapper.findById(id);
        courseMapper.deleteStudentNum(selectedCourse.getCourseId());
        return selectedCourseMapper.deleteSelectedCourse(id);
    }

    @Override
    public boolean isStudentId(int id) {
        List<SelectedCourse> selectedCourseList = selectedCourseMapper.isStudentId(id);
        if (selectedCourseList.isEmpty()){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public List<SelectedCourse> getAllBySid(int studentid) {
        return selectedCourseMapper.getAllBySid(studentid);
    }
}
