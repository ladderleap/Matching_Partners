package generator.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 标签
 * @TableName tag
 */
@TableName(value ="tag")
@Data
public class Tag implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    private String tagname;

    /**
     * 用户 id
     */
    private Long userid;

    /**
     * 父标签 id
     */
    private Long parentid;

    /**
     * 0 - 不是, 1 - 父标签
     */
    private Integer isparent;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 
     */
    private Date updatetime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isdelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}