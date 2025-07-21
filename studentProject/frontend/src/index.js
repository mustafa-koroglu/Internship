// Uygulamanın giriş noktası (index.js)
import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./App";
import reportWebVitals from "./reportWebVitals";
import "bootstrap/dist/css/bootstrap.min.css";

/**
 * React uygulamasının bağlanacağı root DOM elemanını seçer
 */
const root = ReactDOM.createRoot(document.getElementById("root"));

/**
 * Uygulamayı React StrictMode ile render eder (geliştirme sırasında ek uyarılar sağlar)
 */
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

/**
 * Uygulamanın performansını ölçmek için reportWebVitals fonksiyonunu çağırır
 * (İsterseniz loglama veya analiz için fonksiyon gönderebilirsiniz)
 */
reportWebVitals();
