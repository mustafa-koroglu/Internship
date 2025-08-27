import React, { useEffect, useState, useCallback } from "react";
import IpAddressForm from "./IpAddressForm";
import IpAddressList from "./IpAddressList";
import IpAddressEditModal from "./IpAddressEditModal";
import { apiGet, apiPost, apiDelete, apiPut } from "./utils/api";
import { useNotification } from "./hooks/useNotification";

function IpManagement({ role }) {
  const [ipAddresses, setIpAddresses] = useState([]);
  const [showAddForm, setShowAddForm] = useState(false);
  const [editingIpAddress, setEditingIpAddress] = useState(null);
  const [search, setSearch] = useState("");

  const { message, type, setMessage, showError, showSuccess } =
    useNotification();

  // IP adreslerini çek
  const fetchIpAddresses = useCallback(async () => {
    try {
      const data = await apiGet("/v1/ip-addresses");
      // Artık tüm IP'leri göster çünkü CIDR subnet'leri kaydetmiyoruz
      setIpAddresses(data);
    } catch (err) {
      showError(err.message);
    }
  }, [showError]);

  // Arama işlemi
  const handleSearch = useCallback(async () => {
    try {
      if (!search.trim()) {
        await fetchIpAddresses();
        return;
      }
      const data = await apiGet(
        `/v1/ip-addresses/search?q=${encodeURIComponent(search)}`
      );
      // Artık tüm IP'leri göster çünkü CIDR subnet'leri kaydetmiyoruz
      setIpAddresses(data);
    } catch (err) {
      showError(err.message);
    }
  }, [search, fetchIpAddresses, showError]);

  // IP adresi sil
  const handleDelete = useCallback(
    async (ipAddress) => {
      let confirmMessage = "Bu IP adresini silmek istediğinizden emin misiniz?";

      // Eğer subnet veya range ise uyarı mesajı ekle
      if (
        ipAddress.ipAddress.includes("/") ||
        ipAddress.ipAddress.includes("-")
      ) {
        confirmMessage = `Bu ${
          ipAddress.ipAddress.includes("/") ? "subnet" : "IP aralığını"
        } silmek istediğinizden emin misiniz?\n\nBu işlem, bu aralıktaki öğrencilere atanmış tüm IP adreslerini de silecektir!`;
      }

      if (!window.confirm(confirmMessage)) {
        return;
      }

      try {
        await apiDelete(`/v1/ip-addresses/${ipAddress.id}`);
        showSuccess("IP adresi başarıyla silindi!");
        // Sadece ilgili IP adresini listeden kaldır
        setIpAddresses((prev) => prev.filter((ip) => ip.id !== ipAddress.id));
      } catch (err) {
        showError(err.message);
      }
    },
    [showSuccess, showError]
  );

  // IP adresi düzenle
  const handleEdit = useCallback((ip) => {
    setEditingIpAddress(ip);
  }, []);

  // IP adresi düzenleme başarılı
  const handleEditSuccess = useCallback(
    (updatedIp) => {
      setIpAddresses((prev) =>
        prev.map((ip) => (ip.id === updatedIp.id ? updatedIp : ip))
      );
      showSuccess("IP adresi başarıyla güncellendi!");
    },
    [showSuccess]
  );

  // IP adresi düzenleme modalını kapat
  const handleCloseEdit = useCallback(() => {
    setEditingIpAddress(null);
  }, []);

  // IP adresi aktifleştir
  const handleActivate = useCallback(
    async (id) => {
      try {
        await apiPut(`/v1/ip-addresses/${id}`, { isActive: true });
        showSuccess("IP adresi aktifleştirildi!");
        // Sadece ilgili IP adresini güncelle
        setIpAddresses((prev) =>
          prev.map((ip) => (ip.id === id ? { ...ip, isActive: true } : ip))
        );
      } catch (err) {
        showError(err.message);
      }
    },
    [showSuccess, showError]
  );

  // Component mount olduğunda IP adreslerini çek
  useEffect(() => {
    fetchIpAddresses();
  }, [fetchIpAddresses]);

  // Arama değiştiğinde arama yap
  useEffect(() => {
    const timeoutId = setTimeout(handleSearch, 500);
    return () => clearTimeout(timeoutId);
  }, [handleSearch]);

  // Bildirim göster
  const renderNotification = () => {
    if (!message) return null;

    const alertClass =
      type === "success"
        ? "alert-success"
        : type === "error"
        ? "alert-danger"
        : "alert-info";

    return (
      <div
        className={`alert ${alertClass} alert-dismissible fade show`}
        role="alert"
      >
        {message}
        <button
          type="button"
          className="btn-close"
          onClick={() => setMessage("")}
        ></button>
      </div>
    );
  };

  return (
    <div className="container mt-4">
      {renderNotification()}

      <h2 className="mb-4">IP Adresi Yönetimi (IPv4 & IPv6)</h2>

      <div className="row mb-3">
        <div className="col-md-6">
          <input
            type="text"
            className="form-control"
            placeholder="IP adresi veya açıklama ara..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <div className="col-md-6 text-end">
          <button
            className="btn btn-primary"
            onClick={() => setShowAddForm(true)}
          >
            <i className="fas fa-plus me-2"></i>
            Yeni IP Adresi Ekle
          </button>
        </div>
      </div>

      <IpAddressList
        ipAddresses={ipAddresses}
        onUpdate={fetchIpAddresses}
        onDelete={handleDelete}
        onEdit={handleEdit}
        onActivate={handleActivate}
      />

      {showAddForm && (
        <IpAddressForm
          onSubmit={async (ipInput, description) => {
            try {
              const data = await apiPost("/v1/ip-addresses", {
                ipInput,
                description,
              });
              showSuccess("IP adresi başarıyla eklendi!");
              // Yeni IP adresini listeye ekle
              setIpAddresses((prev) => [...prev, data]);
              setShowAddForm(false);
              return { success: true, data };
            } catch (err) {
              // Hata mesajını daha açıklayıcı yap
              let errorMessage = "IP adresi eklenemedi";

              if (err.message.includes("Gecersiz IP formatı")) {
                errorMessage = "Hatalı IP adresi formatı";
              } else if (err.message.includes("Bu IP adresi kaydedilemez")) {
                errorMessage = "Bu IP adresi kaydedilemez";
              } else if (err.message.includes("Zaten mevcut")) {
                errorMessage = "Bu IP adresi kaydedilemez";
              } else if (
                err.message.includes("subnet veya aralık içinde bulunuyor")
              ) {
                errorMessage = "Bu IP adresi mevcut";
              } else if (err.message.includes("parse edilemedi")) {
                errorMessage = "IP adresi formatı tanınmıyor";
              } else if (err.message.includes("API çağrısı başarısız")) {
                errorMessage = "Bu IP adresi mevcut";
              } else {
                errorMessage = err.message;
              }

              showError(errorMessage);
              return { success: false, error: errorMessage };
            }
          }}
          onValidate={async (ipInput) => {
            try {
              const data = await apiGet(
                `/v1/ip-addresses/validate?ipInput=${encodeURIComponent(
                  ipInput
                )}`
              );
              return { success: true, data };
            } catch (err) {
              // Hata mesajını daha açıklayıcı yap
              let errorMessage = "Hatalı IP adresi formatı";

              if (err.message.includes("Gecersiz IP formatı")) {
                errorMessage = "Hatalı IP adresi formatı";
              } else if (err.message.includes("Bu IP adresi kaydedilemez")) {
                errorMessage = "Bu IP adresi kaydedilemez";
              } else if (err.message.includes("Zaten mevcut")) {
                errorMessage = "Bu IP adresi kaydedilemez";
              } else if (
                err.message.includes("subnet veya aralık içinde bulunuyor")
              ) {
                errorMessage = "Bu IP adresi mevcut";
              } else if (err.message.includes("parse edilemedi")) {
                errorMessage = "IP adresi formatı tanınmıyor";
              } else {
                errorMessage = "Bu IP adresi mevcut";
              }

              return { success: false, error: errorMessage };
            }
          }}
          onClose={() => setShowAddForm(false)}
        />
      )}

      {editingIpAddress && (
        <IpAddressEditModal
          ipAddress={editingIpAddress}
          onClose={handleCloseEdit}
          onSuccess={handleEditSuccess}
        />
      )}
    </div>
  );
}

export default IpManagement;
