import React from "react";

const IpAddressList = ({
  ipAddresses,
  loading,
  onEdit,
  onDelete,
  onActivate,
}) => {
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString("tr-TR");
  };

  if (loading) {
    return (
      <div className="text-center mt-4">
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Yükleniyor...</span>
        </div>
        <p className="mt-2">IP adresleri yükleniyor...</p>
      </div>
    );
  }

  return (
    <div className="card">
      <div className="card-header">
        <h5 className="mb-0">IP Adresleri ({ipAddresses.length})</h5>
      </div>

      <div className="card-body p-0">
        {ipAddresses.length === 0 ? (
          <div className="text-center py-4">
            <i className="fas fa-network-wired fa-3x text-muted mb-3"></i>
            <p className="text-muted">Henüz IP adresi eklenmemiş</p>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="table table-hover mb-0">
              <thead className="table-dark">
                <tr>
                  <th>IP Adresi</th>
                  <th>Açıklama</th>
                  <th>Durum</th>
                  <th>Oluşturulma Tarihi</th>
                  <th>Güncellenme Tarihi</th>
                  <th>İşlemler</th>
                </tr>
              </thead>
              <tbody>
                {ipAddresses.map((ip) => (
                  <tr key={ip.id}>
                    <td>
                      <code className="text-primary">{ip.ipAddress}</code>
                    </td>
                    <td>
                      {ip.description ? (
                        <span title={ip.description}>
                          {ip.description.length > 50
                            ? `${ip.description.substring(0, 50)}...`
                            : ip.description}
                        </span>
                      ) : (
                        <span className="text-muted">Açıklama yok</span>
                      )}
                    </td>
                    <td>
                      {ip.isActive ? (
                        <span className="badge bg-success">
                          <i className="fas fa-check me-1"></i>
                          Aktif
                        </span>
                      ) : (
                        <span className="badge bg-secondary">
                          <i className="fas fa-times me-1"></i>
                          Pasif
                        </span>
                      )}
                    </td>
                    <td>
                      <small className="text-muted">
                        {formatDate(ip.createdAt)}
                      </small>
                    </td>
                    <td>
                      <small className="text-muted">
                        {formatDate(ip.updatedAt)}
                      </small>
                    </td>
                    <td>
                      <div className="btn-group btn-group-sm" role="group">
                        <button
                          type="button"
                          className="btn btn-outline-primary"
                          onClick={() => onEdit(ip)}
                          title="Düzenle"
                        >
                          <i className="fas fa-edit"></i>
                        </button>
                        {!ip.isActive && (
                          <button
                            type="button"
                            className="btn btn-outline-success"
                            onClick={() => onActivate(ip.id)}
                            title="Aktifleştir"
                          >
                            <i className="fas fa-check"></i>
                          </button>
                        )}
                        <button
                          type="button"
                          className="btn btn-outline-danger"
                          onClick={() => onDelete(ip.id)}
                          title="Sil"
                        >
                          <i className="fas fa-trash"></i>
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default IpAddressList;
