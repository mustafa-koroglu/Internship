// React ve useState hook'unu import eder
import React, { useState } from "react";

// Login bileşeni, kullanıcı giriş işlemini yönetir
function Login({ onLogin }) {
  // Kullanıcı adı için state
  const [username, setUsername] = useState("");
  // Şifre için state
  const [password, setPassword] = useState("");
  // Hata mesajı için state
  const [error, setError] = useState("");

  // Form gönderildiğinde çalışacak fonksiyon
  const handleSubmit = async (e) => {
    // Sayfanın yeniden yüklenmesini engeller
    e.preventDefault();
    // Hata mesajını sıfırlar
    setError("");
    try {
      // Backend'e giriş isteği gönderir
      const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });
      // Yanıt başarılı değilse hata fırlatır
      if (!response.ok) throw new Error("Giriş başarısız!");
      // Yanıtı JSON olarak çözer
      const data = await response.json();
      // Token, rol ve kullanıcı adını localStorage'a kaydeder
      localStorage.setItem("token", data.token);
      localStorage.setItem("role", data.role);
      localStorage.setItem("username", data.username);
      // onLogin fonksiyonu varsa, kullanıcı rolünü ileterek çağırır
      if (onLogin) onLogin(data.role);
    } catch (err) {
      // Hata durumunda kullanıcıya mesaj gösterir
      setError("Kullanıcı adı veya şifre hatalı!");
    }
  };

  // Bileşenin render edilen kısmı
  return (
    // Sayfanın ortasında hizalanmış bir div
    <div
      className="d-flex justify-content-center align-items-center"
      style={{ minHeight: "80vh" }}
    >
      {/* Giriş formu */}
      <form
        onSubmit={handleSubmit}
        className="border rounded-4 p-4 shadow-sm"
        style={{ minWidth: 340, background: "#fff" }}
      >
        {/* Başlık */}
        <h2 className="mb-4 text-center fw-bold">Giriş Yap</h2>
        {/* Kullanıcı adı inputu */}
        <div className="form-floating mb-3">
          <input
            type="text"
            className="form-control"
            id="floatingUsername"
            placeholder="Kullanıcı Adı"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            autoFocus
          />
          <label htmlFor="floatingUsername">Kullanıcı Adı</label>
        </div>
        {/* Şifre inputu */}
        <div className="form-floating mb-3">
          <input
            type="password"
            className="form-control"
            id="floatingPassword"
            placeholder="Şifre"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <label htmlFor="floatingPassword">Şifre</label>
        </div>
        {/* Giriş butonu */}
        <button type="submit" className="btn btn-primary w-100 mb-2">
          Giriş
        </button>
        {/* Hata mesajı */}
        {error && (
          <div className="alert alert-danger text-center py-2">{error}</div>
        )}
      </form>
    </div>
  );
}

// Login bileşenini dışa aktarır
export default Login;
