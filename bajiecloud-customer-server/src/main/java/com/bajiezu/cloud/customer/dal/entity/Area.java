package com.bajiezu.cloud.customer.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Area {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 地区编号
     */
    private String code;

    /**
     * 地区名称
     */
    private String name;

    /**
     * 上级地区编号
     */
    private String pcode;

    /**
     * 行政级别
     */
    private Integer level;
}
