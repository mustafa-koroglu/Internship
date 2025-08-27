# 🎓 Öğrenci Yönetim Sistemi

Modern web teknolojileri ile geliştirilmiş, mikroservis mimarisine sahip tam yığın (full-stack) öğrenci yönetim sistemidir. Öğrenci kayıtları, ders yönetimi, IP adresi atama ve CSV dosya işleme özelliklerini içeren kapsamlı bir yönetim platformu.

## 🚀 Özellikler

### 👥 Öğrenci Yönetimi

- ✅ Öğrenci kayıt ve güncelleme
- ✅ Öğrenci listesi görüntüleme
- ✅ Arama ve filtreleme
- ✅ Öğrenci onaylama sistemi
- ✅ CSV dosyalarından toplu veri yükleme

### 📚 Ders Yönetimi

- ✅ Ders ekleme ve düzenleme
- ✅ Öğrenci-ders ilişkilendirme
- ✅ Ders listesi yönetimi

### 🌐 IP Adresi Yönetimi

- ✅ IPv4/IPv6 adresi desteği
- ✅ CIDR subnet yönetimi
- ✅ IP aralığı tanımlama
- ✅ Otomatik IP atama
- ✅ Öğrenci-IP ilişkilendirme
- ✅ Network/broadcast adresi kontrolü

### 🔐 Güvenlik

- ✅ JWT tabanlı kimlik doğrulama
- ✅ Role-based access control (ADMIN/USER)
- ✅ Güvenli şifre hashleme
- ✅ CORS yapılandırması

### 📊 CSV İşleme

- ✅ Otomatik CSV dosya izleme
- ✅ Toplu veri yükleme
- ✅ Hata raporlama (.fail/.done uzantıları)
- ✅ Virtual thread ile yüksek performans

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

### Backend

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

### Frontend

- **React 18.2.0** - UI framework
- **React Router DOM 6.20.1** - Routing
- **Bootstrap 5.3.7** - CSS framework
- **JavaScript ES6+** - Programlama dili

## 📋 Sistem Gereksinimleri

### Minimum Gereksinimler

- **Java:** JDK 21+
- **Node.js:** 18.0.0+
- **npm:** 8.0.0+
- **PostgreSQL:** 12.0+
- **RAM:** 4GB
- **Disk:** 2GB

### Önerilen Gereksinimler

- **Java:** JDK 21
- **Node.js:** 20.0.0+
- **PostgreSQL:** 15.0+
- **RAM:** 8GB
- **Disk:** 5GB

## 🚀 Hızlı Başlangıç

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

### 3. Projeyi Klonlayın

```bash
git clone https://github.com/mustafa-koroglu/studentProject.git
cd studentProject
```

### 4. Config Server'ı Başlatın

```bash
cd config-server
mvn spring-boot:run
```

Config Server `http://localhost:8888` adresinde çalışacaktır.

### 5. Backend'i Başlatın

```bash
cd backend

# application.yml dosyasındaki veritabanı ayarlarını düzenleyin
# src/main/resources/application.yml

mvn spring-boot:run
```

Backend `http://localhost:8080` adresinde çalışacaktır.

### 6. Frontend'i Başlatın

```bash
cd frontend
npm install
npm start
```

Frontend `http://localhost:3000` adresinde açılacaktır.

## 🔧 Yapılandırma

### Backend Yapılandırması

`backend/src/main/resources/application.yml` dosyasını düzenleyin:

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

### Veritabanı Bağlantısı

Config Server'da veritabanı ayarlarını yapılandırın:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/student_management
    username: student_user
    password: your_password
    driver-class-name: org.postgresql.Driver
```

## 📚 API Dokümantasyonu

### Kimlik Doğrulama

#### POST /api/v1/auth/login

```json
{
  "username": "admin",
  "password": "password"
}
```

#### POST /api/v1/auth/register

```json
{
  "username": "newuser",
  "password": "password",
  "email": "user@example.com"
}
```

### Öğrenci Yönetimi

#### GET /api/v3/students

Tüm öğrencileri listeler.

#### POST /api/v3/students

Yeni öğrenci ekler.

#### PUT /api/v3/students/{id}

Öğrenci bilgilerini günceller.

#### DELETE /api/v3/students/{id}

Öğrenciyi siler.

### IP Adresi Yönetimi

#### GET /api/v1/ip-addresses

Aktif IP adreslerini listeler.

#### POST /api/v1/ip-addresses

Yeni IP adresi ekler.

#### PUT /api/v1/ip-addresses/{id}

IP adresini günceller.

Detaylı API dokümantasyonu için [docs/API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md) dosyasına bakın.

## 🌐 IP Adresi Formatları

### Desteklenen Formatlar

#### IPv4

- **Tekil IP:** `192.168.1.1`
- **CIDR Subnet:** `192.168.1.0/24`
- **IP Aralığı:** `192.168.1.1-192.168.1.10`

#### IPv6

- **Tekil IP:** `2001:db8::1`
- **CIDR Subnet:** `2001:db8::/32`
- **IP Aralığı:** `2001:db8::1-2001:db8::10`

### Özellikler

- ✅ Otomatik format doğrulama
- ✅ Network/broadcast adresi kontrolü
- ✅ Çakışma kontrolü
- ✅ Otomatik IP atama

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

## 🚀 Dağıtım

### Docker ile Dağıtım

#### Backend

```dockerfile
FROM openjdk:21-jdk-slim
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

#### Frontend

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

## 📁 Proje Yapısı

```
studentProject/
├── backend/                 # Spring Boot uygulaması
│   ├── src/main/java/
│   │   └── com/example/backend/
│   │       ├── config/      # Yapılandırma sınıfları
│   │       ├── controller/  # REST controller'lar
│   │       ├── dataAccess/  # Repository sınıfları
│   │       ├── entities/    # JPA entity'leri
│   │       ├── exception/   # Exception handler'lar
│   │       ├── filter/      # JWT filter'ları
│   │       ├── request/     # Request DTO'ları
│   │       ├── response/    # Response DTO'ları
│   │       ├── scheduler/   # Zamanlanmış görevler
│   │       ├── service/     # İş mantığı katmanı
│   │       └── utility/     # Yardımcı sınıflar
│   └── src/main/resources/
│       └── application.yml
├── frontend/                # React uygulaması
│   ├── src/
│   │   ├── components/      # React bileşenleri
│   │   ├── hooks/           # Custom React hooks
│   │   └── utils/           # Yardımcı fonksiyonlar
│   └── package.json
├── config-server/           # Spring Cloud Config
├── csv-files/               # CSV dosyaları
├── docs/                    # Dokümantasyon
└── README.md
```

## 📖 Dokümantasyon

- **[API Dokümantasyonu](docs/API_DOCUMENTATION.md)** - REST API referansı
- **[Geliştirme Rehberi](docs/DEVELOPMENT_GUIDE.md)** - Geliştirme ortamı kurulumu
- **[Dağıtım Rehberi](docs/DEPLOYMENT_GUIDE.md)** - Production dağıtımı
- **[Katkıda Bulunma](docs/CONTRIBUTING.md)** - Projeye katkı süreci
- **[Changelog](docs/CHANGELOG.md)** - Sürüm geçmişi

## 🤝 Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

Detaylı katkı rehberi için [docs/CONTRIBUTING.md](docs/CONTRIBUTING.md) dosyasına bakın.

## 📄 Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına bakın.

## 📞 İletişim

- **Proje Sahibi:** Mustafa Köroğlu
- **Email:** mustafaa.korogluu@gmail.com
- **GitHub:** [@mustafa-koroglu](https://github.com/mustafa-koroglu)

## 🙏 Teşekkürler

Bu projede kullanılan açık kaynak kütüphanelerin geliştiricilerine teşekkürler.

---

**Not:** Bu dokümantasyon sürekli güncellenmektedir. En güncel bilgiler için GitHub repository'sini kontrol edin.
