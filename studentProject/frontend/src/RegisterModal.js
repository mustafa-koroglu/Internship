import React, { useState } from "react";

/**
 * Admin kullanıcılar için yeni kullanıcı ekleme modalı.
 * Kullanıcı adı, şifre ve rol ile yeni kullanıcı kaydı yapılır.
 * Başarılı olursa modal kapanır, hata olursa mesaj gösterilir.
 *
 * @param {function} onClose - Modalı kapatma fonksiyonu
 */
function RegisterModal({ onClose }) {
  // Form state'leri
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("USER");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  // JWT token (admin olarak giriş yapılmış olmalı)
  const token = localStorage.getItem("token");

  /**
   * Form submit edildiğinde kullanıcı kaydını backend'e gönderir
   * @param {Event} e - Form submit event'i
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    try {
      const response = await fetch("http://localhost:8080/auth/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ username, password, role }),
      });
      if (!response.ok) {
        const data = await response.json().catch(() => ({}));
        throw new Error(data.message || "Kayıt başarısız!");
      }
      setSuccess("Kullanıcı başarıyla kaydedildi");
      setTimeout(() => onClose(), 1000);
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div
      className="modal show d-block"
      tabIndex="-1"
      style={{ background: "rgba(0,0,0,0.4)" }}
    >
      <div className="modal-dialog">
        <div className="modal-content">
          <div className="modal-header">
            <h5 className="modal-title">Yeni Kullanıcı Ekle</h5>
            <button
              type="button"
              className="btn-close"
              onClick={onClose}
            ></button>
          </div>
          <form onSubmit={handleSubmit}>
            <div className="modal-body">
              {/* Hata veya başarı mesajı */}
              {error && <div className="alert alert-danger">{error}</div>}
              {success && <div className="alert alert-success">{success}</div>}
              {/* Kullanıcı adı inputu */}
              <div className="mb-3">
                <label className="form-label">Kullanıcı Adı</label>
                <input
                  type="text"
                  className="form-control"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
              </div>
              {/* Şifre inputu */}
              <div className="mb-3">
                <label className="form-label">Şifre</label>
                <input
                  type="password"
                  className="form-control"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>
              {/* Rol seçimi */}
              <div className="mb-3">
                <label className="form-label">Rol</label>
                <select
                  className="form-select"
                  value={role}
                  onChange={(e) => setRole(e.target.value)}
                >
                  <option value="USER">USER</option>
                  <option value="ADMIN">ADMIN</option>
                </select>
              </div>
            </div>
            <div className="modal-footer">
              <button
                type="button"
                className="btn btn-secondary"
                onClick={onClose}
              >
                Kapat
              </button>
              <button type="submit" className="btn btn-primary">
                Kaydet
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default RegisterModal;
