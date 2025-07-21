// Ana sayfa bileşeni (Home)
import React from "react";
import { useNavigate } from "react-router-dom";

/**
 * Home bileşeni, uygulamanın ana sayfasıdır.
 * Hoş geldiniz mesajı ve öğrenci listesine git butonu içerir.
 *
 * @returns {JSX.Element}
 */
const Home = () => {
  // Sayfa yönlendirmesi yapmak için navigate fonksiyonu
  const navigate = useNavigate();

  return (
    // Ortalanmış bir div ile hoş geldiniz mesajı ve buton
    <div style={{ textAlign: "center", marginTop: "100px" }}>
      {/* Başlık */}
      <h1 style={{ fontSize: "3rem" }}>Welcome to Student Management System</h1>
      {/* Öğrenci listesine yönlendiren buton */}
      <button
        style={{
          marginTop: "24px",
          background: "#1976ed",
          color: "white",
          border: "none",
          borderRadius: "10px",
          padding: "16px 24px",
          fontSize: "1.3rem",
          cursor: "pointer",
        }}
        // Butona tıklanınca "/students" sayfasına yönlendirir
        onClick={() => navigate("/students")}
      >
        Go to Student List
      </button>
    </div>
  );
};

export default Home;
