// React ve gerekli hook'u import eder
import React, { useState } from "react";
// React Router'dan gerekli bileşenleri import eder
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
// Login ve StudentList bileşenlerini import eder
import Login from "./Login";
import StudentList from "./StudentList";

// Ana sayfa bileşeni (Home)
function Home() {
  // Ana sayfa içeriğini render eder
  return (
    <div className="container mt-5 text-center">
      {/* Başlık */}
      <h1>Welcome to Student Management System</h1>
      {/* Öğrenci listesine yönlendiren buton */}
      <Link to="/students" className="btn btn-primary mt-3">
        Go to Student List
      </Link>
    </div>
  );
}

// Uygulamanın ana bileşeni
function App() {
  // Kullanıcı rolünü state olarak tutar (localStorage'dan başlatılır)
  const [role, setRole] = useState(localStorage.getItem("role") || null);

  // Giriş yapıldığında rolü günceller
  const handleLogin = (userRole) => {
    setRole(userRole);
  };

  // Çıkış yapıldığında localStorage'ı temizler ve rolü sıfırlar
  const handleLogout = () => {
    localStorage.clear();
    setRole(null);
  };

  // Uygulamanın render edilen kısmı
  return (
    // Router ile sayfa yönlendirmelerini yönetir
    <Router>
      {/* Sadece giriş yapılmışsa navbar gösterilir */}
      {role && (
        <nav className="navbar navbar-dark bg-dark">
          <div className="container-fluid">
            {/* Ana sayfa linki */}
            <Link to="/" className="navbar-brand">
              Home
            </Link>
            {/* Çıkış butonu */}
            <button className="btn btn-outline-light" onClick={handleLogout}>
              Çıkış Yap
            </button>
          </div>
        </nav>
      )}
      {/* Sayfa yönlendirme kuralları */}
      <Routes>
        {/* Ana sayfa: Giriş yapılmamışsa Login, yapılmışsa Home gösterilir */}
        <Route
          path="/"
          element={!role ? <Login onLogin={handleLogin} /> : <Home />}
        />
        {/* Öğrenci listesi: Giriş yapılmışsa StudentList, yapılmamışsa Login gösterilir */}
        <Route
          path="/students"
          element={
            role ? <StudentList role={role} /> : <Login onLogin={handleLogin} />
          }
        />
      </Routes>
    </Router>
  );
}

// App bileşenini dışa aktarır
export default App;
