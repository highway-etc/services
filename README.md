# highway-etc · 后端 API 与前端从零起步指南

这是一份给新用户的“超详细、可直接照抄”的上手手册。目标是在你的电脑上把整套系统（后端 API + 前端看板）跑起来，并且能看到实时写入数据库的数据。

本指南覆盖两部分仓库：

- services：Spring Boot 后端 API（提供明细、统计、告警查询）
- frontend：Vite + React 前端（看板/明细/告警三页）

同时会用到 infra 仓库提供的本地开发环境（Kafka/Flink/MySQL/MyCat 等）。

## 0. 你需要准备什么

- 必备软件
  - Docker Desktop（Windows/macOS/Linux 均可）
  - Git
  -（可选，本地运行后端）JDK 17（Temurin/OpenJDK 均可）
  -（可选，本地运行前端）Node.js 18+ 与 npm / pnpm / yarn 任一
  -（可选，连接 MyCat）MySQL 5.7 客户端（MyCat 1.x 与 MySQL 8 客户端握手不兼容，建议用 5.7 客户端）

- 仓库（建议与本指南一致的相对位置）

  ```text
  highway-etc/
  ├─ infra/        # 本地开发环境：Kafka/Flink/MySQL/MyCat/…（docker-compose）
  ├─ streaming/    # Flink 实时作业
  ├─ services/     # 你当前所在仓库：后端 API
  └─ frontend/     # 前端看板（Vite + React）
  ```

- 端口约定
  - MyCat：8066（逻辑库 highway_etc）
  - Services 后端：8080
  - Frontend（开发模式）：5173（或 5174），生产 nginx 容器默认 8088

## 1. 一分钟快速体验（推荐先跑通）

这条路径的目标是：不安装 JDK/Node，直接用 Docker 跑后端和前端。

1.启动本地数据基础设施（在 highway-etc/infra 目录）

```powershell
docker compose -f docker-compose.dev.yml up -d
```

确认容器均为 Up，且 Flink Web UI 可访问（端口可能是 8081 或你自定义的端口）。

2.验证 MyCat 正常提供逻辑库（MySQL 5.7 客户端）

```powershell
docker run --rm -it --network=infra_etcnet mysql:5.7 `
  mysql -hmycat -P8066 -uetcuser -petcpass `
  -e "show databases; use highway_etc; show tables;"
```

3.（可选）往 Kafka 发送一些模拟数据（在 highway-etc/infra）

- 如果你已在 infra/scripts 目录添加了 send_mock_data.ps1：

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\scripts\send_mock_data.ps1 -N 200
```

4.在 services 目录构建后端镜像并运行

```powershell
cd ..\services
docker build -t etc-services .
docker run --rm -p 8080:8080 --network infra_etcnet --name etc-services etc-services
```

服务启动后将暴露 <http://localhost:8080>

5.在 frontend 目录构建前端镜像并运行（或先用开发模式跑）

- 生产镜像（nginx 托管静态资源）：

```powershell
cd ..\frontend
docker build -t etc-frontend .
docker run --rm -p 8088:80 --network infra_etcnet --name etc-frontend etc-frontend
```

打开 <http://localhost:8088> 即可访问看板。

- 开发模式（热更新，需 Node 环境）见“本地开发模式”一节。

## 2. 本地开发模式（后端/前端分别热更新）

适合日常开发调试，前端通过代理把 /api 转发给本地后端。

### 2.1 运行后端（services）

1.配置数据库连接（默认已内置 MyCat 连接）

- 应用使用 MyCat 逻辑库 highway_etc，地址为 jdbc:mysql://mycat:8066/highway_etc
- 账号：etcuser / 密码：etcpass
- 注意：运行容器时需要加入 infra_etcnet 网络才能解析 mycat 主机名（见下）

2.本地构建与运行（需要 JDK 17）

```powershell
cd services
mvn -B -DskipTests package
java -jar target/services-0.1.0.jar
```

- 访问 Swagger UI: <http://localhost:8080/swagger-ui.html>

3.用 Docker 运行（无需 JDK）

```powershell
docker build -t etc-services .
docker run --rm -p 8080:8080 --network infra_etcnet etc-services
```

4.验证 API（示例）

```powershell
# 明细（分页）
curl "http://localhost:8080/api/traffic?stationId=100&start=2025-12-01T00:00:00Z&end=2025-12-31T23:59:59Z&page=1&size=20"

# 窗口统计
curl "http://localhost:8080/api/stats?stationId=100&start=2025-12-01T00:00:00Z&end=2025-12-31T23:59:59Z"

# 告警
curl "http://localhost:8080/api/alerts?plate=%E6%B5%99A12****&start=2025-12-01T00:00:00Z&end=2025-12-31T23:59:59Z"
```

### 2.2 运行前端（frontend）

1.安装依赖并启动开发服务器

```powershell
cd ..\frontend
npm install
# 开发模式（Vite）
npm run dev -- --host --port 5173
```

2.配置 API 代理

- 在 frontend/vite.config.ts（或 vite.config.js）增加：

```ts
server: {
  host: true,
  port: 5173,
  proxy: {
    '/api': 'http://localhost:8080'
  }
}
```

- 浏览器打开 <http://localhost:5173>，进入 Dashboard/Traffic/Alerts 页面查看图表与表格数据。

## 3. 数据从哪里来，怎么确认“真的在动”

- 实时链路：Kafka → Flink → MyCat（分库分表）
  - Kafka 主题：etc_traffic（6 分区）
  - Flink 作业：TrafficStreamingJob（明细+30s 统计）、PlateCloneDetectionJob（套牌告警）
  - MyCat 路由：按 station_id 取模 2 → etc_0（偶数）、etc_1（奇数）

- 快速自检命令（在 highway-etc/infra）

```powershell
# 统计两分片的明细条数
docker exec -it mysql mysql -uroot -prootpass -e "select count(*) c0 from etc_0.traffic_pass_dev; select count(*) c1 from etc_1.traffic_pass_dev;"

# 查最近窗口统计（各分片）
docker exec -it mysql mysql -uroot -prootpass -e "select station_id,window_start,window_end,cnt from etc_0.stats_realtime order by id desc limit 5; select station_id,window_start,window_end,cnt from etc_1.stats_realtime order by id desc limit 5;"
```

- 发送测试数据（在 highway-etc/infra）

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\scripts\send_mock_data.ps1 -N 200
```

- 看到的数据
  - 后端：/api/stats 折线与饼图会动；/api/traffic 明细分页可查
  - 数据库：etc_0/ etc_1 的 traffic_pass_dev 有新增；stats_realtime 每 30s 有新窗口

## 4. API 说明（最小可用集）

- GET /api/traffic
  - 参数：stationId（可选）、start（ISO8601）、end（ISO8601）、page、size
  - 返回：分页对象（total/records），records 为 traffic_pass_dev 行
- GET /api/stats
  - 参数：stationId（可选）、start、end
  - 返回：每个时间窗口的聚合（station_id、window_start、window_end、cnt、by_dir、by_type）
- GET /api/alerts
  - 参数：plate（可选，脱敏或部分匹配）、start、end
  - 返回：告警列表（hphm_mask、first_station_id、second_station_id、speed_kmh、confidence、created_at）

打开 Swagger 文档交互调试：<http://localhost:8080/swagger-ui.html>

## 5. 常见问题排查（FAQ）

- Q: 通过 MyCat 连接失败（Access denied / 握手失败）
  - A: 确认使用 MySQL 5.7 客户端连接 MyCat 1.x；账号 etcuser / 等于 etcpass；容器挂载的 server.xml/schema.xml/rule.xml 已生效（重启过 mycat）。
- Q: 后端容器连不上 mycat 主机名
  - A: 启动容器时加 `--network infra_etcnet`；或在 application 配置中改用宿主机 IP（不推荐）。
- Q: 前端跨域失败（CORS）
  - A: 开发模式使用 Vite 代理 `/api` → `http://localhost:8080`；生产镜像直接走同源（nginx 与后端端口不同域时请在 nginx 做反向代理）。
- Q: Flink Job 重启/失败
  - A: 先看 JM/TM 日志（docker logs -n 200 flink-jobmanager/flink-taskmanager）；常见是 JDBC 连接失败或表结构不匹配。
- Q: 如何优雅关机不丢状态
  - A: 使用 infra/scripts/savepoint_and_stop.ps1 保存点并优雅停止；下次用 resume_from_last_savepoint.ps1 恢复。

## 6. 一键脚本（可选）

在 highway-etc/infra/scripts 下已经提供了 Windows PowerShell 版本脚本：

- savepoint_and_stop.ps1：为所有运行中的 Flink 作业创建 savepoint 并优雅停止
- resume_from_last_savepoint.ps1：从最新 savepoint 恢复 TrafficStreamingJob / PlateCloneDetectionJob
- send_mock_data.ps1：向 Kafka 的 etc_traffic 主题推送 N 条标准 JSON

先允许当前会话执行脚本：

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

示例：

```powershell
# 保存点并停止
..\infra\scripts\savepoint_and_stop.ps1

# 从保存点恢复
..\infra\scripts\resume_from_last_savepoint.ps1

# 发送 500 条测试数据
..\infra\scripts\send_mock_data.ps1 -N 500
```

## 7. 目录结构与构建

- services（后端）

  ```text
  services/
  ├─ src/main/java/...        # 控制器/服务/DAO
  ├─ src/main/resources/      # 配置
  ├─ Dockerfile               # 多阶段构建
  └─ pom.xml
  ```

  构建/运行：

  ```powershell
  mvn -B -DskipTests package
  java -jar target/services-0.1.0.jar
  # 或
  docker build -t etc-services .
  docker run --rm -p 8080:8080 --network infra_etcnet etc-services
  ```

- frontend（前端）

  ```text
  frontend/
  ├─ src/                     # 页面/组件
  ├─ vite.config.ts
  ├─ Dockerfile               # nginx 托管静态资源
  └─ package.json
  ```

  开发/运行：

  ```powershell
  npm install
  npm run dev -- --host --port 5173
  # 或
  docker build -t etc-frontend .
  docker run --rm -p 8088:80 --network infra_etcnet etc-frontend
  ```

## 8. 验收清单（你可以用这几条确认“OK 可演示”）

- [ ] <http://localhost:8080/swagger-ui.html> 可打开，三条 API 均可返回数据
- [ ] 前端看板页（Dashboard）能展示折线与饼图，数值随数据变化
- [ ] 明细页（Traffic）分页可用，可按站点/时间过滤
- [ ] 告警页（Alerts）能看到列表与详情（有回放同车牌数据时）
- [ ] MyCat 分库分表生效：偶数 station_id 在 etc_0，奇数在 etc_1
- [ ] 关机前能执行 savepoint_and_stop.ps1；开机后能 resume_from_last_savepoint.ps1 成功恢复

## 9. 进一步的建议（之后再做）

- 接入 Prometheus + Grafana，导入 Flink/Kafka/MySQL Dashboard，做系统可观测性
- 前端添加大屏模式（ECharts 全屏轮播）
- Services 接入 ClickHouse/Trino（OLAP）以支撑大时间范围统计
- batch-ml：离线 ETL（raw/clean/feature）与简单模型的训练/评估

---

有任何一步卡住，带上你执行的命令和控制台输出发 Issue，我们会根据日志帮你快速定位。
