# Dağıtım Rehberi

Bu dokümantasyon, Öğrenci Yönetim Sistemi'ni farklı ortamlarda dağıtma süreçlerini detaylandırır.

## 📋 İçindekiler

- [Ortam Seçimi](#ortam-seçimi)
- [Docker ile Dağıtım](#docker-ile-dağıtım)
- [Cloud Dağıtımı](#cloud-dağıtımı)
- [Production Ortamı](#production-ortamı)
- [Monitoring ve Logging](#monitoring-ve-logging)
- [Backup ve Recovery](#backup-ve-recovery)
- [Güvenlik Kontrolleri](#güvenlik-kontrolü)

## 🎯 Ortam Seçimi

### Development Ortamı

- **Amaç:** Geliştirme ve test
- **Özellikler:** H2 veritabanı, debug modu, hot reload
- **Port:** 8080 (Backend), 3000 (Frontend), 8888 (Config)

### Staging Ortamı

- **Amaç:** Production öncesi test
- **Özellikler:** PostgreSQL, production benzeri ayarlar
- **Port:** 8080 (Backend), 3000 (Frontend), 8888 (Config)

### Production Ortamı

- **Amaç:** Canlı sistem
- **Özellikler:** PostgreSQL cluster, SSL, monitoring, backup
- **Port:** 443 (HTTPS), 80 (HTTP redirect)

## 🐳 Docker ile Dağıtım

### Docker Compose Yapılandırması

#### docker-compose.yml

```yaml
version: "3.8"

services:
  # PostgreSQL Database
  postgres:
    image: postgres:15-alpine
    container_name: student_db
    environment:
      POSTGRES_DB: student_management
      POSTGRES_USER: student_user
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "5432:5432"
    networks:
      - student_network
    restart: unless-stopped

  # Config Server
  config-server:
    build:
      context: ./config-server
      dockerfile: Dockerfile
    container_name: config_server
    environment:
      SPRING_PROFILES_ACTIVE: docker
    ports:
      - "8888:8888"
    networks:
      - student_network
    restart: unless-stopped
    depends_on:
      - postgres

  # Backend Application
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: student_backend
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/student_management
      SPRING_DATASOURCE_USERNAME: student_user
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      CONFIG_SERVER_URL: http://config-server:8888
    ports:
      - "8080:8080"
    networks:
      - student_network
    restart: unless-stopped
    depends_on:
      - postgres
      - config-server

  # Frontend Application
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: student_frontend
    environment:
      REACT_APP_API_URL: http://localhost:8080
    ports:
      - "3000:80"
    networks:
      - student_network
    restart: unless-stopped
    depends_on:
      - backend

  # Nginx Reverse Proxy
  nginx:
    image: nginx:alpine
    container_name: student_nginx
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
    ports:
      - "80:80"
      - "443:443"
    networks:
      - student_network
    restart: unless-stopped
    depends_on:
      - backend
      - frontend

volumes:
  postgres_data:

networks:
  student_network:
    driver: bridge
```

### Backend Dockerfile

#### backend/Dockerfile

```dockerfile
# Multi-stage build
FROM maven:3.9.5-openjdk-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:21-jdk-slim

# Create app user
RUN addgroup --system app && adduser --system --ingroup app app

WORKDIR /app

# Copy the built JAR
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Change ownership
RUN chown app:app app.jar

# Switch to app user
USER app

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Frontend Dockerfile

#### frontend/Dockerfile

```dockerfile
# Build stage
FROM node:18-alpine AS build

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy source code
COPY . .

# Build the application
RUN npm run build

# Production stage
FROM nginx:alpine

# Copy built files
COPY --from=build /app/build /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost/ || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
```

### Nginx Konfigürasyonu

#### nginx.conf

```nginx
upstream backend {
    server backend:8080;
}

upstream frontend {
    server frontend:80;
}

server {
    listen 80;
    server_name localhost;

    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name localhost;

    # SSL Configuration
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;

    # Security headers
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    # API routes
    location /api/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # CORS headers
        add_header Access-Control-Allow-Origin *;
        add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS";
        add_header Access-Control-Allow-Headers "Authorization, Content-Type";
    }

    # Frontend routes
    location / {
        proxy_pass http://frontend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Health check
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
}
```

### Docker Compose ile Çalıştırma

```bash
# Environment variables
export DB_PASSWORD=your_secure_password

# Build and start services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f backend

# Stop services
docker-compose down
```

## ☁️ Cloud Dağıtımı

### AWS Deployment

#### AWS ECS (Elastic Container Service)

##### task-definition.json

```json
{
  "family": "student-management",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "1024",
  "memory": "2048",
  "executionRoleArn": "arn:aws:iam::123456789012:role/ecsTaskExecutionRole",
  "taskRoleArn": "arn:aws:iam::123456789012:role/ecsTaskRole",
  "containerDefinitions": [
    {
      "name": "backend",
      "image": "your-registry/student-backend:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "aws"
        }
      ],
      "secrets": [
        {
          "name": "DB_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:region:account:secret:db-password"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/student-management",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "backend"
        }
      }
    }
  ]
}
```

#### AWS RDS PostgreSQL

```bash
# Create RDS instance
aws rds create-db-instance \
  --db-instance-identifier student-management-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --master-username admin \
  --master-user-password your-password \
  --allocated-storage 20 \
  --vpc-security-group-ids sg-12345678 \
  --db-subnet-group-name default
```

### Google Cloud Platform

#### Cloud Run Deployment

```bash
# Build and push image
gcloud builds submit --tag gcr.io/PROJECT_ID/student-backend

# Deploy to Cloud Run
gcloud run deploy student-backend \
  --image gcr.io/PROJECT_ID/student-backend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars SPRING_PROFILES_ACTIVE=gcp
```

### Azure

#### Azure Container Instances

```bash
# Deploy to ACI
az container create \
  --resource-group myResourceGroup \
  --name student-backend \
  --image your-registry/student-backend:latest \
  --dns-name-label student-backend \
  --ports 8080 \
  --environment-variables SPRING_PROFILES_ACTIVE=azure
```

## 🏭 Production Ortamı

### Production Checklist

#### Güvenlik

- [ ] SSL/TLS sertifikası yüklendi
- [ ] Firewall kuralları yapılandırıldı
- [ ] JWT secret güvenli şekilde saklanıyor
- [ ] Database şifreleme aktif
- [ ] Rate limiting yapılandırıldı

#### Performance

- [ ] Database indexleri oluşturuldu
- [ ] Connection pooling yapılandırıldı
- [ ] Caching stratejisi belirlendi
- [ ] Load balancer yapılandırıldı
- [ ] CDN yapılandırıldı

#### Monitoring

- [ ] Application monitoring kuruldu
- [ ] Database monitoring aktif
- [ ] Log aggregation yapılandırıldı
- [ ] Alerting kuralları belirlendi
- [ ] Health check endpoint'leri aktif

### Production Environment Variables

#### .env.production

```bash
# Database
DB_HOST=your-db-host
DB_PORT=5432
DB_NAME=student_management
DB_USER=student_user
DB_PASSWORD=your_secure_password

# JWT
JWT_SECRET=your_very_long_and_secure_jwt_secret
JWT_EXPIRATION=3600

# Application
SPRING_PROFILES_ACTIVE=production
SERVER_PORT=8080

# Monitoring
ENABLE_METRICS=true
ENABLE_HEALTH_CHECK=true

# Logging
LOG_LEVEL=INFO
LOG_FILE_PATH=/var/log/student-management
```

## 📊 Monitoring ve Logging

### Application Monitoring

#### Prometheus Configuration

```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: "student-backend"
    static_configs:
      - targets: ["localhost:8080"]
    metrics_path: "/actuator/prometheus"
    scrape_interval: 5s

  - job_name: "student-frontend"
    static_configs:
      - targets: ["localhost:3000"]
    metrics_path: "/metrics"
```

#### Grafana Dashboard

```json
{
  "dashboard": {
    "title": "Student Management System",
    "panels": [
      {
        "title": "HTTP Request Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_requests_total[5m])",
            "legendFormat": "{{method}} {{endpoint}}"
          }
        ]
      },
      {
        "title": "Database Connections",
        "type": "graph",
        "targets": [
          {
            "expr": "hikaricp_connections_active",
            "legendFormat": "Active Connections"
          }
        ]
      }
    ]
  }
}
```

### Logging Configuration

#### logback-spring.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <springProfile name="production">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>/var/log/student-management/application.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>/var/log/student-management/application.%d{yyyy-MM-dd}.log</fileNamePattern>
                <maxHistory>30</maxHistory>
            </rollingPolicy>
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>

        <root level="INFO">
            <appender-ref ref="FILE" />
        </root>
    </springProfile>
</configuration>
```

## 💾 Backup ve Recovery

### Database Backup

#### Automated Backup Script

```bash
#!/bin/bash
# backup.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backup/postgres"
DB_NAME="student_management"
DB_USER="student_user"

# Create backup directory
mkdir -p $BACKUP_DIR

# Create backup
pg_dump -h localhost -U $DB_USER -d $DB_NAME > $BACKUP_DIR/backup_$DATE.sql

# Compress backup
gzip $BACKUP_DIR/backup_$DATE.sql

# Remove old backups (keep last 7 days)
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +7 -delete

echo "Backup completed: backup_$DATE.sql.gz"
```

#### Cron Job

```bash
# Add to crontab
0 2 * * * /path/to/backup.sh >> /var/log/backup.log 2>&1
```

### Recovery Procedure

#### Database Recovery

```bash
# Stop application
docker-compose stop backend

# Restore database
gunzip -c backup_20240101_020000.sql.gz | psql -h localhost -U student_user -d student_management

# Start application
docker-compose start backend
```

## 🔒 Güvenlik Kontrolleri

### Security Checklist

#### Network Security

- [ ] Firewall kuralları yapılandırıldı
- [ ] VPN erişimi kuruldu
- [ ] Port taraması yapıldı
- [ ] DDoS koruması aktif

#### Application Security

- [ ] Input validation kontrol edildi
- [ ] SQL injection testleri yapıldı
- [ ] XSS koruması aktif
- [ ] CSRF token kontrol edildi

#### Data Security

- [ ] Veri şifreleme aktif
- [ ] Backup şifreleme yapıldı
- [ ] Access logging aktif
- [ ] Audit trail yapılandırıldı

### Security Testing

#### OWASP ZAP Scan

```bash
# Run security scan
zap-cli quick-scan --self-contained \
  --spider http://localhost:3000 \
  --ajax-spider \
  --scan http://localhost:3000 \
  --scanners xss,sqli \
  --recursive
```

#### SSL Test

```bash
# Test SSL configuration
openssl s_client -connect localhost:443 -servername localhost
```

---

**Not:** Bu dokümantasyon production ortamı için temel rehber niteliğindedir. Spesifik gereksinimlerinize göre uyarlanmalıdır.
