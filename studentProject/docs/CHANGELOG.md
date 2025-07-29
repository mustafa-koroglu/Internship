# Changelog

Bu dosya, Öğrenci Yönetim Sistemi'nde yapılan tüm önemli değişiklikleri kaydeder.

Bu proje [Semantic Versioning](https://semver.org/lang/tr/) kurallarına uyar.

## [Unreleased]

### Added

- Kapsamlı dokümantasyon eklendi
- API dokümantasyonu oluşturuldu
- Dağıtım rehberi eklendi
- Geliştirme rehberi eklendi
- **Öğrenci onay sistemi eklendi**
  - `verified` ve `view` boolean alanları eklendi
  - CSV'den okunan öğrenciler otomatik olarak onaylanmamış ve gizli olarak işaretlenir
  - Manuel eklenen öğrenciler otomatik onaylanır ve görünür olur
  - Admin onaylama ve görünürlük kontrolü özellikleri
  - Kullanıcılar sadece onaylanmış ve görünür öğrencileri görebilir
  - Yeni API endpoint'leri: `/students/verified`, `/students/unverified`, `/students/{id}/approve`, `/students/{id}/visibility`

### Changed

- README.md dosyası güncellendi ve genişletildi
- Student entity'sine `verified` ve `view` alanları eklendi
- StudentResponse, CreateStudentRequest, UpdateStudentRequest DTO'ları güncellendi
- StudentsRepository'ye yeni sorgu metodları eklendi
- StudentService interface'i genişletildi
- StudentManager'a yeni iş mantığı metodları eklendi
- StudentController'a yeni endpoint'ler eklendi
- CsvProcessingService'te CSV'den okunan öğrenciler onaylanmamış olarak işaretlenir
- Frontend StudentList bileşeni güncellendi
  - Admin ve user için farklı görünümler
  - Onaylama ve görünürlük kontrol butonları
  - Farklı endpoint'ler kullanımı

## [1.0.0] - 2024-01-01

### Added

- İlk sürüm yayınlandı
- Spring Boot backend uygulaması
- React frontend uygulaması
- PostgreSQL veritabanı entegrasyonu
- JWT tabanlı kimlik doğrulama
- Öğrenci CRUD işlemleri
- CSV dosya yükleme ve işleme
- Arama ve filtreleme özellikleri
- Responsive web tasarımı
- Bootstrap UI framework entegrasyonu
- Spring Cloud Config Server
- Global exception handling
- CORS yapılandırması
- Lombok entegrasyonu
- OpenCSV kütüphanesi entegrasyonu

### Features

- **Backend API Endpoints:**

  - `POST /api/v1/auth/register` - Kullanıcı kaydı
  - `POST /api/v1/auth/login` - Kullanıcı girişi
  - `GET /api/v3/students` - Öğrenci listesi
  - `GET /api/v3/students/{id}` - Öğrenci detayı
  - `POST /api/v3/students` - Yeni öğrenci ekleme
  - `PUT /api/v3/students/{id}` - Öğrenci güncelleme
  - `DELETE /api/v3/students/{id}` - Öğrenci silme
  - `GET /api/v3/students/search` - Öğrenci arama
  - `POST /api/v3/csv/upload` - CSV dosya yükleme

- **Frontend Components:**

  - Login sayfası
  - Öğrenci listesi
  - Öğrenci ekleme/düzenleme formu
  - Arama ve filtreleme
  - Responsive navbar
  - Modal tabanlı formlar

- **Database Schema:**
  - `app_user` tablosu (kullanıcı yönetimi)
  - `student` tablosu (öğrenci verileri)

### Technical Stack

- **Backend:** Java 21, Spring Boot 3.3.0, Spring Security, Spring Data JPA
- **Frontend:** React 19.1.0, Bootstrap 5.3.7, React Router DOM 7.6.3
- **Database:** PostgreSQL 15
- **Build Tools:** Maven, npm
- **Authentication:** JWT (JSON Web Tokens)
- **CSV Processing:** OpenCSV 5.8

### Security Features

- JWT tabanlı kimlik doğrulama
- Şifre hashleme (BCrypt)
- CORS yapılandırması
- Input validation
- SQL injection koruması

### Performance Features

- Sayfalama (pagination)
- Veritabanı indeksleri
- Connection pooling
- Optimized queries

---

## Sürüm Numaralandırma

Bu proje [Semantic Versioning](https://semver.org/lang/tr/) (SemVer) kurallarına uyar.

### Format: MAJOR.MINOR.PATCH

- **MAJOR:** Uyumsuz API değişiklikleri
- **MINOR:** Geriye uyumlu yeni özellikler
- **PATCH:** Geriye uyumlu hata düzeltmeleri

### Örnekler:

- `1.0.0` - İlk kararlı sürüm
- `1.1.0` - Yeni özellikler eklendi
- `1.1.1` - Hata düzeltmeleri
- `2.0.0` - Büyük değişiklikler (uyumsuz)

## Değişiklik Türleri

### Added

Yeni özellikler eklendi.

### Changed

Mevcut işlevsellikte değişiklikler yapıldı.

### Deprecated

Yakında kaldırılacak özellikler işaretlendi.

### Removed

Kaldırılan özellikler.

### Fixed

Hata düzeltmeleri.

### Security

Güvenlik açıkları düzeltildi.

## Katkıda Bulunma

Yeni değişiklikler eklerken:

1. Uygun değişiklik türünü seçin
2. Açık ve anlaşılır açıklamalar yazın
3. Breaking changes için detaylı açıklama ekleyin
4. Tarih formatını koruyun (YYYY-MM-DD)

### Örnek Giriş:

```markdown
## [1.2.0] - 2024-02-15

### Added

- Yeni öğrenci raporlama özelliği
- Excel export fonksiyonu

### Changed

- Arama algoritması iyileştirildi
- UI performansı artırıldı

### Fixed

- Login sayfasında CSS hatası düzeltildi
- Database connection timeout sorunu çözüldü
```

---

**Not:** Bu changelog, projenin gelişim sürecini takip etmek için kullanılır. Tüm önemli değişiklikler burada belgelenmelidir.
