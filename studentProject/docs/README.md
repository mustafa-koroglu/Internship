# 📚 Dokümantasyon

Bu klasör, Öğrenci Yönetim Sistemi projesinin tüm dokümantasyonunu içerir.

## 📋 Dokümantasyon Listesi

### 🚀 Başlangıç Rehberleri

- **[Ana README](../README.md)** - Proje genel bakışı ve hızlı başlangıç
- **[API Dokümantasyonu](API_DOCUMENTATION.md)** - REST API endpoint'leri ve kullanım örnekleri
- **[Geliştirme Rehberi](DEVELOPMENT_GUIDE.md)** - Geliştirme ortamı kurulumu ve kod standartları

### 🛠️ Geliştirme ve Dağıtım

- **[Dağıtım Rehberi](DEPLOYMENT_GUIDE.md)** - Docker, cloud ve production dağıtımı
- **[Katkıda Bulunma Rehberi](CONTRIBUTING.md)** - Projeye katkıda bulunma süreci
- **[Changelog](CHANGELOG.md)** - Sürüm geçmişi ve değişiklikler

### 📊 Özellik Dokümantasyonu

- **[CSV İşleme Rehberi](SPRING_INTEGRATION_CSV_PROCESSING.md)** - CSV dosya işleme detayları
- **[Detay Bilgileri](detay.txt)** - Proje detayları

## 🎯 Hızlı Başlangıç

### Yeni Geliştirici İçin

1. **[Ana README](../README.md)** - Proje hakkında genel bilgi
2. **[Geliştirme Rehberi](DEVELOPMENT_GUIDE.md)** - Ortam kurulumu
3. **[API Dokümantasyonu](API_DOCUMENTATION.md)** - API kullanımı
4. **[Katkıda Bulunma Rehberi](CONTRIBUTING.md)** - Katkı süreci

### Sistem Yöneticisi İçin

1. **[Dağıtım Rehberi](DEPLOYMENT_GUIDE.md)** - Production kurulumu
2. **[API Dokümantasyonu](API_DOCUMENTATION.md)** - API entegrasyonu

### API Geliştirici İçin

1. **[API Dokümantasyonu](API_DOCUMENTATION.md)** - Detaylı API referansı
2. **[Geliştirme Rehberi](DEVELOPMENT_GUIDE.md)** - Geliştirme standartları

## 🏗️ Proje Mimarisi

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

## 📖 Dokümantasyon Standartları

### Markdown Formatı

Tüm dokümantasyon dosyaları Markdown formatında yazılmıştır ve aşağıdaki standartları takip eder:

- **Başlık Hiyerarşisi:** H1 (#) ile başlayan ana başlık
- **İçindekiler:** Her dokümantasyon dosyası içindekiler bölümü içerir
- **Kod Blokları:** Syntax highlighting ile kod örnekleri
- **Emoji Kullanımı:** Bölümleri ayırmak için emoji kullanımı
- **Linkler:** İç ve dış linkler için uygun format

### Dokümantasyon Güncelleme

Dokümantasyon güncellemeleri için:

1. İlgili dosyayı düzenleyin
2. Değişiklikleri commit edin
3. Changelog'u güncelleyin
4. Pull Request oluşturun

### Dokümantasyon Şablonları

Yeni dokümantasyon dosyası oluştururken aşağıdaki şablonu kullanın:

```markdown
# Dokümantasyon Başlığı

Bu dokümantasyon, [konu] hakkında detaylı bilgi sağlar.

## 📋 İçindekiler

- [Bölüm 1](#bölüm-1)
- [Bölüm 2](#bölüm-2)
- [Bölüm 3](#bölüm-3)

## 🎯 Bölüm 1

İçerik buraya gelecek...

## 🔧 Bölüm 2

İçerik buraya gelecek...

## 📝 Bölüm 3

İçerik buraya gelecek...

---

**Not:** Bu dokümantasyon sürekli güncellenmektedir.
```

## 🔍 Dokümantasyon Arama

### Anahtar Kelimeler

- **API:** REST endpoints, authentication, validation
- **Backend:** Spring Boot, Java, PostgreSQL, JWT
- **Frontend:** React, JavaScript, Bootstrap, components
- **Deployment:** Docker, cloud, production, monitoring
- **Development:** setup, coding standards, testing
- **Security:** authentication, authorization, validation
- **CSV:** file processing, batch upload, validation
- **IP Management:** IPv4, IPv6, CIDR, subnet

### Dosya Organizasyonu

```
docs/
├── README.md                                    # Bu dosya
├── API_DOCUMENTATION.md                         # API referansı
├── DEVELOPMENT_GUIDE.md                         # Geliştirme rehberi
├── DEPLOYMENT_GUIDE.md                          # Dağıtım rehberi
├── CONTRIBUTING.md                              # Katkı rehberi
├── CHANGELOG.md                                 # Sürüm geçmişi
├── SPRING_INTEGRATION_CSV_PROCESSING.md         # CSV işleme rehberi
└── detay.txt                                    # Proje detayları
```

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

## 📞 Destek

Dokümantasyon ile ilgili sorularınız için:

- **GitHub Issues:** [Dokümantasyon Issues](https://github.com/mustafa-koroglu/studentProject/issues?q=label%3Adocumentation)
- **Discussions:** [GitHub Discussions](https://github.com/mustafa-koroglu/studentProject/discussions)
- **Email:** mustafaa.korogluu@gmail.com

## 🤝 Katkıda Bulunma

Dokümantasyonu iyileştirmek için:

1. **[Katkıda Bulunma Rehberi](CONTRIBUTING.md)**'ni okuyun
2. İyileştirme önerilerinizi GitHub Issues'da paylaşın
3. Pull Request ile doğrudan katkıda bulunun

### Dokümantasyon İyileştirme Alanları

- [ ] Daha fazla kod örneği ekleme
- [ ] Screenshot'lar ve diyagramlar ekleme
- [ ] Video tutorial'lar oluşturma
- [ ] Çoklu dil desteği ekleme
- [ ] Interactive dokümantasyon oluşturma
- [ ] API test örnekleri ekleme
- [ ] Troubleshooting rehberi oluşturma

## 📋 Hızlı Referans

### API Endpoints

| Endpoint                | Method | Açıklama          |
| ----------------------- | ------ | ----------------- |
| `/api/v1/auth/login`    | POST   | Kullanıcı girişi  |
| `/api/v1/auth/register` | POST   | Kullanıcı kaydı   |
| `/api/v3/students`      | GET    | Öğrenci listesi   |
| `/api/v3/students`      | POST   | Yeni öğrenci      |
| `/api/v1/ip-addresses`  | GET    | IP adresi listesi |
| `/api/v1/ip-addresses`  | POST   | Yeni IP adresi    |

### Yapılandırma Dosyaları

| Dosya                                              | Açıklama                     |
| -------------------------------------------------- | ---------------------------- |
| `backend/src/main/resources/application.yml`       | Backend yapılandırması       |
| `config-server/src/main/resources/application.yml` | Config Server yapılandırması |
| `frontend/package.json`                            | Frontend bağımlılıkları      |

### Önemli Portlar

| Servis        | Port | Açıklama             |
| ------------- | ---- | -------------------- |
| Frontend      | 3000 | React uygulaması     |
| Backend       | 8080 | Spring Boot API      |
| Config Server | 8888 | Yapılandırma servisi |
| PostgreSQL    | 5432 | Veritabanı           |

---

**Not:** Bu dokümantasyon sürekli güncellenmektedir. En güncel bilgiler için GitHub repository'sini kontrol edin.
