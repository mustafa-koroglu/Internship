// reportWebVitals fonksiyonu, performans ölçümlerini toplamak için kullanılır
const reportWebVitals = (onPerfEntry) => {
  // Eğer onPerfEntry fonksiyonu tanımlı ve bir fonksiyonsa devam et
  if (onPerfEntry && onPerfEntry instanceof Function) {
    // web-vitals kütüphanesini dinamik olarak import eder
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

// reportWebVitals fonksiyonunu dışa aktarır
export default reportWebVitals;
