# Vue对接MES后端接口与前端页面提示词手册（中文教学版）

## 1. 文档目标

本手册用于把当前 Spring Boot MES 后端，完整对接到 Vue 前端。你可以直接按本文：

- 搭建 Vue3 工程骨架
- 对接全部接口（认证、设备、工单、报表、AI诊断）
- 生成页面（含可复制的 AI 提示词）
- 完成联调与常见问题排查


## 2. 后端联调基线

- 后端地址：`http://localhost:8080`
- Swagger 文档：`http://localhost:8080/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`
- 统一返回结构：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 2.1 统一业务码（前端要按这个处理）

- `200`：成功
- `400`：参数错误
- `401`：未认证/认证失效
- `403`：无权限
- `404`：资源不存在
- `409`：数据冲突（如工单版本冲突）
- `500`：服务端异常

### 2.2 测试账号（来自 `data.sql`）

- 管理员：`admin / 123456`
- 运维员：`operator / 123456`

### 2.3 认证与权限

- 登录接口无需 Token：`POST /api/auth/login`
- 其他业务接口都需要 `Authorization: Bearer <token>`
- 仅管理员可访问：
- `DELETE /api/devices/{id}`
- `GET /api/reports/work-orders`


## 3. Vue工程推荐技术栈

- `Vue 3` + `TypeScript` + `Vite`
- `Pinia`（状态管理）
- `Vue Router`（路由与权限控制）
- `Axios`（HTTP请求）
- `Element Plus`（UI组件）
- `ECharts`（报表图表）
- `dayjs`（时间处理）

安装命令（示例）：

```bash
npm create vue@latest mes-web
cd mes-web
npm i axios pinia vue-router element-plus @element-plus/icons-vue echarts dayjs
```


## 4. 前端目录建议（教学版）

```text
src/
  api/
    auth.ts
    device.ts
    workOrder.ts
    report.ts
    ai.ts
  stores/
    auth.ts
  router/
    index.ts
  utils/
    http.ts
    auth.ts
  views/
    LoginView.vue
    dashboard/
      HomeView.vue
    device/
      DeviceListView.vue
    work-order/
      WorkOrderListView.vue
      WorkOrderDetailDrawer.vue
    report/
      ReportView.vue
    ai/
      AiDiagnosisView.vue
  layouts/
    AdminLayout.vue
  types/
    common.ts
    device.ts
    workOrder.ts
    report.ts
    ai.ts
```


## 5. 全接口对接清单（按模块）

## 5.1 认证

### `POST /api/auth/login`

- 鉴权：否
- 请求体：

```json
{
  "username": "admin",
  "password": "123456"
}
```

- `data` 返回字段：
- `token`、`tokenType`、`expiresIn`、`username`、`nickname`、`roles`


## 5.2 设备管理 ` /api/devices `

### `POST /api/devices` 创建设备

- 鉴权：是
- 请求体字段：
- `deviceCode` 必填，唯一
- `deviceName` 必填
- `deviceType` 必填
- `status` 可选：`ONLINE|OFFLINE|FAULT`
- `location` 可选
- `description` 可选

### `PUT /api/devices/{id}` 更新设备

- 鉴权：是
- 请求体字段全部可选（局部更新）
- `status` 若传值，必须是 `ONLINE|OFFLINE|FAULT`

### `DELETE /api/devices/{id}` 删除设备

- 鉴权：是（管理员）

### `GET /api/devices/{id}` 设备详情

- 鉴权：是

### `GET /api/devices` 设备列表

- 鉴权：是
- Query 参数：`status`、`deviceType`、`keyword`

- 设备响应对象字段（前端表格列建议直接对应）：
- `id`
- `deviceCode`
- `deviceName`
- `deviceType`
- `status`
- `location`
- `description`
- `lastHeartbeat`
- `createdAt`
- `updatedAt`


## 5.3 工单管理 ` /api/work-orders `

### `POST /api/work-orders` 创建工单

- 鉴权：是
- 请求体字段：
- `title` 必填
- `description` 可选
- `deviceId` 可选
- `assigneeId` 可选
- `priority` 可选，范围 `1~5`

### `PUT /api/work-orders/{id}/assign` 分配处理人

- 鉴权：是
- 请求体：`assigneeId`、`version`
- 注意：`version` 必填，用于乐观锁

### `PUT /api/work-orders/{id}/progress` 更新进度

- 鉴权：是
- 请求体：`progress`、`version`
- `progress` 范围 `0~100`
- 业务规则：
- 已完成工单不允许回退进度
- 进度 `>0` 且原状态为 `PENDING` 时自动改为 `PROCESSING`
- 进度 `>=100` 自动改为 `COMPLETED`

### `PUT /api/work-orders/{id}/status` 更新状态

- 鉴权：是
- 请求体：`status`、`version`
- `status` 只能是 `PENDING|PROCESSING|COMPLETED`
- 业务规则：
- 已完成工单不允许回退为其他状态
- 改为 `COMPLETED` 时自动写入 `progress=100`

### `GET /api/work-orders/{id}` 工单详情

- 鉴权：是

### `GET /api/work-orders` 工单列表

- 鉴权：是
- Query 参数：`status`、`assigneeId`、`deviceId`

- 工单响应对象字段：
- `id`
- `orderNo`
- `title`
- `description`
- `deviceId`
- `assigneeId`
- `priority`
- `status`
- `progress`
- `version`
- `completedAt`
- `createdAt`
- `updatedAt`


## 5.4 报表统计 ` /api/reports `

### `GET /api/reports/work-orders`

- 鉴权：是（管理员）
- Query 参数：
- `startDate`（可选，格式 `yyyy-MM-dd`）
- `endDate`（可选，格式 `yyyy-MM-dd`）
- `deviceType`（可选）
- 默认：不传时统计最近 7 天

- `data` 返回字段：
- `totalCount`
- `completedCount`
- `overallCompletionRate`
- `items[]`，其中每项包含：
- `statDate`
- `deviceType`
- `totalCount`
- `completedCount`
- `completionRate`


## 5.5 AI诊断 ` /api/ai `

### `POST /api/ai/diagnosis`

- 鉴权：是
- 请求体：

```json
{
  "deviceCode": "DV-20001",
  "deviceStatus": "FAULT",
  "symptom": "电机振动明显，伴随异响"
}
```

- `data` 返回字段：
- `deviceCode`
- `deviceStatus`
- `possibleCauses`（字符串数组）
- `suggestion`
- `confidence`（0~1）
- `fromCache`（是否命中缓存）


## 6. 前端关键代码模板（可直接落地）

## 6.1 环境变量

`.env.development`

```env
VITE_API_BASE_URL=/api
```

`vite.config.ts`（本地代理，避免跨域）

```ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

## 6.2 Axios 封装

`src/utils/http.ts`

```ts
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useAuthStore } from '@/stores/auth'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000
})

http.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (payload?.code !== 200) {
      ElMessage.error(payload?.message || '请求失败')
      if (payload?.code === 401) {
        const authStore = useAuthStore()
        authStore.logout()
        router.push('/login')
      }
      return Promise.reject(payload)
    }
    return payload.data
  },
  (error) => {
    const status = error?.response?.status
    if (status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      router.push('/login')
    }
    ElMessage.error(error?.response?.data?.message || error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default http
```

## 6.3 鉴权状态管理（Pinia）

`src/stores/auth.ts`

```ts
import { defineStore } from 'pinia'

interface AuthState {
  token: string
  username: string
  roles: string[]
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: localStorage.getItem('token') || '',
    username: localStorage.getItem('username') || '',
    roles: JSON.parse(localStorage.getItem('roles') || '[]')
  }),
  actions: {
    setAuth(payload: { token: string; username: string; roles: string[] }) {
      this.token = payload.token
      this.username = payload.username
      this.roles = payload.roles
      localStorage.setItem('token', payload.token)
      localStorage.setItem('username', payload.username)
      localStorage.setItem('roles', JSON.stringify(payload.roles))
    },
    logout() {
      this.token = ''
      this.username = ''
      this.roles = []
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('roles')
    },
    hasRole(role: 'ADMIN' | 'OPERATOR') {
      return this.roles.includes(role)
    }
  }
})
```

## 6.4 路由守卫

`src/router/index.ts`（核心逻辑片段）

```ts
router.beforeEach((to) => {
  const authStore = useAuthStore()
  const whiteList = ['/login']

  if (whiteList.includes(to.path)) return true
  if (!authStore.token) return '/login'

  const requiredRole = to.meta?.role as string | undefined
  if (requiredRole && !authStore.roles.includes(requiredRole)) {
    return '/403'
  }
  return true
})
```

## 6.5 API 模块示例

`src/api/workOrder.ts`

```ts
import http from '@/utils/http'

export const listWorkOrders = (params?: {
  status?: string
  assigneeId?: number
  deviceId?: number
}) => http.get('/work-orders', { params })

export const createWorkOrder = (data: {
  title: string
  description?: string
  deviceId?: number
  assigneeId?: number
  priority?: number
}) => http.post('/work-orders', data)

export const assignWorkOrder = (id: number, data: { assigneeId: number; version: number }) =>
  http.put(`/work-orders/${id}/assign`, data)

export const updateWorkOrderProgress = (id: number, data: { progress: number; version: number }) =>
  http.put(`/work-orders/${id}/progress`, data)

export const updateWorkOrderStatus = (id: number, data: { status: 'PENDING' | 'PROCESSING' | 'COMPLETED'; version: number }) =>
  http.put(`/work-orders/${id}/status`, data)
```


## 7. 页面设计建议（与接口一一映射）

## 7.1 登录页 `LoginView`

- 表单：`username`、`password`
- 提交后保存 `token + roles + username`
- 根据角色跳转：
- `ADMIN` 默认进“报表/设备总览”
- `OPERATOR` 默认进“工单列表”

## 7.2 设备管理页 `DeviceListView`

- 筛选：状态、类型、关键词
- 表格列：设备编码、名称、类型、状态、位置、更新时间
- 操作：新增、编辑、查看详情、删除（仅ADMIN显示）

## 7.3 工单管理页 `WorkOrderListView`

- 筛选：状态、处理人、设备
- 列表列：工单号、标题、优先级、状态、进度、处理人、版本号
- 操作：新建、分配、更新进度、更新状态、查看详情
- 关键点：提交更新时必须带 `version`，冲突时提示“请刷新后重试”

## 7.4 报表页 `ReportView`（ADMIN）

- 筛选：日期范围、设备类型
- 卡片：总工单数、已完成数、总完成率
- 图表：
- 按日期趋势折线图（total/completed）
- 按设备类型完成率柱状图

## 7.5 AI诊断页 `AiDiagnosisView`

- 输入：设备编码、设备状态、异常描述
- 输出：可能原因列表、建议、置信度、是否缓存命中
- 页面提示：当 `fromCache=true` 时显示“来自缓存”标签


## 8. 可直接使用的 AI 提示词（重点）

以下提示词可用于 ChatGPT、Claude、Cursor、Copilot Chat 等。

## 8.1 通用工程提示词（先用这个生成基础骨架）

```text
你是一名资深前端架构师，请使用 Vue3 + TypeScript + Vite + Pinia + Vue Router + Axios + Element Plus，
实现一个 MES 管理前端。请严格遵守以下后端接口契约：

1) 后端基地址：/api（由 Vite 代理到 http://localhost:8080）
2) 统一响应结构：{ code, message, data }，仅当 code===200 时视为成功
3) 登录接口：POST /api/auth/login
4) 设备接口：
   POST /api/devices
   PUT /api/devices/{id}
   DELETE /api/devices/{id}（仅 ADMIN）
   GET /api/devices/{id}
   GET /api/devices
5) 工单接口：
   POST /api/work-orders
   PUT /api/work-orders/{id}/assign
   PUT /api/work-orders/{id}/progress
   PUT /api/work-orders/{id}/status
   GET /api/work-orders/{id}
   GET /api/work-orders
6) 报表接口：GET /api/reports/work-orders（仅 ADMIN）
7) AI接口：POST /api/ai/diagnosis
8) Token 规范：Authorization: Bearer <token>
9) 工单更新接口必须传 version，若返回 code=409 要提示用户刷新重试

输出要求：
- 给出完整项目目录
- 生成关键文件代码（router、store、http封装、api模块、主布局）
- 所有代码必须是 TypeScript
- 关键逻辑添加中文注释
- 不要伪代码，提供可运行代码
```

## 8.2 登录页提示词

```text
请实现 LoginView.vue：
- 使用 Element Plus 表单
- 字段：username、password，必填校验
- 调用 POST /api/auth/login
- 成功后保存 token、username、roles 到 Pinia+localStorage
- 跳转到 /dashboard
- 失败提示 message
- 使用 <script setup lang="ts">
请直接输出完整 SFC 文件。
```

## 8.3 设备管理页提示词

```text
请实现 DeviceListView.vue：
- 顶部筛选：status、deviceType、keyword
- 调用 GET /api/devices 列表
- 表格显示：deviceCode、deviceName、deviceType、status、location、updatedAt
- 提供新增/编辑弹窗：
  新增调用 POST /api/devices
  编辑调用 PUT /api/devices/{id}
- 删除按钮仅在 roles 包含 ADMIN 时展示，调用 DELETE /api/devices/{id}
- 所有请求通过 api/device.ts 调用
- 对 code!=200、401、403、409 做友好提示
请输出完整页面代码 + 对应 API 文件代码。
```

## 8.4 工单管理页提示词

```text
请实现 WorkOrderListView.vue + WorkOrderActionDialog.vue：
- 列表接口：GET /api/work-orders
- 新建工单：POST /api/work-orders
- 分配处理人：PUT /api/work-orders/{id}/assign
- 更新进度：PUT /api/work-orders/{id}/progress
- 更新状态：PUT /api/work-orders/{id}/status
- 每次更新必须带当前行 version
- 若返回 code=409，提示“数据已被他人更新，请刷新后重试”
- 进度条使用 Element Plus Progress
- 状态标签区分颜色（PENDING/PROCESSING/COMPLETED）
请输出可运行代码，含 TypeScript 类型定义。
```

## 8.5 报表页提示词

```text
请实现 ReportView.vue（仅 ADMIN 可见）：
- 查询条件：日期范围(startDate/endDate) + deviceType
- 调用 GET /api/reports/work-orders
- 页面显示：
  1) 总工单数、已完成数、整体完成率
  2) 按日期折线图（totalCount、completedCount）
  3) 按设备类型完成率柱状图
- 图表使用 ECharts
- 日期格式统一 yyyy-MM-dd
请输出完整 SFC 代码和图表 option 生成函数。
```

## 8.6 AI诊断页提示词

```text
请实现 AiDiagnosisView.vue：
- 输入项：deviceCode、deviceStatus(ONLINE/OFFLINE/FAULT)、symptom
- 调用 POST /api/ai/diagnosis
- 结果展示：possibleCauses 列表、suggestion、confidence、fromCache
- 若 fromCache=true 显示“缓存结果”标签
- 页面要有加载态、空态、错误提示
请输出完整可运行代码。
```

## 8.7 路由与权限提示词

```text
请实现 router/index.ts 与权限守卫：
- 白名单：/login
- 未登录访问其他页面跳转 /login
- 路由 meta.role='ADMIN' 时，仅 ADMIN 可访问，否则跳转 /403
- 提供基础路由：/login、/dashboard、/devices、/work-orders、/reports、/ai、/403
- 使用 TypeScript 并给出完整文件代码
```


## 9. 前端联调顺序（建议）

1. 先完成登录与 token 持久化
2. 完成 Axios 拦截器与统一错误处理
3. 打通设备模块 CRUD（验证权限差异）
4. 打通工单模块（重点验证 version 冲突）
5. 打通报表图表
6. 打通 AI 诊断页
7. 最后统一样式、菜单权限、异常页


## 10. 常见坑位与排查

- 跨域问题：优先使用 Vite 代理 `/api`，避免直接跨域请求。
- Token 前缀错误：必须是 `Bearer `（含空格）。
- 只看 HTTP 200 不够：要检查业务 `code` 是否等于 `200`。
- 忘记带工单 `version`：会触发 `409` 冲突。
- 角色判断不一致：后端按 `ADMIN/OPERATOR`，前端菜单权限也按同名判断。
- 报表日期格式错误：必须是 `yyyy-MM-dd`。


## 11. 与 Swagger 对照使用方式

- 启动后端后，先在浏览器打开：`http://localhost:8080/swagger-ui.html`
- 先调 `POST /api/auth/login` 获取 token
- 在 Swagger 右上角 `Authorize` 输入：`Bearer <token>`
- 再对照本手册完成 Vue 联调


## 12. 你可以直接复制的“整包生成提示词”

```text
请你作为资深 Vue3 + TypeScript 工程师，生成一个可运行的 MES 前端项目（Vite）。
后端是 Spring Boot，统一响应结构为 {code,message,data}，baseURL=/api。

必须对接以下接口：
- POST /api/auth/login
- POST /api/devices
- PUT /api/devices/{id}
- DELETE /api/devices/{id}
- GET /api/devices/{id}
- GET /api/devices
- POST /api/work-orders
- PUT /api/work-orders/{id}/assign
- PUT /api/work-orders/{id}/progress
- PUT /api/work-orders/{id}/status
- GET /api/work-orders/{id}
- GET /api/work-orders
- GET /api/reports/work-orders
- POST /api/ai/diagnosis

业务规则：
- 除登录外都要带 Authorization: Bearer <token>
- DELETE /api/devices/{id}、GET /api/reports/work-orders 仅 ADMIN 可访问
- 工单 assign/progress/status 请求必须携带 version；code=409 时提示刷新重试
- 设备状态枚举：ONLINE/OFFLINE/FAULT
- 工单状态枚举：PENDING/PROCESSING/COMPLETED

技术要求：
- Vue3 + TS + Pinia + Vue Router + Axios + Element Plus + ECharts
- 必须提供：router 守卫、auth store、http 拦截器、api 分层、页面级 CRUD
- 页面包括：登录、首页、设备管理、工单管理、报表、AI诊断、403
- 所有关键逻辑用中文注释

输出要求：
- 先输出完整目录树
- 再按文件逐个输出完整代码（不要省略）
- 代码可直接复制运行
```
