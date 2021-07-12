package com.mint.autumn.mybatisstudent.controller;


import com.mint.autumn.mybatisstudent.domain.Course;
import com.mint.autumn.mybatisstudent.service.CourseService;
import com.mint.autumn.mybatisstudent.util.AjaxResult;
import com.mint.autumn.mybatisstudent.util.Data;
import com.mint.autumn.mybatisstudent.util.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Classname CourseController
 * @Description None
 * @Date 2019/6/29 20:02
 * @Created by Jay
 */
@Controller
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/course_list")
    public String courseList(){
        return "course/courseList";
    }

    /**
     * get course list
     * @param page
     * @param rows
     * @param name
     * @param teacherid
     * @param from
     * @return
     */
    @PostMapping("/getCourseList")
    @ResponseBody
    public Object getClazzList(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "100")Integer rows,
                               String name,
                               @RequestParam(value = "teacherid", defaultValue = "0")String teacherid ,String from){
        Map<String,Object> paramMap = new HashMap();
        paramMap.put("pageno",page);
        paramMap.put("pagesize",rows);
        if(!StringUtils.isEmpty(name))  paramMap.put("name",name);
        if(!teacherid.equals("0"))  paramMap.put("teacherId",teacherid);
        PageBean<Course> pageBean = courseService.queryPage(paramMap);
        if(!StringUtils.isEmpty(from) && from.equals("combox")){
            return pageBean.getDatas();
        }else{
            Map<String,Object> result = new HashMap();
            result.put("total",pageBean.getTotalsize());
            result.put("rows",pageBean.getDatas());
            return result;
        }
    }

    /**
     * add course
     * @param course
     * @return
     */
    @PostMapping("/addCourse")
    @ResponseBody
    public AjaxResult addCourse(Course course){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            int count = courseService.addCourse(course);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("add course successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to add course");
            }
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("fail to add course");
        }
        return ajaxResult;
    }


    /**
     * update course information
     * @param course
     * @return
     */
    @PostMapping("/editCourse")
    @ResponseBody
    public AjaxResult editCourse(Course course){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            int count = courseService.editCourse(course);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("update course successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to update course");
            }
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("fail to update course");
        }
        return ajaxResult;
    }


    @PostMapping("/deleteCourse")
    @ResponseBody
    public AjaxResult deleteCourse(Data data){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            int count = courseService.deleteCourse(data.getIds());
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("delete course successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to delete course");
            }
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("could not delete the course that has any students or teachers");
        }
        return ajaxResult;
    }
}
