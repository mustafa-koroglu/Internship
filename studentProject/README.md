# Öğrenci Yönetim Sistemi

Bu proje, öğrenci bilgilerini yönetmek için geliştirilmiş modern bir web uygulamasıdır. Spring Boot backend ve React frontend kullanılarak oluşturulmuştur.

## 🚀 Özellikler

- **Öğrenci CRUD İşlemleri**: Öğrenci ekleme, görüntüleme, güncelleme ve silme
- **Modern UI**: Bootstrap 5 ile responsive tasarım
- **Real-time İstatistikler**: Dashboard'da öğrenci sayıları
- **Arama ve Filtreleme**: Öğrencileri hızlıca bulma
- **Exception Handling**: Kapsamlı hata yönetimi
- **CORS Desteği**: Frontend-Backend iletişimi

## 🛠️ Teknolojiler

### Backend

- **Spring Boot 3.5.3**
- **Java 24**
- **Spring Data JPA**
- **PostgreSQL**
- **Spring Security**
- **Lombok**

### Frontend

- **React 19.1.0**
- **React Router DOM 6.8.0**
- **Bootstrap 5.1**
- **React Bootstrap**
- **React Bootstrap Icons**

## 📋 Gereksinimler

- Java 24+
- Node.js 16+
- PostgreSQL 12+
- Maven 3.6+

## 🚀 Kurulum

### 1. Veritabanı Kurulumu

PostgreSQL'de yeni bir veritabanı oluşturun:

```sql
CREATE DATABASE students;
```

### 2. Backend Kurulumu

```bash
cd backend

# Bağımlılıkları yükleyin
mvn clean install

# Uygulamayı çalıştırın
mvn spring-boot:run
```

Backend `http://localhost:8080` adresinde çalışacaktır.

### 3. Frontend Kurulumu

```bash
cd frontend

# Bağımlılıkları yükleyin
npm install

# Uygulamayı çalıştırın
npm start
```

Frontend `http://localhost:3000` adresinde çalışacaktır.

## 📁 Proje Yapısı

```
studentProject/
├── backend/
│   ├── src/main/java/com/example/backend/
│   │   ├── config/          # CORS ve diğer konfigürasyonlar
│   │   ├── controller/      # REST API controllers
│   │   ├── dataAccess/      # Repository interfaces
│   │   ├── entities/        # JPA entities
│   │   ├── exception/       # Custom exceptions
│   │   └── service/         # Business logic
│   └── src/main/resources/
│       └── application.properties
└── frontend/
    ├── src/
    │   ├── components/      # React components
    │   │   ├── Home.js      # Ana sayfa
    │   │   ├── Navbar.js    # Navigasyon
    │   │   └── StudentList.js # Öğrenci listesi
    │   ├── App.js           # Ana uygulama
    │   └── index.js         # Giriş noktası
    └── package.json
```

## 🔧 Konfigürasyon

### Backend Konfigürasyonu

`backend/src/main/resources/application.properties` dosyasında veritabanı ayarlarını düzenleyin:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/students
spring.datasource.username=postgres
spring.datasource.password=123
```

### Frontend Konfigürasyonu

API endpoint'leri `frontend/src/components/` dosyalarında `API_BASE_URL` değişkeni ile yapılandırılmıştır.

## 📚 API Endpoints

| Method | Endpoint                | Açıklama                 |
| ------ | ----------------------- | ------------------------ |
| GET    | `/api/v3/students`      | Tüm öğrencileri listele  |
| GET    | `/api/v3/students/{id}` | ID'ye göre öğrenci getir |
| POST   | `/api/v3/students`      | Yeni öğrenci ekle        |
| PUT    | `/api/v3/students/{id}` | Öğrenci güncelle         |
| DELETE | `/api/v3/students/{id}` | Öğrenci sil              |

## 🎨 Kullanım

1. **Ana Sayfa**: Sistem genel bakışı ve istatistikler
2. **Öğrenciler**: Öğrenci listesi ve yönetimi
3. **Yeni Öğrenci Ekle**: Modal form ile öğrenci ekleme
4. **Düzenleme**: Mevcut öğrenci bilgilerini güncelleme
5. **Silme**: Öğrenci kaydını silme (onay ile)

## 🔒 Güvenlik

- Spring Security entegrasyonu mevcut
- CORS konfigürasyonu yapılandırılmış
- Input validation ve sanitization

## 🐛 Hata Ayıklama

### Yaygın Sorunlar

1. **CORS Hatası**: Backend'de CORS konfigürasyonunun doğru olduğundan emin olun
2. **Veritabanı Bağlantısı**: PostgreSQL servisinin çalıştığından emin olun
3. **Port Çakışması**: 8080 ve 3000 portlarının boş olduğundan emin olun

### Loglar

Backend logları konsol çıktısında görülebilir. Frontend için browser developer tools kullanın.

## 🤝 Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/AmazingFeature`)
3. Commit yapın (`git commit -m 'Add some AmazingFeature'`)
4. Push yapın (`git push origin feature/AmazingFeature`)
5. Pull Request oluşturun

## 📄 Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

## 👨‍💻 Geliştirici

Bu proje eğitim amaçlı geliştirilmiştir.

---

**Not**: Bu proje geliştirme aşamasındadır ve production kullanımı için ek güvenlik önlemleri alınması gerekebilir.
