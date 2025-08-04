// Uygulamanın ana bileşeni ve yönlendirme yapısı
import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Login from "./Login";
import StudentList from "./StudentList";
import LessonManagement from "./LessonManagement";
import RegisterModal from "./RegisterModal";

function App() {
  const [role, setRole] = useState(localStorage.getItem("role") || null);
  const [showRegisterModal, setShowRegisterModal] = useState(false);

  const handleLogin = (userRole) => {
    setRole(userRole);
  };

  const handleLogout = () => {
    localStorage.clear();
    setRole(null);
  };

  return (
    <Router>
      {role && (
        <nav className="navbar navbar-dark bg-dark">
          <div className="container-fluid">
            <Link to="/" className="btn btn-outline-light me-2">
              Home
            </Link>
            <div className="navbar-nav me-auto d-flex flex-row gap-2">
              <Link to="/students" className="btn btn-outline-light">
                Öğrenci Listesi
              </Link>
              {role === "ADMIN" && (
                <Link to="/lessons" className="btn btn-outline-light">
                  Ders Yönetimi
                </Link>
              )}
            </div>
            <div className="d-flex align-items-center gap-2">
              {role === "ADMIN" && (
                <button
                  className="btn btn-warning"
                  onClick={() => setShowRegisterModal(true)}
                >
                  Kullanıcı Ekle
                </button>
              )}
              <button className="btn btn-outline-light" onClick={handleLogout}>
                Çıkış Yap
              </button>
            </div>
          </div>
        </nav>
      )}
      {showRegisterModal && (
        <RegisterModal onClose={() => setShowRegisterModal(false)} />
      )}
      <Routes>
        <Route
          path="/"
          element={
            !role ? (
              <Login onLogin={handleLogin} />
            ) : (
              <div className="container mt-5 text-center">
                <h1>Welcome to Student Management System</h1>
                <div className="mt-3">
                  <Link to="/students" className="btn btn-primary me-3">
                    Öğrenci Listesi
                  </Link>
                  {role === "ADMIN" && (
                    <Link to="/lessons" className="btn btn-success">
                      Ders Yönetimi
                    </Link>
                  )}
                </div>
              </div>
            )
          }
        />
        <Route
          path="/students"
          element={
            role ? <StudentList role={role} /> : <Login onLogin={handleLogin} />
          }
        />
        <Route
          path="/lessons"
          element={
            role ? (
              <LessonManagement role={role} />
            ) : (
              <Login onLogin={handleLogin} />
            )
          }
        />
      </Routes>
    </Router>
  );
}

export default App;
