import React, { useEffect, useState, useCallback } from "react";

function AssignIpAddressModal({ student, onClose, onSuccess }) {
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [assignedIp, setAssignedIp] = useState(null);

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

  const showError = useCallback((msg) => {
    setError(msg);
    setTimeout(() => setError(""), 5000);
  }, []);

  const showSuccess = useCallback((msg) => {
    setError(msg);
    setTimeout(() => setError(""), 3000);
  }, []);

  // Otomatik IP atama
  const handleAssignIp = useCallback(async () => {
    if (loading) return;
    setLoading(true);
    setAssignedIp(null);

    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/ip-addresses/assign-random/${student.id}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        const data = await response.json().catch(() => ({}));
        throw new Error(data.message || "IP adresi atama başarısız!");
      }

      const assignedIpData = await response.json();
      setAssignedIp(assignedIpData);
      showSuccess("IP adresi başarıyla atandı!");
      
      // Başarı callback'ini çağır ama modalı kapatma
      if (onSuccess) onSuccess();
    } catch (err) {
      showError(err.message);
    } finally {
      setLoading(false);
    }
  }, [
    loading,
    student.id,
    token,
    showSuccess,
    showError,
    onSuccess,
    handleClose,
  ]);

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
      <div
        onClick={(e) => e.stopPropagation()}
        style={{
          backgroundColor: "white",
          borderRadius: "8px",
          maxWidth: "500px",
          width: "100%",
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
            {student.name} {student.surname} - IPv4 Adresi Atama
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

        <div style={{ padding: "1.5rem" }}>
          <div style={{ marginBottom: "1.5rem" }}>
            <h6
              style={{
                fontWeight: "600",
                color: "#495057",
                marginBottom: "1rem",
              }}
            >
              🎯 Otomatik IPv4 Adresi Atama
            </h6>
            <p style={{ color: "#6c757d", lineHeight: "1.6" }}>
              Bu öğrenciye otomatik olarak uygun bir IPv4 adresi atanacaktır.
            </p>
          </div>

          {assignedIp && (
            <div
              style={{
                backgroundColor: "#d4edda",
                border: "1px solid #c3e6cb",
                borderRadius: "6px",
                padding: "1rem",
                marginBottom: "1rem",
                color: "#155724",
              }}
            >
              <h6 style={{ margin: "0 0 0.5rem 0", fontWeight: "600" }}>
                ✅ IPv4 Adresi Atandı
              </h6>
              <div style={{ fontWeight: "600", fontSize: "1.1rem" }}>
                {assignedIp.ipAddress}
              </div>
              {assignedIp.description && (
                <div style={{ fontSize: "0.9rem", marginTop: "0.25rem" }}>
                  {assignedIp.description}
                </div>
              )}
            </div>
          )}

          <div
            style={{
              display: "flex",
              gap: "0.75rem",
              justifyContent: "flex-end",
            }}
          >
            <button
              type="button"
              onClick={handleCancel}
              disabled={loading}
              style={{
                padding: "0.5rem 1rem",
                border: "1px solid #6c757d",
                borderRadius: "4px",
                backgroundColor: "white",
                color: "#6c757d",
                cursor: loading ? "not-allowed" : "pointer",
                minWidth: "80px",
              }}
            >
              İptal
            </button>
            <button
              type="button"
              onClick={handleAssignIp}
              disabled={loading}
              style={{
                padding: "0.5rem 1rem",
                border: "none",
                borderRadius: "4px",
                backgroundColor: "#007bff",
                color: "white",
                cursor: loading ? "not-allowed" : "pointer",
                minWidth: "120px",
                fontWeight: "600",
              }}
            >
              {loading ? (
                <>
                  <span
                    className="spinner-border spinner-border-sm me-2"
                    role="status"
                  />
                  Atanıyor...
                </>
              ) : (
                "IP Ata"
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default AssignIpAddressModal;
