# services：Spring Boot 后端 API 说明书

提供明细查询、窗口统计、套牌告警、大屏总览等 REST API，默认对接 infra 提供的 MyCat 分库分表数据库。本文档面向新手，按最少步骤跑通 + 常见问题排查组织。

## 技术栈与依赖

- Spring Boot 3 + Spring Web + Spring Data
- MySQL Connector 5.1.49（兼容 MyCat 1.x）
- Swagger（springdoc-openapi），默认 <http://localhost:8080/swagger-ui.html>
- 数据源：jdbc:mysql://mycat:8066/highway_etc（账号 etcuser/etcpass，见 application.yml）

## 目录速览

- src/main/java/com/highway/etc/api：控制器（Traffic/Stats/Alerts/Overview）
- src/main/resources/application.yml：数据源与 Swagger 配置
- Dockerfile：多阶段构建，产出轻量镜像

## 快速跑通（最简 3 步）

1) 启动基础设施：在 infra 目录执行 `docker compose -f docker-compose.dev.yml up -d`。
2) 打包运行（本机有 JDK17）：

```powershell
mvn -B -DskipTests package
java -jar target/services-0.1.0.jar
```

或容器运行（无需 JDK）：

```powershell
docker build -t etc-services .
docker run --rm -p 8080:8080 --network infra_etcnet etc-services
```

3) 打开 <http://localhost:8080/swagger-ui.html> 验证接口可用。

## API 摘要

- GET /api/traffic：分页明细，参数 stationId、start、end、licensePlate、page（0 基）、size。
- GET /api/stats：窗口聚合，参数 stationId、start、end。
- GET /api/alerts：套牌告警，参数 plate、stationId、start、end。
- GET /api/overview：大屏总览，参数 windowMinutes（默认 60，范围 5~1440）。
返回字段详见 docs/api.md 或 Swagger。

## 与实时链路的关系

- 数据源来自 streaming 作业写入的 traffic_pass_dev（明细）、stats_realtime（30s 窗口）、alert（套牌告警）。
- MyCat 按 station_id 分片：偶数 -> etc_0，奇数 -> etc_1。请确保 infra 中的 MyCat 配置已加载。

## 本地调试贴士

- 代理网络：容器运行时务必附加 `--network infra_etcnet`，否则无法解析 mycat/kafka。
- SQL 性能：确认 traffic_pass_dev 上有 `(station_id,gcsj)` 联合索引，stats_realtime 有 `window_end` 索引。
- 时间格式：start/end 传 ISO8601 或 `yyyy-MM-dd HH:mm:ss`，内部按字符串传给仓储层解析。

## 常见问题

- 通过 MyCat 连接被拒：请使用 MySQL 5.7 客户端；检查账号密码等于 etcuser/etcpass；确认 MyCat 容器已重启加载配置。
- 接口总是 0 行：检查 streaming 是否在写库；用 `select count(*) from etc_0.traffic_pass_dev` / etc_1 验证；再查 stats_realtime 是否有新窗口。
- 容器内连不上 mycat：确保 --network infra_etcnet；或临时把数据源改为宿主机 IP（不推荐）。

## 构建与部署

- 本地：`mvn -B -DskipTests package && java -jar target/services-0.1.0.jar`
- 镜像：`docker build -t etc-services .` 后 `docker run --rm -p 8080:8080 --network infra_etcnet etc-services`

## 验收清单

- Swagger 可访问，/api/traffic、/api/stats、/api/alerts、/api/overview 均能返回数据。
- MyCat 分片生效（偶数在 etc_0，奇数在 etc_1）。
- 前端（或 curl）访问 /api/overview 时返回非零指标。
