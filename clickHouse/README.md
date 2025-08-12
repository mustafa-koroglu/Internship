# Spring Boot ClickHouse Öğrenci Yönetim Uygulaması

Bu proje, Spring Boot ve ClickHouse kullanarak geliştirilmiş bir öğrenci yönetim sistemidir.

## Teknolojiler

- **Spring Boot 3.5.4**
- **ClickHouse Database**
- **Spring JDBC**
- **Docker & Docker Compose**
- **Lombok**

## Proje Yapısı

```
src/main/java/com/example/clickhouse/
├── ClickHouseApplication.java    # Ana uygulama sınıfı
├── controller/
│   └── StudentController.java    # REST API Controller
├── service/
│   └── StudentService.java       # İş mantığı katmanı
├── repository/
│   └── StudentRepository.java    # Veri erişim katmanı
└── entities/
    └── Student.java              # Veri modeli
```

## Kurulum ve Çalıştırma

### 1. Gereksinimler

- Java 21
- Docker & Docker Compose
- Maven

### 2. ClickHouse Veritabanını Başlatma

```bash
# Docker Compose ile ClickHouse'u başlat
docker-compose up -d
```

### 3. Spring Boot Uygulamasını Çalıştırma

```bash
# Maven ile uygulamayı derle ve çalıştır
mvn spring-boot:run
```

Uygulama `http://localhost:8080` adresinde çalışacaktır.

## API Endpoints

### Öğrenci İşlemleri

| HTTP Method | Endpoint | Açıklama |
|-------------|----------|----------|
| GET | `/students` | Tüm öğrencileri listele |
| GET | `/students/{id}` | ID'ye göre öğrenci getir |
| POST | `/students` | Yeni öğrenci ekle |
| PUT | `/students/{id}` | Öğrenci bilgilerini güncelle |
| DELETE | `/students/{id}` | Öğrenci sil |

### Örnek API Kullanımı

#### Yeni Öğrenci Ekleme
```bash
curl -X POST http://localhost:8080/students \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ahmet",
    "surname": "Yılmaz",
    "number": "2024001"
  }'
```

#### Tüm Öğrencileri Listeleme
```bash
curl -X GET http://localhost:8080/students
```

#### Öğrenci Güncelleme
```bash
curl -X PUT http://localhost:8080/students/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ahmet",
    "surname": "Yılmaz",
    "number": "2024001"
  }'
```

#### Öğrenci Silme
```bash
curl -X DELETE http://localhost:8080/students/1
```

## ClickHouse Tablo Yapısı

### Students Tablosu

```sql
CREATE TABLE students (
    id UInt64,
    name String,
    surname String,
    number String
) ENGINE = MergeTree()
ORDER BY id;
```

### Tablo Oluşturma SQL'leri

```sql
-- Veritabanı oluştur
CREATE DATABASE IF NOT EXISTS students;

-- Veritabanını kullan
USE students;

-- Students tablosunu oluştur
CREATE TABLE IF NOT EXISTS students (
    id UInt64,
    name String,
    surname String,
    number String
) ENGINE = MergeTree()
ORDER BY id;
```

## Veritabanı Bağlantı Ayarları

`application.properties` dosyasında ClickHouse bağlantı ayarları:

```properties
spring.datasource.url=jdbc:clickhouse://localhost:8123/students
spring.datasource.driver-class-name=com.clickhouse.jdbc.ClickHouseDriver
spring.datasource.username=default
spring.datasource.password=
```

## Docker Servisleri

- **ClickHouse Server**: `localhost:8123` (HTTP) ve `localhost:9000` (Native)
- **Spring Boot App**: `localhost:8080`

## Özellikler

- ✅ Katmanlı mimari (Controller, Service, Repository, Model)
- ✅ RESTful API endpoints
- ✅ ClickHouse veritabanı entegrasyonu
- ✅ Docker container desteği
- ✅ CRUD işlemleri (Create, Read, Update, Delete)
- ✅ Basit ve anlaşılır kod yapısı

## Sorun Giderme

### ClickHouse Bağlantı Sorunu
Eğer ClickHouse'a bağlanamıyorsanız:
1. Docker container'ın çalıştığından emin olun: `docker ps`
2. Portların açık olduğunu kontrol edin: `netstat -an | grep 8123`
3. Container loglarını kontrol edin: `docker logs clickhouse-server`

### Uygulama Başlatma Sorunu
Eğer Spring Boot uygulaması başlamıyorsa:
1. Java 21'in yüklü olduğundan emin olun
2. Maven dependencies'lerin yüklendiğini kontrol edin: `mvn clean install`
3. Application loglarını kontrol edin
