import React from "react";
import { useNavigate } from "react-router-dom";

const Home = () => {
  const navigate = useNavigate();

  return (
    <div style={{ textAlign: "center", marginTop: "100px" }}>
      <h1 style={{ fontSize: "3rem" }}>Welcome to Student Management System</h1>
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
        onClick={() => navigate("/students")}
      >
        Go to Student List
      </button>
    </div>
  );
};

export default Home;
