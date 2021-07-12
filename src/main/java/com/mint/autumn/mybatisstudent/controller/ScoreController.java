package com.mint.autumn.mybatisstudent.controller;


import com.mint.autumn.mybatisstudent.domain.Score;
import com.mint.autumn.mybatisstudent.domain.ScoreStats;
import com.mint.autumn.mybatisstudent.domain.Student;
import com.mint.autumn.mybatisstudent.service.CourseService;
import com.mint.autumn.mybatisstudent.service.ScoreService;
import com.mint.autumn.mybatisstudent.service.SelectedCourseService;
import com.mint.autumn.mybatisstudent.service.StudentService;
import com.mint.autumn.mybatisstudent.util.AjaxResult;
import com.mint.autumn.mybatisstudent.util.Const;
import com.mint.autumn.mybatisstudent.util.PageBean;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Classname ScoreController
 * @Description score controller
 * @Date 2019/7/3 11:38
 * @Created by Jay
 */
@Controller
@RequestMapping("/score")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private SelectedCourseService selectedCourseService;


    @GetMapping("/score_list")
    public String scoreList(){
        return "score/scoreList";
    }


    /**
     * get score list
     * @param page
     * @param rows
     * @param studentid
     * @param courseid
     * @param from
     * @param session
     * @return
     */
    @RequestMapping("/getScoreList")
    @ResponseBody
    public Object getScoreList(@RequestParam(value = "page", defaultValue = "1")Integer page,
                                    @RequestParam(value = "rows", defaultValue = "100")Integer rows,
                                    @RequestParam(value = "studentid", defaultValue = "0")String studentid,
                                    @RequestParam(value = "courseid", defaultValue = "0")String courseid,
                                    String from, HttpSession session){
        Map<String,Object> paramMap = new HashMap();
        paramMap.put("pageno",page);
        paramMap.put("pagesize",rows);
        if(!studentid.equals("0"))  paramMap.put("studentid",studentid);
        if(!courseid.equals("0"))  paramMap.put("courseid",courseid);

        //check if student or teacher.
        Student student = (Student) session.getAttribute(Const.STUDENT);
        if(!StringUtils.isEmpty(student)){
            //if student, only inquire himself.
            paramMap.put("studentid",student.getId());
        }
        PageBean<Score> pageBean = scoreService.queryPage(paramMap);
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
     * add score
     * @param score
     * @return
     */
    @PostMapping("/addScore")
    @ResponseBody
    public AjaxResult addScore(Score score){
        AjaxResult ajaxResult = new AjaxResult();
        //check if has score.
        if(scoreService.isScore(score)){
            //true为已签到
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("please do not double input score ");
        }else{
            int count = scoreService.addScore(score);
            if(count > 0){
                //签到成功
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("input score successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("system error, please try again");
            }
        }
        return ajaxResult;
    }


    /**
     * update student score
     * @param score
     * @return
     */
    @PostMapping("/editScore")
    @ResponseBody
    public AjaxResult editScore(Score score){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            int count = scoreService.editScore(score);
            if(count > 0){
                //签到成功
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("update score successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("system error, please try again");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("system error, please try again");
        }
        return ajaxResult;
    }

    /**
     * delete score
     * @param id
     * @return
     */
    @PostMapping("/deleteScore")
    @ResponseBody
    public AjaxResult deleteScore(Integer id){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            int count = scoreService.deleteScore(id);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("delete score successfully");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("system error, please try again");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("system error, please try again");
        }
        return ajaxResult;
    }

    /**
     * export to xlsx file and import into database.
     * @param importScore
     * @param response
     */
    @PostMapping("/importScore")
    @ResponseBody
    public void importScore(@RequestParam("importScore") MultipartFile importScore, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        try {
            InputStream inputStream = importScore.getInputStream();
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheetAt = xssfWorkbook.getSheetAt(0);
            int count = 0;
            String errorMsg = "";
            for(int rowNum = 1; rowNum <= sheetAt.getLastRowNum(); rowNum++){
                XSSFRow row = sheetAt.getRow(rowNum); // get the rowNum row
                //第0列
                XSSFCell cell = row.getCell(0); // get the 0 column of the rowNum row(rowNum,0)
                if(cell == null){
                    errorMsg += "the " + rowNum + " row student missing!\n";
                    continue;
                }
                //the first column
                cell = row.getCell(1);
                if(cell == null){
                    errorMsg += "the " + rowNum + " row course missing!\n";
                    continue;
                }
                //the second column
                cell = row.getCell(2);
                if(cell == null){
                    errorMsg += "the " + rowNum + " row score missing!\n";
                    continue;
                }
                double scoreValue = cell.getNumericCellValue();
                //the third column
                cell = row.getCell(3);
                String remark = null;
                if(cell != null){
                    remark = cell.getStringCellValue();
                }

                //save student id and course id into database.
                // first, get course id and student id.
                int studentId = studentService.findByName(row.getCell(0).getStringCellValue());
                int courseId = courseService.findByName(row.getCell(1).getStringCellValue());
                // check whether it is in database.
                Score score = new Score();
                score.setStudentId(studentId);
                score.setCourseId(courseId);
                score.setScore(scoreValue);
                score.setRemark(remark);
                if(!scoreService.isScore(score)){
                    // save record into database.
                    int i = scoreService.addScore(score);
                    if(i > 0){
                        count ++ ;
                    }
                }else{
                    errorMsg += "the " + rowNum + " row is exist!\n";
                }
            }
            errorMsg += "input " + count + " rows score record successfully!";
            response.getWriter().write("<div id='message'>"+errorMsg+"</div>");

        } catch (IOException e) {
            e.printStackTrace();
            try {
                response.getWriter().write("<div id='message'>upload error</div>");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }


    /**
     * 导出xlsx表
     * @param response
     * @param score
     * @param session
     */
    @RequestMapping("/exportScore")
    @ResponseBody
    private void exportScore(HttpServletResponse response,Score score,HttpSession session) {
        //get current user type
        Student student = (Student) session.getAttribute(Const.STUDENT);
        if(!StringUtils.isEmpty(student)){
            //if student, only inquire himself
            score.setStudentId(student.getId());
        }
        try {
            response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode("score_list_sid_"+score.getStudentId()+"_cid_"+score.getStudentId()+".xls", "UTF-8"));
            response.setHeader("Connection", "close");
            response.setHeader("Content-Type", "application/octet-stream");
            ServletOutputStream outputStream = response.getOutputStream();
            List<Score> scoreList = scoreService.getAll(score);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
            XSSFSheet createSheet = xssfWorkbook.createSheet("score list");
            XSSFRow createRow = createSheet.createRow(0);
            createRow.createCell(0).setCellValue("student");
            createRow.createCell(1).setCellValue("course");
            createRow.createCell(2).setCellValue("score");
            createRow.createCell(3).setCellValue("comment");
            //load data into excel file
            int row = 1;
            for( Score s:scoreList){
                createRow = createSheet.createRow(row++);
                createRow.createCell(0).setCellValue(s.getStudentName());
                createRow.createCell(1).setCellValue(s.getCourseName());
                createRow.createCell(2).setCellValue(s.getScore());
                createRow.createCell(3).setCellValue(s.getRemark());
            }
            xssfWorkbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * redirect to status page.
     * @return
     */
    @RequestMapping("/scoreStats")
    public String scoreStats(){
        return "/score/scoreStats";
    }


    /**
     * score status
     * @param courseid
     * @param searchType
     * @return
     */
    @RequestMapping("/getScoreStatsList")
    @ResponseBody
    public Object getScoreStatsList(@RequestParam(value = "courseid", defaultValue = "0")Integer courseid,
                                        String searchType){
        AjaxResult ajaxResult = new AjaxResult();
        if(searchType.equals("avg")){
            ScoreStats scoreStats = scoreService.getAvgStats(courseid);

            List<Double> scoreList = new ArrayList<Double>();
            scoreList.add(scoreStats.getMax_score());
            scoreList.add(scoreStats.getMin_score());
            scoreList.add(scoreStats.getAvg_score());

            List<String> avgStringList = new ArrayList<String>();
            avgStringList.add("the highest score");
            avgStringList.add("the lowest score");
            avgStringList.add("average score");

            Map<String, Object> retMap = new HashMap<String, Object>();
            retMap.put("courseName", scoreStats.getCourseName());
            retMap.put("scoreList", scoreList);
            retMap.put("avgList", avgStringList);
            retMap.put("type", "success");

            return retMap;
        }

        Score score = new Score();
        score.setCourseId(courseid);
        List<Score> scoreList = scoreService.getAll(score);


        List<Integer> numberList = new ArrayList<Integer>();
        numberList.add(0);
        numberList.add(0);
        numberList.add(0);
        numberList.add(0);
        numberList.add(0);

        List<String> rangeStringList = new ArrayList<String>();
        rangeStringList.add("<60");
        rangeStringList.add("60~70");
        rangeStringList.add("70~80");
        rangeStringList.add("80~90");
        rangeStringList.add("90~100");

        String courseName = "";

        for(Score sc : scoreList){
            courseName = sc.getCourseName();  //get course name
            double scoreValue = sc.getScore();//get course score
            if(scoreValue < 60){
                numberList.set(0, numberList.get(0)+1);
                continue;
            }
            if(scoreValue <= 70 && scoreValue >= 60){
                numberList.set(1, numberList.get(1)+1);
                continue;
            }
            if(scoreValue <= 80 && scoreValue > 70){
                numberList.set(2, numberList.get(2)+1);
                continue;
            }
            if(scoreValue <= 90 && scoreValue > 80){
                numberList.set(3, numberList.get(3)+1);
                continue;
            }
            if(scoreValue <= 100 && scoreValue > 90){
                numberList.set(4, numberList.get(4)+1);
                continue;
            }
        }
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("courseName", courseName);
        retMap.put("numberList", numberList);
        retMap.put("rangeList", rangeStringList);
        retMap.put("type", "success");
        return retMap;
    }

}
