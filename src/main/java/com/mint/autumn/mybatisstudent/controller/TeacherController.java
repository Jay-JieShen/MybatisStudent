package com.mint.autumn.mybatisstudent.controller;


import com.mint.autumn.mybatisstudent.domain.Teacher;
import com.mint.autumn.mybatisstudent.service.TeacherService;
import com.mint.autumn.mybatisstudent.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Classname TeacherController
 * @Description None
 * @Date 2019/6/28 18:49
 * @Created by Jay
 */
@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;


    @RequestMapping("/teacher_list")
    public String teacherList(){
        return "teacher/teacherList";
    }

    /**
     * get teacher list
     * @param page
     * @param rows
     * @param teacherName
     * @param clazzid
     * @param from
     * @param session
     * @return
     */
    @PostMapping("/getTeacherList")
    @ResponseBody
    public Object getTeacherList(@RequestParam(value = "page", defaultValue = "1")Integer page,
                                 @RequestParam(value = "rows", defaultValue = "100")Integer rows,
                                 String teacherName,
                                 @RequestParam(value = "clazzid", defaultValue = "0")String clazzid, String from, HttpSession session){
        Map<String,Object> paramMap = new HashMap();
        paramMap.put("pageno",page);
        paramMap.put("pagesize",rows);
        if(!StringUtils.isEmpty(teacherName))  paramMap.put("username",teacherName);
        if(!clazzid.equals("0"))  paramMap.put("clazzid",clazzid);

        //check if teacher or student
        Teacher teacher = (Teacher) session.getAttribute(Const.TEACHER);
        if(!StringUtils.isEmpty(teacher)){
            //if teacher, only inquire himself.
            paramMap.put("teacherid",teacher.getId());
        }

        PageBean<Teacher> pageBean = teacherService.queryPage(paramMap);
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
     * delete teacher
     * @param data
     * @return
     */
    @PostMapping("/deleteTeacher")
    @ResponseBody
    public AjaxResult deleteTeacher(Data data){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            File fileDir = UploadUtil.getImgDirFile();
            for(Integer id : data.getIds()){
                Teacher byId = teacherService.findById(id);
                if(!byId.getPhoto().isEmpty()){
                    File file = new File(fileDir.getAbsolutePath() + File.separator + byId.getPhoto());
                    if(file != null){
                        file.delete();
                    }
                }
            }
            int count = teacherService.deleteTeacher(data.getIds());
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("delete teacher successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to delete teacher");
            }
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("fail to delete teacher");
        }
        return ajaxResult;
    }

    /**
     * add teacher
     * @param files
     * @param teacher
     * @return
     * @throws IOException
     */
    @RequestMapping("/addTeacher")
    @ResponseBody
    public AjaxResult addTeacher(@RequestParam("file") MultipartFile[] files, Teacher teacher) throws IOException {

        AjaxResult ajaxResult = new AjaxResult();
        teacher.setSn(SnGenerateUtil.generateTeacherSn(teacher.getClazzId()));

        // image directory.
        File fileDir = UploadUtil.getImgDirFile();
        for(MultipartFile fileImg : files){

            // get image file name
            String extName = fileImg.getOriginalFilename().substring(fileImg.getOriginalFilename().lastIndexOf("."));
            String uuidName = UUID.randomUUID().toString();

            try {
                // get full image file path
                File newFile = new File(fileDir.getAbsolutePath() + File.separator +uuidName+ extName);

                // upload image to file path
                fileImg.transferTo(newFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
            teacher.setPhoto(uuidName+extName);
        }
        //save student information to database.
        try{
            int count = teacherService.addTeacher(teacher);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("save teacher successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to save teacher");
            }
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("fail to save teacher");
        }

        ajaxResult.setSuccess(true);
        return ajaxResult;
    }

    @PostMapping("/editTeacher")
    @ResponseBody
    public AjaxResult editTeacher(@RequestParam("file") MultipartFile[] files,Teacher teacher){
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
                // image file path
                File newFile = new File(fileDir.getAbsolutePath() + File.separator +uuidName+ extName);
                // upload image file to file path
                fileImg.transferTo(newFile);

                Teacher byId = teacherService.findById(teacher.getId());
                File file = new File(fileDir.getAbsolutePath() + File.separator + byId.getPhoto());
                if(file != null){
                    file.delete();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            teacher.setPhoto(uuidName+extName);
        }

        try{
            int count = teacherService.editTeacher(teacher);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("update teacher successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to update teacher");
            }
        }catch(Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("fail to update teacher");
        }
        return ajaxResult;
    }
}
