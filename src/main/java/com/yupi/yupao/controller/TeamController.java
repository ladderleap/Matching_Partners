package com.yupi.yupao.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.yupao.common.BaseResponse;
import com.yupi.yupao.common.ErrorCode;
import com.yupi.yupao.common.ResultUtils;
import com.yupi.yupao.exception.BusinessException;
import com.yupi.yupao.model.domain.Team;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.model.dto.TeamQuery;
import com.yupi.yupao.service.UserService;
import com.yupi.yupao.service.impl.TeamServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author: YL
 * @Desc:
 * @create: 2024-05-04 19:30
 **/
@RestController
public class TeamController {
    @Autowired
    TeamServiceImpl teamService;
    @Autowired
    UserService userService;

    @PostMapping("/add")
    public BaseResponse<Team> add(@RequestBody Team team, HttpServletRequest request) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamService.addTeam(team,loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "add team fail");
        }
        return ResultUtils.success(team);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> delete(@RequestBody Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.removeById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "delete team fail");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team teamServiceById = teamService.getById(id);
        if (teamServiceById == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "get team fail");
        }
        return ResultUtils.success(teamServiceById);
    }

    @PostMapping("/update")
    public BaseResponse<Team> updateTeamById(@RequestBody Team team) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean updateResult = teamService.updateById(team);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "update team fail");
        }
        return ResultUtils.success(team);
    }

    @PostMapping("/list")
    public BaseResponse<List<Team>> listTeam(@RequestBody TeamQuery teamQuery) {
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        List<Team> teamList = teamService.list(teamQueryWrapper);
        return ResultUtils.success(teamList);
    }

}
