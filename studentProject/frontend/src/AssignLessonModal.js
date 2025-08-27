import React, { useEffect, useState, useCallback } from "react";

function AssignLessonModal({ student, onClose, onSuccess }) {
  const [allLessons, setAllLessons] = useState([]);
  const [selectedLessons, setSelectedLessons] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const token = localStorage.getItem("token");

  // Modal kapanma fonksiyonu
  const handleClose = useCallback(() => {
    if (!loading) onClose();
  }, [loading, onClose]);

  // Escape ile kapatma
  useEffect(() => {
    const handleEscape = (e) => {
      if (e.key === "Escape") {
        handleClose();
      }
    };
    document.addEventListener("keydown", handleEscape);
    
    // Sadece scroll'u kilitle, pozisyonu değiştirme
    document.body.style.overflow = "hidden";

    return () => {
      document.removeEventListener("keydown", handleEscape);
      document.body.style.overflow = "unset";
    };
  }, [handleClose]);

  // Dersleri yükle
  useEffect(() => {
    const fetchLessons = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/lessons", {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (!res.ok) {
          const data = await res.json().catch(() => ({}));
          throw new Error(data.message || "Dersler yüklenemedi!");
        }
        const data = await res.json();
        setAllLessons(data);
        if (student.lessons?.length > 0) {
          setSelectedLessons(student.lessons.map((l) => l.id));
        }
      } catch (err) {
        setError(err.message);
      }
    };
    fetchLessons();
  }, [student, token]);

  const showError = useCallback((msg) => {
    setError(msg);
    setTimeout(() => setError(""), 3000);
  }, []);

  const showSuccess = useCallback((msg) => {
    setError(msg);
    setTimeout(() => setError(""), 3000);
  }, []);

  // Ders seç/kaldır
  const handleLessonToggle = useCallback(
    (lessonId) => {
      if (loading) return;
      setSelectedLessons((prev) =>
        prev.includes(lessonId)
          ? prev.filter((id) => id !== lessonId)
          : [...prev, lessonId]
      );
    },
    [loading]
  );

  // Form gönderme
  const handleSubmit = useCallback(
    async (e) => {
      e.preventDefault();
      if (loading) return;
      setLoading(true);

      try {
        const response = await fetch(
          "http://localhost:8080/api/lessons/assign",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({
              studentId: student.id,
              lessonIds: selectedLessons,
            }),
          }
        );
        if (!response.ok) {
          const data = await response.json().catch(() => ({}));
          throw new Error(data.message || "Ders atama başarısız!");
        }
        showSuccess("Dersler başarıyla atandı!");
        if (onSuccess) onSuccess();
        handleClose();
      } catch (err) {
        showError(err.message);
      } finally {
        setLoading(false);
      }
    },
    [
      loading,
      student.id,
      selectedLessons,
      token,
      showSuccess,
      showError,
      onSuccess,
      handleClose,
    ]
  );

  const handleCancel = useCallback(() => {
    if (!loading) handleClose();
  }, [loading, handleClose]);

  const clearError = useCallback(() => setError(""), []);

  return (
    <div
      onClick={(e) => {
        if (e.target === e.currentTarget) {
          handleClose();
        }
      }}
      style={{
        position: "fixed",
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: "rgba(0, 0, 0, 0.5)",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        zIndex: 9999,
        padding: "20px",
      }}
    >
      <form
        onSubmit={handleSubmit}
        onClick={(e) => e.stopPropagation()}
        style={{
          backgroundColor: "white",
          borderRadius: "8px",
          maxWidth: "900px",
          width: "100%",
          maxHeight: "90vh",
          overflow: "hidden",
          boxShadow: "0 10px 30px rgba(0, 0, 0, 0.3)",
          position: "relative",
          display: "flex",
          flexDirection: "column",
        }}
      >
        <div
          style={{
            padding: "1rem 1.5rem",
            borderBottom: "1px solid #dee2e6",
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            backgroundColor: "#f8f9fa",
          }}
        >
          <h5
            style={{
              margin: 0,
              fontSize: "1.25rem",
              fontWeight: "600",
              color: "#333",
            }}
          >
            {student.name} {student.surname} - Ders Atama
          </h5>
          <button
            type="button"
            onClick={handleCancel}
            disabled={loading}
            style={{
              background: "none",
              border: "none",
              fontSize: "1.5rem",
              cursor: loading ? "not-allowed" : "pointer",
              padding: 0,
              width: "30px",
              height: "30px",
              color: "#666",
              borderRadius: "4px",
              transition: "background-color 0.2s",
            }}
            onMouseEnter={(e) => (e.target.style.backgroundColor = "#e9ecef")}
            onMouseLeave={(e) =>
              (e.target.style.backgroundColor = "transparent")
            }
            aria-label="Kapat"
          >
            ×
          </button>
        </div>

        {error && (
          <div style={{ padding: "1rem 1.5rem 0" }}>
            <div
              style={{
                padding: "0.75rem 1.25rem",
                borderRadius: "4px",
                color: error.includes("başarıyla") ? "#155724" : "#721c24",
                backgroundColor: error.includes("başarıyla")
                  ? "#d4edda"
                  : "#f8d7da",
                border: `1px solid ${
                  error.includes("başarıyla") ? "#c3e6cb" : "#f5c6cb"
                }`,
                marginBottom: "1rem",
              }}
              role="alert"
            >
              {error}
              <button
                type="button"
                onClick={clearError}
                style={{
                  float: "right",
                  fontWeight: "700",
                  lineHeight: "1",
                  color: "inherit",
                  background: "none",
                  border: "none",
                  fontSize: "1.2rem",
                  cursor: "pointer",
                }}
                aria-label="Mesajı kapat"
              >
                ×
              </button>
            </div>
          </div>
        )}

        <div
          style={{
            padding: "1.5rem",
            maxHeight: "60vh",
            overflowY: "auto",
            display: "flex",
            gap: "1rem",
          }}
        >
          <div
            style={{
              flex: 1,
              border: "1px solid #dee2e6",
              borderRadius: "8px",
              padding: "1rem",
              backgroundColor: "#f8f9fa",
              overflowY: "auto",
            }}
          >
            <h6
              style={{
                fontWeight: "600",
                color: "#495057",
                marginBottom: "1rem",
              }}
            >
              📚 Mevcut Dersler
            </h6>
            {allLessons.length === 0 ? (
              <p style={{ textAlign: "center", color: "#6c757d" }}>
                Henüz ders eklenmemiş.
              </p>
            ) : (
              allLessons.map((lesson) => (
                <div
                  key={lesson.id}
                  style={{
                    marginBottom: "0.75rem",
                    padding: "0.5rem",
                    borderRadius: "4px",
                    backgroundColor: "white",
                    border: "1px solid #e9ecef",
                  }}
                >
                  <label
                    style={{
                      cursor: loading ? "not-allowed" : "pointer",
                      display: "flex",
                      alignItems: "center",
                    }}
                  >
                    <input
                      type="checkbox"
                      disabled={loading}
                      checked={selectedLessons.includes(lesson.id)}
                      onChange={() => handleLessonToggle(lesson.id)}
                      style={{
                        marginRight: "0.5rem",
                        cursor: loading ? "not-allowed" : "pointer",
                      }}
                    />
                    <div>
                      <div style={{ fontWeight: "600", color: "#495057" }}>
                        {lesson.name}
                      </div>
                      {lesson.description && (
                        <div style={{ fontSize: "0.875rem", color: "#6c757d" }}>
                          {lesson.description}
                        </div>
                      )}
                      <div style={{ fontSize: "0.75rem", color: "#6c757d" }}>
                        📅 {lesson.academicYear} - {lesson.term}
                      </div>
                    </div>
                  </label>
                </div>
              ))
            )}
          </div>

          <div
            style={{
              flex: 1,
              border: "1px solid #dee2e6",
              borderRadius: "8px",
              padding: "1rem",
              backgroundColor: "#f8f9fa",
              overflowY: "auto",
            }}
          >
            <h6
              style={{
                fontWeight: "600",
                color: "#495057",
                marginBottom: "1rem",
              }}
            >
              ✅ Seçili Dersler
            </h6>
            {selectedLessons.length === 0 ? (
              <p style={{ textAlign: "center", color: "#6c757d" }}>
                Henüz ders seçilmemiş.
              </p>
            ) : (
              selectedLessons.map((lessonId) => {
                const lesson = allLessons.find((l) => l.id === lessonId);
                if (!lesson) return null;
                return (
                  <div
                    key={lesson.id}
                    style={{
                      backgroundColor: "white",
                      border: "1px solid #28a745",
                      borderRadius: "6px",
                      padding: "0.75rem",
                      marginBottom: "0.5rem",
                      color: "#28a745",
                    }}
                  >
                    <div style={{ fontWeight: "600" }}>{lesson.name}</div>
                    {lesson.description && (
                      <div style={{ fontSize: "0.875rem", color: "#6c757d" }}>
                        {lesson.description}
                      </div>
                    )}
                    <div style={{ fontSize: "0.75rem", color: "#6c757d" }}>
                      📅 {lesson.academicYear} - {lesson.term}
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>

        <div
          style={{
            padding: "1rem 1.5rem",
            borderTop: "1px solid #dee2e6",
            display: "flex",
            justifyContent: "flex-end",
            gap: "0.75rem",
            backgroundColor: "#f8f9fa",
          }}
        >
          <button
            type="button"
            onClick={handleCancel}
            disabled={loading}
            style={{ minWidth: "80px" }}
          >
            İptal
          </button>
          <button type="submit" disabled={loading} style={{ minWidth: "80px" }}>
            {loading ? (
              <>
                <span
                  className="spinner-border spinner-border-sm me-2"
                  role="status"
                />
                Kaydediliyor...
              </>
            ) : (
              "Kaydet"
            )}
          </button>
        </div>
      </form>
    </div>
  );
}

export default AssignLessonModal;
