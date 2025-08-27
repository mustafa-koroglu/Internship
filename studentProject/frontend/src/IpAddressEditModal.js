import React, { useState, useEffect } from "react";
import { apiPut } from "./utils/api";

function IpAddressEditModal({ ipAddress, onClose, onSuccess }) {
  const [formData, setFormData] = useState({
    ipAddress: "",
    description: "",
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (ipAddress) {
      setFormData({
        ipAddress: ipAddress.ipAddress || "",
        description: ipAddress.description || "",
      });
    }
  }, [ipAddress]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.ipAddress.trim()) {
      setError("IP adresi boş olamaz");
      return;
    }

    // Aynı IP adresi ile güncelleme yapılıyorsa hata verme
    if (
      formData.ipAddress === ipAddress.ipAddress &&
      formData.description === ipAddress.description
    ) {
      setError("Değişiklik yapmadınız");
      return;
    }

    setIsSubmitting(true);
    setError("");

    try {
      const updatedIp = await apiPut(`/v1/ip-addresses/${ipAddress.id}`, {
        ipInput: formData.ipAddress,
        description: formData.description,
      });

      onSuccess(updatedIp);
      onClose();
    } catch (err) {
      let errorMessage = "IP adresi güncellenemedi";

      if (err.message.includes("Gecersiz IP formatı")) {
        errorMessage = "Hatalı IP adresi formatı";
      } else if (err.message.includes("Bu IP adresi kaydedilemez")) {
        errorMessage = "Bu IP adresi kaydedilemez";
      } else if (err.message.includes("network, broadcast veya özel adres")) {
        errorMessage = "Bu IP adresi kaydedilemez";
      } else if (err.message.includes("Zaten mevcut")) {
        errorMessage = "Bu IP adresi kaydedilemez";
      } else if (err.message.includes("parse edilemedi")) {
        errorMessage = "IP adresi formatı tanınmıyor";
      } else if (err.message.includes("API çağrısı başarısız")) {
        errorMessage = "Bu IP adresi kaydedilemez";
      } else {
        errorMessage = err.message;
      }

      setError(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  if (!ipAddress) return null;

  return (
    <div
      className="modal fade show"
      style={{ display: "block", zIndex: 1050 }}
      tabIndex="-1"
    >
      <div
        className="modal-dialog"
        style={{ zIndex: 1055 }}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="modal-content">
          <div className="modal-header">
            <h5 className="modal-title">IP Adresi Düzenle</h5>
            <button
              type="button"
              className="btn-close"
              onClick={onClose}
              disabled={isSubmitting}
            ></button>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="modal-body">
              {error && (
                <div className="alert alert-danger" role="alert">
                  {error}
                </div>
              )}

              <div className="mb-3">
                <label htmlFor="ipAddress" className="form-label">
                  IP Adresi <span className="text-danger">*</span>
                </label>
                <input
                  type="text"
                  className="form-control"
                  id="ipAddress"
                  name="ipAddress"
                  value={formData.ipAddress}
                  onChange={handleChange}
                  placeholder="192.168.1.1, 192.168.1.0/24, 192.168.1.1-192.168.1.10"
                  required
                  disabled={isSubmitting}
                />
                <div className="form-text">
                  Tekil IP adresi, CIDR subnet veya IP aralığı formatında
                  girebilirsiniz
                </div>
              </div>

              <div className="mb-3">
                <label htmlFor="description" className="form-label">
                  Açıklama
                </label>
                <textarea
                  className="form-control"
                  id="description"
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  placeholder="IP adresi açıklaması..."
                  rows="3"
                  disabled={isSubmitting}
                ></textarea>
              </div>
            </div>

            <div className="modal-footer">
              <button
                type="button"
                className="btn btn-secondary"
                onClick={onClose}
                disabled={isSubmitting}
              >
                İptal
              </button>
              <button
                type="submit"
                className="btn btn-primary"
                disabled={isSubmitting}
              >
                {isSubmitting ? (
                  <>
                    <span
                      className="spinner-border spinner-border-sm me-2"
                      role="status"
                      aria-hidden="true"
                    ></span>
                    Güncelleniyor...
                  </>
                ) : (
                  "Güncelle"
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default IpAddressEditModal;
