// Kullanıcı giriş işlemini yöneten Login bileşeni
import React, { useState } from "react";

function Login({ onLogin }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });
      if (!response.ok) throw new Error("Giriş başarısız!");
      const data = await response.json();
      localStorage.setItem("token", data.token);
      localStorage.setItem("role", data.role);
      localStorage.setItem("username", data.username);
      if (onLogin) onLogin(data.role);
    } catch (err) {
      setError("Kullanıcı adı veya şifre hatalı!");
    }
  };

  return (
    <div
      className="d-flex justify-content-center align-items-center"
      style={{ minHeight: "80vh" }}
    >
      <form
        onSubmit={handleSubmit}
        className="border rounded-4 p-4 shadow-sm"
        style={{ minWidth: 340, background: "#fff" }}
      >
        <h2 className="mb-4 text-center fw-bold">Giriş Yap</h2>
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
        <button type="submit" className="btn btn-primary w-100 mb-2">
          Giriş
        </button>
        {error && (
          <div className="alert alert-danger text-center py-2">{error}</div>
        )}
      </form>
    </div>
  );
}

export default Login;
