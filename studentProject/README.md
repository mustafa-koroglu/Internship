# Student Management System

Bu proje, öğrenci yönetimi için geliştirilmiş tam yığın (full-stack) bir web uygulamasıdır. Backend Spring Boot (Java), frontend ise React (JavaScript) ile yazılmıştır.

## Özellikler

- Kullanıcı girişi (JWT ile güvenli kimlik doğrulama)
- Rol tabanlı yetkilendirme (ADMIN/USER)
- Öğrenci listeleme, arama, ekleme, güncelleme, silme
- Öğrenci numarası eşsiz (unique) kontrolü
- Modern ve responsive arayüz (Bootstrap)

---

## Kurulum

### 1. Backend (Spring Boot)

#### Gereksinimler

- Java 17 veya üzeri
- Maven
- PostgreSQL (veya uygun JDBC destekli başka bir veritabanı)

#### Adımlar

1. `backend/src/main/resources/application.properties` dosyasındaki veritabanı ayarlarını kendi ortamınıza göre düzenleyin.
2. Terminalde backend klasörüne gidin:
   ```sh
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```
3. Uygulama varsayılan olarak `http://localhost:8080` adresinde çalışacaktır.

### 2. Frontend (React)

#### Gereksinimler

- Node.js (v16+ önerilir)
- npm

#### Adımlar

1. Terminalde frontend klasörüne gidin:
   ```sh
   cd frontend
   npm install
   npm start
   ```
2. Uygulama varsayılan olarak `http://localhost:3000` adresinde açılır.

---

## Kullanım

- Giriş yapmak için kullanıcı adı ve şifre girin.
- Rolünüze göre öğrenci ekleyebilir, güncelleyebilir veya silebilirsiniz.
- Arama kutusunu kullanarak isim, soyisim veya numaraya göre filtreleme yapabilirsiniz.
- Aynı numaraya sahip iki öğrenci eklenemez; sistem uyarı verir.

---

## Önemli Notlar

- İlk kullanıcıyı ve rolleri veritabanına manuel eklemeniz gerekebilir.
- JWT token süresi dolarsa tekrar giriş yapmanız gerekir.
- Backend ve frontend portları farklıdır; CORS ayarları backend'de yapılmıştır.

---

## Proje Yapısı

```
studentProject/
  backend/   # Spring Boot backend
    src/main/java/com/example/backend/...
    src/main/resources/application.properties
    ...
  frontend/  # React frontend
    src/
    public/
    ...
```

---

## Katkı ve Lisans

Bu proje eğitim ve demo amaçlıdır. Katkıda bulunmak için fork'layıp pull request gönderebilirsiniz.

---

Herhangi bir sorunla karşılaşırsanız lütfen bir issue açın veya iletişime geçin.
