# Deployment Plan: EC2 t3.micro Ubuntu 24.04 Production

## 1. Mục tiêu

Deploy project AutoLift Loyalty lên AWS EC2 theo mô hình một server nhỏ phục vụ demo/portfolio production.

Thông tin môi trường:

```text
EC2 instance type: t3.micro
Operating system: Ubuntu Server 24.04 LTS
Default SSH user: ubuntu
Region: ap-southeast-1
Application: Docker container
PostgreSQL: standalone trên host, không chạy Docker
Redis: standalone trên host, không chạy Docker
Deploy trigger: push main, tag v*, hoặc manual workflow_dispatch
```

Ghi chú quan trọng:

```text
Không dùng ec2-user vì instance đang là Ubuntu.
Không dùng yum vì Ubuntu dùng apt.
Không mở PostgreSQL 5432 hoặc Redis 6379 ra Internet.
Không cài pgAdmin, RedisInsight, Portainer trên t3.micro vì RAM thấp.
Quản lý PostgreSQL/Redis bằng CLI, systemd và log.
```

---

## 2. Sơ đồ triển khai

```text
GitHub Repository
        |
        | push main / tag v* / manual dispatch
        v
GitHub Actions
        |
        | SSH bằng EC2_SSH_KEY
        v
EC2 t3.micro Ubuntu 24.04
        |
        |-- PostgreSQL standalone: 127.0.0.1:5432
        |-- Redis standalone:      127.0.0.1:6379
        |-- Docker Engine
        |-- AutoLift app container: port 8080
```

---

## 3. AWS Security Group

Security Group nên mở tối thiểu như sau:

```text
SSH 22      Source: My IP
HTTP 80     Source: 0.0.0.0/0, nếu có dùng Nginx/domain
HTTPS 443   Source: 0.0.0.0/0, nếu có dùng SSL
TCP 8080    Source: My IP khi test; chỉ mở public nếu thật sự cần demo API trực tiếp
```

Không mở public:

```text
PostgreSQL 5432
Redis 6379
```

Lý do: PostgreSQL và Redis chỉ phục vụ app trên cùng EC2, nên không cần expose ra ngoài.

---

## 4. SSH vào EC2

Trên máy local:

```bash
cd ~/.ssh/aws
chmod 400 autolift-key.pem
ssh -i autolift-key.pem ubuntu@ec2-18-140-56-38.ap-southeast-1.compute.amazonaws.com
```

Nếu đổi instance hoặc public DNS thay đổi, cập nhật lại host tương ứng trong GitHub secret `EC2_PRODUCTION_HOST`.

---

## 5. Setup Ubuntu base

Chạy trên EC2:

```bash
sudo apt update
sudo apt upgrade -y

sudo timedatectl set-timezone Asia/Ho_Chi_Minh

sudo apt install -y \
  curl \
  wget \
  git \
  unzip \
  ca-certificates \
  gnupg \
  lsb-release \
  htop
```

Kiểm tra OS:

```bash
lsb_release -a
uname -a
```

---

## 6. Tạo swap cho t3.micro

`t3.micro` chỉ có RAM thấp, nên thêm swap để tránh lỗi thiếu RAM khi chạy Spring Boot + PostgreSQL + Redis.

Chạy trên EC2:

```bash
sudo fallocate -l 1G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

free -h
```

Nếu lệnh `fallocate` lỗi, dùng cách thay thế:

```bash
sudo dd if=/dev/zero of=/swapfile bs=1M count=1024
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

free -h
```

---

## 7. Cài Docker Engine và Docker Compose plugin

Chạy trên EC2:

```bash
sudo apt remove -y docker.io docker-compose docker-compose-v2 docker-doc podman-docker containerd runc || true

sudo apt update
sudo apt install -y ca-certificates curl

sudo install -m 0755 -d /etc/apt/keyrings

sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg \
  -o /etc/apt/keyrings/docker.asc

sudo chmod a+r /etc/apt/keyrings/docker.asc

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" \
  | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt update

sudo apt install -y \
  docker-ce \
  docker-ce-cli \
  containerd.io \
  docker-buildx-plugin \
  docker-compose-plugin

sudo systemctl enable docker
sudo systemctl start docker

sudo usermod -aG docker ubuntu
```

Logout rồi SSH lại để group `docker` có hiệu lực:

```bash
exit
ssh -i ~/.ssh/aws/autolift-key.pem ubuntu@ec2-18-140-56-38.ap-southeast-1.compute.amazonaws.com
```

Kiểm tra Docker:

```bash
docker --version
docker compose version
docker ps
```

---

## 8. Cài PostgreSQL standalone

Chạy trên EC2:

```bash
sudo apt update
sudo apt install -y postgresql postgresql-contrib

sudo systemctl enable postgresql
sudo systemctl start postgresql

psql --version
sudo systemctl status postgresql --no-pager
```

Tạo database, user và schema:

```bash
sudo -u postgres psql
```

Trong PostgreSQL shell, chạy:

```sql
CREATE DATABASE autolift_db;
CREATE USER autolift WITH ENCRYPTED PASSWORD 'autolift_secret';
GRANT ALL PRIVILEGES ON DATABASE autolift_db TO autolift;

\c autolift_db

GRANT ALL ON SCHEMA public TO autolift;
ALTER SCHEMA public OWNER TO autolift;

CREATE SCHEMA IF NOT EXISTS sandbox AUTHORIZATION autolift;
GRANT ALL ON SCHEMA sandbox TO autolift;

\q
```

Kiểm tra kết nối bằng user app:

```bash
psql -h localhost -U autolift -d autolift_db
```

Nhập password:

```text
autolift_secret
```

Trong PostgreSQL shell, kiểm tra schema:

```sql
\dn
\q
```

Không chỉnh PostgreSQL thành public.

Không sửa:

```text
listen_addresses = '*'
```

Không thêm:

```text
host all all 0.0.0.0/0 scram-sha-256
```

Với mô hình một EC2, app chỉ cần kết nối PostgreSQL qua local host.

---

## 9. Cài Redis standalone

Chạy trên EC2:

```bash
sudo apt update
sudo apt install -y redis-server

sudo systemctl enable redis-server
sudo systemctl start redis-server

redis-server --version
redis-cli ping
```

Nếu kết quả là:

```text
PONG
```

thì Redis chạy ổn.

Kiểm tra Redis chỉ bind local:

```bash
sudo grep -E "^(bind|protected-mode)" /etc/redis/redis.conf
```

Nên giữ cấu hình dạng:

```text
bind 127.0.0.1 -::1
protected-mode yes
```

Nếu có chỉnh file Redis config thì restart:

```bash
sudo systemctl restart redis-server
redis-cli ping
```

Không mở Redis port 6379 trong Security Group.

---

## 10. Tạo thư mục deploy

Chạy trên EC2:

```bash
sudo mkdir -p /opt/autolift
sudo mkdir -p /var/log/autolift

sudo chown -R ubuntu:ubuntu /opt/autolift
sudo chown -R ubuntu:ubuntu /var/log/autolift
```

Không cần tự tạo:

```text
/var/log/postgresql
/var/log/redis
```

PostgreSQL và Redis package trên Ubuntu tự quản lý log/service theo cấu hình mặc định.

---

## 11. GitHub Secrets

Vào GitHub repository:

```text
Settings > Secrets and variables > Actions > New repository secret
```

Thêm từng secret một:

| Name | Secret value |
|---|---|
| `EC2_PRODUCTION_HOST` | `ec2-18-140-56-38.ap-southeast-1.compute.amazonaws.com` |
| `EC2_PRODUCTION_USER` | `ubuntu` |
| `EC2_SSH_KEY` | Toàn bộ nội dung file `autolift-key.pem` |
| `POSTGRES_DB` | `autolift_db` |
| `POSTGRES_USER` | `autolift` |
| `POSTGRES_PASSWORD` | `autolift_secret` |
| `JWT_SECRET` | Chuỗi random tạo bằng `openssl rand -base64 64` |

Tạo JWT secret trên máy local:

```bash
openssl rand -base64 64
```

Lấy nội dung private key để paste vào `EC2_SSH_KEY`:

```bash
cat ~/.ssh/aws/autolift-key.pem
```

Copy đầy đủ từ dòng đầu đến dòng cuối:

```text
-----BEGIN ...
...
-----END ...
```

Không commit file `.pem` vào GitHub repository.

---

## 12. File `.env.production` trong repository

File `.env.production` chỉ là template, không chứa secret thật.

```env
SPRING_PROFILES_ACTIVE=prod

SERVER_PORT=8080

POSTGRES_HOST=127.0.0.1
POSTGRES_PORT=5432
POSTGRES_DB=autolift_db
POSTGRES_USER=autolift
POSTGRES_PASSWORD=change_me

REDIS_HOST=127.0.0.1
REDIS_PORT=6379

JWT_SECRET=change_me
```

Khi deploy, GitHub Actions sẽ tạo file `.env` thật trên EC2 từ GitHub Secrets.

---

## 13. File `docker-compose.prod.yml`

Vì PostgreSQL và Redis chạy standalone trên host, compose chỉ chạy app.

```yaml
services:
  autolift-app:
    image: autolift-loyalty:latest
    container_name: autolift-app
    network_mode: host
    env_file:
      - .env
    restart: unless-stopped
    volumes:
      - /var/log/autolift:/app/logs
```

Với `network_mode: host`, app container có thể kết nối PostgreSQL/Redis trên host qua:

```text
127.0.0.1:5432
127.0.0.1:6379
```

Nếu sau này tách PostgreSQL sang RDS hoặc Redis sang ElastiCache, bỏ `network_mode: host` và dùng hostname riêng.

---

## 14. File `.github/workflows/deploy.yml`

```yaml
name: Deploy Production

on:
  push:
    branches:
      - main
    tags:
      - "v*"
  workflow_dispatch:

jobs:
  deploy:
    name: Deploy to EC2 Ubuntu
    runs-on: ubuntu-latest

    steps:
      - name: Deploy via SSH
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.EC2_PRODUCTION_HOST }}
          username: ${{ secrets.EC2_PRODUCTION_USER }}
          key: ${{ secrets.EC2_SSH_KEY }}
          script: |
            set -e

            echo "==> Go to deploy directory"
            mkdir -p /opt/autolift
            cd /opt/autolift

            echo "==> Clone or update repository"
            if [ ! -d ".git" ]; then
              git clone https://github.com/LqTan/autolift-loyalty.git .
            else
              git fetch origin
              git reset --hard origin/main
              git clean -fd
            fi

            echo "==> Create production .env"
            cat > .env <<EOF
            SPRING_PROFILES_ACTIVE=prod
            SERVER_PORT=8080
            POSTGRES_HOST=127.0.0.1
            POSTGRES_PORT=5432
            POSTGRES_DB=${{ secrets.POSTGRES_DB }}
            POSTGRES_USER=${{ secrets.POSTGRES_USER }}
            POSTGRES_PASSWORD=${{ secrets.POSTGRES_PASSWORD }}
            REDIS_HOST=127.0.0.1
            REDIS_PORT=6379
            JWT_SECRET=${{ secrets.JWT_SECRET }}
            EOF

            echo "==> Build Docker image"
            docker build -t autolift-loyalty:latest .

            echo "==> Restart application"
            docker compose -f docker-compose.prod.yml up -d

            echo "==> Show containers"
            docker ps

            echo "==> Wait for app startup"
            sleep 20

            echo "==> Health check"
            curl -f http://localhost:8080/actuator/health
```

Nếu repository name khác, sửa dòng:

```bash
git clone https://github.com/LqTan/autolift-loyalty.git .
```

thành repo đúng của bạn.

---

## 15. Deploy thủ công lần đầu để kiểm tra

Chạy trên EC2:

```bash
cd /opt/autolift

git clone https://github.com/LqTan/autolift-loyalty.git . || git pull origin main
```

Tạo `.env` thủ công:

```bash
nano .env
```

Nội dung:

```env
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

POSTGRES_HOST=127.0.0.1
POSTGRES_PORT=5432
POSTGRES_DB=autolift_db
POSTGRES_USER=autolift
POSTGRES_PASSWORD=autolift_secret

REDIS_HOST=127.0.0.1
REDIS_PORT=6379

JWT_SECRET=paste_jwt_secret_here
```

Build và chạy app:

```bash
docker build -t autolift-loyalty:latest .
docker compose -f docker-compose.prod.yml up -d

docker ps
docker logs -f autolift-app
```

Kiểm tra health local:

```bash
curl http://localhost:8080/actuator/health
```

Nếu Security Group đã mở port 8080, kiểm tra từ máy local:

```bash
curl http://ec2-18-140-56-38.ap-southeast-1.compute.amazonaws.com:8080/actuator/health
```

---

## 16. Quản lý service hằng ngày

Kiểm tra PostgreSQL:

```bash
sudo systemctl status postgresql --no-pager
sudo -u postgres psql -c "\l"
psql -h localhost -U autolift -d autolift_db
```

Kiểm tra Redis:

```bash
sudo systemctl status redis-server --no-pager
redis-cli ping
```

Kiểm tra Docker app:

```bash
docker ps
docker logs -f autolift-app
curl http://localhost:8080/actuator/health
```

Kiểm tra tài nguyên:

```bash
free -h
df -h
docker stats
```

Restart app:

```bash
cd /opt/autolift
docker compose -f docker-compose.prod.yml restart
```

Stop app:

```bash
cd /opt/autolift
docker compose -f docker-compose.prod.yml down
```

---

## 17. Optional: SSH tunnel để quản lý PostgreSQL bằng DBeaver/pgAdmin local

Không cài DBeaver/pgAdmin trên EC2. Nếu muốn xem database bằng GUI, cài DBeaver hoặc pgAdmin trên máy cá nhân rồi dùng SSH tunnel.

Tạo tunnel từ máy local:

```bash
ssh -i ~/.ssh/aws/autolift-key.pem \
  -L 5433:127.0.0.1:5432 \
  ubuntu@ec2-18-140-56-38.ap-southeast-1.compute.amazonaws.com
```

Sau đó trên DBeaver/pgAdmin local, connect:

```text
Host: localhost
Port: 5433
Database: autolift_db
User: autolift
Password: autolift_secret
```

---

## 18. Checklist

```text
[ ] EC2 đang chạy Ubuntu 24.04
[ ] SSH được bằng user ubuntu
[ ] Security Group chỉ mở SSH từ My IP
[ ] Security Group không mở PostgreSQL 5432 public
[ ] Security Group không mở Redis 6379 public
[ ] Swap 1G đã bật
[ ] Docker Engine đã cài
[ ] Docker Compose plugin đã cài
[ ] PostgreSQL service đang running
[ ] Database autolift_db đã tạo
[ ] User autolift đã tạo
[ ] Schema sandbox đã tạo nếu app dùng default_schema=sandbox
[ ] Redis service đang running
[ ] redis-cli ping trả về PONG
[ ] /opt/autolift thuộc user ubuntu
[ ] GitHub Secrets đã thêm đủ
[ ] EC2_PRODUCTION_USER là ubuntu
[ ] EC2_SSH_KEY là nội dung private key thật
[ ] docker-compose.prod.yml chỉ chạy app
[ ] App kết nối PostgreSQL/Redis qua 127.0.0.1
[ ] Deploy thủ công lần đầu chạy được
[ ] GitHub Actions deploy.yml chạy thành công
[ ] /actuator/health trả về UP
```

---

## 19. Các lỗi thường gặp

### SSH báo Permission denied publickey

Kiểm tra:

```text
Đúng user chưa: Ubuntu phải là ubuntu.
Đúng private key chưa.
Key pair của instance có đúng autolift-key không.
File pem đã chmod 400 chưa.
GitHub secret EC2_PRODUCTION_USER có phải ubuntu không.
```

### Docker báo permission denied

Chạy:

```bash
sudo usermod -aG docker ubuntu
exit
```

SSH lại rồi thử:

```bash
docker ps
```

### App không kết nối được PostgreSQL

Kiểm tra:

```bash
sudo systemctl status postgresql --no-pager
psql -h localhost -U autolift -d autolift_db
docker logs -f autolift-app
```

Kiểm tra `.env`:

```bash
cat /opt/autolift/.env
```

Các giá trị nên là:

```text
POSTGRES_HOST=127.0.0.1
POSTGRES_PORT=5432
POSTGRES_DB=autolift_db
POSTGRES_USER=autolift
```

### App không kết nối được Redis

Kiểm tra:

```bash
sudo systemctl status redis-server --no-pager
redis-cli ping
docker logs -f autolift-app
```

Các giá trị nên là:

```text
REDIS_HOST=127.0.0.1
REDIS_PORT=6379
```

### Health check fail

Kiểm tra app có bật actuator chưa. Nếu chưa có actuator endpoint, đổi health check thành endpoint thật của app hoặc bỏ bước `curl -f`.

```bash
docker logs -f autolift-app
curl -v http://localhost:8080/actuator/health
```

---

## 20. Kết luận

Plan này dùng đúng cho EC2 `t3.micro` chạy Ubuntu 24.04:

```text
ubuntu thay cho ec2-user
apt thay cho yum
PostgreSQL standalone bằng systemd
Redis standalone bằng systemd
App chạy Docker container
DB và Redis chỉ listen local
GitHub Actions SSH vào EC2 để deploy
Không cài GUI management tool trên EC2
```