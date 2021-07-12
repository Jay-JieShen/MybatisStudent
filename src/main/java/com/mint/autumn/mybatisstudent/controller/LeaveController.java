package com.mint.autumn.mybatisstudent.controller;


import com.mint.autumn.mybatisstudent.domain.Leave;
import com.mint.autumn.mybatisstudent.service.LeaveService;
import com.mint.autumn.mybatisstudent.util.AjaxResult;
import com.mint.autumn.mybatisstudent.util.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @Classname LeaveController
 * @Description leave controller
 * @Date 2019/7/2 15:43
 * @Created by Jay
 */
@Controller
@RequestMapping("/leave")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @RequestMapping("leave_list")
    public String leaveList(){
        return "leave/leaveList";
    }

    /**
     * get leave list
     * @param page
     * @param rows
     * @param studentid
     * @param from
     * @return
     */
    @PostMapping("/getLeaveList")
    @ResponseBody
    public Object getClazzList(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "100")Integer rows,
                               @RequestParam(value = "studentid", defaultValue = "0")String studentid,
                               String from){
        Map<String,Object> paramMap = new HashMap();
        paramMap.put("pageno",page);
        paramMap.put("pagesize",rows);
        if(!studentid.equals("0"))  paramMap.put("studentId",studentid);
        PageBean<Leave> pageBean = leaveService.queryPage(paramMap);
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
     * add student leave
     * @param leave
     * @return
     */
    @PostMapping("/addLeave")
    @ResponseBody
    public AjaxResult addLeave(Leave leave){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            int count = leaveService.addLeave(leave);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("add leave successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to add leave");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("system error, please try again");
        }
        return ajaxResult;
    }

    /**
     * update leave information
     * @param leave
     * @return
     */
    @PostMapping("/editLeave")
    @ResponseBody
    public AjaxResult editLeave(Leave leave){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            int count = leaveService.editLeave(leave);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("update leave successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to update leave information");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("system error, please try again");
        }
        return ajaxResult;
    }

    /**
     * determine whether approve leave.
     * @param leave
     * @return
     */
    @PostMapping("/checkLeave")
    @ResponseBody
    public AjaxResult checkLeave(Leave leave){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            int count = leaveService.checkLeave(leave);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("grant leave");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("deny leave");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("system error, please try again");
        }
        return ajaxResult;
    }


    /**
     * delete leave
     * @param id
     * @return
     */
    @PostMapping("/deleteLeave")
    @ResponseBody
    public AjaxResult deleteLeave(Integer id){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            int count = leaveService.deleteLeave(id);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("delete leave successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("fail to delete leave");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("system error, please try again");
        }
        return ajaxResult;
    }


}
