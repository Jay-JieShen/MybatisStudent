package com.mint.autumn.mybatisstudent.controller;


import com.mint.autumn.mybatisstudent.domain.Student;
import com.mint.autumn.mybatisstudent.service.ClazzService;
import com.mint.autumn.mybatisstudent.service.SelectedCourseService;
import com.mint.autumn.mybatisstudent.service.StudentService;
import com.mint.autumn.mybatisstudent.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @Classname StudentController
 * @Description None
 * @Date 2019/6/25 20:00
 * @Created by Jay
 */
@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private ClazzService clazzService;
    @Autowired
    private SelectedCourseService selectedCourseService;

    /**
     * redirect student list
     * @return
     */
    @GetMapping("/student_list")
    public String studentList(){
        return "student/studentList";
    }

    /**
     * get student list
     * @param page
     * @param rows
     * @param studentName
     * @param clazzid
     * @param from
     * @param session
     * @return
     */
    @RequestMapping("/getStudentList")
    @ResponseBody
    public Object getStudentList(@RequestParam(value = "page", defaultValue = "1")Integer page,
                                 @RequestParam(value = "rows", defaultValue = "100")Integer rows,
                                 String studentName,
                                 @RequestParam(value = "clazzid", defaultValue = "0")String clazzid, String from, HttpSession session){
        Map<String,Object> paramMap = new HashMap();
        paramMap.put("pageno",page);
        paramMap.put("pagesize",rows);
        if(!StringUtils.isEmpty(studentName))  paramMap.put("username",studentName);
        if(!clazzid.equals("0"))  paramMap.put("clazzid",clazzid);

        //check if teacher or student.
        Student student = (Student) session.getAttribute(Const.STUDENT);
        if(!StringUtils.isEmpty(student)){
            //if student, only inquire himself.
            paramMap.put("studentid",student.getId());
        }

        PageBean<Student> pageBean = studentService.queryPage(paramMap);
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
     * delete student.
     * @param data
     * @return
     */
    @PostMapping("/deleteStudent")
    @ResponseBody
    public AjaxResult deleteStudent(Data data){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            List<Integer> ids = data.getIds();
            Iterator<Integer> iterator = ids.iterator();
            while (iterator.hasNext()){  //check if this course has any students.
                if(!selectedCourseService.isStudentId(iterator.next())){
                    ajaxResult.setSuccess(false);
                    ajaxResult.setMessage("could not delete the course that has students");
                    return ajaxResult;
                }
            }
            File fileDir = UploadUtil.getImgDirFile();
            for(Integer id : ids){
                Student byId = studentService.findById(id);
                if(!byId.getPhoto().isEmpty()){
                    File file = new File(fileDir.getAbsolutePath() + File.separator + byId.getPhoto());
                    if(file != null){
                        file.delete();
                    }
                }
            }
            int count = studentService.deleteStudent(ids);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("delete student successfully");

            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to delete student");
            }

        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("fail to delete");
        }
        return ajaxResult;
    }


    /**
     * add student
     * @param files
     * @param student
     * @return
     * @throws IOException
     */
    @RequestMapping("/addStudent")
    @ResponseBody
    public AjaxResult addStudent(@RequestParam("file") MultipartFile[] files,Student student) throws IOException {

        AjaxResult ajaxResult = new AjaxResult();
        student.setSn(SnGenerateUtil.generateSn(student.getClazzId()));

        // get image directory.
        File fileDir = UploadUtil.getImgDirFile();
        for(MultipartFile fileImg : files){

            // get image file name
            String extName = fileImg.getOriginalFilename().substring(fileImg.getOriginalFilename().lastIndexOf("."));
            String uuidName = UUID.randomUUID().toString();

            try {
                // get full file path
                File newFile = new File(fileDir.getAbsolutePath() + File.separator +uuidName+ extName);

                // upload image file
                fileImg.transferTo(newFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
            student.setPhoto(uuidName+extName);
        }
        //save student information into database.
        try{
            int count = studentService.addStudent(student);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("save student successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to save student");
            }
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("fail to save student");
        }

        ajaxResult.setSuccess(true);
        return ajaxResult;
    }

    /**
     * update student information
     * @param files
     * @param student
     * @return
     */
    @PostMapping("/editStudent")
    @ResponseBody
    public AjaxResult editStudent(@RequestParam("file") MultipartFile[] files,Student student){
        AjaxResult ajaxResult = new AjaxResult();

        // image directory
        File fileDir = UploadUtil.getImgDirFile();
        for(MultipartFile fileImg : files){

            String name = fileImg.getOriginalFilename();
            if(name.equals("")){
                break;
            }

            // get image file name
            String extName = fileImg.getOriginalFilename().substring(fileImg.getOriginalFilename().lastIndexOf("."));
            String uuidName = UUID.randomUUID().toString();

            try {
                // full file path
                File newFile = new File(fileDir.getAbsolutePath() + File.separator +uuidName+ extName);
                // upload image
                fileImg.transferTo(newFile);

                Student byId = studentService.findById(student.getId());
                File file = new File(fileDir.getAbsolutePath() + File.separator + byId.getPhoto());
                if(file != null){
                    file.delete();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            student.setPhoto(uuidName+extName);
        }

        try{
            int count = studentService.editStudent(student);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("update student successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to update student");
            }
        }catch(Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("fail to update student");
        }
        return ajaxResult;
    }
}
