// Uygulamanın ana bileşeni ve yönlendirme yapısı burada tanımlanır.
import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Login from "./Login";
import StudentList from "./StudentList";
import RegisterModal from "./RegisterModal";

/**
 * Ana sayfa bileşeni (Home)
 * Giriş yapmış kullanıcıya hoş geldiniz ve öğrenci listesine git butonu gösterir.
 */
function Home() {
  return (
    <div className="container mt-5 text-center">
      <h1>Welcome to Student Management System</h1>
      <Link to="/students" className="btn btn-primary mt-3">
        Go to Student List
      </Link>
    </div>
  );
}

/**
 * Uygulamanın ana bileşeni.
 * Kullanıcı rolüne göre yönlendirme, navbar ve modal yönetimi burada yapılır.
 */
function App() {
  // Kullanıcı rolünü state olarak tutar (localStorage'dan başlatılır)
  const [role, setRole] = useState(localStorage.getItem("role") || null);
  // Kullanıcı ekleme modalının açık/kapalı durumu
  const [showRegisterModal, setShowRegisterModal] = useState(false);

  /**
   * Giriş yapıldığında rolü günceller
   * @param {string} userRole - Kullanıcı rolü (ADMIN/USER)
   */
  const handleLogin = (userRole) => {
    setRole(userRole);
  };

  /**
   * Çıkış yapıldığında localStorage'ı temizler ve rolü sıfırlar
   */
  const handleLogout = () => {
    localStorage.clear();
    setRole(null);
  };

  return (
    <Router>
      {/* Sadece giriş yapılmışsa navbar gösterilir */}
      {role && (
        <nav className="navbar navbar-dark bg-dark">
          <div className="container-fluid">
            {/* Ana sayfa linki */}
            <Link to="/" className="navbar-brand">
              Home
            </Link>
            <div className="d-flex align-items-center gap-2">
              {/* Sadece admin ise kullanıcı ekle butonu */}
              {role === "ADMIN" && (
                <button
                  className="btn btn-warning"
                  onClick={() => setShowRegisterModal(true)}
                >
                  Kullanıcı Ekle
                </button>
              )}
              {/* Çıkış butonu */}
              <button className="btn btn-outline-light" onClick={handleLogout}>
                Çıkış Yap
              </button>
            </div>
          </div>
        </nav>
      )}
      {/* Kullanıcı ekleme modalı */}
      {showRegisterModal && (
        <RegisterModal onClose={() => setShowRegisterModal(false)} />
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

export default App;
