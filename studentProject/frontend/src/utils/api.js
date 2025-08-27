// API çağrıları için utility fonksiyonları
const API_BASE_URL = "http://localhost:8080/api";

// Token'ı al
export const getToken = () => localStorage.getItem("token");

// API çağrısı yap
export const apiCall = async (endpoint, options = {}) => {
  const token = getToken();
  const url = `${API_BASE_URL}${endpoint}`;

  const defaultOptions = {
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
      ...options.headers,
    },
  };

  const response = await fetch(url, { ...defaultOptions, ...options });

  if (!response.ok) {
    const errorText = await response.text();
    let errorMessage;
    try {
      const data = JSON.parse(errorText);
      errorMessage = data.message || data;
    } catch {
      errorMessage = errorText;
    }
    throw new Error(errorMessage || "API çağrısı başarısız!");
  }

  // DELETE istekleri için boş response döndür
  if (options.method === "DELETE") {
    return null;
  }

  return response.json();
};

// GET isteği
export const apiGet = (endpoint) => apiCall(endpoint);

// POST isteği
export const apiPost = (endpoint, data) =>
  apiCall(endpoint, {
    method: "POST",
    body: JSON.stringify(data),
  });

// PUT isteği
export const apiPut = (endpoint, data) =>
  apiCall(endpoint, {
    method: "PUT",
    body: JSON.stringify(data),
  });

// DELETE isteği
export const apiDelete = (endpoint) =>
  apiCall(endpoint, {
    method: "DELETE",
  });

// Öğrenci API fonksiyonları
export const studentApi = {
  getAll: (isAdmin) =>
    apiGet(isAdmin ? "/v3/students" : "/v3/students/verified"),
  search: (query, isAdmin) =>
    apiGet(
      `${
        isAdmin ? "/v3/students/search" : "/v3/students/verified/search"
      }?q=${encodeURIComponent(query)}`
    ),
  create: (student) => apiPost("/v3/students", student),
  update: (id, student) => apiPut(`/v3/students/${id}`, student),
  delete: (id) => apiDelete(`/v3/students/${id}`),
  approve: (id) => apiPut(`/v3/students/${id}/approve`),
  toggleVisibility: (id, view) =>
    apiPut(`/v3/students/${id}/visibility?view=${view}`),
  assignLesson: (studentId, lessonId) =>
    apiPut(`/v3/students/${studentId}/assign-lesson`, { lessonId }),
  assignIpAddress: (studentId, ipAddressId) =>
    apiPut(`/v3/students/${studentId}/assign-ip`, { ipAddressId }),
};
