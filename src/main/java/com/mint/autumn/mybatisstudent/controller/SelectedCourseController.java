package com.mint.autumn.mybatisstudent.controller;


import com.mint.autumn.mybatisstudent.domain.SelectedCourse;
import com.mint.autumn.mybatisstudent.domain.Student;
import com.mint.autumn.mybatisstudent.service.SelectedCourseService;
import com.mint.autumn.mybatisstudent.util.AjaxResult;
import com.mint.autumn.mybatisstudent.util.Const;
import com.mint.autumn.mybatisstudent.util.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @Classname SelectedCourseController
 * @Description selected course controller
 * @Date 2019/6/30 10:39
 * @Created by Jay
 */
@Controller
@RequestMapping("/selectedCourse")
public class SelectedCourseController {

    @Autowired
    private SelectedCourseService selectedCourseService;



    @GetMapping("/selectedCourse_list")
    public String selectedCourseList(){
        return "course/selectedCourseList";
    }

    /**
     * get selected course list
     * @param page
     * @param rows
     * @param studentid
     * @param courseid
     * @param from
     * @return
     */
    @PostMapping("/getSelectedCourseList")
    @ResponseBody
    public Object getClazzList(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "100")Integer rows,
                               @RequestParam(value = "teacherid", defaultValue = "0")String studentid,
                               @RequestParam(value = "teacherid", defaultValue = "0")String courseid ,String from,HttpSession session){
        Map<String,Object> paramMap = new HashMap();
        paramMap.put("pageno",page);
        paramMap.put("pagesize",rows);
        if(!studentid.equals("0"))  paramMap.put("studentId",studentid);
        if(!courseid.equals("0"))  paramMap.put("courseId",courseid);
        //check if teacher or student
        Student student = (Student) session.getAttribute(Const.STUDENT);
        if(!StringUtils.isEmpty(student)){
            //if student, inquire himself
            paramMap.put("studentid",student.getId());
        }
        PageBean<SelectedCourse> pageBean = selectedCourseService.queryPage(paramMap);
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
     * add selected course
     * @param selectedCourse
     * @return
     */
    @PostMapping("/addSelectedCourse")
    @ResponseBody
    public AjaxResult addSelectedCourse(SelectedCourse selectedCourse){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            int count = selectedCourseService.addSelectedCourse(selectedCourse);
            if(count == 1){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("add selected course successfully");
            }else if(count == 0){
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("selected course is full");
            }else if(count == 2){
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("this course is selected already");
            }
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("system error, please try again!");
        }
        return ajaxResult;
    }


    /**
     * delete selected course
     * @param id
     * @return
     */
    @PostMapping("/deleteSelectedCourse")
    @ResponseBody
    public AjaxResult deleteSelectedCourse(Integer id){
        AjaxResult ajaxResult = new AjaxResult();

        try {
            int count = selectedCourseService.deleteSelectedCourse(id);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("delete selected course successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to delete course");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ajaxResult;
    }


}
