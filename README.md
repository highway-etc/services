# services (Spring Boot API)

- api/: Data API
- alert/: 告警服务
- .github/workflows/java-ci.yml: 构建与镜像推送

## 功能

- /api/traffic?stationId&start&end&page&size：分页查询 traffic_pass_dev
- /api/stats?stationId&start&end：查询 stats_realtime（含 by_dir/by_type JSON）
- /api/alerts?plate&start&end：查询 alert_plate_clone（车牌脱敏字段）
- Swagger UI: <http://localhost:8080/swagger-ui.html>

## 目前完成的工作

- Spring Boot 数据 API（traffic/stats/alerts）接入 MyCat 数据源，提供分页和时间区间查询，包含 Swagger UI。
- 采用多阶段 Docker 构建（maven -> temurin 17），默认暴露 8080。
- 前端新增 Vite + React 看板（Dashboard/Traffic/Alerts），使用 axios + echarts + dayjs 拉取以上 API 并展示图表/表格。

## 用户操作

```powershell
# 后端本地运行（在 services 目录，确保 MyCat 正在运行且能访问 jdbc:mysql://mycat:8066/highway_etc）
mvn -B -DskipTests package
java -jar target/services-0.1.0.jar

# 或 Docker 运行（需在 infra_etcnet 网络以访问 mycat 服务）
docker build -t etc-services .
docker run --rm -p 8080:8080 --network infra_etcnet etc-services

# 前端本地运行（需将 /api 代理到后端 8080）
cd ../frontend
npm install
# 在 frontend/vite.config.js 的 server 下补充代理:
#   proxy: { '/api': 'http://localhost:8080' }
npm run dev -- --host --port 5173
```

## 数据源

- MyCat: jdbc:mysql://mycat:8066/highway_etc（etcuser/etcpass），驱动 com.mysql.jdbc.Driver (5.1.49)

## 验收方式

- 后端 API：
  - `curl "http://localhost:8080/api/traffic?size=1"` 应返回包含 total、records 的 JSON。
  - `curl "http://localhost:8080/api/stats"` 应返回窗口统计 JSON，字段含 windowStart/windowEnd/totalCount/uniquePlates/avgSpeed。
  - `curl "http://localhost:8080/api/alerts"` 应返回告警列表，含 stationId、licensePlate、timestamp、alertType。
  - 打开 <http://localhost:8080/swagger-ui.html> 查看接口文档。
- 前端：启动 Vite 后访问 <http://localhost:5173/dashboard>，应显示折线图与最新窗口表；<http://localhost:5173/traffic> 支持车牌过滤/分页；<http://localhost:5173/alerts> 支持按站点过滤。
- 环境前置：MyCat 已加载 highway_etc 数据，容器在 infra_etcnet 网络内（或本地 host 解析 mycat）。
