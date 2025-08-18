import React, { useState, useEffect } from "react";
import IpAddressList from "./IpAddressList";
import IpAddressForm from "./IpAddressForm";
import IpAddressEditModal from "./IpAddressEditModal";

const IpManagement = ({ role }) => {
  const [ipAddresses, setIpAddresses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [editingIp, setEditingIp] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");

  const API_BASE_URL = "http://localhost:8080/api/v1/ip-addresses";

  // JWT token'ı al
  const getAuthHeaders = () => {
    const token = localStorage.getItem("token");
    return {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    };
  };

  // IP adreslerini getir (tümü - aktif ve pasif)
  const fetchIpAddresses = async (search = "") => {
    try {
      setLoading(true);
      const url = search
        ? `${API_BASE_URL}/search?q=${encodeURIComponent(search)}`
        : `${API_BASE_URL}/all`;
      const response = await fetch(url, {
        method: "GET",
        headers: getAuthHeaders(),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      setIpAddresses(data);
      setError(null);
    } catch (err) {
      console.error("IP adresleri getirilemedi:", err);
      setError("IP adresleri yüklenirken hata oluştu");
    } finally {
      setLoading(false);
    }
  };

  // IP adresi oluştur
  const createIpAddress = async (ipInput, description) => {
    try {
      const response = await fetch(API_BASE_URL, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify({
          ipInput,
          description,
        }),
      });

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData || "IP adresi oluşturulamadı");
      }

      const data = await response.json();
      setShowForm(false);
      fetchIpAddresses(searchTerm);
      return { success: true, data: data };
    } catch (err) {
      console.error("IP adresi oluşturulamadı:", err);
      return { success: false, error: err.message };
    }
  };

  // IP adresi güncelle
  const updateIpAddress = async (id, ipInput, description, isActive) => {
    try {
      const response = await fetch(`${API_BASE_URL}/${id}`, {
        method: "PUT",
        headers: getAuthHeaders(),
        body: JSON.stringify({
          ipInput,
          description,
          isActive,
        }),
      });

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData || "IP adresi güncellenemedi");
      }

      const data = await response.json();
      setEditingIp(null);
      fetchIpAddresses(searchTerm);
      return { success: true, data: data };
    } catch (err) {
      console.error("IP adresi güncellenemedi:", err);
      return { success: false, error: err.message };
    }
  };

  // IP adresi sil
  const deleteIpAddress = async (id) => {
    if (!window.confirm("Bu IP adresini silmek istediğinizden emin misiniz?")) {
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/${id}`, {
        method: "DELETE",
        headers: getAuthHeaders(),
      });

      if (!response.ok) {
        throw new Error("IP adresi silinemedi");
      }

      fetchIpAddresses(searchTerm);
    } catch (err) {
      console.error("IP adresi silinemedi:", err);
      alert("IP adresi silinirken hata oluştu");
    }
  };

  // IP adresini aktifleştir
  const activateIpAddress = async (id) => {
    if (!window.confirm("Bu IP adresini aktifleştirmek istediğinizden emin misiniz?")) {
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/${id}`, {
        method: "PUT",
        headers: getAuthHeaders(),
        body: JSON.stringify({
          ipInput: ipAddresses.find(ip => ip.id === id)?.ipAddress || "",
          description: ipAddresses.find(ip => ip.id === id)?.description || "",
          isActive: true
        })
      });

      if (!response.ok) {
        throw new Error("IP adresi aktifleştirilemedi");
      }

      fetchIpAddresses(searchTerm);
      alert("IP adresi başarıyla aktifleştirildi!");
    } catch (err) {
      console.error("IP adresi aktifleştirilemedi:", err);
      alert("IP adresi aktifleştirilirken hata oluştu");
    }
  };

  // IP adresi doğrula
  const validateIpAddress = async (ipInput) => {
    try {
      const response = await fetch(
        `${API_BASE_URL}/validate?ipInput=${encodeURIComponent(ipInput)}`,
        {
          method: "GET",
          headers: getAuthHeaders(),
        }
      );

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData || "IP adresi doğrulanamadı");
      }

      const data = await response.json();
      return { success: true, data: data };
    } catch (err) {
      return { success: false, error: err.message };
    }
  };

  // Arama işlemi (öğrenci listesi gibi)
  useEffect(() => {
    const timeoutId = setTimeout(() => {
      fetchIpAddresses(searchTerm);
    }, 300);

    return () => clearTimeout(timeoutId);
  }, [searchTerm]);

  // Düzenleme modal'ını aç
  const handleEdit = (ipAddress) => {
    setEditingIp(ipAddress);
  };

  // Düzenleme modal'ını kapat
  const handleCloseEdit = () => {
    setEditingIp(null);
  };

  useEffect(() => {
    fetchIpAddresses();
  }, []);

  // Modal açıkken body scroll'unu engelle
  useEffect(() => {
    if (showForm || editingIp) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "auto";
    }

    return () => {
      document.body.style.overflow = "auto";
    };
  }, [showForm, editingIp]);

  if (role !== "ADMIN") {
    return (
      <div className="container mt-5">
        <div className="alert alert-warning">
          Bu sayfaya erişim yetkiniz bulunmamaktadır.
        </div>
      </div>
    );
  }

  return (
    <div className="container mt-4">
      <h2 className="mb-4">IP Adresi Yönetimi</h2>

      {error && (
        <div
          className="alert alert-danger alert-dismissible fade show"
          role="alert"
        >
          {error}
          <button
            type="button"
            className="btn-close"
            onClick={() => setError(null)}
          ></button>
        </div>
      )}

      <div className="row mb-3">
        <div className="col-md-6">
          <input
            type="text"
            className="form-control"
            placeholder="IP adresi veya açıklama ara..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <div className="col-md-6 text-end">
          <button className="btn btn-primary" onClick={() => setShowForm(true)}>
            <i className="fas fa-plus me-2"></i>
            Yeni IP Adresi Ekle
          </button>
        </div>
      </div>

      <IpAddressList
        ipAddresses={ipAddresses}
        loading={loading}
        onEdit={handleEdit}
        onDelete={deleteIpAddress}
        onActivate={activateIpAddress}
      />

      {showForm && (
        <IpAddressForm
          onSubmit={createIpAddress}
          onValidate={validateIpAddress}
          onClose={() => setShowForm(false)}
        />
      )}

      {editingIp && (
        <IpAddressEditModal
          ipAddress={editingIp}
          onSubmit={updateIpAddress}
          onValidate={validateIpAddress}
          onClose={handleCloseEdit}
        />
      )}
    </div>
  );
};

export default IpManagement;
