// Uygulamanın performans ölçümlerini toplamak için kullanılan yardımcı fonksiyon.

/**
 * reportWebVitals fonksiyonu, performans ölçümlerini toplamak için kullanılır.
 * web-vitals kütüphanesinden metrikleri toplar ve onPerfEntry fonksiyonuna iletir.
 *
 * @param {function} onPerfEntry - Performans verilerini işlemek için fonksiyon
 */
const reportWebVitals = (onPerfEntry) => {
  if (onPerfEntry && onPerfEntry instanceof Function) {
    import("web-vitals").then(({ getCLS, getFID, getFCP, getLCP, getTTFB }) => {
      // Core Web Vitals metriklerini toplar ve onPerfEntry fonksiyonuna iletir
      getCLS(onPerfEntry); // Cumulative Layout Shift
      getFID(onPerfEntry); // First Input Delay
      getFCP(onPerfEntry); // First Contentful Paint
      getLCP(onPerfEntry); // Largest Contentful Paint
      getTTFB(onPerfEntry); // Time to First Byte
    });
  }
};

export default reportWebVitals;
