/**
 * 查询参数类型定义
 * 用于前端过滤查询接口
 */

/**
 * 基础查询参数
 */
export interface BaseQueryParam {
  /** 页码，默认1 */
  pageNum?: number;
  /** 每页大小，默认10 */
  pageSize?: number;
  /** 关键词搜索 */
  keyword?: string;
  /** 开始时间 */
  startTime?: string;
  /** 结束时间 */
  endTime?: string;
  /** 创建时间开始 */
  createdAtBegin?: string;
  /** 创建时间结束 */
  createdAtEnd?: string;
  /** 更新时间开始 */
  updatedAtBegin?: string;
  /** 更新时间结束 */
  updatedAtEnd?: string;
}

/**
 * 分页结果
 */
export interface PageResult<T> {
  /** 数据列表 */
  list: T[];
  /** 总记录数 */
  total: number;
  /** 当前页码 */
  pageNum: number;
  /** 每页大小 */
  pageSize: number;
  /** 总页数 */
  totalPages?: number;
}

/**
 * 社区查询参数
 */
export interface CommunityQueryParam extends BaseQueryParam {
  /** 社区编号（精确匹配） */
  communityNo?: string;
  /** 社区编号模糊搜索 */
  communityNoLike?: string;
  /** 楼栋名称（精确匹配） */
  buildingName?: string;
  /** 楼栋名称模糊搜索 */
  buildingNameLike?: string;
  /** 楼栋地址（精确匹配） */
  buildingAddress?: string;
  /** 楼栋地址模糊搜索 */
  buildingAddressLike?: string;
  /** 城市（精确匹配） */
  city?: string;
  /** 城市列表（IN查询） */
  cityList?: string[];
  /** 纬度最小值 */
  coordLatMin?: number;
  /** 纬度最大值 */
  coordLatMax?: number;
  /** 经度最小值 */
  coordLngMin?: number;
  /** 经度最大值 */
  coordLngMax?: number;
}

/**
 * 道闸查询参数
 */
export interface BarrierGateQueryParam extends BaseQueryParam {
  /** 道闸编号（精确匹配） */
  gateNo?: string;
  /** 道闸编号模糊搜索 */
  gateNoLike?: string;
  /** 社区ID（精确匹配） */
  communityId?: number;
  /** 社区ID列表（IN查询） */
  communityIdList?: number[];
  /** 设备编号（精确匹配） */
  deviceNo?: string;
  /** 设备编号模糊搜索 */
  deviceNoLike?: string;
  /** 门岗位置（精确匹配） */
  doorLocation?: string;
  /** 门岗位置模糊搜索 */
  doorLocationLike?: string;
  /** 设备位置（精确匹配） 1-入口, 2-出口 */
  devicePosition?: number;
  /** 设备位置列表（IN查询） */
  devicePositionList?: number[];
  /** 屏幕位置（精确匹配） 1-左侧, 2-右侧 */
  screenPosition?: number;
  /** 屏幕位置列表（IN查询） */
  screenPositionList?: number[];
  /** 灯箱方向（精确匹配） 1-正面, 2-反面 */
  lightboxDirection?: number;
  /** 灯箱方向列表（IN查询） */
  lightboxDirectionList?: number[];
  /** 城市（关联社区表查询） */
  city?: string;
  /** 楼栋名称模糊搜索（关联社区表） */
  buildingNameLike?: string;
}

/**
 * 框架查询参数
 */
export interface FrameQueryParam extends BaseQueryParam {
  /** 框架编号（精确匹配） */
  frameNo?: string;
  /** 框架编号模糊搜索 */
  frameNoLike?: string;
  /** 社区ID（精确匹配） */
  communityId?: number;
  /** 社区ID列表（IN查询） */
  communityIdList?: number[];
  /** 楼栋（精确匹配） */
  building?: string;
  /** 楼栋模糊搜索 */
  buildingLike?: string;
  /** 单元（精确匹配） */
  unit?: string;
  /** 单元模糊搜索 */
  unitLike?: string;
  /** 电梯（精确匹配） */
  elevator?: string;
  /** 电梯模糊搜索 */
  elevatorLike?: string;
  /** 内外位置（精确匹配） 1-内, 2-外 */
  innerPosition?: number;
  /** 内外位置列表（IN查询） */
  innerPositionList?: number[];
  /** 城市（关联社区表查询） */
  city?: string;
  /** 楼栋名称模糊搜索（关联社区表） */
  buildingNameLike?: string;
}

/**
 * 方案查询参数
 */
export interface PlanQueryParam extends BaseQueryParam {
  /** 方案编号（精确匹配） */
  planNo?: string;
  /** 方案编号模糊搜索 */
  planNoLike?: string;
  /** 方案名称（精确匹配） */
  planName?: string;
  /** 方案名称模糊搜索 */
  planNameLike?: string;
  /** 客户名称（精确匹配） */
  customer?: string;
  /** 客户名称模糊搜索 */
  customerLike?: string;
  /** 投放状态（精确匹配） */
  releaseStatus?: number;
  /** 投放状态列表（IN查询） */
  releaseStatusList?: number[];
  /** 销售类型（精确匹配） */
  salesType?: number;
  /** 销售类型列表（IN查询） */
  salesTypeList?: number[];
  /** 媒体类型（精确匹配） */
  mediaType?: number;
  /** 媒体类型列表（IN查询） */
  mediaTypeList?: number[];
  /** 投放开始日期（大于等于） */
  releaseDateBeginFrom?: string;
  /** 投放开始日期（小于等于） */
  releaseDateBeginTo?: string;
  /** 投放结束日期（大于等于） */
  releaseDateEndFrom?: string;
  /** 投放结束日期（小于等于） */
  releaseDateEndTo?: string;
  /** 销售人员（精确匹配） */
  salesPerson?: string;
  /** 销售人员模糊搜索 */
  salesPersonLike?: string;
  /** 预算最小值 */
  budgetMin?: number;
  /** 预算最大值 */
  budgetMax?: number;
  /** 预计触达人数最小值 */
  estimatedReachMin?: number;
  /** 预计触达人数最大值 */
  estimatedReachMax?: number;
}
