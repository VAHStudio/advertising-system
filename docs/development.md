# 开发指南

## 开发环境搭建

### 1. 后端开发环境

```bash
# 安装 Java 21
# macOS
brew install openjdk@21

# 配置环境变量
echo 'export JAVA_HOME=/usr/local/opt/openjdk@21' >> ~/.zshrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
source ~/.zshrc

# 验证
java -version

# 安装 Maven
brew install maven
```

### 2. 前端开发环境

```bash
# 安装 Node.js 20
# macOS
brew install node@20

# 或使用 nvm
nvm install 20
nvm use 20

# 验证
node -v
npm -v

# 安装 pnpm（推荐）
npm install -g pnpm
```

### 3. 数据库

```bash
# 使用 Docker 启动 MySQL
docker run -d \
  --name mysql8 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=outdoor_saas \
  -p 3306:3306 \
  mysql:8.0

# 或使用本地 MySQL
brew install mysql
brew services start mysql
```

### 4. IDE 配置

**IntelliJ IDEA 推荐设置：**

1. **插件安装**
   - Lombok
   - MyBatisX
   - Rainbow Brackets
   - .env files support

2. **代码风格**
   - 导入项目代码风格配置
   - 设置缩进：4 空格
   - 设置编码：UTF-8

3. **运行配置**
   - 主类：`OutdoorSaasApplication`
   - VM options：`-Dspring.profiles.active=dev`

**VS Code 推荐设置：**

```json
{
  "extensions": [
    "dbaeumer.vscode-eslint",
    "Vue.volar",
    "bradlc.vscode-tailwindcss",
    "esbenp.prettier-vscode"
  ],
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode"
}
```

---

## 项目启动

### 1. 启动后端

```bash
cd outdoor-saas-be

# 开发模式
mvn spring-boot:run

# 或使用 IDE 运行
# 主类: OutdoorSaasApplication

# 访问
# API: http://localhost:16000/api
# Swagger: http://localhost:16000/swagger-ui.html
```

### 2. 启动前端

```bash
cd outdoor-saas-fe

# 安装依赖
npm install

# 开发模式
npm run dev

# 访问 http://localhost:3000
```

---

## 代码规范

### 1. Java 代码规范

#### 命名规范

```java
// 类名：PascalCase
public class UserService { }

// 方法名：camelCase
public void getUserById() { }

// 变量名：camelCase
private String userName;

// 常量：UPPER_SNAKE_CASE
private static final String DEFAULT_ROLE = "USER";

// 包名：全小写
package com.touhuwai.service;
```

#### 代码格式

```java
// 类注释
/**
 * 用户服务类
 * 提供用户相关的业务逻辑
 */
@Service
public class UserService {
    
    // 字段注入改为构造器注入
    private final UserMapper userMapper;
    
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    /**
     * 根据 ID 获取用户
     * @param id 用户 ID
     * @return 用户信息
     */
    public User getById(Long id) {
        return userMapper.selectById(id);
    }
}
```

### 2. TypeScript/Vue 代码规范

#### 命名规范

```typescript
// 组件名：PascalCase
// UserProfile.vue

// 接口名：PascalCase
interface UserInfo {
  id: number;
  name: string;
}

// 类型别名：PascalCase
type UserRole = 'admin' | 'user';

// 函数名：camelCase
function getUserById(id: number): UserInfo { }

// Hook 名：use 前缀
function useUserData() { }

// 常量：UPPER_SNAKE_CASE
const API_BASE_URL = '/api';
```

#### Vue 组件规范

```vue
<template>
  <!-- 组件模板 -->
  <div class="user-profile">
    <h1>{{ user.name }}</h1>
  </div>
</template>

<script setup lang="ts">
// 1. 导入顺序：Vue → 第三方 → 组件 → 服务 → 类型
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import UserAvatar from '@/components/UserAvatar.vue';
import { userService } from '@/services/userService';
import type { UserInfo } from '@/types/user';

// 2. Props 定义
interface Props {
  userId: number;
}

const props = withDefaults(defineProps<Props>(), {
  userId: 0,
});

// 3. Emits 定义
const emit = defineEmits<{
  (e: 'update', user: UserInfo): void;
}>();

// 4. 响应式数据
const user = ref<UserInfo | null>(null);
const isLoading = ref(false);
const error = ref<string | null>(null);

// 5. 计算属性
const displayName = computed(() => user.value?.name ?? 'Unknown');

// 6. 方法
async function loadUser() {
  try {
    isLoading.value = true;
    user.value = await userService.getById(props.userId);
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载失败';
  } finally {
    isLoading.value = false;
  }
}

// 7. 生命周期
onMounted(() => {
  loadUser();
});
</script>

<style scoped>
/* 8. 样式 */
.user-profile {
  @apply p-4 bg-white rounded-lg;
}
</style>
```

---

## Git 工作流

### 1. 分支策略

```
main        生产分支
  │
  ├── develop    开发分支
  │     │
  │     ├── feature/login      功能分支
  │     ├── feature/dashboard
  │     └── bugfix/issue-123
  │
  └── hotfix/security-patch    热修复分支
```

### 2. 提交规范

```bash
# 格式: <type>(<scope>): <subject>
# 示例:

feat(user): 添加用户登录功能
fix(api): 修复计划查询 N+1 问题
docs(readme): 更新部署文档
style(css): 调整按钮样式
refactor(service): 重构社区服务
perf(query): 优化数据库查询
test(unit): 添加单元测试
chore(deps): 更新依赖版本
```

### 3. 提交示例

```bash
# 1. 创建功能分支
git checkout -b feature/ai-assistant develop

# 2. 开发并提交
git add .
git commit -m "feat(ai): 实现流式对话功能"

# 3. 推送分支
git push origin feature/ai-assistant

# 4. 创建 Pull Request
# 在 GitHub/GitLab 上创建 PR，请求合并到 develop

# 5. 代码审查通过后合并
git checkout develop
git merge --no-ff feature/ai-assistant
git branch -d feature/ai-assistant
```

---

## 数据库开发

### 1. 迁移规范

```sql
-- V{版本号}__{描述}.sql
-- 示例: V8__add_user_phone.sql

-- 1. 添加列
ALTER TABLE users ADD COLUMN phone VARCHAR(20) AFTER email;

-- 2. 添加索引
CREATE INDEX idx_phone ON users(phone);

-- 3. 数据迁移（如果需要）
UPDATE users SET phone = '' WHERE phone IS NULL;

-- 4. 添加约束
ALTER TABLE users MODIFY phone VARCHAR(20) NOT NULL DEFAULT '';
```

### 2. 命名规范

| 对象 | 规范 | 示例 |
|------|------|------|
| 表名 | snake_case，复数 | users, communities |
| 列名 | snake_case | user_name, created_at |
| 索引 | idx_{列名} | idx_user_name |
| 唯一索引 | uk_{列名} | uk_email |
| 外键 | fk_{表}_{列} | fk_plan_community_id |

---

## API 开发

### 1. Controller 规范

```java
@RestController
@RequestMapping("/api/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable @Min(1) Long id) {
        return Result.success(userService.getById(id));
    }
    
    @PostMapping
    public Result<Long> create(@RequestBody @Valid UserCreateRequest request) {
        return Result.success(userService.create(request));
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid UserUpdateRequest request) {
        userService.update(id, request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable @Min(1) Long id) {
        userService.delete(id);
        return Result.success();
    }
}
```

### 2. DTO 规范

```java
@Data
public class UserCreateRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度3-50字符")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
        message = "密码必须包含大小写字母和数字，至少8位"
    )
    private String password;
    
    @Email(message = "邮箱格式不正确")
    private String email;
}
```

---

## 测试

### 1. 单元测试

```java
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void shouldReturnUserWhenExists() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        when(userService.getById(1L)).thenReturn(user);
        
        // when & then
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.username").value("test"));
    }
}
```

### 2. 运行测试

```bash
# 后端测试
cd outdoor-saas-be
mvn test

# 运行单个测试
mvn test -Dtest=UserServiceTest

# 前端测试
cd outdoor-saas-fe
npm run test

# 运行单个测试
npm run test -- UserProfile.spec.ts
```

---

## 调试技巧

### 1. 后端调试

```bash
# 1. IDE 调试
# - 设置断点
# - 以 Debug 模式启动
# - 使用 Step Over/Into 逐步执行

# 2. 日志调试
log.debug("User info: {}", user);
log.info("Processing plan: id={}", planId);
log.error("Failed to save", exception);

# 3. Actuator 端点
# /actuator/health    健康检查
# /actuator/info      应用信息
# /actuator/metrics   性能指标
```

### 2. 前端调试

```typescript
// 1. Vue DevTools
// 安装浏览器扩展，查看组件状态和事件

// 2. 控制台调试
console.log('User data:', user.value);
console.table(plans.value);
console.trace('Stack trace');

// 3. 断点调试
// 在 Chrome DevTools Sources 面板设置断点

// 4. 网络调试
// Network 面板查看 API 请求
```

---

## 常见问题

### 1. 后端问题

**问题**: MyBatis 绑定错误
```
解决方案:
1. 检查 Mapper XML 文件路径
2. 检查 namespace 是否与接口全名一致
3. 检查 SQL ID 是否与方法名一致
```

**问题**: JWT 验证失败
```
解决方案:
1. 检查 token 是否正确传递
2. 检查 token 是否过期
3. 检查 JWT secret 配置
```

### 2. 前端问题

**问题**: 跨域错误
```
解决方案:
1. 检查 vite.config.ts 代理配置
2. 检查后端 CORS 配置
3. 确保请求路径正确
```

**问题**: 类型错误
```
解决方案:
1. 检查接口类型定义
2. 使用类型断言时要谨慎
3. 确保类型文件已导入
```

---

## 性能优化

### 1. 后端优化

```java
// 1. 使用缓存
@Cacheable(value = "users", key = "#id")
public User getById(Long id) { }

// 2. 批量查询代替循环
public List<User> getByIds(List<Long> ids) {
    return userMapper.selectBatchIds(ids);
}

// 3. 异步处理
@Async
public void sendNotification(Long userId) { }
```

### 2. 前端优化

```typescript
// 1. 虚拟列表（大数据量）
import { useVirtualList } from '@vueuse/core';

// 2. 防抖/节流
import { debounce } from 'lodash-es';
const search = debounce((keyword: string) => {
  // 搜索逻辑
}, 300);

// 3. 组件懒加载
const PlanDetail = defineAsyncComponent(() => 
  import('./PlanDetail.vue')
);
```

---

## 安全开发

### 1. 输入验证

```java
// 永远不要信任用户输入
@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "包含非法字符")
private String username;

// 使用预编译语句防止 SQL 注入
@Select("SELECT * FROM user WHERE id = #{id}")
User getById(Long id);
```

### 2. XSS 防护

```vue
<!-- Vue 自动转义 -->
<div>{{ userInput }}</div>

<!-- 避免使用 v-html -->
<!-- 如果必须使用，先进行过滤 -->
<div v-html="sanitize(userInput)"></div>
```

### 3. 敏感信息

```bash
# 不要提交到版本控制
echo ".env" >> .gitignore
echo "*.log" >> .gitignore

# 使用环境变量
# .env 文件
DB_PASSWORD=your_secret_password
JWT_SECRET=your_secret_key
```

---

## 文档维护

### 1. API 文档

使用 Swagger/OpenAPI 自动生成：

```java
@Operation(summary = "获取用户详情", description = "根据用户ID获取详细信息")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "成功"),
    @ApiResponse(responseCode = "404", description = "用户不存在")
})
@GetMapping("/{id}")
public Result<User> getById(@PathVariable Long id) { }
```

### 2. 代码注释

```java
/**
 * 计算投放计划的预算总额
 * 
 * @param planId 计划ID
 * @return 预算金额（元）
 * @throws PlanNotFoundException 当计划不存在时
 * @since 1.0.0
 */
public BigDecimal calculateBudget(Long planId) { }
```

---

## 发布流程

### 1. 版本号规范

采用语义化版本：MAJOR.MINOR.PATCH

```
1.0.0    初始版本
1.1.0    新增功能（向后兼容）
1.1.1    问题修复
2.0.0    重大变更（不兼容）
```

### 2. 发布检查清单

- [ ] 所有测试通过
- [ ] 代码审查完成
- [ ] 文档已更新
- [ ] 数据库迁移已测试
- [ ] 配置文件已更新
- [ ] 备份策略已确认

### 3. 发布步骤

```bash
# 1. 创建发布分支
git checkout -b release/v1.1.0 develop

# 2. 更新版本号
# pom.xml: <version>1.1.0</version>
# package.json: "version": "1.1.0"

# 3. 提交版本更新
git add .
git commit -m "chore(release): bump version to 1.1.0"

# 4. 合并到 main
git checkout main
git merge --no-ff release/v1.1.0
git tag -a v1.1.0 -m "Release version 1.1.0"

# 5. 合并到 develop
git checkout develop
git merge --no-ff release/v1.1.0

# 6. 删除发布分支
git branch -d release/v1.1.0

# 7. 推送
git push origin main --tags
git push origin develop
```
