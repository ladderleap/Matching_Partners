package com.yupi.yupao.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.yupao.common.BaseResponse;
import com.yupi.yupao.common.DeleteRequest;
import com.yupi.yupao.common.ErrorCode;
import com.yupi.yupao.common.ResultUtils;
import com.yupi.yupao.exception.BusinessException;
import com.yupi.yupao.model.domain.Team;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.model.domain.UserTeam;
import com.yupi.yupao.model.dto.TeamQuery;
import com.yupi.yupao.model.request.TeamAddRequest;
import com.yupi.yupao.model.request.TeamJoinRequest;
import com.yupi.yupao.model.request.TeamQuitRequest;
import com.yupi.yupao.model.request.TeamUpdateRequest;
import com.yupi.yupao.model.vo.TeamUserVO;
import com.yupi.yupao.service.TeamService;
import com.yupi.yupao.service.UserService;
import com.yupi.yupao.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: YL
 * @Desc:
 * @create: 2024-05-04 19:30
 **/
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:3000"})
@Slf4j
public class TeamController {
    @Autowired
    TeamService teamService;
    @Autowired
    UserService userService;
    @Autowired
    UserTeamService userTeamService;

    @PostMapping("/add")
    public BaseResponse<Team> add(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        User loginUser = userService.getLoginUser(request);
        teamService.addTeam(team,loginUser);
        return ResultUtils.success(team);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> delete(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NO_AUTH,"login user is null");
        }

        boolean result = teamService.deleteTeam(id,loginUser);
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
    public BaseResponse<Boolean> updateTeamById(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        boolean updateResult = teamService.updateTeam(teamUpdateRequest,loginUser);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "update team fail");
        }
        return ResultUtils.success(updateResult);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeam(TeamQuery teamQuery,HttpServletRequest servletRequest) {
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(servletRequest);
//        获取所有的队伍列表
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin);
//        获取所有的队伍列表id 并将id 转换为list
        List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());

        User loginUser = userService.getLoginUser(servletRequest);
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();

        userTeamQueryWrapper.eq("userId",loginUser.getId());
        if(CollectionUtils.isNotEmpty(teamIdList)){
            userTeamQueryWrapper.in("teamId", teamIdList);
        }

        List<UserTeam> userTeams = userTeamService.list(userTeamQueryWrapper);
        Set<Long> teamIdSet = userTeams.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
        teamList.forEach(team -> {
            boolean hashJoin = teamIdSet.contains(team.getId());
            team.setHasJoin(hashJoin);
        });

        QueryWrapper<UserTeam> queryTeamHasJoinNum = new QueryWrapper<>();
        queryTeamHasJoinNum.in("teamId",teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(queryTeamHasJoinNum);
//        进行id去重 去重之后获取到一个key 为id value 为对应数量的 teamuser 集合 相当于是 以teamid 为key  对应的数据为value
//        我们只需要获取该key 为 teamid 的 value 值也就是 一个集合的大小 即可得到数量
        Map<Long, List<UserTeam>> listMap = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
//        然后我们对 team列表进行遍历 对每个team 的已加入人数进行赋值  方式为 上面以 teamid 分组的map集合 获取对应id 的key 的value 的大小
        teamList.forEach(team -> team.setHasJoinNum(listMap.getOrDefault(team.getId(),new ArrayList<>()).size()));
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/my/create")
    public BaseResponse<List<Team>> listMyCreateTeam(TeamQuery teamQuery, HttpServletRequest servletRequest) {
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(servletRequest);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",loginUser.getId());
        List<Team> teamList = teamService.list(queryWrapper);
        return ResultUtils.success(teamList);

    }

    /**
     * @param teamQuery
     * @param servletRequest
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyTeam(TeamQuery teamQuery,HttpServletRequest servletRequest) {
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(servletRequest);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",loginUser.getId());
        List<UserTeam> userTeams = userTeamService.list(queryWrapper);
        //        去重 取出不重复的队伍id
//        1,2
//        1,4
//        2,3
//        会分组为
//          1=> 2,4
//          2=> 3  这么做的意义是如果出现了重复可以去重避免重复查询同一个teamId
        Map<Long, List<UserTeam>> listMap = userTeams.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        ArrayList<Long> idList = new ArrayList<>(listMap.keySet());


        QueryWrapper<UserTeam> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.in("teamId",idList);
        List<UserTeam> teamMemberCount = userTeamService.list(teamQueryWrapper);
        Map<Long, List<UserTeam>> teamMemberListMap = teamMemberCount.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));


        teamQuery.setIdList(idList);
        List<TeamUserVO> teamUserVOS = teamService.listTeams(teamQuery, true);
        teamUserVOS.forEach(teamUserVO -> teamUserVO.setHasJoinNum(teamMemberListMap.getOrDefault(teamUserVO.getId(),new ArrayList<>()).size())
                .setHasJoin(true));
        return ResultUtils.success(teamUserVOS);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {{
        if(teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(teamService.joinTeam(teamJoinRequest,loginUser));
    }}


    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {{
        if(teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(teamService.quitTeam(teamQuitRequest,loginUser));
    }}


}
