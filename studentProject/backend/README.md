# 🚀 Backend - Spring Boot API

Öğrenci Yönetim Sistemi'nin backend API'si. Spring Boot 3.3.0 ile geliştirilmiş, mikroservis mimarisine sahip modern bir REST API.

## 🏗️ Mimari

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │  Config Server  │
│   (React)       │◄──►│  (Spring Boot)  │◄──►│  (Spring Cloud) │
│   Port: 3000    │    │   Port: 8080    │    │   Port: 8888    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   PostgreSQL    │
                       │   Database      │
                       └─────────────────┘
```

## 🛠️ Teknoloji Stack

- **Java 21** - Programlama dili
- **Spring Boot 3.3.0** - Framework
- **Spring Security** - Güvenlik
- **Spring Data JPA** - Veritabanı erişimi
- **Spring Cloud Config** - Yapılandırma yönetimi
- **PostgreSQL** - Veritabanı
- **JWT** - Kimlik doğrulama
- **OpenCSV 5.8** - CSV işleme
- **Virtual Threads** - Asenkron işleme
- **Maven** - Bağımlılık yönetimi
- **Lombok** - Boilerplate kod azaltma

## 📁 Proje Yapısı

```
src/main/java/com/example/backend/
├── config/          # Yapılandırma sınıfları
│   ├── SecurityConfig.java
│   ├── CorsConfig.java
│   └── JwtConfig.java
├── controller/      # REST controller'lar
│   ├── AuthController.java
│   ├── StudentController.java
│   ├── LessonController.java
│   └── IpAddressController.java
├── dataAccess/      # Repository sınıfları
│   ├── StudentsRepository.java
│   ├── LessonRepository.java
│   └── IpAddressRepository.java
├── entities/        # JPA entity'leri
│   ├── Student.java
│   ├── Lesson.java
│   ├── IpAddress.java
│   └── AppUser.java
├── exception/       # Exception handler'lar
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── filter/          # JWT filter'ları
│   └── JwtAuthenticationFilter.java
├── request/         # Request DTO'ları
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   └── CreateStudentRequest.java
├── response/        # Response DTO'ları
│   ├── AuthResponse.java
│   ├── StudentResponse.java
│   └── IpAddressResponse.java
├── scheduler/       # Zamanlanmış görevler
│   └── CsvProcessingScheduler.java
├── service/         # İş mantığı katmanı
│   ├── abstracts/   # Service interface'leri
│   └── concretes/   # Service implementasyonları
└── utility/         # Yardımcı sınıflar
    ├── IpValidationUtil.java
    ├── IpParseUtil.java
    └── JwtUtil.java
```

## 🚀 Özellikler

### 🔐 Kimlik Doğrulama ve Güvenlik

- **JWT tabanlı authentication**
- **Role-based access control** (ADMIN/USER)
- **BCrypt şifre hashleme**
- **CORS yapılandırması**
- **Global exception handling**

### 👥 Öğrenci Yönetimi

- **CRUD işlemleri** (Create, Read, Update, Delete)
- **Arama ve filtreleme**
- **Öğrenci onaylama sistemi**
- **Pagination desteği**

### 📚 Ders Yönetimi

- **Ders CRUD işlemleri**
- **Öğrenci-ders ilişkilendirme**
- **Ders listesi yönetimi**

### 🌐 IP Adresi Yönetimi

- **IPv4/IPv6 desteği**
- **CIDR subnet yönetimi**
- **IP aralığı tanımlama**
- **Otomatik IP atama**
- **Network/broadcast kontrolü**

### 📊 CSV İşleme

- **Otomatik dosya izleme** (30 saniyede bir)
- **Virtual thread ile yüksek performans**
- **Hata raporlama** (.fail/.done uzantıları)
- **Toplu veri yükleme**

## 🔧 Kurulum ve Çalıştırma

### Ön Gereksinimler

- **Java 21+**
- **Maven 3.6+**
- **PostgreSQL 12.0+**

### 1. Veritabanı Kurulumu

```sql
CREATE DATABASE student_management;
CREATE USER student_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE student_management TO student_user;
```

### 2. Yapılandırma

`src/main/resources/application.yml` dosyasını düzenleyin:

```yaml
server:
  port: 8080

spring:
  application:
    name: backend

  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: true

  config:
    import: "configserver:"

csv:
  watch:
    directory: ./csv-files
```

### 3. Uygulamayı Başlatma

```bash
# Proje dizininde
mvn clean install
mvn spring-boot:run
```

Uygulama `http://localhost:8080` adresinde çalışacaktır.

## 📚 API Endpoints

### 🔐 Kimlik Doğrulama

```
POST /api/v1/auth/login     # Kullanıcı girişi
POST /api/v1/auth/register  # Kullanıcı kaydı
```

### 👥 Öğrenci Yönetimi

```
GET    /api/v3/students           # Öğrenci listesi
GET    /api/v3/students/{id}      # Öğrenci detayı
POST   /api/v3/students           # Yeni öğrenci
PUT    /api/v3/students/{id}      # Öğrenci güncelleme
DELETE /api/v3/students/{id}      # Öğrenci silme
GET    /api/v3/students/search    # Öğrenci arama
```

### 📚 Ders Yönetimi

```
GET    /api/v3/lessons            # Ders listesi
GET    /api/v3/lessons/{id}       # Ders detayı
POST   /api/v3/lessons            # Yeni ders
PUT    /api/v3/lessons/{id}       # Ders güncelleme
DELETE /api/v3/lessons/{id}       # Ders silme
```

### 🌐 IP Adresi Yönetimi

```
GET    /api/v1/ip-addresses              # IP adresi listesi
GET    /api/v1/ip-addresses/{id}         # IP adresi detayı
POST   /api/v1/ip-addresses              # Yeni IP adresi
PUT    /api/v1/ip-addresses/{id}         # IP adresi güncelleme
DELETE /api/v1/ip-addresses/{id}         # IP adresi silme
GET    /api/v1/ip-addresses/search       # IP adresi arama
POST   /api/v1/ip-addresses/assign       # IP adresi atama
```

## 📊 Veritabanı Şeması

### Tablolar

1. **app_user** - Kullanıcı bilgileri
2. **student** - Öğrenci bilgileri
3. **lesson** - Ders bilgileri
4. **ip_addresses** - IP adresi bilgileri
5. **file** - Dosya işleme kayıtları

## 🔐 Güvenlik

### JWT Token Yapısı

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "username",
    "iat": 1640995200,
    "exp": 1640998800
  }
}
```

### Roller

- **ADMIN:** Tüm işlemleri yapabilir
- **USER:** Sadece öğrenci listesini görüntüleyebilir

## 📊 CSV İşleme

### Desteklenen Format

```csv
studentNumber,firstName,lastName,email,phoneNumber,birthDate,department,grade
2024001,Ahmet,Yılmaz,ahmet@example.com,555-123-4567,2000-01-01,Bilgisayar Mühendisliği,3.50
```

### Özellikler

- ✅ Otomatik dosya izleme (30 saniyede bir)
- ✅ Hata raporlama (.fail uzantısı)
- ✅ Başarılı işleme (.done uzantısı)
- ✅ Virtual thread ile yüksek performans
- ✅ Toplu veri yükleme

## 🧪 Test

### Test Çalıştırma

```bash
# Tüm testleri çalıştır
mvn test

# Belirli test sınıfını çalıştır
mvn test -Dtest=StudentControllerTest

# Test coverage raporu
mvn jacoco:report
```

### Test Yapısı

```
src/test/java/com/example/backend/
├── controller/      # Controller testleri
├── service/         # Service testleri
├── repository/      # Repository testleri
└── integration/     # Integration testleri
```

## 📈 Performans Optimizasyonları

### Virtual Threads

- CSV işleme için yüksek performanslı asenkron işleme
- I/O bound işlemler için optimize edilmiş

### Stream API

- Fonksiyonel programlama ile veri işleme
- Memory efficient operations

### Database Optimizations

- Connection pooling
- Query optimization
- Indexing strategies

## 🔧 Geliştirme

### Kod Standartları

- **Google Java Style Guide**
- **Lombok** kullanımı
- **Single Responsibility Principle**
- **Dependency Injection**

### Logging

```java
@Slf4j
public class StudentService {
    public void createStudent(Student student) {
        log.info("Öğrenci oluşturuluyor: {}", student.getStudentNumber());
        // ...
    }
}
```

### Exception Handling

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        // ...
    }
}
```

## 🚀 Dağıtım

### Docker ile Dağıtım

```dockerfile
FROM openjdk:21-jdk-slim
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Production Ortamı

- **Database:** PostgreSQL cluster
- **Application:** Spring Boot JAR
- **Load Balancer:** Nginx
- **Monitoring:** Prometheus + Grafana

## 📞 Destek

- **GitHub Issues:** [Backend Issues](https://github.com/mustafa-koroglu/studentProject/issues?q=label%3Abackend)
- **Documentation:** [API Documentation](../docs/API_DOCUMENTATION.md)
- **Development Guide:** [Development Guide](../docs/DEVELOPMENT_GUIDE.md)

---

**Not:** Bu dokümantasyon sürekli güncellenmektedir. En güncel bilgiler için GitHub repository'sini kontrol edin.
