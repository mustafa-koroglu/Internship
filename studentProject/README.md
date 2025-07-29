# Öğrenci Yönetim Sistemi

Bu proje, modern web teknolojileri kullanılarak geliştirilmiş, mikroservis mimarisine sahip tam yığın (full-stack) bir öğrenci yönetim sistemidir.

## 📋 İçindekiler

- [Proje Mimarisi](#proje-mimarisi)
- [Kullanılan Teknolojiler](#kullanılan-teknolojiler)
- [Sistem Gereksinimleri](#sistem-gereksinimleri)
- [Kurulum ve Çalıştırma](#kurulum-ve-çalıştırma)
- [API Dokümantasyonu](#api-dokümantasyonu)
- [Veritabanı Şeması](#veritabanı-şeması)
- [Güvenlik](#güvenlik)
- [CSV İşleme](#csv-işleme)
- [Geliştirme Rehberi](#geliştirme-rehberi)
- [Test](#test)
- [Dağıtım](#dağıtım)
- [Katkıda Bulunma](#katkıda-bulunma)
- [Lisans](#lisans)

## 🏗️ Proje Mimarisi

Proje üç ana bileşenden oluşmaktadır:

### 1. Backend (Spring Boot)

- **Port:** 8080
- **Amaç:** Öğrenci verilerinin yönetildiği, RESTful API'ler sunan ana iş mantığı katmanı
- **Özellikler:**
  - JWT tabanlı kimlik doğrulama ve yetkilendirme
  - PostgreSQL veritabanı entegrasyonu
  - CSV dosya işleme ve toplu veri yükleme
  - Otomatik veri işleme scheduler'ı
  - CORS yapılandırması
  - Global exception handling

### 2. Frontend (React)

- **Port:** 3000
- **Amaç:** Kullanıcıların etkileşimde bulunduğu modern web arayüzü
- **Özellikler:**
  - Responsive tasarım (Bootstrap)
  - JWT token tabanlı oturum yönetimi
  - Öğrenci CRUD işlemleri
  - Arama ve filtreleme
  - Modal tabanlı form işlemleri

### 3. Config Server (Spring Cloud Config)

- **Port:** 8888
- **Amaç:** Merkezi yapılandırma yönetimi
- **Özellikler:**
  - Ortam bazlı konfigürasyon
  - Dinamik yapılandırma güncellemeleri
  - Güvenli yapılandırma yönetimi

## 🛠️ Kullanılan Teknolojiler

### Backend Stack

- **Java 21** - Programlama dili
- **Spring Boot 3.3.0** - Framework
- **Spring Security** - Güvenlik
- **Spring Data JPA** - Veritabanı erişimi
- **PostgreSQL** - Veritabanı
- **JWT (JSON Web Tokens)** - Kimlik doğrulama
- **OpenCSV 5.8** - CSV işleme
- **Maven** - Bağımlılık yönetimi
- **Spring Cloud Config** - Yapılandırma yönetimi

### Frontend Stack

- **React 19.1.0** - UI framework
- **React Router DOM 7.6.3** - Routing
- **Bootstrap 5.3.7** - CSS framework
- **Axios** - HTTP client
- **JavaScript ES6+** - Programlama dili

### DevOps & Araçlar

- **Git** - Versiyon kontrolü
- **Maven** - Build tool
- **npm** - Package manager

## 💻 Sistem Gereksinimleri

### Minimum Gereksinimler

- **Java:** JDK 21 veya üzeri
- **Node.js:** 18.0.0 veya üzeri
- **npm:** 8.0.0 veya üzeri
- **PostgreSQL:** 12.0 veya üzeri
- **RAM:** 4GB
- **Disk:** 2GB boş alan

### Önerilen Gereksinimler

- **Java:** JDK 21
- **Node.js:** 20.0.0
- **PostgreSQL:** 15.0
- **RAM:** 8GB
- **Disk:** 5GB boş alan

## 🚀 Kurulum ve Çalıştırma

### 1. Ön Gereksinimler

```bash
# Java versiyonunu kontrol edin
java -version

# Node.js versiyonunu kontrol edin
node --version

# npm versiyonunu kontrol edin
npm --version
```

### 2. Veritabanı Kurulumu

```sql
-- PostgreSQL'de yeni veritabanı oluşturun
CREATE DATABASE student_management;
CREATE USER student_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE student_management TO student_user;
```

### 3. Config Server

```bash
cd config-server
mvn spring-boot:run
```

Config Server `http://localhost:8888` adresinde çalışacaktır.

### 4. Backend

```bash
cd backend

# application.yml dosyasındaki veritabanı ayarlarını düzenleyin
# src/main/resources/application.yml

mvn spring-boot:run
```

Backend `http://localhost:8080` adresinde çalışacaktır.

### 5. Frontend

```bash
cd frontend
npm install
npm start
```

Frontend `http://localhost:3000` adresinde açılacaktır.

## 📚 API Dokümantasyonu

### Kimlik Doğrulama Endpoint'leri

#### POST /api/v1/auth/register

Yeni kullanıcı kaydı oluşturur.

**Request Body:**

```json
{
  "username": "string",
  "password": "string",
  "email": "string"
}
```

**Response:**

```json
{
  "message": "Kullanıcı başarıyla kaydedildi",
  "timestamp": "2024-01-01T00:00:00"
}
```

#### POST /api/v1/auth/login

Kullanıcı girişi yapar ve JWT token döner.

**Request Body:**

```json
{
  "username": "string",
  "password": "string"
}
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "string",
  "expiresIn": 3600
}
```

### Öğrenci Yönetimi Endpoint'leri

#### GET /api/v3/students

Tüm öğrencileri listeler.

**Headers:**

```
Authorization: Bearer <JWT_TOKEN>
```

**Query Parameters:**

- `page` (optional): Sayfa numarası (default: 0)
- `size` (optional): Sayfa boyutu (default: 10)
- `sort` (optional): Sıralama alanı (default: id)

**Response:**

```json
{
  "content": [
    {
      "id": 1,
      "studentNumber": "2024001",
      "firstName": "Ahmet",
      "lastName": "Yılmaz",
      "email": "ahmet@example.com",
      "phoneNumber": "555-123-4567",
      "birthDate": "2000-01-01",
      "department": "Bilgisayar Mühendisliği",
      "grade": "3.50"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "currentPage": 0
}
```

#### GET /api/v3/students/{id}

Belirtilen ID'ye sahip öğrenciyi getirir.

#### POST /api/v3/students

Yeni öğrenci ekler.

**Request Body:**

```json
{
  "studentNumber": "2024001",
  "firstName": "Ahmet",
  "lastName": "Yılmaz",
  "email": "ahmet@example.com",
  "phoneNumber": "555-123-4567",
  "birthDate": "2000-01-01",
  "department": "Bilgisayar Mühendisliği",
  "grade": "3.50"
}
```

#### PUT /api/v3/students/{id}

Öğrenci bilgilerini günceller.

#### DELETE /api/v3/students/{id}

Öğrenciyi siler.

#### GET /api/v3/students/search

Öğrenci arama yapar.

**Query Parameters:**

- `q`: Arama terimi (isim, soyisim veya numara)

### CSV İşleme Endpoint'leri

#### POST /api/v3/csv/upload

CSV dosyası yükler ve işler.

**Headers:**

```
Content-Type: multipart/form-data
Authorization: Bearer <JWT_TOKEN>
```

**Form Data:**

- `file`: CSV dosyası

## 🗄️ Veritabanı Şeması

### AppUser Tablosu

```sql
CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Student Tablosu

```sql
CREATE TABLE student (
    id BIGSERIAL PRIMARY KEY,
    student_number VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    birth_date DATE,
    department VARCHAR(100),
    grade DECIMAL(3,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🔒 Güvenlik

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

### Güvenlik Özellikleri

- JWT tabanlı kimlik doğrulama
- Şifre hashleme (BCrypt)
- CORS yapılandırması
- Role-based access control
- Token expiration
- Secure headers

## 📊 CSV İşleme

### Desteklenen CSV Formatı

```csv
studentNumber,firstName,lastName,email,phoneNumber,birthDate,department,grade
2024001,Ahmet,Yılmaz,ahmet@example.com,555-123-4567,2000-01-01,Bilgisayar Mühendisliği,3.50
2024002,Ayşe,Demir,ayse@example.com,555-123-4568,2000-02-01,Elektrik Mühendisliği,3.75
```

### CSV İşleme Özellikleri

- Otomatik veri doğrulama
- Hata raporlama
- Toplu veri yükleme
- Zamanlanmış işleme
- Duplicate kontrol

## 👨‍💻 Geliştirme Rehberi

### Kod Standartları

- **Java:** Google Java Style Guide
- **JavaScript:** ESLint + Prettier
- **Commit Messages:** Conventional Commits

### Proje Yapısı

#### Backend

```
src/main/java/com/example/backend/
├── config/          # Yapılandırma sınıfları
├── controller/      # REST controller'lar
├── dataAccess/      # Repository sınıfları
├── entities/        # JPA entity'leri
├── exception/       # Exception handler'lar
├── filter/          # JWT filter'ları
├── request/         # Request DTO'ları
├── response/        # Response DTO'ları
├── scheduler/       # Zamanlanmış görevler
├── service/         # İş mantığı katmanı
└── utility/         # Yardımcı sınıflar
```

#### Frontend

```
src/
├── components/      # React bileşenleri
├── services/        # API servisleri
├── utils/           # Yardımcı fonksiyonlar
├── hooks/           # Custom React hooks
└── styles/          # CSS dosyaları
```

### Geliştirme Ortamı Kurulumu

```bash
# Backend için IDE ayarları
# IntelliJ IDEA veya Eclipse kullanın
# Lombok plugin'ini yükleyin

# Frontend için
npm install -g eslint prettier
```

## 🧪 Test

### Backend Testleri

```bash
cd backend
mvn test
```

### Frontend Testleri

```bash
cd frontend
npm test
```

### Test Kapsamı

- Unit testler
- Integration testler
- API testleri
- UI testleri

## 🚀 Dağıtım

### Docker ile Dağıtım

#### Backend Dockerfile

```dockerfile
FROM openjdk:21-jdk-slim
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

#### Frontend Dockerfile

```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
FROM nginx:alpine
COPY --from=0 /app/build /usr/share/nginx/html
EXPOSE 80
```

### Production Ortamı

- **Database:** PostgreSQL cluster
- **Backend:** Spring Boot JAR
- **Frontend:** Nginx
- **Load Balancer:** Nginx
- **Monitoring:** Prometheus + Grafana

## 🤝 Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

### Katkı Rehberi

- Kod standartlarına uyun
- Test yazın
- Dokümantasyonu güncelleyin
- Issue template'ini kullanın

## 📄 Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına bakın.

## 📞 İletişim

- **Proje Sahibi:** [Adınız]
- **Email:** [email@example.com]
- **GitHub:** [github.com/username]

## 🙏 Teşekkürler

Bu projede kullanılan açık kaynak kütüphanelerin geliştiricilerine teşekkürler.

---

**Not:** Bu dokümantasyon sürekli güncellenmektedir. En güncel bilgiler için GitHub repository'sini kontrol edin.
