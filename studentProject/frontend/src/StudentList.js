// Öğrenci listesi ve işlemlerini yöneten ana bileşen
import React, { useEffect, useState, useCallback } from "react";
import AssignLessonModal from "./AssignLessonModal";
import AssignIpAddressModal from "./AssignIpAddressModal";
import { studentApi } from "./utils/api";
import { useNotification } from "./hooks/useNotification";

function StudentList({ role }) {
  const [students, setStudents] = useState([]);
  const [showAddForm, setShowAddForm] = useState(false);
  const [newStudent, setNewStudent] = useState({
    name: "",
    surname: "",
    number: "",
  });
  const [search, setSearch] = useState("");
  const [showAssignModal, setShowAssignModal] = useState(false);
  const [showAssignIpModal, setShowAssignIpModal] = useState(false);
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [expandedStudents, setExpandedStudents] = useState(new Set());
  const [expandedIpStudents, setExpandedIpStudents] = useState(new Set());
  const [editingStudent, setEditingStudent] = useState(null);
  const [editForm, setEditForm] = useState({
    name: "",
    surname: "",
    number: "",
    verified: false,
    view: false,
  });
  const [isEditing, setIsEditing] = useState(false);
  const [editLoading, setEditLoading] = useState(false);

  const isAdmin = role === "ADMIN";
  const { message, type, setMessage, showSuccess, showError } =
    useNotification();

  // Öğrenci listesini çek
  const fetchStudents = useCallback(async () => {
    try {
      const data = await studentApi.getAll(isAdmin);
      setStudents(data);
    } catch (err) {
      showError(err.message);
    }
  }, [isAdmin, showError]);

  // Arama işlemi
  const handleSearch = useCallback(async () => {
    try {
      if (!search.trim()) {
        await fetchStudents();
        return;
      }
      const data = await studentApi.search(search, isAdmin);
      setStudents(data);
    } catch (err) {
      showError(err.message);
    }
  }, [search, isAdmin, fetchStudents, showError]);

  // Öğrenci onayla
  const handleApprove = useCallback(
    async (id) => {
      try {
        await studentApi.approve(id);
        showSuccess("Öğrenci başarıyla onaylandı!");
        // Sadece ilgili öğrenciyi güncelle
        setStudents((prevStudents) =>
          prevStudents.map((student) =>
            student.id === id ? { ...student, verified: true } : student
          )
        );
      } catch (err) {
        showError(err.message);
      }
    },
    [showSuccess, showError]
  );

  // Görünürlük kontrolü
  const handleToggleVisibility = useCallback(
    async (id, currentView) => {
      try {
        await studentApi.toggleVisibility(id, (!currentView).toString());
        showSuccess("Görünürlük başarıyla değiştirildi!");
        // Sadece ilgili öğrenciyi güncelle
        setStudents((prevStudents) =>
          prevStudents.map((student) =>
            student.id === id ? { ...student, view: !currentView } : student
          )
        );
      } catch (err) {
        showError(err.message);
      }
    },
    [showSuccess, showError]
  );

  // Yeni öğrenci ekle
  const handleAddSubmit = useCallback(
    async (e) => {
      e.preventDefault();
      try {
        const newStudentData = await studentApi.create(newStudent);
        showSuccess("Öğrenci başarıyla eklendi!");
        setNewStudent({ name: "", surname: "", number: "" });
        setShowAddForm(false);
        // Yeni öğrenciyi listeye ekle
        setStudents((prevStudents) => [...prevStudents, newStudentData]);
      } catch (err) {
        showError(err.message);
      }
    },
    [newStudent, showSuccess, showError]
  );

  // Öğrenci düzenle
  const handleEditStudent = useCallback((student) => {
    setEditingStudent(student);
    setEditForm({
      name: student.name,
      surname: student.surname,
      number: student.number,
      verified: student.verified,
      view: student.view,
    });
    setIsEditing(true);

    // Edit modal'ı açıldığında sayfanın yukarı çıkmasını engelle
    // Modal'ın görünür olması için kısa bir gecikme ile scroll yap
    setTimeout(() => {
      const editModal = document.querySelector(".card.border-primary");
      if (editModal) {
        editModal.scrollIntoView({
          behavior: "smooth",
          block: "nearest",
          inline: "nearest",
        });
      }
    }, 100);
  }, []);

  // Edit panelini kapat
  const handleCloseEdit = useCallback(() => {
    setEditingStudent(null);
    setEditForm({
      name: "",
      surname: "",
      number: "",
      verified: false,
      view: false,
    });
    setIsEditing(false);
  }, []);

  // Öğrenci güncelle
  const handleUpdateStudent = useCallback(async () => {
    if (!editingStudent) return;

    setEditLoading(true);
    try {
      const updatedStudentData = await studentApi.update(
        editingStudent.id,
        editForm
      );
      showSuccess("Öğrenci başarıyla güncellendi!");
      handleCloseEdit();
      // Sadece ilgili öğrenciyi güncelle
      setStudents((prevStudents) =>
        prevStudents.map((student) =>
          student.id === editingStudent.id ? updatedStudentData : student
        )
      );
    } catch (err) {
      showError(err.message);
    } finally {
      setEditLoading(false);
    }
  }, [editingStudent, editForm, handleCloseEdit, showSuccess, showError]);

  // Öğrenci sil
  const handleDeleteStudent = useCallback(
    async (id) => {
      if (!window.confirm("Bu öğrenciyi silmek istediğinizden emin misiniz?"))
        return;

      try {
        await studentApi.delete(id);
        showSuccess("Öğrenci başarıyla silindi!");
        // Sadece ilgili öğrenciyi listeden kaldır
        setStudents((prevStudents) =>
          prevStudents.filter((student) => student.id !== id)
        );
      } catch (err) {
        showError(err.message);
      }
    },
    [showSuccess, showError]
  );

  // Ders atama modalını aç
  const handleAssignLesson = useCallback((student) => {
    setSelectedStudent(student);
    setShowAssignModal(true);
  }, []);

  // IP atama modalını aç
  const handleAssignIp = useCallback((student) => {
    setSelectedStudent(student);
    setShowAssignIpModal(true);
  }, []);

  // Modal kapatma işlemleri
  const handleCloseAssignModal = useCallback(() => {
    setShowAssignModal(false);
    setSelectedStudent(null);
  }, []);

  const handleCloseAssignIpModal = useCallback(() => {
    setShowAssignIpModal(false);
    setSelectedStudent(null);
  }, []);

  // Ders atama başarılı olduğunda
  const handleAssignSuccess = useCallback(() => {
    if (selectedStudent) {
      const fetchUpdatedStudent = async () => {
        try {
          const response = await fetch(
            `http://localhost:8080/api/v3/students/${selectedStudent.id}`,
            {
              headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`,
              },
            }
          );

          if (response.ok) {
            const updatedStudent = await response.json();

            setStudents((prevStudents) =>
              prevStudents.map((student) =>
                student.id === updatedStudent.id ? updatedStudent : student
              )
            );

            if (editingStudent && editingStudent.id === selectedStudent.id) {
              setEditingStudent(updatedStudent);
            }
          }
        } catch (err) {
          console.error("Öğrenci bilgileri yenilenemedi:", err);
        }
      };

      fetchUpdatedStudent();
    }
  }, [selectedStudent, editingStudent]);

  // Genişletme/daraltma işlemleri
  const toggleExpanded = useCallback((studentId) => {
    setExpandedStudents((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(studentId)) {
        newSet.delete(studentId);
      } else {
        newSet.add(studentId);
      }
      return newSet;
    });
  }, []);

  const toggleIpExpanded = useCallback((studentId) => {
    setExpandedIpStudents((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(studentId)) {
        newSet.delete(studentId);
      } else {
        newSet.add(studentId);
      }
      return newSet;
    });
  }, []);

  // Input değişiklikleri
  const handleInputChange = useCallback((e) => {
    const { name, value, type, checked } = e.target;
    setNewStudent((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  }, []);

  const handleEditInputChange = useCallback((e) => {
    const { name, value, type, checked } = e.target;
    setEditForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  }, []);

  // Component mount olduğunda öğrencileri çek
  useEffect(() => {
    fetchStudents();
  }, [fetchStudents]);

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

  // Ana render
  return (
    <div className="container mt-4">
      {renderNotification()}

      <h2 className="mb-4">Öğrenci Listesi</h2>

      <div className="row mb-3">
        <div className="col-md-6">
          <input
            type="text"
            className="form-control"
            placeholder="Öğrenci ara..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <div className="col-md-6 text-end">
          {isAdmin && (
            <button
              className="btn btn-primary"
              onClick={() => setShowAddForm(!showAddForm)}
            >
              Yeni Öğrenci Ekle
            </button>
          )}
        </div>
      </div>

      {/* Yeni öğrenci formu */}
      {showAddForm && (
        <form className="mb-3" onSubmit={handleAddSubmit}>
          <div className="row g-2">
            <div className="col">
              <input
                className="form-control"
                placeholder="Name"
                name="name"
                value={newStudent.name}
                onChange={handleInputChange}
                required
              />
            </div>
            <div className="col">
              <input
                className="form-control"
                placeholder="Surname"
                name="surname"
                value={newStudent.surname}
                onChange={handleInputChange}
                required
              />
            </div>
            <div className="col">
              <input
                className="form-control"
                placeholder="Number"
                name="number"
                value={newStudent.number}
                onChange={handleInputChange}
                required
              />
            </div>
            <div className="col-auto">
              <button type="submit" className="btn btn-success">
                Ekle
              </button>
            </div>
          </div>
        </form>
      )}

      {/* Düzenleme modalı */}
      {editingStudent && (
        <div className="card mb-4 border-primary">
          <div className="card-header bg-primary text-white d-flex justify-content-between align-items-center">
            <h5 className="mb-0">
              📝 {editingStudent.name} {editingStudent.surname} - Öğrenci
              Düzenle
            </h5>
            <button
              type="button"
              className="btn-close btn-close-white"
              onClick={handleCloseEdit}
            ></button>
          </div>
          <div className="card-body">
            <div className="row">
              <div className="col-md-6">
                <div className="card">
                  <div className="card-header d-flex justify-content-between align-items-center">
                    <h6 className="mb-0">📝 Öğrenci Bilgileri</h6>
                    <button
                      className={`btn btn-sm ${
                        isEditing ? "btn-secondary" : "btn-primary"
                      }`}
                      onClick={() => setIsEditing(!isEditing)}
                      disabled={editLoading}
                    >
                      {isEditing ? "İptal" : "Düzenle"}
                    </button>
                  </div>
                  <div className="card-body">
                    {isEditing ? (
                      <form
                        onSubmit={(e) => {
                          e.preventDefault();
                          handleUpdateStudent();
                        }}
                      >
                        <div className="mb-3">
                          <label className="form-label">Ad</label>
                          <input
                            type="text"
                            className="form-control"
                            name="name"
                            value={editForm.name}
                            onChange={handleEditInputChange}
                            required
                          />
                        </div>
                        <div className="mb-3">
                          <label className="form-label">Soyad</label>
                          <input
                            type="text"
                            className="form-control"
                            name="surname"
                            value={editForm.surname}
                            onChange={handleEditInputChange}
                            required
                          />
                        </div>
                        <div className="mb-3">
                          <label className="form-label">Öğrenci Numarası</label>
                          <input
                            type="text"
                            className="form-control"
                            name="number"
                            value={editForm.number}
                            onChange={handleEditInputChange}
                            required
                          />
                        </div>
                        <div className="mb-3">
                          <div className="form-check">
                            <input
                              type="checkbox"
                              className="form-check-input"
                              name="verified"
                              checked={editForm.verified}
                              onChange={handleEditInputChange}
                            />
                            <label className="form-check-label">Onaylı</label>
                          </div>
                        </div>
                        <div className="mb-3">
                          <div className="form-check">
                            <input
                              type="checkbox"
                              className="form-check-input"
                              name="view"
                              checked={editForm.view}
                              onChange={handleEditInputChange}
                            />
                            <label className="form-check-label">Görünür</label>
                          </div>
                        </div>
                        <button
                          type="submit"
                          className="btn btn-success"
                          disabled={editLoading}
                        >
                          {editLoading ? (
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
                      </form>
                    ) : (
                      <div>
                        <div className="mb-3">
                          <strong>Ad:</strong> {editingStudent.name}
                        </div>
                        <div className="mb-3">
                          <strong>Soyad:</strong> {editingStudent.surname}
                        </div>
                        <div className="mb-3">
                          <strong>Öğrenci Numarası:</strong>{" "}
                          {editingStudent.number}
                        </div>
                        <div className="mb-3">
                          <strong>Onay Durumu:</strong>{" "}
                          <span
                            className={`badge ${
                              editingStudent.verified
                                ? "bg-success"
                                : "bg-warning"
                            }`}
                          >
                            {editingStudent.verified ? "Onaylı" : "Onaysız"}
                          </span>
                        </div>
                        <div className="mb-3">
                          <strong>Görünürlük:</strong>{" "}
                          <span
                            className={`badge ${
                              editingStudent.view
                                ? "bg-success"
                                : "bg-secondary"
                            }`}
                          >
                            {editingStudent.view ? "Görünür" : "Gizli"}
                          </span>
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              </div>

              <div className="col-md-6">
                <div className="card">
                  <div className="card-header">
                    <h6 className="mb-0">📚 Ders Yönetimi</h6>
                  </div>
                  <div className="card-body">
                    <div className="mb-3">
                      <button
                        className="btn btn-primary w-100 mb-2"
                        onClick={() => handleAssignLesson(editingStudent)}
                        disabled={editLoading}
                      >
                        📚 Ders Ata/Düzenle
                      </button>
                      <button
                        className="btn btn-info w-100"
                        onClick={() => handleAssignIp(editingStudent)}
                        disabled={editLoading}
                      >
                        🌐 IPv4 Adresi Ata
                      </button>
                    </div>

                    <div>
                      <h6>Mevcut Dersler:</h6>
                      {editingStudent.lessons &&
                      editingStudent.lessons.length > 0 ? (
                        <div className="row">
                          {editingStudent.lessons.map((lesson) => (
                            <div key={lesson.id} className="col-12 mb-2">
                              <div className="card border-success">
                                <div className="card-body p-2">
                                  <h6 className="card-title mb-1">
                                    {lesson.name}
                                  </h6>
                                  {lesson.description && (
                                    <p className="card-text small mb-1">
                                      {lesson.description}
                                    </p>
                                  )}
                                  <small className="text-muted">
                                    📅 {lesson.academicYear} - {lesson.term}
                                  </small>
                                </div>
                              </div>
                            </div>
                          ))}
                        </div>
                      ) : (
                        <div className="text-center text-muted py-3">
                          <p>Henüz ders atanmamış.</p>
                          <p>Ders atamak için yukarıdaki butona tıklayın.</p>
                        </div>
                      )}
                    </div>

                    <div className="mt-4">
                      <h6>Mevcut IP Adresleri:</h6>
                      {editingStudent.ipAddresses &&
                      editingStudent.ipAddresses.length > 0 ? (
                        <div className="row">
                          {editingStudent.ipAddresses.map((ipAddress) => (
                            <div key={ipAddress.id} className="col-12 mb-2">
                              <div className="card border-info">
                                <div className="card-body p-2">
                                  <h6 className="card-title mb-1">
                                    {ipAddress.ipAddress}
                                  </h6>
                                  {ipAddress.description && (
                                    <p className="card-text small mb-1">
                                      {ipAddress.description}
                                    </p>
                                  )}
                                  <small className="text-muted">
                                    📅{" "}
                                    {new Date(
                                      ipAddress.createdAt
                                    ).toLocaleDateString("tr-TR")}
                                  </small>
                                </div>
                              </div>
                            </div>
                          ))}
                        </div>
                      ) : (
                        <div className="text-center text-muted py-3">
                          <p>Henüz IPv4 adresi atanmamış.</p>
                          <p>
                            IPv4 adresi atamak için yukarıdaki butona tıklayın.
                          </p>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Öğrenci listesi */}
      <div className="table-responsive">
        <table className="table table-striped table-hover">
          <thead className="table-dark">
            <tr>
              <th>Ad</th>
              <th>Soyad</th>
              <th>Numara</th>
              {isAdmin && <th>Doğrulandı</th>}
              {isAdmin && <th>Görünüm</th>}
              <th>Dersler</th>
              <th>IPv4 Adresleri</th>
              {isAdmin && (
                <th style={{ width: "240px", whiteSpace: "nowrap" }}>
                  Eylemler
                </th>
              )}
            </tr>
          </thead>
          <tbody>
            {students.map((student) => (
              <React.Fragment key={student.id}>
                <tr>
                  <td>{student.name}</td>
                  <td>{student.surname}</td>
                  <td>{student.number}</td>
                  {isAdmin && (
                    <>
                      <td>
                        <span
                          className={`badge ${
                            student.verified ? "bg-success" : "bg-warning"
                          }`}
                        >
                          {student.verified ? "Onaylı" : "Onaysız"}
                        </span>
                      </td>
                      <td>
                        <span
                          className={`badge ${
                            student.view ? "bg-success" : "bg-secondary"
                          }`}
                        >
                          {student.view ? "Görünür" : "Gizli"}
                        </span>
                      </td>
                    </>
                  )}
                  <td>
                    {student.lessons && student.lessons.length > 0 ? (
                      <button
                        className="btn btn-info btn-sm"
                        onClick={() => toggleExpanded(student.id)}
                        title={`${student.lessons.length} ders göster/gizle`}
                      >
                        📚 ({student.lessons.length})
                      </button>
                    ) : (
                      <span className="text-muted">Ders yok</span>
                    )}
                  </td>
                  <td>
                    {student.ipAddresses && student.ipAddresses.length > 0 ? (
                      <button
                        className="btn btn-warning btn-sm"
                        onClick={() => toggleIpExpanded(student.id)}
                        title={`${student.ipAddresses.length} IPv4 adresi göster/gizle`}
                      >
                        🌐 ({student.ipAddresses.length})
                      </button>
                    ) : (
                      <span className="text-muted">IPv4 yok</span>
                    )}
                  </td>
                  {isAdmin && (
                    <td style={{ width: "240px", whiteSpace: "nowrap" }}>
                      <button
                        className="btn btn-primary btn-sm me-1"
                        onClick={() => handleEditStudent(student)}
                        title="Düzenle"
                      >
                        Edit
                      </button>
                      {!student.verified && (
                        <button
                          className="btn btn-success btn-sm me-1"
                          onClick={() => handleApprove(student.id)}
                          title="Onayla"
                        >
                          ✓
                        </button>
                      )}
                      <button
                        className={`btn btn-sm me-1 ${
                          student.view ? "btn-warning" : "btn-info"
                        }`}
                        onClick={() =>
                          handleToggleVisibility(student.id, student.view)
                        }
                        title={student.view ? "Gizle" : "Göster"}
                      >
                        {student.view ? "👁️" : "👁️‍🗨️"}
                      </button>
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => handleDeleteStudent(student.id)}
                      >
                        Delete
                      </button>
                    </td>
                  )}
                </tr>
                {expandedStudents.has(student.id) &&
                  student.lessons &&
                  student.lessons.length > 0 && (
                    <tr>
                      <td colSpan={isAdmin ? 8 : 5} className="p-0">
                        <div className="p-3 border-top bg-light">
                          <div className="d-flex justify-content-between align-items-center mb-2">
                            <h6 className="mb-0">
                              📚 {student.name} {student.surname} - Dersler
                            </h6>
                            <button
                              className="btn btn-sm btn-outline-secondary"
                              onClick={() => toggleExpanded(student.id)}
                            >
                              ✕ Kapat
                            </button>
                          </div>
                          <div className="row">
                            {student.lessons.map((lesson) => (
                              <div key={lesson.id} className="col-md-4 mb-2">
                                <div className="card border-primary">
                                  <div className="card-body p-2">
                                    <h6 className="card-title mb-1 text-primary">
                                      {lesson.name}
                                    </h6>
                                    {lesson.description && (
                                      <p className="card-text small mb-1">
                                        {lesson.description}
                                      </p>
                                    )}
                                    <small className="text-muted">
                                      📅 {lesson.academicYear} - {lesson.term}
                                    </small>
                                  </div>
                                </div>
                              </div>
                            ))}
                          </div>
                        </div>
                      </td>
                    </tr>
                  )}
                {expandedIpStudents.has(student.id) &&
                  student.ipAddresses &&
                  student.ipAddresses.length > 0 && (
                    <tr>
                      <td colSpan={isAdmin ? 8 : 5} className="p-0">
                        <div className="p-3 border-top bg-light">
                          <div className="d-flex justify-content-between align-items-center mb-2">
                            <h6 className="mb-0">
                              🌐 {student.name} {student.surname} - IP Adresleri
                            </h6>
                            <button
                              className="btn btn-sm btn-outline-secondary"
                              onClick={() => toggleIpExpanded(student.id)}
                            >
                              ✕ Kapat
                            </button>
                          </div>
                          <div className="row">
                            {student.ipAddresses.map((ipAddress) => (
                              <div key={ipAddress.id} className="col-md-4 mb-2">
                                <div className="card border-warning">
                                  <div className="card-body p-2">
                                    <h6 className="card-title mb-1 text-warning">
                                      {ipAddress.ipAddress}
                                    </h6>
                                    {ipAddress.description && (
                                      <p className="card-text small mb-1">
                                        {ipAddress.description}
                                      </p>
                                    )}
                                    <small className="text-muted">
                                      📅{" "}
                                      {new Date(
                                        ipAddress.createdAt
                                      ).toLocaleDateString("tr-TR")}
                                    </small>
                                  </div>
                                </div>
                              </div>
                            ))}
                          </div>
                        </div>
                      </td>
                    </tr>
                  )}
              </React.Fragment>
            ))}
          </tbody>
        </table>
      </div>

      {students.length === 0 && (
        <div className="text-center text-muted mt-4">
          <p>Öğrenci bulunamadı.</p>
        </div>
      )}

      {/* Modal'lar */}
      {showAssignModal && selectedStudent && (
        <AssignLessonModal
          student={selectedStudent}
          onClose={handleCloseAssignModal}
          onSuccess={handleAssignSuccess}
        />
      )}

      {showAssignIpModal && selectedStudent && (
        <AssignIpAddressModal
          student={selectedStudent}
          onClose={handleCloseAssignIpModal}
          onSuccess={handleAssignSuccess}
        />
      )}
    </div>
  );
}

export default StudentList;
