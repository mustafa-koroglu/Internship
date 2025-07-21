// Uygulamanın üst kısmında gezinme çubuğu olarak kullanılan Navbar bileşeni.
import React from "react";
import { Link } from "react-router-dom";

/**
 * Navbar bileşeni, uygulamanın üst kısmında gezinme çubuğu olarak kullanılır.
 * Kullanıcı giriş yaptıysa çıkış butonu, yapmadıysa login linki gösterir.
 *
 * @param {boolean} isLoggedIn - Kullanıcı giriş yapmış mı?
 * @param {function} onLogout - Çıkış butonuna tıklanınca çağrılır
 */
function Navbar({ isLoggedIn, onLogout }) {
  return (
    // Bootstrap ile stillendirilmiş bir navbar
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
      <div className="container">
        {/* Sol üstteki logo veya ana sayfa linki */}
        <Link className="navbar-brand" to="/">
          Home
        </Link>
        {/* Navbar'ın sağ kısmı */}
        <div className="collapse navbar-collapse">
          <ul className="navbar-nav ms-auto">
            {/* Kullanıcı giriş yaptıysa çıkış butonu göster */}
            {isLoggedIn ? (
              <li className="nav-item">
                <button className="btn btn-outline-light" onClick={onLogout}>
                  Logout
                </button>
              </li>
            ) : (
              // Giriş yapılmadıysa login linki göster
              <li className="nav-item">
                <Link className="nav-link" to="/login">
                  Login
                </Link>
              </li>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
