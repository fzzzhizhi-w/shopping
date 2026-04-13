package com.shopping.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("group_buy_member")
public class GroupBuyMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long groupBuyId;

    private Long userId;

    private LocalDateTime joinTime;
}
