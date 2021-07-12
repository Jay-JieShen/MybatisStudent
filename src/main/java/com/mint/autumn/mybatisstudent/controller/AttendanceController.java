package com.mint.autumn.mybatisstudent.controller;


import com.mint.autumn.mybatisstudent.domain.Attendance;
import com.mint.autumn.mybatisstudent.domain.Course;
import com.mint.autumn.mybatisstudent.domain.SelectedCourse;
import com.mint.autumn.mybatisstudent.domain.Student;
import com.mint.autumn.mybatisstudent.service.AttendanceService;
import com.mint.autumn.mybatisstudent.service.CourseService;
import com.mint.autumn.mybatisstudent.service.SelectedCourseService;
import com.mint.autumn.mybatisstudent.util.AjaxResult;
import com.mint.autumn.mybatisstudent.util.Const;
import com.mint.autumn.mybatisstudent.util.DateFormatUtil;
import com.mint.autumn.mybatisstudent.util.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @Classname AttendanceController
 * @Description None
 * @Date 2019/7/1 11:57
 * @Created by Jay
 */
@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private SelectedCourseService selectedCourseService;
    @Autowired
    private CourseService courseService;

    @GetMapping("/attendance_list")
    public String attendanceList(){
        return "attendance/attendanceList";
    }


    /**
     * get attendance list
     * @param page
     * @param rows
     * @param studentid
     * @param courseid
     * @param type
     * @param date
     * @param from
     * @param session
     * @return
     */
    @RequestMapping("/getAttendanceList")
    @ResponseBody
    public Object getAttendanceList(@RequestParam(value = "page", defaultValue = "1")Integer page,
                                 @RequestParam(value = "rows", defaultValue = "100")Integer rows,
                                 @RequestParam(value = "studentid", defaultValue = "0")String studentid,
                                 @RequestParam(value = "courseid", defaultValue = "0")String courseid,
                                 String type,String date, String from, HttpSession session){
        Map<String,Object> paramMap = new HashMap();
        paramMap.put("pageno",page);
        paramMap.put("pagesize",rows);
        if(!studentid.equals("0"))  paramMap.put("studentid",studentid);
        if(!courseid.equals("0"))  paramMap.put("courseid",courseid);
        if(!StringUtils.isEmpty(type))  paramMap.put("type",type);
        if(!StringUtils.isEmpty(date))  paramMap.put("date",date);

        //Determine whether he is a teacher or a student
        Student student = (Student) session.getAttribute(Const.STUDENT);
        if(!StringUtils.isEmpty(student)){
            //if a student, he only inquires himself.
            paramMap.put("studentid",student.getId());
        }
        PageBean<Attendance> pageBean = attendanceService.queryPage(paramMap);
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
     * inquire a student's selected course by studentid.
     * @param studentid
     * @return
     */
    @RequestMapping("/getStudentSelectedCourseList")
    @ResponseBody
    public Object getStudentSelectedCourseList(@RequestParam(value = "studentid", defaultValue = "0")String studentid){
        //inquire selected course by student id
        List<SelectedCourse> selectedCourseList = selectedCourseService.getAllBySid(Integer.parseInt(studentid));
        //inquire selected course information list by student id.
        List<Integer> ids = new ArrayList<>();
        for(SelectedCourse selectedCourse : selectedCourseList){
            ids.add(selectedCourse.getCourseId());
        }
        List<Course> courseList = courseService.getCourseById(ids);
        return courseList;
    }


    /**
     * add attendance
     * @param attendance
     * @return
     */
    @PostMapping("/addAttendance")
    @ResponseBody
    public AjaxResult addAttendance(Attendance attendance){
        AjaxResult ajaxResult = new AjaxResult();
        attendance.setDate(DateFormatUtil.getFormatDate(new Date(),"yyyy-MM-dd"));
        //whether sign in or not.
        if(attendanceService.isAttendance(attendance)){
            //true means sign in already.
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("please do not double sign in!");
        }else{
            int count = attendanceService.addAtendance(attendance);
            if(count > 0){
                //sign in successfully
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("sign in successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("system error, please sign in again!");
            }
        }
        return ajaxResult;
    }

    /**
     * delete sign in record.
     * @param id
     * @return
     */
    @PostMapping("/deleteAttendance")
    @ResponseBody
    public AjaxResult deleteAttendance(Integer id){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            int count = attendanceService.deleteAttendance(id);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("delete successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to delete ");
            }
        } catch (Exception e) {
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("system error, please try again");
            e.printStackTrace();
        }
        return ajaxResult;
    }
}
