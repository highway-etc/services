# highway-etc 后端接口规范（面向前端/Agent 开发）

> 基于 Spring Boot + MyCat 分库，默认部署地址 `http://localhost:8080`。时间字段均返回 ISO8601（无时区偏移部分）。

## 通用约定

- 所有接口为无鉴权 GET/JSON，前缀 `/api`。
- 分页参数 `page` 为 **0 基**，默认 0；`size` 默认 20，最小 1，最大 200。
- 车牌字段返回脱敏 `hphm_mask`，前端可用 `licensePlate` 或 `plate` 片段模糊匹配。
- 时间参数接受 ISO8601 字符串（例如 `2025-12-01T00:00:00`），含边界。
- 统一排序：按时间倒序（最新在前）。

## 接口一览

| 路径 | 作用 | 关键参数 |
| --- | --- | --- |
| `GET /api/traffic` | 分页查询通行明细（源表 `traffic_pass_dev`） | `stationId?` `start?` `end?` `licensePlate?` `page=0` `size=20` |
| `GET /api/stats` | 30 秒窗口聚合 | `stationId?` `start?` `end?` |
| `GET /api/alerts` | 套牌告警列表 | `plate?` `stationId?` `start?` `end?` |

## 1. 通行明细 `GET /api/traffic`

- **参数**
  - `stationId` (int, 可选)：按站点过滤。
  - `start` / `end` (string, 可选)：按 `gcsj` 时间范围过滤，含边界。
  - `licensePlate` (string, 可选)：模糊匹配脱敏车牌 `hphm_mask`，支持任意片段。
  - `page` (int, 默认 0)：0 基页码。
  - `size` (int, 默认 20，1~200)。
- **返回**

  ```json
  {
    "total": 1234,
    "records": [
      {
        "timestamp": "2025-12-17T10:15:30",
        "licensePlate": "浙A12****",
        "stationId": 101,
        "speed": null
      }
    ]
  }
  ```

- **行为**：按 `gcsj` 倒序；`speed` 预留字段目前为 null。

## 2. 窗口统计 `GET /api/stats`

  ```json
  [
    {
      "stationId": 101,
      "windowStart": "2025-12-17T10:15:00",
      "windowEnd": "2025-12-17T10:15:30",
      "totalCount": 320,
      "uniquePlates": 260,
      "avgSpeed": null
    }
  ]
  ```

### GET /api/overview

- 参数：
  - windowMinutes（可选，默认 60，范围 5~1440）：统计窗口长度（向前滚动）
- 返回：
  - totalTraffic：窗口内总流量
  - uniquePlates：窗口内去重车牌数
  - alertCount：窗口内告警数
  - topStations：[{stationId,count}] 窗口内 Top5 站点
  - trafficTrend：[{windowStart,count}] 按分钟聚合的趋势，用于折线/面积图
- 用途：大屏/总览卡片与趋势图

- **行为**：
  1) 优先读取实时表 `stats_realtime`（最多 200 条）。
  2) 若实时表无数据，则从 `traffic_pass_dev` 按 30 秒窗口现算（同样限制 200 条）。
  3) 按 `window_end` 倒序。

## 3. 套牌告警 `GET /api/alerts`

- **参数**
  - `plate` (string, 可选)：模糊匹配 `hphm_mask`。
  - `stationId` (int, 可选)：按 `first_station_id` 过滤。
  - `start` / `end` (string, 可选)：按 `created_at` 时间范围。
- **返回**

  ```json
  [
    {
      "stationId": 102,
      "licensePlate": "浙A12****",
      "timestamp": "2025-12-17T10:20:11",
      "alertType": "Plate Clone"
    }
  ]
  ```

- **行为**：按 `created_at` 倒序，最多 200 条。

## 调用示例（axios）

```js
// 分页明细（前端 1 基 → 后端 0 基）
const page1 = await axios.get('/api/traffic', {
  params: {
    page: 0,
    size: 20,
    stationId: 101,
    licensePlate: 'A12'
  }
});

// 最近窗口统计（按站点）
const stats = await axios.get('/api/stats', { params: { stationId: 101 } });

// 套牌告警（站点+车牌片段）
const alerts = await axios.get('/api/alerts', {
  params: { stationId: 101, plate: 'A12', start: '2025-12-01T00:00:00' }
});
```

## 集成提示

- 若需要前端 1 基页码，发送请求时请减一（示例中已演示）。
- 所有筛选参数可为空；当无数据时返回空列表（或 total=0）。
- Swagger UI：`/swagger-ui.html` 可直接试调与查看 OpenAPI（v3）。
