// Öğrenci listesi ve işlemlerini yöneten ana bileşen
import React, { useEffect, useState } from "react";
import AssignLessonModal from "./AssignLessonModal";

function StudentList({ role }) {
  const [students, setStudents] = useState([]);
  const [error, setError] = useState("");
  const [showAddForm, setShowAddForm] = useState(false);
  const [newStudent, setNewStudent] = useState({
    name: "",
    surname: "",
    number: "",
  });
  const [search, setSearch] = useState("");
  const [showAssignModal, setShowAssignModal] = useState(false);
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [expandedStudents, setExpandedStudents] = useState(new Set());
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

  const token = localStorage.getItem("token");
  const isAdmin = role === "ADMIN";

  // Hata mesajını göster
  function showError(msg) {
    setError(msg);
    setTimeout(() => setError(""), 3000);
  }

  // Başarı mesajını göster
  function showSuccess(msg) {
    setError("");
    setTimeout(() => {
      setError(msg);
      setTimeout(() => setError(""), 3000);
    }, 100);
  }

  // Öğrenci listesini çek
  function fetchStudents() {
    const endpoint = isAdmin ? "/students" : "/students/verified";
    const url = `http://localhost:8080/api/v3${endpoint}`;

    fetch(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then(async (res) => {
        if (!res.ok) {
          const data = await res.json().catch(() => ({}));
          throw new Error(data.message || "Veri çekme başarısız!");
        }
        return res.json();
      })
      .then((data) => {
        setStudents(data);
      })
      .catch((err) => showError(err.message));
  }

  // Arama işlemi
  function handleSearch() {
    if (!search.trim()) {
      fetchStudents();
      return;
    }

    const endpoint = isAdmin ? "/students/search" : "/students/verified/search";

    fetch(
      `http://localhost:8080/api/v3${endpoint}?q=${encodeURIComponent(search)}`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    )
      .then(async (res) => {
        if (!res.ok) {
          const data = await res.json().catch(() => ({}));
          throw new Error(data.message || "Arama başarısız!");
        }
        return res.json();
      })
      .then((data) => {
        setStudents(data);
      })
      .catch((err) => showError(err.message));
  }

  // Öğrenci onayla
  function handleApprove(id) {
    fetch(`http://localhost:8080/api/v3/students/${id}/approve`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then(async (res) => {
        if (!res.ok) {
          const data = await res.json().catch(() => ({}));
          throw new Error(data.message || "Onaylama başarısız!");
        }
        return res.json();
      })
      .then(() => {
        showSuccess("Öğrenci başarıyla onaylandı!");
        fetchStudents();
      })
      .catch((err) => showError(err.message));
  }

  // Görünürlük kontrolü
  function handleToggleVisibility(id, currentView) {
    fetch(
      `http://localhost:8080/api/v3/students/${id}/visibility?view=${(!currentView).toString()}`,
      {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    )
      .then(async (res) => {
        if (!res.ok) {
          const data = await res.json().catch(() => ({}));
          throw new Error(data.message || "Görünürlük değiştirme başarısız!");
        }
        return res.json();
      })
      .then(() => {
        showSuccess("Görünürlük başarıyla değiştirildi!");
        fetchStudents();
      })
      .catch((err) => showError(err.message));
  }

  // Yeni öğrenci ekle
  function handleAddSubmit(e) {
    e.preventDefault();
    fetch("http://localhost:8080/api/v3/students", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(newStudent),
    })
      .then(async (res) => {
        if (!res.ok) {
          const data = await res.json().catch(() => ({}));
          throw new Error(data.message || "Ekleme başarısız!");
        }
        return res.json();
      })
      .then(() => {
        showSuccess("Öğrenci başarıyla eklendi!");
        setNewStudent({ name: "", surname: "", number: "" });
        setShowAddForm(false);
        fetchStudents();
      })
      .catch((err) => showError(err.message));
  }

  // Öğrenci düzenle
  function handleEditStudent(student) {
    setEditingStudent(student);
    setEditForm({
      name: student.name,
      surname: student.surname,
      number: student.number,
      verified: student.verified,
      view: student.view,
    });
    setIsEditing(false);
  }

  // Edit panelini kapat
  function handleCloseEdit() {
    setEditingStudent(null);
    setEditForm({
      name: "",
      surname: "",
      number: "",
      verified: false,
      view: false,
    });
    setIsEditing(false);
  }

  // Edit formunu güncelle
  function handleEditUpdate(e) {
    e.preventDefault();
    setEditLoading(true);

    fetch(`http://localhost:8080/api/v3/students/${editingStudent.id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(editForm),
    })
      .then(async (res) => {
        if (!res.ok) {
          const data = await res.json().catch(() => ({}));
          throw new Error(data.message || "Güncelleme başarısız!");
        }
        return res.json();
      })
      .then(() => {
        showSuccess("Öğrenci başarıyla güncellendi!");
        setIsEditing(false);
        fetchStudents();
      })
      .catch((err) => showError(err.message))
      .finally(() => {
        setEditLoading(false);
      });
  }

  // Öğrenci sil
  function handleDelete(id) {
    if (window.confirm("Bu öğrenciyi silmek istediğinizden emin misiniz?")) {
      fetch(`http://localhost:8080/api/v3/students/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
        .then(async (res) => {
          if (!res.ok) {
            const data = await res.json().catch(() => ({}));
            throw new Error(data.message || "Silme başarısız!");
          }
        })
        .then(() => {
          showSuccess("Öğrenci başarıyla silindi!");
          fetchStudents();
        })
        .catch((err) => showError(err.message));
    }
  }

  // Ders atama modalını aç
  function handleAssignLessons(student) {
    setSelectedStudent(student);
    setShowAssignModal(true);
  }

  // Ders atama modalını kapat
  function handleCloseAssignModal() {
    setShowAssignModal(false);
    setSelectedStudent(null);
  }

  // Ders atama başarılı olduğunda
  function handleAssignSuccess() {
    if (selectedStudent) {
      const fetchUpdatedStudent = async () => {
        try {
          const response = await fetch(
            `http://localhost:8080/api/v3/students/${selectedStudent.id}`,
            {
              headers: {
                Authorization: `Bearer ${token}`,
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
  }

  // Öğrenci derslerini genişlet/daralt
  function toggleStudentExpansion(studentId) {
    setExpandedStudents((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(studentId)) {
        newSet.delete(studentId);
      } else {
        newSet.add(studentId);
      }
      return newSet;
    });
  }

  useEffect(() => {
    fetchStudents();
  }, [isAdmin]);

  useEffect(() => {
    const timeoutId = setTimeout(() => {
      handleSearch();
    }, 300);

    return () => clearTimeout(timeoutId);
  }, [search]);

  return (
    <div className="container mt-4">
      <h2 className="mb-4">Öğrenci Listesi</h2>

      {error && (
        <div
          className={`alert ${
            error.includes("başarıyla") ? "alert-success" : "alert-danger"
          } alert-dismissible fade show`}
          role="alert"
        >
          {error}
          <button
            type="button"
            className="btn-close"
            onClick={() => setError("")}
          ></button>
        </div>
      )}

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

      {isAdmin && showAddForm && (
        <form className="mb-3" onSubmit={handleAddSubmit}>
          <div className="row g-2">
            <div className="col">
              <input
                className="form-control"
                placeholder="Name"
                value={newStudent.name}
                onChange={(e) =>
                  setNewStudent({ ...newStudent, name: e.target.value })
                }
                required
              />
            </div>
            <div className="col">
              <input
                className="form-control"
                placeholder="Surname"
                value={newStudent.surname}
                onChange={(e) =>
                  setNewStudent({ ...newStudent, surname: e.target.value })
                }
                required
              />
            </div>
            <div className="col">
              <input
                className="form-control"
                placeholder="Number"
                value={newStudent.number}
                onChange={(e) =>
                  setNewStudent({ ...newStudent, number: e.target.value })
                }
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
                      <form onSubmit={handleEditUpdate}>
                        <div className="mb-3">
                          <label className="form-label">Ad</label>
                          <input
                            type="text"
                            className="form-control"
                            value={editForm.name}
                            onChange={(e) =>
                              setEditForm({ ...editForm, name: e.target.value })
                            }
                            required
                          />
                        </div>
                        <div className="mb-3">
                          <label className="form-label">Soyad</label>
                          <input
                            type="text"
                            className="form-control"
                            value={editForm.surname}
                            onChange={(e) =>
                              setEditForm({
                                ...editForm,
                                surname: e.target.value,
                              })
                            }
                            required
                          />
                        </div>
                        <div className="mb-3">
                          <label className="form-label">Öğrenci Numarası</label>
                          <input
                            type="text"
                            className="form-control"
                            value={editForm.number}
                            onChange={(e) =>
                              setEditForm({
                                ...editForm,
                                number: e.target.value,
                              })
                            }
                            required
                          />
                        </div>
                        <div className="mb-3">
                          <div className="form-check">
                            <input
                              type="checkbox"
                              className="form-check-input"
                              id="verified"
                              checked={editForm.verified}
                              onChange={(e) =>
                                setEditForm({
                                  ...editForm,
                                  verified: e.target.checked,
                                })
                              }
                            />
                            <label
                              className="form-check-label"
                              htmlFor="verified"
                            >
                              Onaylı
                            </label>
                          </div>
                        </div>
                        <div className="mb-3">
                          <div className="form-check">
                            <input
                              type="checkbox"
                              className="form-check-input"
                              id="view"
                              checked={editForm.view}
                              onChange={(e) =>
                                setEditForm({
                                  ...editForm,
                                  view: e.target.checked,
                                })
                              }
                            />
                            <label className="form-check-label" htmlFor="view">
                              Görünür
                            </label>
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
                        className="btn btn-primary w-100"
                        onClick={() => handleAssignLessons(editingStudent)}
                        disabled={editLoading}
                      >
                        📚 Ders Ata/Düzenle
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
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

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
                        onClick={() => toggleStudentExpansion(student.id)}
                        title={`${student.lessons.length} ders göster/gizle`}
                      >
                        📚 ({student.lessons.length})
                      </button>
                    ) : (
                      <span className="text-muted">Ders yok</span>
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
                        onClick={() => handleDelete(student.id)}
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
                      <td colSpan={isAdmin ? 7 : 4} className="p-0">
                        <div className="p-3 border-top bg-light">
                          <div className="d-flex justify-content-between align-items-center mb-2">
                            <h6 className="mb-0">
                              📚 {student.name} {student.surname} - Dersler
                            </h6>
                            <button
                              className="btn btn-sm btn-outline-secondary"
                              onClick={() => toggleStudentExpansion(student.id)}
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

      {showAssignModal && selectedStudent && (
        <AssignLessonModal
          student={selectedStudent}
          onClose={handleCloseAssignModal}
          onSuccess={handleAssignSuccess}
        />
      )}
    </div>
  );
}

export default StudentList;
