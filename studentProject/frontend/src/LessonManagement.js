import React, { useEffect, useState, useCallback } from "react";
import { apiGet, apiPost, apiPut, apiDelete } from "./utils/api";
import { useNotification } from "./hooks/useNotification";

function LessonManagement({ role }) {
  const [lessons, setLessons] = useState([]);
  const [showAddForm, setShowAddForm] = useState(false);
  const [editId, setEditId] = useState(null);
  const [newLesson, setNewLesson] = useState({
    name: "",
    description: "",
    academicYear: "",
    term: "",
  });
  const [editLesson, setEditLesson] = useState({
    name: "",
    description: "",
    academicYear: "",
    term: "",
  });

  const { message, type, setMessage, showSuccess, showError } =
    useNotification();

  // Dersleri çek
  const fetchLessons = useCallback(async () => {
    try {
      const data = await apiGet("/lessons");
      setLessons(data);
    } catch (err) {
      showError(err.message);
    }
  }, [showError]);

  // Yeni ders ekle
  const handleAddSubmit = useCallback(
    async (e) => {
      e.preventDefault();
      try {
        const data = await apiPost("/lessons", newLesson);
        setLessons((prev) => [...prev, data]);
        setShowAddForm(false);
        setNewLesson({ name: "", description: "", academicYear: "", term: "" });
        showSuccess("Ders başarıyla eklendi!");
      } catch (err) {
        showError(err.message);
      }
    },
    [newLesson, showSuccess, showError]
  );

  // Ders güncelle
  const handleEditSubmit = useCallback(
    async (e, id) => {
      e.preventDefault();
      try {
        const data = await apiPut(`/lessons/${id}`, editLesson);
        setLessons((prev) => prev.map((l) => (l.id === id ? data : l)));
        setEditId(null);
        setEditLesson({
          name: "",
          description: "",
          academicYear: "",
          term: "",
        });
        showSuccess("Ders başarıyla güncellendi!");
      } catch (err) {
        showError(err.message);
      }
    },
    [editLesson, showSuccess, showError]
  );

  // Ders sil
  const handleDelete = useCallback(
    async (id) => {
      if (!window.confirm("Bu dersi silmek istediğinizden emin misiniz?"))
        return;

      try {
        await apiDelete(`/lessons/${id}`);
        setLessons((prev) => prev.filter((l) => l.id !== id));
        showSuccess("Ders başarıyla silindi!");
      } catch (err) {
        showError(err.message);
      }
    },
    [showSuccess, showError]
  );

  // Düzenleme modunu aç
  const handleEdit = useCallback((lesson) => {
    setEditId(lesson.id);
    setEditLesson({
      name: lesson.name,
      description: lesson.description,
      academicYear: lesson.academicYear,
      term: lesson.term,
    });
  }, []);

  // Düzenleme modunu kapat
  const handleCancelEdit = useCallback(() => {
    setEditId(null);
    setEditLesson({
      name: "",
      description: "",
      academicYear: "",
      term: "",
    });
  }, []);

  // Input değişiklikleri
  const handleInputChange = useCallback((e) => {
    const { name, value } = e.target;
    setNewLesson((prev) => ({ ...prev, [name]: value }));
  }, []);

  const handleEditInputChange = useCallback((e) => {
    const { name, value } = e.target;
    setEditLesson((prev) => ({ ...prev, [name]: value }));
  }, []);

  // Component mount olduğunda dersleri çek
  useEffect(() => {
    fetchLessons();
  }, [fetchLessons]);

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

      <h2 className="mb-4">Ders Yönetimi</h2>

      <div className="row mb-3">
        <div className="col-md-6">
          <h4>Mevcut Dersler</h4>
        </div>
        <div className="col-md-6 text-end">
          <button
            className="btn btn-primary"
            onClick={() => setShowAddForm(!showAddForm)}
          >
            Yeni Ders Ekle
          </button>
        </div>
      </div>

      {showAddForm && (
        <form className="mb-4 p-3 border rounded" onSubmit={handleAddSubmit}>
          <h5>Yeni Ders Ekle</h5>
          <div className="row g-3">
            <div className="col-md-6">
              <input
                className="form-control"
                placeholder="Ders Adı"
                name="name"
                value={newLesson.name}
                onChange={handleInputChange}
                required
              />
            </div>
            <div className="col-md-6">
              <input
                className="form-control"
                placeholder="Açıklama"
                name="description"
                value={newLesson.description}
                onChange={handleInputChange}
              />
            </div>
            <div className="col-md-6">
              <input
                className="form-control"
                placeholder="Akademik Yıl (örn: 2023-2024)"
                name="academicYear"
                value={newLesson.academicYear}
                onChange={handleInputChange}
                required
              />
            </div>
            <div className="col-md-6">
              <select
                className="form-control"
                name="term"
                value={newLesson.term}
                onChange={handleInputChange}
                required
              >
                <option value="">Dönem Seçin</option>
                <option value="Güz">Güz</option>
                <option value="Bahar">Bahar</option>
                <option value="Yaz">Yaz</option>
              </select>
            </div>
            <div className="col-12">
              <button className="btn btn-success me-2" type="submit">
                Kaydet
              </button>
              <button
                className="btn btn-secondary"
                type="button"
                onClick={() => setShowAddForm(false)}
              >
                İptal
              </button>
            </div>
          </div>
        </form>
      )}

      <div className="table-responsive">
        <table className="table table-bordered table-striped">
          <thead>
            <tr>
              <th className="bg-dark text-white">Ders Adı</th>
              <th className="bg-dark text-white">Açıklama</th>
              <th className="bg-dark text-white">Akademik Yıl</th>
              <th className="bg-dark text-white">Dönem</th>
              <th
                className="bg-dark text-white text-center"
                style={{ width: "150px" }}
              >
                İşlemler
              </th>
            </tr>
          </thead>
          <tbody>
            {lessons.map((lesson) => (
              <tr key={lesson.id}>
                {editId === lesson.id ? (
                  <>
                    <td>
                      <input
                        className="form-control"
                        name="name"
                        value={editLesson.name}
                        onChange={handleEditInputChange}
                      />
                    </td>
                    <td>
                      <input
                        className="form-control"
                        name="description"
                        value={editLesson.description}
                        onChange={handleEditInputChange}
                      />
                    </td>
                    <td>
                      <input
                        className="form-control"
                        name="academicYear"
                        value={editLesson.academicYear}
                        onChange={handleEditInputChange}
                      />
                    </td>
                    <td>
                      <select
                        className="form-control"
                        name="term"
                        value={editLesson.term}
                        onChange={handleEditInputChange}
                      >
                        <option value="Güz">Güz</option>
                        <option value="Bahar">Bahar</option>
                        <option value="Yaz">Yaz</option>
                      </select>
                    </td>
                    <td className="text-center">
                      <button
                        className="btn btn-success btn-sm me-2"
                        onClick={(e) => handleEditSubmit(e, lesson.id)}
                      >
                        Kaydet
                      </button>
                      <button
                        className="btn btn-secondary btn-sm"
                        onClick={() => setEditId(null)}
                      >
                        İptal
                      </button>
                    </td>
                  </>
                ) : (
                  <>
                    <td>{lesson.name}</td>
                    <td>{lesson.description}</td>
                    <td>{lesson.academicYear}</td>
                    <td>{lesson.term}</td>
                    <td className="text-center">
                      <button
                        className="btn btn-primary btn-sm me-2"
                        onClick={() => handleEdit(lesson)}
                      >
                        Düzenle
                      </button>
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => handleDelete(lesson.id)}
                      >
                        Sil
                      </button>
                    </td>
                  </>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {lessons.length === 0 && (
        <div className="text-center text-muted mt-4">
          <p>Henüz ders eklenmemiş.</p>
        </div>
      )}
    </div>
  );
}

export default LessonManagement;
