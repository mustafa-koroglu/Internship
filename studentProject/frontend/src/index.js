// Uygulamanın giriş noktası
import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./App";
import "bootstrap/dist/css/bootstrap.min.css";

// React uygulamasının bağlanacağı root DOM elemanını seçer
const root = ReactDOM.createRoot(document.getElementById("root"));

// Uygulamayı React StrictMode ile render eder
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
