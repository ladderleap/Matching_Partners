package com.yupi.yupao.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupao.model.domain.User;
import lombok.Data;

/**
 * @author: YL
 * @Desc:
 * @create: 2024-05-24 22:55
 **/
@Data
public class RecommendUserVo {
    private Page<User> pageResult;
    private long  total;
}
