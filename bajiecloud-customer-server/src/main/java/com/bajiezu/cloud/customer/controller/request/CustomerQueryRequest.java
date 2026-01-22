package com.bajiezu.cloud.customer.controller.request;

import com.bajiezu.cloud.common.web.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CustomerQueryRequest extends PageParam {

  @Schema(description = "查询关键字")
  @NotNull(message = "查询关键字不能为空")
  private String key;

  @Schema(description = "查询类型, MOBILE: 手机号查询, NAME: 姓名查询, ALL: 全部查询", allowableValues = {"MOBILE",
      "NAME", "ALL"})
  private QueryType queryType = QueryType.ALL;



  @Schema(description = "查询类型, MOBILE: 手机号查询, NAME: 姓名查询, ALL: 全部查询", allowableValues = {"MOBILE",
      "NAME", "ALL"})
  public enum QueryType {
    @Schema(description = "手机号查询")
    MOBILE,
    @Schema(description = "姓名查询")
    NAME,
    @Schema(description = "全部查询")
    ALL
  }

}
