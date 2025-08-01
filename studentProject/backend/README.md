## Yeni Özellik: Admin ile Kullanıcı Ekleme

- Sadece admin rolü ile giriş yapanlar `/auth/register` endpointini kullanarak yeni kullanıcı ekleyebilir.
- Endpoint: `POST /auth/register` (Authorization header ile JWT token gereklidir)
- Body: `{ "username": "...", "password": "...", "role": "ADMIN" | "USER" }`
- Kullanıcı adı benzersiz olmalıdır, şifre hashlenerek kaydedilir.

## CSV İşleme Job'ı

### Özellikler

- Her saat başı otomatik olarak çalışır
- `../csv-files` klasöründeki `.csv` dosyalarını kontrol eder (ana proje dizininde)
- CSV dosyalarından öğrenci kayıtlarını okur ve veritabanına kaydeder
- **Temiz Mimari**: 
  - `CsvFileProcessor`: CSV dosya işleme mantığı
  - `CsvProcessingService`: Dosya yönetimi ve thread yönetimi
  - Tek sorumluluk prensibi uygulanmış
- **Multithreading desteği**: Dosyalar sırayla işlenir (bir dosya bittiğinde diğeri başlar)
  - CompletableFuture ile asenkron zincir oluşturulur
  - Tek thread executor kullanılarak sıralı işleme sağlanır
  - Her dosya için detaylı loglama yapılır
  - Hata durumunda diğer dosyalar etkilenmez
- İşlem tamamlandıktan sonra dosya ismini `.done` olarak günceller
- Hata durumunda dosya ismini `.fail` olarak günceller

### CSV Dosya Formatı

CSV dosyaları aşağıdaki sütunları içermelidir:

```csv
name,surname,number
Ahmet,Yılmaz,2024001
Ayşe,Kaya,2024002
```

### Desteklenen Sütun İsimleri

- **name**: `name`, `ad`, `isim`
- **surname**: `surname`, `soyad`, `lastname`
- **number**: `number`, `numara`, `student_number`, `ogrenci_no`

### Manuel İşleme

CSV dosyalarını manuel olarak işlemek için:

- Endpoint: `POST /api/csv/process`
- Authentication gerekmez

### Konfigürasyon

`application.yml` dosyasında CSV klasörü konfigürasyonu:

```yaml
csv:
  watch:
    directory: ../csv-files
```

### Cron Expression

Job her saat başı çalışır: `0 0 * * * *`
