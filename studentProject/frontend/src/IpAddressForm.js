import React, { useState } from "react";

const IpAddressForm = ({ onSubmit, onValidate, onClose }) => {
  const [formData, setFormData] = useState({
    ipInput: "",
    description: "",
  });

  const [validation, setValidation] = useState({
    isValid: false,
    message: "",
    ipCount: 0,
    ips: [],
    inputTypeDescription: "",
  });

  const [isValidating, setIsValidating] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleInputChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    if (name === "ipInput" && value.trim()) {
      validateIpInput(value);
    } else if (name === "ipInput" && !value.trim()) {
      setValidation({
        isValid: false,
        message: "",
        ipCount: 0,
        ips: [],
        inputTypeDescription: "",
      });
    }
  };

  const validateIpInput = async (ipInput) => {
    if (!ipInput.trim()) return;

    setIsValidating(true);

    try {
      const result = await onValidate(ipInput);

      if (result.success) {
        setValidation({
          isValid: true,
          message: `Geçerli format! IP adresi eklenecek.`,
          ipCount: result.data.ipCount,
          ips: result.data.ips || [],
          inputTypeDescription: result.data.inputTypeDescription || "",
        });
      } else {
        // Hata mesajını özelleştir
        let errorMessage = result.error;
        if (result.error.includes("Bu IP adresi zaten mevcut")) {
          errorMessage = "Bu IP adresi kaydedilemez";
        } else if (result.error.includes("Bu IP adresi kaydedilemez")) {
          errorMessage = "Bu IP adresi kaydedilemez";
        }

        setValidation({
          isValid: false,
          message: errorMessage,
          ipCount: 0,
          ips: [],
          inputTypeDescription: "",
        });
      }
    } catch (error) {
      setValidation({
        isValid: false,
        message: "Doğrulama sırasında hata oluştu",
        ipCount: 0,
        ips: [],
        inputTypeDescription: "",
      });
    } finally {
      setIsValidating(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validation.isValid) {
      alert("Lütfen geçerli bir IP adresi girin");
      return;
    }

    setIsSubmitting(true);

    try {
      const result = await onSubmit(formData.ipInput, formData.description);

      if (result.success) {
        alert("IP adresi başarıyla eklendi!");
        onClose();
      } else {
        alert(`Hata: ${result.error}`);
      }
    } catch (error) {
      alert("IP adresi eklenirken hata oluştu");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div
      className="modal fade show"
      style={{ display: "block", zIndex: 1050 }}
      tabIndex="-1"
    >
      <div
        className="modal-dialog modal-lg"
        style={{ zIndex: 1055 }}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="modal-content">
          <div className="modal-header">
            <h5 className="modal-title">Yeni IP Adresi Ekle</h5>
            <button
              type="button"
              className="btn-close"
              onClick={onClose}
              disabled={isSubmitting}
            ></button>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="modal-body">
              <div className="row">
                <div className="col-12 mb-3">
                  <label htmlFor="ipInput" className="form-label">
                    IP Adresi <span className="text-danger">*</span>
                  </label>
                  <input
                    type="text"
                    className={`form-control ${
                      validation.isValid
                        ? "is-valid"
                        : validation.message
                        ? "is-invalid"
                        : ""
                    }`}
                    id="ipInput"
                    name="ipInput"
                    value={formData.ipInput}
                    onChange={handleInputChange}
                    placeholder="192.168.1.1, 2001:db8::1, 192.168.1.0/24, 2001:db8::/32"
                    required
                    disabled={isSubmitting}
                  />
                  <div className="form-text">
                    <strong>Desteklenen formatlar:</strong>
                    <br />
                    <strong>IPv4:</strong>
                    <br />
                    • Tekil IP: 192.168.1.1
                    <br />
                    • CIDR Subnet: 192.168.1.0/24
                    <br />
                    • IP Aralığı: 192.168.1.1-192.168.1.10
                    <br />
                    <strong>IPv6:</strong>
                    <br />
                    • Tekil IP: 2001:db8::1
                    <br />
                    • CIDR Subnet: 2001:db8::/32
                    <br />• IP Aralığı: 2001:db8::1-2001:db8::10
                  </div>
                  {isValidating && (
                    <div className="mt-2">
                      <small className="text-info">
                        <i className="fas fa-spinner fa-spin me-1"></i>
                        Doğrulanıyor...
                      </small>
                    </div>
                  )}
                  {validation.message && (
                    <div
                      className={`mt-2 ${
                        validation.isValid ? "text-success" : "text-danger"
                      }`}
                    >
                      <small>
                        <i
                          className={`fas ${
                            validation.isValid
                              ? "fa-check"
                              : "fa-exclamation-triangle"
                          } me-1`}
                        ></i>
                        {validation.message}
                        {validation.inputTypeDescription && (
                          <span className="ms-2 text-muted">
                            (Format: {validation.inputTypeDescription})
                          </span>
                        )}
                      </small>
                    </div>
                  )}
                </div>

                <div className="col-12 mb-3">
                  <label htmlFor="description" className="form-label">
                    Açıklama
                  </label>
                  <textarea
                    className="form-control"
                    id="description"
                    name="description"
                    value={formData.description}
                    onChange={handleInputChange}
                    placeholder="IP adresi için açıklama (opsiyonel)"
                    rows="3"
                    maxLength="500"
                    disabled={isSubmitting}
                  ></textarea>
                  <div className="form-text">
                    {formData.description.length}/500 karakter
                  </div>
                </div>

                {validation.isValid && validation.ips.length > 0 && (
                  <div className="col-12 mb-3">
                    <label className="form-label">Eklenecek IP Adresi</label>
                    <div className="border rounded p-2 bg-light">
                      <code className="text-primary">{validation.ips[0]}</code>
                    </div>
                  </div>
                )}
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
                disabled={!validation.isValid || isSubmitting}
              >
                {isSubmitting ? (
                  <>
                    <i className="fas fa-spinner fa-spin me-1"></i>
                    Ekleniyor...
                  </>
                ) : (
                  <>
                    <i className="fas fa-plus me-1"></i>
                    Ekle
                  </>
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
      <div className="modal-backdrop fade show" style={{ zIndex: 1040 }}></div>
    </div>
  );
};

export default IpAddressForm;
