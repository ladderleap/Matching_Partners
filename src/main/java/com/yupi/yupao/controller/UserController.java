package com.yupi.yupao.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupao.common.BaseResponse;
import com.yupi.yupao.common.ErrorCode;
import com.yupi.yupao.common.ResultUtils;
import com.yupi.yupao.exception.BusinessException;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.model.request.TagsUpdateRequest;
import com.yupi.yupao.model.request.UserLoginRequest;
import com.yupi.yupao.model.request.UserRegisterRequest;
import com.yupi.yupao.model.vo.RecommendUserVo;
import com.yupi.yupao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.yupi.yupao.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:3000"})
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

//        String planetCode = userRegisterRequest.getPlanetCode();

        long result = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        String sessionId = request.getSession().getId();
        log.info("该用户的sessionI为:{}",sessionId);
        System.out.println(sessionId);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

//    @PostMapping("/login")
//    public String login(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes, HttpServletRequest request) {
//        // 假设这里进行用户名和密码的验证
//        if (username.equals("111") && password.equals("111")) {
//            HttpSession session = request.getSession();
//            session.setAttribute("username", username);
//            return "redirect:/user/welcome";
//        } else {
//            return "redirect:/";
//        }
//    }
//
//    @GetMapping("/welcome")
//    @ResponseBody
//    public String welcome() {
//        return "welcome";
//    }



    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @GetMapping("/search/tags")
    public List<User> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList) {
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.searchUsersByTags(tagNameList);
    }

    // todo 推荐多个，未实现
//    @GetMapping("/recommend")
//    public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
//        User loginUser = userService.getLoginUser(request);
//        String redisKey = String.format("yupao:user:recommend:%s", loginUser.getId());
//        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
//        // 如果有缓存，直接读缓存
//        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
//        if (userPage != null) {
//            return ResultUtils.success(userPage);
//        }
//        // 无缓存，查数据库
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
//        // 写缓存
//        try {
//            valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
//        } catch (Exception e) {
//            log.error("redis set key error", e);
//        }
//        return ResultUtils.success(userPage);
//    }
    @GetMapping("/recommend")
    public BaseResponse<RecommendUserVo> recommend(long pageNum, long pageSize, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String recommendKey = String.format("recommend:user:%s", loginUser.getId());
        long count = userService.count();
        Page<User> page = (Page<User>) redisTemplate.opsForValue().get(recommendKey);
        RecommendUserVo recommendUserVo = new RecommendUserVo();
        if(page != null){
            recommendUserVo.setPageResult(page);
            recommendUserVo.setTotal(count);
            return ResultUtils.success(recommendUserVo);
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        page = userService.page(new Page<>(pageNum, pageSize), userQueryWrapper);
        try {
            redisTemplate.opsForValue().set(recommendKey,page,10000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key",e);
        }
        recommendUserVo.setPageResult(page);
        recommendUserVo.setTotal(count);
        return ResultUtils.success(recommendUserVo);
    }
    
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        int result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);
    }
    
    
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 获取最匹配的用户
     *
     * @param num
     * @param request
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(int num, HttpServletRequest request) {

         User loginUser = userService.getLoginUser(request);
        List<User> users = userService.matchUsers(num, loginUser);
        return ResultUtils.success(users);
    }

    @GetMapping("/tags")
    public BaseResponse<String> userTags(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String userTags = userService.getUserTags(loginUser);
        return ResultUtils.success(userTags);
    }
    @PostMapping("/update/tags")
    public BaseResponse<Boolean> updateTags(@RequestBody TagsUpdateRequest tagsUpdateRequest, HttpServletRequest request) {
        String updateTags = tagsUpdateRequest.getTags();
        if(StringUtils.isBlank(updateTags)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求数据为空");
        }
        boolean result = userService.updateUserTags(updateTags, request);
        return ResultUtils.success(result);
    }

}
