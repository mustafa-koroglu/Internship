import React from "react";
import { Link } from "react-router-dom";

const Navbar = () => (
  <nav
    style={{
      background: "#23272b",
      padding: "16px 0",
      color: "white",
      fontSize: "2rem",
      paddingLeft: "16px",
    }}
  >
    <Link to="/" style={{ color: "white", textDecoration: "none" }}>
      Home
    </Link>
  </nav>
);

export default Navbar;
