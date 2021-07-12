package com.mint.autumn.mybatisstudent.controller;


import com.mint.autumn.mybatisstudent.domain.Clazz;
import com.mint.autumn.mybatisstudent.service.ClazzService;
import com.mint.autumn.mybatisstudent.service.StudentService;
import com.mint.autumn.mybatisstudent.util.AjaxResult;
import com.mint.autumn.mybatisstudent.util.Data;
import com.mint.autumn.mybatisstudent.util.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Classname ClazzController
 * @Description class management.
 * @Date 2019/6/26 9:08
 * @Created by Jay
 */
@Controller
@RequestMapping("/clazz")
public class ClazzController {

    @Autowired
    private ClazzService clazzService;
    @Autowired
    private StudentService studentService;

    /**
     * redirect class list.
     * @return
     */
    @GetMapping("/clazz_list")
    public String clazzList(){
        return "clazz/clazzList";
    }

    /**
     * get class list
     * @param page
     * @param rows
     * @param clazzName
     * @return
     */
    @PostMapping("/getClazzList")
    @ResponseBody
    public Object getClazzList(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "100")Integer rows, String clazzName, String from){
        Map<String,Object> paramMap = new HashMap();
        paramMap.put("pageno",page);
        paramMap.put("pagesize",rows);
        if(!StringUtils.isEmpty(clazzName))  paramMap.put("name",clazzName);
        PageBean<Clazz> pageBean = clazzService.queryPage(paramMap);
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
     * add class
     * @param clazz
     * @return
     */
    @PostMapping("/addClazz")
    @ResponseBody
    public AjaxResult addClazz(Clazz clazz){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            int count = clazzService.addClazz(clazz);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("add student successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to add student");
            }
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("fail to add student");
        }
        return ajaxResult;
    }

    /**
     * delete class
     * @param data
     * @return
     */
    @PostMapping("/deleteClazz")
    @ResponseBody
    public AjaxResult deleteClazz(Data data){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            List<Integer> ids = data.getIds();
            Iterator<Integer> iterator = ids.iterator();
            while (iterator.hasNext()){  //check if any students in this class.
                if(!studentService.isStudentByClazzId(iterator.next())){
                    ajaxResult.setSuccess(false);
                    ajaxResult.setMessage("could not delete the class that has students");
                    return ajaxResult;
                }
            }
            int count = clazzService.deleteClazz(data.getIds());
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("delete class successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to delete class");
            }
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("could not delete the class that has any students or teachers");
        }
        return ajaxResult;
    }

    /**
     * update class
     * @param clazz
     * @return
     */
    @PostMapping("/editClazz")
    @ResponseBody
    public AjaxResult editClazz(Clazz clazz){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            int count = clazzService.editClazz(clazz);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("update class successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to update class");
            }
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("fail to update class");
        }
        return ajaxResult;
    }
}
