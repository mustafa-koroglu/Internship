# Öğrenci Yönetim Sistemi

Bu proje, modern web teknolojileri kullanılarak geliştirilmiş, mikroservis mimarisine sahip tam yığın (full-stack) bir öğrenci yönetim sistemidir.

## Proje Mimarisi

Proje üç ana bileşenden oluşmaktadır:

1.  **Backend (Spring Boot):** Öğrenci verilerinin yönetildiği, RESTful API'ler sunan ana iş mantığı katmanıdır. JWT tabanlı kimlik doğrulama ve yetkilendirme mekanizmalarını içerir.
2.  **Frontend (React):** Kullanıcıların etkileşimde bulunduğu, backend API'lerini kullanarak verileri listeleyen, ekleyen, güncelleyen ve silen modern bir arayüzdür.
3.  **Config Server (Spring Cloud Config):** Backend servisinin yapılandırma (konfigürasyon) dosyalarını merkezi bir yerden yönetir. Bu sayede farklı ortamlar (geliştirme, test, production) için ayarları kolayca yönetebiliriz.

## Kullanılan Teknolojiler

- **Backend:**
  - Java 21
  - Spring Boot 3
  - Spring Security (JWT ile)
  - Spring Data JPA
  - Maven
  - PostgreSQL (veya H2, MySQL vb.)
- **Frontend:**
  - React
  - JavaScript (ES6+)
  - axios (HTTP istekleri için)
  - Bootstrap (Stil için)
- **Yapılandırma:**
  - Spring Cloud Config Server

---

## Kurulum ve Çalıştırma

Projeyi çalıştırmak için aşağıdaki adımları sırasıyla takip ediniz.

### 1. Config Server

Önce yapılandırma sunucusunu ayağa kaldırmalısınız.

```bash
cd config-server
mvn spring-boot:run
```

Config Server varsayılan olarak `http://localhost:8888` adresinde çalışacaktır.

### 2. Backend

Config server çalıştıktan sonra backend servisini başlatabilirsiniz.

```bash
cd backend
# backend/src/main/resources/application.yml dosyasındaki
# veritabanı ayarlarını kendi ortamınıza göre düzenleyin.
mvn spring-boot:run
```

Backend servisi varsayılan olarak `http://localhost:8080` adresinde çalışacaktır.

### 3. Frontend

Son olarak arayüz uygulamasını çalıştırın.

```bash
cd frontend
npm install
npm start
```

Frontend uygulaması tarayıcınızda `http://localhost:3000` adresinde açılacaktır.

---

## API Endpointleri (Backend)

- `POST /api/v1/auth/register`: Yeni kullanıcı kaydı.
- `POST /api/v1/auth/login`: Kullanıcı girişi ve JWT token alma.
- `GET /api/v3/students`: Tüm öğrencileri listeler.
- `GET /api/v3/students/{id}`: Belirtilen ID'ye sahip öğrenciyi getirir.
- `POST /api/v3/students`: Yeni öğrenci ekler.
- `PUT /api/v3/students/{id}`: Öğrenci bilgilerini günceller.
- `DELETE /api/v3/students/{id}`: Öğrenciyi siler.
- `GET /api/v3/students/search?q={searchTerm}`: İsim, soyisim veya numaraya göre arama yapar.

---

Bu proje, Spring Boot ve React ile modern, güvenli ve ölçeklenebilir web uygulamaları geliştirmek için bir başlangıç noktası sunmaktadır.
