package com.yupi.yupao.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author: YL
 * @Desc:
 * @create: 2024-05-24 21:56
 **/
@Data
public class TagsUpdateRequest implements Serializable {
    private String tags;
}
