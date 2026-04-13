package com.shopping.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("group_buy")
public class GroupBuy {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    private BigDecimal groupPrice;

    private Integer minMembers;

    private Integer maxMembers;

    private Integer currentMembers;

    /** 0=open, 1=success, 2=failed, 3=expired */
    private Integer status;

    private String shareCode;

    private Long creatorId;

    private LocalDateTime expireTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
