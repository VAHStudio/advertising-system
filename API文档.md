# 广告投放管理系统 API 文档

## 项目概述

- **项目名称**: 广告投放管理系统
- **技术栈**: Spring Boot 3.5+, Java 21, MyBatis, Alibaba Druid, MySQL
- **API文档**: Knife4j (Swagger UI)
- **基础路径**: `/api`

## 访问API文档

启动项目后，可以通过以下地址访问API文档：

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **Knife4j**: http://localhost:8080/api/doc.html

## 通用响应格式

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {},
    "timestamp": 1704067200000
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 接口列表

### 一、社区管理 (Community)

**基础路径**: `/api/community`

#### 1. 根据ID查询社区
- **请求方式**: GET
- **请求路径**: `/api/community/{id}`
- **参数说明**:
  - `id` (路径参数): 社区ID
- **响应示例**:
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "id": 1,
        "communityNo": "COMM001",
        "buildingName": "阳光花园",
        "buildingAddress": "北京市朝阳区xxx街道",
        "coordLat": 39.9042,
        "coordLng": 116.4074,
        "city": "北京"
    }
}
```

#### 2. 根据编号查询社区
- **请求方式**: GET
- **请求路径**: `/api/community/no/{communityNo}`
- **参数说明**:
  - `communityNo` (路径参数): 社区编号

#### 3. 查询所有社区
- **请求方式**: GET
- **请求路径**: `/api/community/list`

#### 4. 分页查询社区
- **请求方式**: POST
- **请求路径**: `/api/community/page?pageNum=1&pageSize=10`
- **请求体**:
```json
{
    "communityNo": "COMM",
    "buildingName": "阳光",
    "buildingAddress": "",
    "city": "北京"
}
```

#### 5. 新增社区
- **请求方式**: POST
- **请求路径**: `/api/community`
- **请求体**:
```json
{
    "communityNo": "COMM002",
    "buildingName": "翠湖小区",
    "buildingAddress": "上海市浦东新区xxx路",
    "coordLat": 31.2304,
    "coordLng": 121.4737,
    "city": "上海"
}
```

#### 6. 批量新增社区
- **请求方式**: POST
- **请求路径**: `/api/community/batch`
- **请求体**:
```json
[
    {
        "communityNo": "COMM003",
        "buildingName": "花园小区",
        "city": "广州"
    },
    {
        "communityNo": "COMM004",
        "buildingName": "海景花园",
        "city": "深圳"
    }
]
```

#### 7. 更新社区
- **请求方式**: PUT
- **请求路径**: `/api/community`
- **请求体**:
```json
{
    "id": 1,
    "buildingName": "阳光花园二期",
    "buildingAddress": "北京市朝阳区xxx街道xxx号"
}
```

#### 8. 删除社区
- **请求方式**: DELETE
- **请求路径**: `/api/community/{id}`
- **参数说明**:
  - `id` (路径参数): 社区ID

#### 9. 批量删除社区
- **请求方式**: DELETE
- **请求路径**: `/api/community/batch?ids=1,2,3`
- **参数说明**:
  - `ids` (查询参数): 社区ID列表

#### 10. 根据城市查询社区
- **请求方式**: GET
- **请求路径**: `/api/community/city/{city}`
- **参数说明**:
  - `city` (路径参数): 城市名称

---

### 二、道闸管理 (Barrier Gate)

**基础路径**: `/api/barrier-gate`

#### 1. 根据ID查询道闸
- **请求方式**: GET
- **请求路径**: `/api/barrier-gate/{id}`
- **参数说明**:
  - `id` (路径参数): 道闸ID

#### 2. 根据编号查询道闸
- **请求方式**: GET
- **请求路径**: `/api/barrier-gate/no/{gateNo}`
- **参数说明**:
  - `gateNo` (路径参数): 道闸编号

#### 3. 查询所有道闸
- **请求方式**: GET
- **请求路径**: `/api/barrier-gate/list`

#### 4. 分页查询道闸
- **请求方式**: POST
- **请求路径**: `/api/barrier-gate/page?pageNum=1&pageSize=10`
- **请求体**:
```json
{
    "gateNo": "GATE",
    "communityId": 1,
    "deviceNo": "",
    "devicePosition": 1,
    "screenPosition": 1,
    "lightboxDirection": 1
}
```

#### 5. 根据社区查询道闸
- **请求方式**: GET
- **请求路径**: `/api/barrier-gate/community/{communityId}`
- **参数说明**:
  - `communityId` (路径参数): 社区ID

#### 6. 新增道闸
- **请求方式**: POST
- **请求路径**: `/api/barrier-gate`
- **请求体**:
```json
{
    "gateNo": "GATE001",
    "communityId": 1,
    "deviceNo": "DEV001",
    "doorLocation": "东门",
    "devicePosition": 1,
    "screenPosition": 1,
    "lightboxDirection": 1
}
```
- **字段说明**:
  - `devicePosition`: 1-进口, 2-出口, 3-进出口
  - `screenPosition`: 1-A, 2-B
  - `lightboxDirection`: 1-朝外, 2-朝内, 3-临街面

#### 7. 批量新增道闸
- **请求方式**: POST
- **请求路径**: `/api/barrier-gate/batch`

#### 8. 更新道闸
- **请求方式**: PUT
- **请求路径**: `/api/barrier-gate`

#### 9. 删除道闸
- **请求方式**: DELETE
- **请求路径**: `/api/barrier-gate/{id}`

#### 10. 批量删除道闸
- **请求方式**: DELETE
- **请求路径**: `/api/barrier-gate/batch?ids=1,2,3`

---

### 三、框架管理 (Frame)

**基础路径**: `/api/frame`

#### 1. 根据ID查询框架
- **请求方式**: GET
- **请求路径**: `/api/frame/{id}`

#### 2. 根据编号查询框架
- **请求方式**: GET
- **请求路径**: `/api/frame/no/{frameNo}`

#### 3. 查询所有框架
- **请求方式**: GET
- **请求路径**: `/api/frame/list`

#### 4. 分页查询框架
- **请求方式**: POST
- **请求路径**: `/api/frame/page?pageNum=1&pageSize=10`
- **请求体**:
```json
{
    "frameNo": "FRAME",
    "communityId": 1,
    "building": "A座",
    "unit": "1单元",
    "elevator": "1号电梯",
    "innerPosition": 1
}
```
- **字段说明**:
  - `innerPosition`: 1-左, 2-中, 3-右

#### 5. 根据社区查询框架
- **请求方式**: GET
- **请求路径**: `/api/frame/community/{communityId}`

#### 6. 新增框架
- **请求方式**: POST
- **请求路径**: `/api/frame`
- **请求体**:
```json
{
    "frameNo": "FRAME001",
    "communityId": 1,
    "building": "A座",
    "unit": "1单元",
    "elevator": "1号电梯",
    "innerPosition": 1
}
```

#### 7. 批量新增框架
- **请求方式**: POST
- **请求路径**: `/api/frame/batch`

#### 8. 更新框架
- **请求方式**: PUT
- **请求路径**: `/api/frame`

#### 9. 删除框架
- **请求方式**: DELETE
- **请求路径**: `/api/frame/{id}`

#### 10. 批量删除框架
- **请求方式**: DELETE
- **请求路径**: `/api/frame/batch?ids=1,2,3`

---

### 四、方案管理 (Plan)

**基础路径**: `/api/plan`

#### 1. 根据ID查询方案
- **请求方式**: GET
- **请求路径**: `/api/plan/{id}`

#### 2. 根据编号查询方案
- **请求方式**: GET
- **请求路径**: `/api/plan/no/{planNo}`

#### 3. 查询所有方案
- **请求方式**: GET
- **请求路径**: `/api/plan/list`

#### 4. 分页查询方案
- **请求方式**: POST
- **请求路径**: `/api/plan/page?pageNum=1&pageSize=10`
- **请求体**:
```json
{
    "planNo": "PLAN",
    "planName": "春季推广",
    "customer": "某某公司",
    "releaseStatus": 3,
    "salesType": 1,
    "releaseDateBegin": "2024-01-01",
    "releaseDateEnd": "2024-12-31"
}
```
- **字段说明**:
  - `releaseStatus`: 1-意向, 2-锁位, 3-执行中, 4-执行完毕, 5-档
  - `salesType`: 1-销售, 2-公益, 3-置换, 4-赠送, 5-余位, 6-其他

#### 5. 根据客户查询方案
- **请求方式**: GET
- **请求路径**: `/api/plan/customer/{customer}`

#### 6. 根据状态查询方案
- **请求方式**: GET
- **请求路径**: `/api/plan/status/{releaseStatus}`
- **参数说明**:
  - `releaseStatus` (路径参数): 发布状态 (1-意向, 2-锁位, 3-执行中, 4-执行完毕, 5-档)

#### 7. 新增方案
- **请求方式**: POST
- **请求路径**: `/api/plan`
- **请求体**:
```json
{
    "planNo": "PLAN001",
    "planName": "春季品牌推广方案",
    "customer": "某某科技有限公司",
    "releaseDateBegin": "2024-03-01",
    "releaseDateEnd": "2024-05-31",
    "releaseStatus": 3,
    "salesType": 1,
    "mediaRequirements": "需要在高端社区投放"
}
```

#### 8. 批量新增方案
- **请求方式**: POST
- **请求路径**: `/api/plan/batch`

#### 9. 更新方案
- **请求方式**: PUT
- **请求路径**: `/api/plan`

#### 10. 删除方案
- **请求方式**: DELETE
- **请求路径**: `/api/plan/{id}`

#### 11. 批量删除方案
- **请求方式**: DELETE
- **请求路径**: `/api/plan/batch?ids=1,2,3`

---

### 五、方案社区关联管理 (Plan Community)

**基础路径**: `/api/plan-community`

#### 1. 根据ID查询关联
- **请求方式**: GET
- **请求路径**: `/api/plan-community/{id}`

#### 2. 根据方案和社区查询关联
- **请求方式**: GET
- **请求路径**: `/api/plan-community/query?planId=1&communityId=1`
- **参数说明**:
  - `planId` (查询参数): 方案ID
  - `communityId` (查询参数): 社区ID

#### 3. 查询所有关联
- **请求方式**: GET
- **请求路径**: `/api/plan-community/list`

#### 4. 分页查询关联
- **请求方式**: POST
- **请求路径**: `/api/plan-community/page?pageNum=1&pageSize=10`
- **请求体**:
```json
{
    "planId": 1,
    "communityId": 1
}
```

#### 5. 根据方案查询关联
- **请求方式**: GET
- **请求路径**: `/api/plan-community/plan/{planId}`

#### 6. 根据社区查询关联
- **请求方式**: GET
- **请求路径**: `/api/plan-community/community/{communityId}`

#### 7. 新增关联
- **请求方式**: POST
- **请求路径**: `/api/plan-community`
- **请求体**:
```json
{
    "planId": 1,
    "communityId": 1,
    "releaseDateBegin": "2024-03-01",
    "releaseDateEnd": "2024-05-31",
    "barrierRequiredQty": 2,
    "frameRequiredQty": 5
}
```

#### 8. 批量新增关联
- **请求方式**: POST
- **请求路径**: `/api/plan-community/batch`

#### 9. 更新关联
- **请求方式**: PUT
- **请求路径**: `/api/plan-community`

#### 10. 删除关联
- **请求方式**: DELETE
- **请求路径**: `/api/plan-community/{id}`

#### 11. 批量删除关联
- **请求方式**: DELETE
- **请求路径**: `/api/plan-community/batch?ids=1,2,3`

---

### 六、方案道闸明细管理 (Plan Barrier)

**基础路径**: `/api/plan-barrier`

#### 1. 根据ID查询明细
- **请求方式**: GET
- **请求路径**: `/api/plan-barrier/{id}`

#### 2. 查询所有明细
- **请求方式**: GET
- **请求路径**: `/api/plan-barrier/list`

#### 3. 分页查询明细
- **请求方式**: POST
- **请求路径**: `/api/plan-barrier/page?pageNum=1&pageSize=10`
- **请求体**:
```json
{
    "planId": 1,
    "barrierGateId": 1,
    "planCommunityId": 1,
    "releaseStatus": 4
}
```
- **字段说明**:
  - `releaseStatus`: 1-意向, 2-锁位, 3-待刊发, 4-刊发中, 5-可调, 6-到期, 7-已下刊

#### 4. 根据方案查询明细
- **请求方式**: GET
- **请求路径**: `/api/plan-barrier/plan/{planId}`

#### 5. 根据道闸查询明细
- **请求方式**: GET
- **请求路径**: `/api/plan-barrier/barrier/{barrierGateId}`

#### 6. 根据方案社区查询明细
- **请求方式**: GET
- **请求路径**: `/api/plan-barrier/plan-community/{planCommunityId}`

#### 7. 根据状态查询明细
- **请求方式**: GET
- **请求路径**: `/api/plan-barrier/status/{releaseStatus}`

#### 8. 新增明细
- **请求方式**: POST
- **请求路径**: `/api/plan-barrier`
- **请求体**:
```json
{
    "planId": 1,
    "barrierGateId": 1,
    "planCommunityId": 1,
    "releaseDateBegin": "2024-03-01",
    "releaseDateEnd": "2024-05-31",
    "releaseStatus": 4
}
```

#### 9. 批量新增明细
- **请求方式**: POST
- **请求路径**: `/api/plan-barrier/batch`

#### 10. 更新明细
- **请求方式**: PUT
- **请求路径**: `/api/plan-barrier`

#### 11. 删除明细
- **请求方式**: DELETE
- **请求路径**: `/api/plan-barrier/{id}`

#### 12. 批量删除明细
- **请求方式**: DELETE
- **请求路径**: `/api/plan-barrier/batch?ids=1,2,3`

---

### 七、方案框架明细管理 (Plan Frame)

**基础路径**: `/api/plan-frame`

#### 1. 根据ID查询明细
- **请求方式**: GET
- **请求路径**: `/api/plan-frame/{id}`

#### 2. 查询所有明细
- **请求方式**: GET
- **请求路径**: `/api/plan-frame/list`

#### 3. 分页查询明细
- **请求方式**: POST
- **请求路径**: `/api/plan-frame/page?pageNum=1&pageSize=10`
- **请求体**:
```json
{
    "planId": 1,
    "frameId": 1,
    "planCommunityId": 1,
    "releaseStatus": 4
}
```

#### 4. 根据方案查询明细
- **请求方式**: GET
- **请求路径**: `/api/plan-frame/plan/{planId}`

#### 5. 根据框架查询明细
- **请求方式**: GET
- **请求路径**: `/api/plan-frame/frame/{frameId}`

#### 6. 根据方案社区查询明细
- **请求方式**: GET
- **请求路径**: `/api/plan-frame/plan-community/{planCommunityId}`

#### 7. 根据状态查询明细
- **请求方式**: GET
- **请求路径**: `/api/plan-frame/status/{releaseStatus}`

#### 8. 新增明细
- **请求方式**: POST
- **请求路径**: `/api/plan-frame`
- **请求体**:
```json
{
    "planId": 1,
    "frameId": 1,
    "planCommunityId": 1,
    "releaseDateBegin": "2024-03-01",
    "releaseDateEnd": "2024-05-31",
    "releaseStatus": 4
}
```

#### 9. 批量新增明细
- **请求方式**: POST
- **请求路径**: `/api/plan-frame/batch`

#### 10. 更新明细
- **请求方式**: PUT
- **请求路径**: `/api/plan-frame`

#### 11. 删除明细
- **请求方式**: DELETE
- **请求路径**: `/api/plan-frame/{id}`

#### 12. 批量删除明细
- **请求方式**: DELETE
- **请求路径**: `/api/plan-frame/batch?ids=1,2,3`

---

## 枚举值说明

### 设备位置 (devicePosition)
| 值 | 说明 |
|----|------|
| 1 | 进口 |
| 2 | 出口 |
| 3 | 进出口 |

### 画面位置 (screenPosition)
| 值 | 说明 |
|----|------|
| 1 | A |
| 2 | B |

### 灯箱朝向 (lightboxDirection)
| 值 | 说明 |
|----|------|
| 1 | 朝外 |
| 2 | 朝内 |
| 3 | 临街面 |

### 梯内位置 (innerPosition)
| 值 | 说明 |
|----|------|
| 1 | 左 |
| 2 | 中 |
| 3 | 右 |

### 方案发布状态 (releaseStatus)
| 值 | 说明 |
|----|------|
| 1 | 意向 |
| 2 | 锁位 |
| 3 | 执行中 |
| 4 | 执行完毕 |
| 5 | 档 |

### 销售类型 (salesType)
| 值 | 说明 |
|----|------|
| 1 | 销售 |
| 2 | 公益 |
| 3 | 置换 |
| 4 | 赠送 |
| 5 | 余位 |
| 6 | 其他 |

### 明细发布状态 (releaseStatus)
| 值 | 说明 |
|----|------|
| 1 | 意向 |
| 2 | 锁位 |
| 3 | 待刊发 |
| 4 | 刊发中 |
| 5 | 可调 |
| 6 | 到期 |
| 7 | 已下刊 |

---

## Druid监控

- **地址**: http://localhost:8080/api/druid/index.html
- **用户名**: admin
- **密码**: admin

## 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mvp?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root
```

## 项目结构

```
advertising-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/advertising/
│   │   │       ├── AdvertisingSystemApplication.java  # 启动类
│   │   │       ├── common/                            # 通用类
│   │   │       │   ├── Result.java                    # 通用响应结果
│   │   │       │   ├── PageResult.java                # 分页结果
│   │   │       │   ├── PageRequest.java               # 分页请求
│   │   │       │   └── GlobalExceptionHandler.java    # 全局异常处理
│   │   │       ├── config/                            # 配置类
│   │   │       │   ├── CorsConfig.java                # 跨域配置
│   │   │       │   └── WebMvcConfig.java              # Web MVC配置
│   │   │       ├── controller/                        # 控制器层
│   │   │       │   ├── CommunityController.java
│   │   │       │   ├── BarrierGateController.java
│   │   │       │   ├── FrameController.java
│   │   │       │   ├── PlanController.java
│   │   │       │   ├── PlanCommunityController.java
│   │   │       │   ├── PlanBarrierController.java
│   │   │       │   └── PlanFrameController.java
│   │   │       ├── entity/                            # 实体类
│   │   │       │   ├── Community.java
│   │   │       │   ├── BarrierGate.java
│   │   │       │   ├── Frame.java
│   │   │       │   ├── Plan.java
│   │   │       │   ├── PlanCommunity.java
│   │   │       │   ├── PlanBarrier.java
│   │   │       │   └── PlanFrame.java
│   │   │       ├── enums/                             # 枚举类
│   │   │       │   ├── DevicePositionEnum.java
│   │   │       │   ├── ScreenPositionEnum.java
│   │   │       │   ├── LightboxDirectionEnum.java
│   │   │       │   ├── InnerPositionEnum.java
│   │   │       │   ├── PlanReleaseStatusEnum.java
│   │   │       │   ├── SalesTypeEnum.java
│   │   │       │   └── DetailReleaseStatusEnum.java
│   │   │       ├── mapper/                            # 数据访问层
│   │   │       │   ├── CommunityMapper.java
│   │   │       │   ├── BarrierGateMapper.java
│   │   │       │   ├── FrameMapper.java
│   │   │       │   ├── PlanMapper.java
│   │   │       │   ├── PlanCommunityMapper.java
│   │   │       │   ├── PlanBarrierMapper.java
│   │   │       │   └── PlanFrameMapper.java
│   │   │       └── service/                           # 业务逻辑层
│   │   │           ├── CommunityService.java
│   │   │           ├── BarrierGateService.java
│   │   │           ├── FrameService.java
│   │   │           ├── PlanService.java
│   │   │           ├── PlanCommunityService.java
│   │   │           ├── PlanBarrierService.java
│   │   │           ├── PlanFrameService.java
│   │   │           └── impl/                          # 实现类
│   │   │               ├── CommunityServiceImpl.java
│   │   │               ├── BarrierGateServiceImpl.java
│   │   │               ├── FrameServiceImpl.java
│   │   │               ├── PlanServiceImpl.java
│   │   │               ├── PlanCommunityServiceImpl.java
│   │   │               ├── PlanBarrierServiceImpl.java
│   │   │               └── PlanFrameServiceImpl.java
│   │   └── resources/
│   │       ├── mapper/                                # MyBatis映射文件
│   │       │   ├── CommunityMapper.xml
│   │       │   ├── BarrierGateMapper.xml
│   │       │   ├── FrameMapper.xml
│   │       │   ├── PlanMapper.xml
│   │       │   ├── PlanCommunityMapper.xml
│   │       │   ├── PlanBarrierMapper.xml
│   │       │   └── PlanFrameMapper.xml
│   │       └── application.yml                        # 配置文件
│   └── test/
├── pom.xml                                            # Maven配置
└── API文档.md                                         # API文档
```

## 启动项目

```bash
# 进入项目目录
cd advertising-system

# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/advertising-system-1.0.0.jar
```

项目启动后，访问 http://localhost:8080/api/doc.html 查看API文档。
