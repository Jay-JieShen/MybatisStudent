package com.mint.autumn.mybatisstudent.controller;


import com.mint.autumn.mybatisstudent.domain.Admin;
import com.mint.autumn.mybatisstudent.domain.Student;
import com.mint.autumn.mybatisstudent.domain.Teacher;
import com.mint.autumn.mybatisstudent.service.AdminService;
import com.mint.autumn.mybatisstudent.service.StudentService;
import com.mint.autumn.mybatisstudent.service.TeacherService;
import com.mint.autumn.mybatisstudent.util.AjaxResult;
import com.mint.autumn.mybatisstudent.util.Const;
import com.mint.autumn.mybatisstudent.util.CpachaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @Classname SystemController
 * @Description None
 * @Date 2019/6/24 19:25
 * @Created by Jay
 */
@Controller
@RequestMapping("/system")
public class SystemController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;

    /**
     * redirect to login page
     * @return
     */
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    /**
     * login
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public AjaxResult submitlogin(String username, String password, String code, String type,
                                  HttpSession session){
        AjaxResult ajaxResult = new AjaxResult();
        if(StringUtils.isEmpty(username)){
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("please input user name");
            return ajaxResult;
        }
        if(StringUtils.isEmpty(password)){
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("please input password");
            return ajaxResult;
        }
        if(StringUtils.isEmpty(code)){
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("please input check code");
            return ajaxResult;
        }
        if(StringUtils.isEmpty(session.getAttribute(Const.CODE))){
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("session time out, please re-login");
            return ajaxResult;
        }else{
            if(!code.equalsIgnoreCase((String) session.getAttribute(Const.CODE))){
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("check code error");
                return ajaxResult;
            }
        }
        //base on type
        switch (type){
            case "1":{ //admin
                Admin admin = new Admin();
                admin.setPassword(password);
                admin.setUsername(username);
                Admin ad = adminService.findByAdmin(admin);
                if(StringUtils.isEmpty(ad)){
                    ajaxResult.setSuccess(false);
                    ajaxResult.setMessage("user name or password wrong");
                    return ajaxResult;
                }
                ajaxResult.setSuccess(true);
                session.setAttribute(Const.ADMIN,ad);
                session.setAttribute(Const.USERTYPE,"1");
                break;
            }
            case "2":{
                Student student = new Student();
                student.setPassword(password);
                student.setUsername(username);
                Student st = studentService.findByStudent(student);
                if(StringUtils.isEmpty(st)){
                    ajaxResult.setSuccess(false);
                    ajaxResult.setMessage("user name or password wrong");
                    return ajaxResult;
                }
                ajaxResult.setSuccess(true);
                session.setAttribute(Const.STUDENT,st);
                session.setAttribute(Const.USERTYPE,"2");
                break;
            }
            case "3":{
                Teacher teacher = new Teacher();
                teacher.setPassword(password);
                teacher.setUsername(username);
                Teacher tr = teacherService.findByTeacher(teacher);
                if(StringUtils.isEmpty(tr)){
                    ajaxResult.setSuccess(false);
                    ajaxResult.setMessage("user name or password wrong");
                    return ajaxResult;
                }
                ajaxResult.setSuccess(true);
                session.setAttribute(Const.TEACHER,tr);
                session.setAttribute(Const.USERTYPE,"3");
                break;
            }
        }
        return ajaxResult;
    }

    /**
     * checkcode
     * @param request
     * @param response
     * @param vl
     * @param w
     * @param h
     */
    @GetMapping("/checkCode")
    public void generateCpacha(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(value="vl",defaultValue="4",required=false) Integer vl,
                               @RequestParam(value="w",defaultValue="110",required=false) Integer w,
                               @RequestParam(value="h",defaultValue="39",required=false) Integer h){
        CpachaUtil cpachaUtil = new CpachaUtil(vl, w, h);
        String generatorVCode = cpachaUtil.generatorVCode();
        request.getSession().setAttribute(Const.CODE, generatorVCode);
        BufferedImage generatorRotateVCodeImage = cpachaUtil.generatorRotateVCodeImage(generatorVCode, true);
        try {
            ImageIO.write(generatorRotateVCodeImage, "gif", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * redirect index
     * @return
     */
    @GetMapping("/index")
    public String index(){
        return "system/index";
    }


    /**
     * login out
     * @param session
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "login";
    }


    /**
     * get image address
     * @param sid
     * @param tid
     * @return
     */
    @RequestMapping("/getPhoto")
    @ResponseBody
    public AjaxResult getPhoto(@RequestParam(value = "sid",defaultValue = "0") Integer sid,
                               @RequestParam(value = "tid",defaultValue = "0")Integer tid){
        AjaxResult ajaxResult = new AjaxResult();
        if(sid != 0){
            Student student = studentService.findById(sid);
            ajaxResult.setImgurl(student.getPhoto());
            return ajaxResult;
        }
        if(tid!=0){
            Teacher teacher = teacherService.findById(tid);
            ajaxResult.setImgurl(teacher.getPhoto());
            return ajaxResult;
        }

        return ajaxResult;
    }


    @GetMapping("/personalView")
    public String personalView(){
        return "system/personalView";
    }


    /**
     * change password
     * @param password
     * @param newpassword
     * @param session
     * @return
     */
    @PostMapping("/editPassword")
    @ResponseBody
    public AjaxResult editPassword(String password,String newpassword,HttpSession session){
        AjaxResult ajaxResult = new AjaxResult();
        String usertype = (String) session.getAttribute(Const.USERTYPE);
        if (usertype.equals("1")){
            //管理员
            Admin admin = (Admin)session.getAttribute(Const.ADMIN);
            if(!password.equals(admin.getPassword())){
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("old password is wrong");
                return ajaxResult;
            }
            admin.setPassword(newpassword);
            try{
                int count = adminService.editPswdByAdmin(admin);
                if(count > 0){
                    ajaxResult.setSuccess(true);
                    ajaxResult.setMessage("update password successfully, please re-login");
                }else{
                    ajaxResult.setSuccess(false);
                    ajaxResult.setMessage("fail to update password");
                }
            }catch (Exception e){
                e.printStackTrace();
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to update password");
            }
        }
        if(usertype.equals("2")){
            //student
            Student student = (Student)session.getAttribute(Const.STUDENT);
            if(!password.equals(student.getPassword())){
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("old password is wrong");
                return ajaxResult;
            }
            student.setPassword(newpassword);
            try{
                int count = studentService.editPswdByStudent(student);
                if(count > 0){
                    ajaxResult.setSuccess(true);
                    ajaxResult.setMessage("update password successfully, please re-login");
                }else{
                    ajaxResult.setSuccess(false);
                    ajaxResult.setMessage("fail to update password");
                }
            }catch (Exception e){
                e.printStackTrace();
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to update password");
            }
        }
        if(usertype.equals("3")){
            //teacher
            Teacher teacher = (Teacher) session.getAttribute(Const.TEACHER);
            if(!password.equals(teacher.getPassword())){
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("old password is wrong");
                return ajaxResult;
            }
            teacher.setPassword(newpassword);
            try{
                int count = teacherService.editPswdByTeacher(teacher);
                if(count > 0){
                    ajaxResult.setSuccess(true);
                    ajaxResult.setMessage("update password successfully, please re-login");
                }else{
                    ajaxResult.setSuccess(false);
                    ajaxResult.setMessage("fail to update password");
                }
            }catch (Exception e){
                e.printStackTrace();
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to update password");
            }
        }
        return ajaxResult;
    }

}
