# 🎨 Frontend - React Uygulaması

Öğrenci Yönetim Sistemi'nin frontend uygulaması. React 18.2.0 ile geliştirilmiş, modern ve responsive web arayüzü.

## 🏗️ Mimari

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │  Config Server  │
│   (React)       │◄──►│  (Spring Boot)  │◄──►│  (Spring Cloud) │
│   Port: 3000    │    │   Port: 8080    │    │   Port: 8888    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🛠️ Teknoloji Stack

- **React 18.2.0** - UI framework
- **React Router DOM 6.20.1** - Client-side routing
- **Bootstrap 5.3.7** - CSS framework
- **JavaScript ES6+** - Programlama dili
- **Axios** - HTTP client
- **npm** - Package manager

## 📁 Proje Yapısı

```
src/
├── components/          # React bileşenleri
│   ├── Login.js         # Giriş sayfası
│   ├── StudentList.js   # Öğrenci listesi
│   ├── IpAddressList.js # IP adresi listesi
│   ├── LessonManagement.js # Ders yönetimi
│   ├── IpManagement.js  # IP yönetimi
│   ├── AssignIpAddressModal.js # IP atama modalı
│   ├── AssignLessonModal.js # Ders atama modalı
│   ├── RegisterModal.js # Kayıt modalı
│   └── IpAddressForm.js # IP form bileşeni
├── hooks/               # Custom React hooks
│   └── useNotification.js # Bildirim hook'u
├── utils/               # Yardımcı fonksiyonlar
│   └── api.js           # API servisleri
├── App.js               # Ana uygulama bileşeni
├── App.css              # Ana stil dosyası
├── index.js             # Uygulama giriş noktası
└── index.css            # Global stiller
```

## 🚀 Özellikler

### 🔐 Kimlik Doğrulama
- **JWT tabanlı authentication**
- **Role-based access control** (ADMIN/USER)
- **Güvenli token yönetimi**
- **Otomatik logout**

### 👥 Öğrenci Yönetimi
- **Öğrenci listesi görüntüleme**
- **Öğrenci ekleme/düzenleme**
- **Arama ve filtreleme**
- **Öğrenci onaylama sistemi**
- **Responsive tasarım**

### 📚 Ders Yönetimi
- **Ders listesi görüntüleme**
- **Ders ekleme/düzenleme**
- **Öğrenci-ders ilişkilendirme**
- **Modal tabanlı formlar**

### 🌐 IP Adresi Yönetimi
- **IP adresi listesi**
- **IP adresi ekleme/düzenleme**
- **Otomatik IP atama**
- **CIDR ve range desteği**
- **Network kontrolü**

### 🎨 UI/UX Özellikleri
- **Responsive tasarım**
- **Bootstrap 5.3.7**
- **Modal tabanlı formlar**
- **Toast bildirimleri**
- **Loading states**
- **Error handling**

## 🔧 Kurulum ve Çalıştırma

### Ön Gereksinimler
- **Node.js 18.0.0+**
- **npm 8.0.0+**

### 1. Bağımlılıkları Yükleme
```bash
# Proje dizininde
npm install
```

### 2. Geliştirme Sunucusunu Başlatma
```bash
npm start
```

Uygulama `http://localhost:3000` adresinde açılacaktır.

### 3. Production Build
```bash
npm run build
```

Build dosyaları `build/` klasöründe oluşturulacaktır.

## 📱 Bileşenler

### 🔐 Login.js
Kullanıcı girişi için ana bileşen.

```jsx
import React, { useState } from 'react';

const Login = () => {
  const [credentials, setCredentials] = useState({
    username: '',
    password: ''
  });

  const handleLogin = async () => {
    // Login logic
  };

  return (
    <div className="login-container">
      {/* Login form */}
    </div>
  );
};
```

### 👥 StudentList.js
Öğrenci listesi ve yönetimi.

```jsx
import React, { useState, useEffect } from 'react';

const StudentList = () => {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStudents();
  }, []);

  return (
    <div className="student-list">
      {/* Student list UI */}
    </div>
  );
};
```

### 🌐 IpAddressList.js
IP adresi listesi ve yönetimi.

```jsx
import React, { useState, useEffect } from 'react';

const IpAddressList = () => {
  const [ipAddresses, setIpAddresses] = useState([]);

  const handleAssignIp = async (studentId, ipId) => {
    // IP assignment logic
  };

  return (
    <div className="ip-address-list">
      {/* IP address list UI */}
    </div>
  );
};
```

## 🔧 Yapılandırma

### API Yapılandırması
`src/utils/api.js` dosyasında API endpoint'leri tanımlanmıştır:

```javascript
const API_BASE_URL = 'http://localhost:8080/api';

export const api = {
  // Auth endpoints
  login: (credentials) => axios.post(`${API_BASE_URL}/v1/auth/login`, credentials),
  register: (userData) => axios.post(`${API_BASE_URL}/v1/auth/register`, userData),
  
  // Student endpoints
  getStudents: () => axios.get(`${API_BASE_URL}/v3/students`),
  createStudent: (student) => axios.post(`${API_BASE_URL}/v3/students`, student),
  
  // IP address endpoints
  getIpAddresses: () => axios.get(`${API_BASE_URL}/v1/ip-addresses`),
  assignIpAddress: (studentId, ipIds) => axios.post(`${API_BASE_URL}/v1/ip-addresses/assign`, { studentId, ipAddressIds: ipIds })
};
```

### Environment Variables
`.env` dosyası oluşturarak environment variables tanımlayabilirsiniz:

```env
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_ENV=development
```

## 🎨 Stil ve Tasarım

### Bootstrap 5.3.7
Modern ve responsive tasarım için Bootstrap kullanılmıştır:

```jsx
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';

// Bootstrap bileşenleri
<Button variant="primary" onClick={handleClick}>
  Kaydet
</Button>

<Modal show={showModal} onHide={handleClose}>
  <Modal.Header closeButton>
    <Modal.Title>Öğrenci Ekle</Modal.Title>
  </Modal.Header>
  <Modal.Body>
    {/* Form content */}
  </Modal.Body>
</Modal>
```

### Custom CSS
`src/App.css` dosyasında özel stiller tanımlanmıştır:

```css
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.student-list {
  padding: 20px;
}

.ip-address-list {
  margin-top: 20px;
}
```

## 🔐 Güvenlik

### JWT Token Yönetimi
```javascript
// Token'ı localStorage'a kaydetme
const saveToken = (token) => {
  localStorage.setItem('token', token);
};

// Token'ı localStorage'dan alma
const getToken = () => {
  return localStorage.getItem('token');
};

// Token'ı silme (logout)
const removeToken = () => {
  localStorage.removeItem('token');
};
```

### Axios Interceptors
```javascript
// Request interceptor - Token ekleme
axios.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - Error handling
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      removeToken();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

## 🧪 Test

### Test Çalıştırma
```bash
# Testleri çalıştır
npm test

# Test coverage raporu
npm test -- --coverage

# Belirli test dosyasını çalıştır
npm test -- --testPathPattern=Login.test.js
```

### Test Örnekleri
```javascript
import { render, screen, fireEvent } from '@testing-library/react';
import Login from './Login';

test('login form renders correctly', () => {
  render(<Login />);
  
  expect(screen.getByLabelText(/kullanıcı adı/i)).toBeInTheDocument();
  expect(screen.getByLabelText(/şifre/i)).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /giriş yap/i })).toBeInTheDocument();
});

test('login form submission', async () => {
  render(<Login />);
  
  fireEvent.change(screen.getByLabelText(/kullanıcı adı/i), {
    target: { value: 'admin' }
  });
  fireEvent.change(screen.getByLabelText(/şifre/i), {
    target: { value: 'password' }
  });
  
  fireEvent.click(screen.getByRole('button', { name: /giriş yap/i }));
  
  // Assert login logic
});
```

## 📱 Responsive Tasarım

### Bootstrap Grid Sistemi
```jsx
<div className="container">
  <div className="row">
    <div className="col-md-6 col-lg-4">
      {/* Student card */}
    </div>
    <div className="col-md-6 col-lg-4">
      {/* IP address card */}
    </div>
    <div className="col-md-6 col-lg-4">
      {/* Lesson card */}
    </div>
  </div>
</div>
```

### Mobile-First Approach
```css
/* Mobile styles */
.student-card {
  margin-bottom: 15px;
}

/* Tablet styles */
@media (min-width: 768px) {
  .student-card {
    margin-bottom: 20px;
  }
}

/* Desktop styles */
@media (min-width: 992px) {
  .student-card {
    margin-bottom: 25px;
  }
}
```

## 🚀 Dağıtım

### Production Build
```bash
# Production build oluştur
npm run build

# Build dosyalarını kontrol et
ls -la build/
```

### Docker ile Dağıtım
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

CMD ["nginx", "-g", "daemon off;"]
```

### Static Hosting
Build dosyaları aşağıdaki platformlarda host edilebilir:
- **Netlify**
- **Vercel**
- **GitHub Pages**
- **AWS S3**
- **Firebase Hosting**

## 🔧 Geliştirme

### Kod Standartları
- **ESLint** - Code linting
- **Prettier** - Code formatting
- **Functional components** - Hooks kullanımı
- **PropTypes** - Type checking

### State Management
```javascript
// Local state with useState
const [students, setStudents] = useState([]);

// Custom hooks for reusable logic
const useStudents = () => {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  
  const fetchStudents = async () => {
    try {
      setLoading(true);
      const response = await api.getStudents();
      setStudents(response.data);
    } catch (error) {
      console.error('Error fetching students:', error);
    } finally {
      setLoading(false);
    }
  };
  
  return { students, loading, fetchStudents };
};
```

## 📞 Destek

- **GitHub Issues:** [Frontend Issues](https://github.com/mustafa-koroglu/studentProject/issues?q=label%3Afrontend)
- **Documentation:** [API Documentation](../docs/API_DOCUMENTATION.md)
- **Development Guide:** [Development Guide](../docs/DEVELOPMENT_GUIDE.md)

---

**Not:** Bu dokümantasyon sürekli güncellenmektedir. En güncel bilgiler için GitHub repository'sini kontrol edin.
