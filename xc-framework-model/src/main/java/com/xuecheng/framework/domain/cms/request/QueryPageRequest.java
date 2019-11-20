package com.xuecheng.framework.domain.cms.request;

import com.xuecheng.framework.model.request.RequestData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName QueryPageRequest
 * @date 2019年06月09日 下午 8:34
 */
@Data
public class QueryPageRequest extends RequestData {
	//站点id
	@ApiModelProperty("站点id")
	private String siteId;
	//页面ID
	@ApiModelProperty("页面ID")
	private String pageId;
	//页面名称
	@ApiModelProperty("页面名称")
	private String pageName;
	//别名
	@ApiModelProperty("页面别名")
	private String pageAliase;
	//模版id
	@ApiModelProperty("模板id")
	private String templateId;
	//页面类型
	@ApiModelProperty("页面类型")
	private String pageType;

}
