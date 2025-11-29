# MinIO 对象存储配置指南

## 问题说明

如果遇到以下错误：
```
创建Bucket失败, 请核对配置信息:[Unable to execute HTTP request: Connect to 127.0.0.1:9000 [/127.0.0.1] failed: Connection refused]
```

这表示系统无法连接到 MinIO 服务。请按照本指南进行配置。

## 快速解决方案

### 方案1：使用 Docker 启动 MinIO（推荐）

这是最简单快捷的方式：

```bash
# 启动 MinIO 容器
docker run -d \
  --name minio \
  -p 9000:9000 \
  -p 9001:9001 \
  -e "MINIO_ROOT_USER=ruoyi" \
  -e "MINIO_ROOT_PASSWORD=ruoyi123" \
  -v D:\minio\data:/data \
  -v D:\minio\config:/root/.minio \
  minio/minio server /data --console-address ":9001"
```

**参数说明：**
- `-p 9000:9000`: API 端口（系统连接使用）
- `-p 9001:9001`: 控制台端口（Web管理界面）
- `MINIO_ROOT_USER`: 管理员用户名（默认：ruoyi）
- `MINIO_ROOT_PASSWORD`: 管理员密码（默认：ruoyi123）
- `-v D:\minio\data:/data`: 数据存储目录（Windows路径，请根据实际情况修改）
- `-v D:\minio\config:/root/.minio`: 配置文件目录

**Linux/Mac 路径示例：**
```bash
docker run -d \
  --name minio \
  -p 9000:9000 \
  -p 9001:9001 \
  -e "MINIO_ROOT_USER=ruoyi" \
  -e "MINIO_ROOT_PASSWORD=ruoyi123" \
  -v /opt/minio/data:/data \
  -v /opt/minio/config:/root/.minio \
  minio/minio server /data --console-address ":9001"
```

**验证服务：**
```bash
# 检查容器是否运行
docker ps | grep minio

# 查看日志
docker logs minio
```

**访问控制台：**
- 浏览器访问：http://localhost:9001
- 使用用户名：`ruoyi` 密码：`ruoyi123` 登录

### 方案2：使用 Docker Compose（推荐用于生产环境）

创建 `docker-compose-minio.yml` 文件：

```yaml
version: '3.8'

services:
  minio:
    image: minio/minio:latest
    container_name: minio
    ports:
      - "9000:9000"  # API端口
      - "9001:9001"  # 控制台端口
    environment:
      MINIO_ROOT_USER: ruoyi
      MINIO_ROOT_PASSWORD: ruoyi123
    volumes:
      - ./minio/data:/data
      - ./minio/config:/root/.minio
    command: server /data --console-address ":9001"
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 30s
      timeout: 20s
      retries: 3
```

启动服务：
```bash
docker-compose -f docker-compose-minio.yml up -d
```

### 方案3：本地安装 MinIO

#### Windows

1. 下载 MinIO：https://dl.min.io/server/minio/release/windows-amd64/minio.exe

2. 创建数据目录：
```powershell
mkdir D:\minio\data
```

3. 启动 MinIO：
```powershell
# 设置环境变量
$env:MINIO_ROOT_USER="ruoyi"
$env:MINIO_ROOT_PASSWORD="ruoyi123"

# 启动服务
.\minio.exe server D:\minio\data --console-address ":9001"
```

#### Linux/Mac

1. 下载 MinIO：
```bash
wget https://dl.min.io/server/minio/release/linux-amd64/minio
chmod +x minio
sudo mv minio /usr/local/bin/
```

2. 启动服务：
```bash
export MINIO_ROOT_USER=ruoyi
export MINIO_ROOT_PASSWORD=ruoyi123
minio server /opt/minio/data --console-address ":9001"
```

3. 创建系统服务（可选）：
```bash
sudo nano /etc/systemd/system/minio.service
```

添加以下内容：
```ini
[Unit]
Description=MinIO Object Storage
After=network.target

[Service]
Type=simple
User=minio
Environment="MINIO_ROOT_USER=ruoyi"
Environment="MINIO_ROOT_PASSWORD=ruoyi123"
ExecStart=/usr/local/bin/minio server /opt/minio/data --console-address ":9001"
Restart=always

[Install]
WantedBy=multi-user.target
```

启动服务：
```bash
sudo systemctl daemon-reload
sudo systemctl start minio
sudo systemctl enable minio
```

## 系统配置

### 1. 检查数据库配置

系统默认配置（在 `sys_oss_config` 表中）：
- **configKey**: `minio`
- **endpoint**: `127.0.0.1:9000`
- **accessKey**: `ruoyi`
- **secretKey**: `ruoyi123`
- **bucketName**: `ruoyi`

### 2. 通过系统界面配置

1. 登录系统
2. 导航到：**系统管理 → 对象存储 → 对象存储配置**
3. 找到 `minio` 配置项
4. 检查以下配置：
   - **访问站点(endpoint)**: `127.0.0.1:9000`（如果MinIO在其他机器，请修改为实际IP）
   - **accessKey**: `ruoyi`
   - **secretKey**: `ruoyi123`
   - **桶名称**: `ruoyi`
   - **是否HTTPS**: `否`
   - **桶权限类型**: `public`（推荐）

### 3. 创建存储桶

MinIO 启动后，需要在控制台创建存储桶：

1. 访问 http://localhost:9001
2. 使用 `ruoyi` / `ruoyi123` 登录
3. 点击左侧 "Buckets"
4. 点击 "Create Bucket"
5. 输入桶名称：`ruoyi`
6. 选择访问策略：`Public`（如果需要公开访问）

**注意**：如果桶已存在，系统会自动使用现有桶。

## 验证配置

### 1. 检查 MinIO 服务

```bash
# 检查端口是否监听
# Windows
netstat -an | findstr 9000

# Linux/Mac
netstat -an | grep 9000
# 或
ss -tlnp | grep 9000
```

### 2. 测试连接

使用 curl 测试：
```bash
curl http://127.0.0.1:9000/minio/health/live
```

应该返回：`OK`

### 3. 在系统中测试

1. 登录系统
2. 导航到：**系统管理 → 对象存储 → 对象存储配置**
3. 点击 `minio` 配置的"测试连接"按钮（如果有）
4. 或尝试上传一个文件测试

## 常见问题

### 问题1：连接被拒绝 (Connection refused)

**原因**：MinIO 服务未启动

**解决方案**：
1. 检查 MinIO 是否运行：`docker ps | grep minio` 或检查进程
2. 如果未运行，按照上述方案启动 MinIO
3. 检查防火墙是否阻止了 9000 端口

### 问题2：认证失败

**原因**：用户名或密码不正确

**解决方案**：
1. 检查系统配置中的 accessKey 和 secretKey
2. 检查 MinIO 启动时的环境变量
3. 确保两者一致

### 问题3：无法创建 Bucket

**原因**：
- 权限不足
- Bucket 名称不符合规范（只能包含小写字母、数字和连字符）

**解决方案**：
1. 确保使用正确的 accessKey 和 secretKey
2. 检查 Bucket 名称是否符合规范
3. 在 MinIO 控制台手动创建 Bucket

### 问题4：跨域问题（CORS）

如果前端直接访问 MinIO，可能遇到跨域问题。

**解决方案**：
在 MinIO 控制台设置 CORS：
1. 访问 http://localhost:9001
2. 进入 **Settings → CORS**
3. 添加规则允许前端域名访问

### 问题5：Windows 路径问题

**症状**：Docker 容器无法访问 Windows 路径

**解决方案**：
1. 使用 Docker Desktop 的共享驱动器功能
2. 或使用 Linux 风格的路径：`/c/Users/YourName/minio/data`
3. 或使用 Docker 卷（volume）

## 生产环境建议

1. **使用 HTTPS**：
   - 配置 SSL 证书
   - 在系统配置中设置 `isHttps` 为 `Y`

2. **修改默认密码**：
   - 不要使用默认的 `ruoyi123`
   - 使用强密码

3. **数据备份**：
   - 定期备份 `/data` 目录
   - 配置 MinIO 的版本控制

4. **监控**：
   - 配置健康检查
   - 监控磁盘空间
   - 设置告警

5. **网络配置**：
   - 如果 MinIO 在其他服务器，修改 endpoint 为实际 IP 或域名
   - 配置防火墙规则

## 相关资源

- MinIO 官方文档：https://min.io/docs/
- MinIO Docker Hub：https://hub.docker.com/r/minio/minio
- 系统部署指南：`../部署指南.md`

---

**提示**：如果问题仍未解决，请检查系统日志文件，查看详细的错误信息。

