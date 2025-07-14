// React ana kütüphanesini import eder
import React from "react";
// ReactDOM kütüphanesini (React uygulamasını DOM'a bağlamak için) import eder
import ReactDOM from "react-dom/client";
// Uygulama genelinde geçerli olacak CSS dosyasını import eder
import "./index.css";
// Ana uygulama bileşenini import eder
import App from "./App";
// Performans ölçümü için reportWebVitals fonksiyonunu import eder
import reportWebVitals from "./reportWebVitals";
// Bootstrap CSS kütüphanesini import eder (tüm uygulamada stil için)
import "bootstrap/dist/css/bootstrap.min.css";

// React uygulamasının bağlanacağı root DOM elemanını seçer
const root = ReactDOM.createRoot(document.getElementById("root"));

// Uygulamayı React StrictMode ile render eder (geliştirme sırasında ek uyarılar sağlar)
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

// Uygulamanın performansını ölçmek için reportWebVitals fonksiyonunu çağırır
// (İsterseniz loglama veya analiz için fonksiyon gönderebilirsiniz)
reportWebVitals();
