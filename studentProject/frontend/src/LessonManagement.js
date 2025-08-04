import React, { useEffect, useState } from "react";

function LessonManagement({ role }) {
  const [lessons, setLessons] = useState([]);
  const [error, setError] = useState("");
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

  const token = localStorage.getItem("token");

  function showError(msg) {
    setError(msg);
    setTimeout(() => setError(""), 3000);
  }

  function showSuccess(msg) {
    setError("");
    setTimeout(() => {
      setError(msg);
      setTimeout(() => setError(""), 3000);
    }, 100);
  }

  function fetchLessons() {
    fetch("http://localhost:8080/api/lessons", {
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
        setLessons(data);
      })
      .catch((err) => showError(err.message));
  }

  function handleAddSubmit(e) {
    e.preventDefault();
    fetch("http://localhost:8080/api/lessons", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(newLesson),
    })
      .then(async (res) => {
        if (!res.ok) {
          const data = await res.json().catch(() => ({}));
          throw new Error(data.message || "Ekleme başarısız!");
        }
        return res.json();
      })
      .then((data) => {
        setLessons((prev) => [...prev, data]);
        setShowAddForm(false);
        setNewLesson({ name: "", description: "", academicYear: "", term: "" });
        showSuccess("Ders başarıyla eklendi!");
      })
      .catch((err) => showError(err.message));
  }

  function handleEditSubmit(e, id) {
    e.preventDefault();
    fetch(`http://localhost:8080/api/lessons/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(editLesson),
    })
      .then(async (res) => {
        if (!res.ok) {
          const data = await res.json().catch(() => ({}));
          throw new Error(data.message || "Güncelleme başarısız!");
        }
        return res.json();
      })
      .then((data) => {
        setLessons((prev) => prev.map((l) => (l.id === id ? data : l)));
        setEditId(null);
        showSuccess("Ders başarıyla güncellendi!");
      })
      .catch((err) => showError(err.message));
  }

  function handleDelete(id) {
    if (!window.confirm("Bu dersi silmek istediğinizden emin misiniz?")) {
      return;
    }

    fetch(`http://localhost:8080/api/lessons/${id}`, {
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
        setLessons((prev) => prev.filter((l) => l.id !== id));
        showSuccess("Ders başarıyla silindi!");
      })
      .catch((err) => showError(err.message));
  }

  useEffect(() => {
    fetchLessons();
  }, []);

  if (role !== "ADMIN") {
    return (
      <div className="container mt-5 text-center">
        <h2>Erişim Reddedildi</h2>
        <p>Bu sayfaya erişim yetkiniz bulunmamaktadır.</p>
      </div>
    );
  }

  return (
    <div className="container mt-4">
      <h2 className="mb-4">Ders Yönetimi</h2>

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
                value={newLesson.name}
                onChange={(e) =>
                  setNewLesson({ ...newLesson, name: e.target.value })
                }
                required
              />
            </div>
            <div className="col-md-6">
              <input
                className="form-control"
                placeholder="Açıklama"
                value={newLesson.description}
                onChange={(e) =>
                  setNewLesson({ ...newLesson, description: e.target.value })
                }
              />
            </div>
            <div className="col-md-6">
              <input
                className="form-control"
                placeholder="Akademik Yıl (örn: 2023-2024)"
                value={newLesson.academicYear}
                onChange={(e) =>
                  setNewLesson({ ...newLesson, academicYear: e.target.value })
                }
                required
              />
            </div>
            <div className="col-md-6">
              <select
                className="form-control"
                value={newLesson.term}
                onChange={(e) =>
                  setNewLesson({ ...newLesson, term: e.target.value })
                }
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
                        value={editLesson.name}
                        onChange={(e) =>
                          setEditLesson({ ...editLesson, name: e.target.value })
                        }
                      />
                    </td>
                    <td>
                      <input
                        className="form-control"
                        value={editLesson.description}
                        onChange={(e) =>
                          setEditLesson({
                            ...editLesson,
                            description: e.target.value,
                          })
                        }
                      />
                    </td>
                    <td>
                      <input
                        className="form-control"
                        value={editLesson.academicYear}
                        onChange={(e) =>
                          setEditLesson({
                            ...editLesson,
                            academicYear: e.target.value,
                          })
                        }
                      />
                    </td>
                    <td>
                      <select
                        className="form-control"
                        value={editLesson.term}
                        onChange={(e) =>
                          setEditLesson({ ...editLesson, term: e.target.value })
                        }
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
                        onClick={() => {
                          setEditId(lesson.id);
                          setEditLesson({
                            name: lesson.name,
                            description: lesson.description,
                            academicYear: lesson.academicYear,
                            term: lesson.term,
                          });
                        }}
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
