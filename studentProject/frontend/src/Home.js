// React kütüphanesini import eder
import React from "react";
// Sayfa yönlendirmesi için useNavigate hook'unu import eder
import { useNavigate } from "react-router-dom";

// Home bileşeni, ana sayfa olarak kullanılır
const Home = () => {
  // Sayfa yönlendirmesi yapmak için navigate fonksiyonunu alır
  const navigate = useNavigate();

  // Bileşenin render edilen kısmı
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

// Home bileşenini dışa aktarır
export default Home;
