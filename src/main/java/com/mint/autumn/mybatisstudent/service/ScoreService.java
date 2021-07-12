package com.mint.autumn.mybatisstudent.service;


import com.mint.autumn.mybatisstudent.domain.Score;
import com.mint.autumn.mybatisstudent.domain.ScoreStats;
import com.mint.autumn.mybatisstudent.util.PageBean;

import java.util.List;
import java.util.Map;

/**
 * @Classname ScoreService
 * @Description None
 * @Date 2019/7/3 11:45
 * @Created by Jay
 */
public interface ScoreService {
    PageBean<Score> queryPage(Map<String, Object> paramMap);

    boolean isScore(Score score);

    int addScore(Score score);

    int editScore(Score score);

    int deleteScore(Integer id);

    List<Score> getAll(Score score);

    ScoreStats getAvgStats(Integer courseid);
}
