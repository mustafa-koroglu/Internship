# API Dokümantasyonu

Bu dokümantasyon, Öğrenci Yönetim Sistemi'nin REST API endpoint'lerini detaylandırır.

## 📋 İçindekiler

- [Genel Bilgiler](#genel-bilgiler)
- [Kimlik Doğrulama](#kimlik-doğrulama)
- [Öğrenci Yönetimi](#öğrenci-yönetimi)
- [CSV İşleme](#csv-işleme)
- [Dosya Yönetimi](#dosya-yönetimi)
- [Hata Kodları](#hata-kodları)
- [Örnekler](#örnekler)

## 🔧 Genel Bilgiler

### Base URL

```
http://localhost:8080
```

### Content Type

```
Content-Type: application/json
```

### Kimlik Doğrulama

API'nin çoğu endpoint'i JWT token gerektirir. Token'ı Authorization header'ında gönderin:

```
Authorization: Bearer <JWT_TOKEN>
```

### Response Format

Tüm başarılı response'lar aşağıdaki formatta döner:

```json
{
  "success": true,
  "data": {},
  "message": "İşlem başarılı",
  "timestamp": "2024-01-01T00:00:00"
}
```

## 🔐 Kimlik Doğrulama

### POST /api/v1/auth/register

Yeni kullanıcı kaydı oluşturur.

**Request Body:**

```json
{
  "username": "string",
  "password": "string",
  "email": "string"
}
```

**Validation Rules:**

- `username`: 3-50 karakter, alfanumerik
- `password`: 6-100 karakter
- `email`: Geçerli email formatı

**Response (200):**

```json
{
  "success": true,
  "data": null,
  "message": "Kullanıcı başarıyla kaydedildi",
  "timestamp": "2024-01-01T00:00:00"
}
```

**Error Response (400):**

```json
{
  "success": false,
  "error": "VALIDATION_ERROR",
  "message": "Geçersiz email formatı",
  "timestamp": "2024-01-01T00:00:00"
}
```

### POST /api/v1/auth/login

Kullanıcı girişi yapar ve JWT token döner.

**Request Body:**

```json
{
  "username": "string",
  "password": "string"
}
```

**Response (200):**

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "testuser",
    "expiresIn": 3600
  },
  "message": "Giriş başarılı",
  "timestamp": "2024-01-01T00:00:00"
}
```

**Error Response (401):**

```json
{
  "success": false,
  "error": "AUTHENTICATION_ERROR",
  "message": "Geçersiz kullanıcı adı veya şifre",
  "timestamp": "2024-01-01T00:00:00"
}
```

## 👥 Öğrenci Yönetimi

### GET /api/v3/students

Tüm öğrencileri sayfalı olarak listeler.

**Headers:**

```
Authorization: Bearer <JWT_TOKEN>
```

**Query Parameters:**

- `page` (optional): Sayfa numarası (default: 0)
- `size` (optional): Sayfa boyutu (default: 10, max: 100)
- `sort` (optional): Sıralama alanı (default: id)
- `direction` (optional): Sıralama yönü (ASC/DESC, default: ASC)

**Response (200):**

```json
{
  "success": true,
  "data": {
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
        "grade": 3.5,
        "createdAt": "2024-01-01T00:00:00",
        "updatedAt": "2024-01-01T00:00:00"
      }
    ],
    "totalElements": 100,
    "totalPages": 10,
    "currentPage": 0,
    "size": 10,
    "first": true,
    "last": false
  },
  "message": "Öğrenciler başarıyla getirildi",
  "timestamp": "2024-01-01T00:00:00"
}
```

### GET /api/v3/students/{id}

Belirtilen ID'ye sahip öğrenciyi getirir.

**Headers:**

```
Authorization: Bearer <JWT_TOKEN>
```

**Path Parameters:**

- `id`: Öğrenci ID'si (Long)

**Response (200):**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "studentNumber": "2024001",
    "firstName": "Ahmet",
    "lastName": "Yılmaz",
    "email": "ahmet@example.com",
    "phoneNumber": "555-123-4567",
    "birthDate": "2000-01-01",
    "department": "Bilgisayar Mühendisliği",
    "grade": 3.5,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  },
  "message": "Öğrenci başarıyla getirildi",
  "timestamp": "2024-01-01T00:00:00"
}
```

**Error Response (404):**

```json
{
  "success": false,
  "error": "RESOURCE_NOT_FOUND",
  "message": "Öğrenci bulunamadı",
  "timestamp": "2024-01-01T00:00:00"
}
```

### POST /api/v3/students

Yeni öğrenci ekler.

**Headers:**

```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

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
  "grade": 3.5
}
```

**Validation Rules:**

- `studentNumber`: 3-20 karakter, benzersiz olmalı
- `firstName`: 2-50 karakter
- `lastName`: 2-50 karakter
- `email`: Geçerli email formatı, benzersiz olmalı
- `phoneNumber`: 10-20 karakter (optional)
- `birthDate`: Geçerli tarih (optional)
- `department`: 2-100 karakter (optional)
- `grade`: 0.00-4.00 arası (optional)

**Response (201):**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "studentNumber": "2024001",
    "firstName": "Ahmet",
    "lastName": "Yılmaz",
    "email": "ahmet@example.com",
    "phoneNumber": "555-123-4567",
    "birthDate": "2000-01-01",
    "department": "Bilgisayar Mühendisliği",
    "grade": 3.5,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  },
  "message": "Öğrenci başarıyla oluşturuldu",
  "timestamp": "2024-01-01T00:00:00"
}
```

### PUT /api/v3/students/{id}

Öğrenci bilgilerini günceller.

**Headers:**

```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Path Parameters:**

- `id`: Öğrenci ID'si (Long)

**Request Body:** (POST ile aynı format)

**Response (200):**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "studentNumber": "2024001",
    "firstName": "Ahmet",
    "lastName": "Yılmaz",
    "email": "ahmet@example.com",
    "phoneNumber": "555-123-4567",
    "birthDate": "2000-01-01",
    "department": "Bilgisayar Mühendisliği",
    "grade": 3.75,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  },
  "message": "Öğrenci başarıyla güncellendi",
  "timestamp": "2024-01-01T00:00:00"
}
```

### DELETE /api/v3/students/{id}

Öğrenciyi siler.

**Headers:**

```
Authorization: Bearer <JWT_TOKEN>
```

**Path Parameters:**

- `id`: Öğrenci ID'si (Long)

**Response (200):**

```json
{
  "success": true,
  "data": null,
  "message": "Öğrenci başarıyla silindi",
  "timestamp": "2024-01-01T00:00:00"
}
```

### GET /api/v3/students/search

Öğrenci arama yapar.

**Headers:**

```
Authorization: Bearer <JWT_TOKEN>
```

**Query Parameters:**

- `q`: Arama terimi (isim, soyisim veya numara)
- `page` (optional): Sayfa numarası (default: 0)
- `size` (optional): Sayfa boyutu (default: 10)

**Response (200):**

```json
{
  "success": true,
  "data": {
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
        "grade": 3.5
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0
  },
  "message": "Arama sonuçları getirildi",
  "timestamp": "2024-01-01T00:00:00"
}
```

## 📊 CSV İşleme

### POST /api/v3/csv/upload

CSV dosyası yükler ve işler.

**Headers:**

```
Authorization: Bearer <JWT_TOKEN>
Content-Type: multipart/form-data
```

**Form Data:**

- `file`: CSV dosyası (max: 10MB)

**CSV Format:**

```csv
studentNumber,firstName,lastName,email,phoneNumber,birthDate,department,grade
2024001,Ahmet,Yılmaz,ahmet@example.com,555-123-4567,2000-01-01,Bilgisayar Mühendisliği,3.50
2024002,Ayşe,Demir,ayse@example.com,555-123-4568,2000-02-01,Elektrik Mühendisliği,3.75
```

**Response (200):**

```json
{
  "success": true,
  "data": {
    "totalRows": 100,
    "successfulRows": 95,
    "failedRows": 5,
    "errors": [
      {
        "row": 6,
        "field": "email",
        "message": "Geçersiz email formatı"
      }
    ]
  },
  "message": "CSV dosyası başarıyla işlendi",
  "timestamp": "2024-01-01T00:00:00"
}
```

## ❌ Hata Kodları

### HTTP Status Codes

| Kod | Açıklama           |
| --- | ------------------ |
| 200 | Başarılı           |
| 201 | Oluşturuldu        |
| 400 | Geçersiz İstek     |
| 401 | Yetkisiz Erişim    |
| 403 | Yasaklı            |
| 404 | Bulunamadı         |
| 409 | Çakışma            |
| 422 | İşlenemeyen Varlık |
| 500 | Sunucu Hatası      |

### Error Types

| Error Code            | Açıklama                |
| --------------------- | ----------------------- |
| VALIDATION_ERROR      | Doğrulama hatası        |
| AUTHENTICATION_ERROR  | Kimlik doğrulama hatası |
| AUTHORIZATION_ERROR   | Yetkilendirme hatası    |
| RESOURCE_NOT_FOUND    | Kaynak bulunamadı       |
| DUPLICATE_RESOURCE    | Çakışan kaynak          |
| INTERNAL_SERVER_ERROR | Sunucu hatası           |

## 📝 Örnekler

### cURL Örnekleri

#### Kullanıcı Kaydı

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com"
  }'
```

#### Kullanıcı Girişi

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

#### Öğrenci Listesi

```bash
curl -X GET http://localhost:8080/api/v3/students \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Öğrenci Ekleme

```bash
curl -X POST http://localhost:8080/api/v3/students \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "studentNumber": "2024001",
    "firstName": "Ahmet",
    "lastName": "Yılmaz",
    "email": "ahmet@example.com",
    "phoneNumber": "555-123-4567",
    "birthDate": "2000-01-01",
    "department": "Bilgisayar Mühendisliği",
    "grade": 3.50
  }'
```

### JavaScript/Node.js Örnekleri

#### Axios ile API Kullanımı

```javascript
const axios = require("axios");

// Login
const login = async (username, password) => {
  try {
    const response = await axios.post(
      "http://localhost:8080/api/v1/auth/login",
      {
        username,
        password,
      }
    );
    return response.data.data.token;
  } catch (error) {
    console.error("Login error:", error.response.data);
  }
};

// Get Students
const getStudents = async (token) => {
  try {
    const response = await axios.get("http://localhost:8080/api/v3/students", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data.data;
  } catch (error) {
    console.error("Get students error:", error.response.data);
  }
};
```

### Python Örnekleri

#### Requests ile API Kullanımı

```python
import requests
import json

# Login
def login(username, password):
    url = "http://localhost:8080/api/v1/auth/login"
    data = {
        "username": username,
        "password": password
    }
    response = requests.post(url, json=data)
    if response.status_code == 200:
        return response.json()['data']['token']
    else:
        print(f"Login error: {response.json()}")
        return None

# Get Students
def get_students(token):
    url = "http://localhost:8080/api/v3/students"
    headers = {
        "Authorization": f"Bearer {token}"
    }
    response = requests.get(url, headers=headers)
    if response.status_code == 200:
        return response.json()['data']
    else:
        print(f"Get students error: {response.json()}")
        return None
```

## 📁 Dosya Yönetimi

Bu bölüm, CSV dosyalarının işlenme kayıtlarını yönetmek için kullanılan endpoint'leri içerir.

### GET /api/v3/files

Tüm dosya kayıtlarını getirir (en son işlenenler önce).

**Authorization:** ADMIN rolü gerekli

**Response (200):**

```json
[
  {
    "id": 1,
    "fileName": "students",
    "fullFileName": "students.csv",
    "status": "DONE",
    "processedAt": "2024-01-01T10:30:00",
    "studentCount": 25,
    "description": "25 öğrenci başarıyla işlendi"
  },
  {
    "id": 2,
    "fileName": "invalid_file",
    "fullFileName": "invalid_file.csv",
    "status": "FAIL",
    "processedAt": "2024-01-01T10:25:00",
    "studentCount": 0,
    "description": "Geçersiz header formatı"
  }
]
```

### GET /api/v3/files/{id}

Belirli bir dosya kaydını getirir.

**Authorization:** ADMIN rolü gerekli

**Response (200):**

```json
{
  "id": 1,
  "fileName": "students",
  "fullFileName": "students.csv",
  "status": "DONE",
  "processedAt": "2024-01-01T10:30:00",
  "studentCount": 25,
  "description": "25 öğrenci başarıyla işlendi"
}
```

### GET /api/v3/files/status/{status}

Belirli bir duruma sahip dosyaları getirir.

**Authorization:** ADMIN rolü gerekli

**Parameters:**

- `status`: DONE veya FAIL

**Response (200):**

```json
[
  {
    "id": 1,
    "fileName": "students",
    "fullFileName": "students.csv",
    "status": "DONE",
    "processedAt": "2024-01-01T10:30:00",
    "studentCount": 25,
    "description": "25 öğrenci başarıyla işlendi"
  }
]
```

### GET /api/v3/files/search

Dosya adına göre arama yapar.

**Authorization:** ADMIN rolü gerekli

**Query Parameters:**

- `fileName`: Dosya adı (kısmi eşleşme)

**Response (200):**

```json
[
  {
    "id": 1,
    "fileName": "students",
    "fullFileName": "students.csv",
    "status": "DONE",
    "processedAt": "2024-01-01T10:30:00",
    "studentCount": 25,
    "description": "25 öğrenci başarıyla işlendi"
  }
]
```

### GET /api/v3/files/recent

En son işlenen N dosyayı getirir.

**Authorization:** ADMIN rolü gerekli

**Query Parameters:**

- `limit`: Limit sayısı (varsayılan: 10)

**Response (200):**

```json
[
  {
    "id": 1,
    "fileName": "students",
    "fullFileName": "students.csv",
    "status": "DONE",
    "processedAt": "2024-01-01T10:30:00",
    "studentCount": 25,
    "description": "25 öğrenci başarıyla işlendi"
  }
]
```

### GET /api/v3/files/date-range

Belirli bir tarih aralığında işlenen dosyaları getirir.

**Authorization:** ADMIN rolü gerekli

**Query Parameters:**

- `startDate`: Başlangıç tarihi (yyyy-MM-dd HH:mm:ss)
- `endDate`: Bitiş tarihi (yyyy-MM-dd HH:mm:ss)

**Response (200):**

```json
[
  {
    "id": 1,
    "fileName": "students",
    "fullFileName": "students.csv",
    "status": "DONE",
    "processedAt": "2024-01-01T10:30:00",
    "studentCount": 25,
    "description": "25 öğrenci başarıyla işlendi"
  }
]
```

### GET /api/v3/files/stats

Dosya işleme istatistiklerini getirir.

**Authorization:** ADMIN rolü gerekli

**Response (200):**

```json
{
  "totalFiles": 10,
  "doneFiles": 8,
  "failFiles": 2,
  "totalStudents": 200
}
```

---

**Not:** Bu dokümantasyon API'nin güncel versiyonunu yansıtır. Değişiklikler için repository'yi kontrol edin.
