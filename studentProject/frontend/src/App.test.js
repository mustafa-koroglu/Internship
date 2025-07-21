import { render, screen } from "@testing-library/react";
import App from "./App";

/**
 * App bileşeninin temel render testini yapar.
 * "learn react" metninin ekranda olup olmadığını kontrol eder.
 */
test("renders learn react link", () => {
  render(<App />);
  const linkElement = screen.getByText(/learn react/i);
  expect(linkElement).toBeInTheDocument();
});
