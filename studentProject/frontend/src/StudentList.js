// Öğrenci listesini ve işlemlerini yöneten ana bileşen.
import React, { useEffect, useState } from "react";

/**
 * StudentList bileşeni, öğrenci listesini, arama, ekleme, silme ve güncelleme işlemlerini yönetir.
 * Admin ve user rolleri için farklı buton ve işlemler sunar.
 *
 * @param {string} role - Kullanıcı rolü (ADMIN/USER)
 */
function StudentList({ role }) {
  // Öğrenci listesini tutan state
  const [students, setStudents] = useState([]);
  // Hata mesajını tutan state
  const [error, setError] = useState("");
  // Ekleme formunun gösterilip gösterilmeyeceğini tutan state
  const [showAddForm, setShowAddForm] = useState(false);
  // Yeni öğrenci ekleme formu için state
  const [newStudent, setNewStudent] = useState({
    name: "",
    surname: "",
    number: "",
  });
  // Düzenlenen öğrencinin id'sini tutan state
  const [editId, setEditId] = useState(null);
  // Düzenleme formu için öğrenci bilgilerini tutan state
  const [editStudent, setEditStudent] = useState({
    name: "",
    surname: "",
    number: "",
  });
  // Arama kutusundaki değeri tutan state
  const [search, setSearch] = useState("");

  // JWT token'ı localStorage'dan alır
  const token = localStorage.getItem("token");
  // Kullanıcının admin olup olmadığını kontrol eder
  const isAdmin = role === "ADMIN";

  /**
   * Hata mesajını ekranda 3 saniye gösterip otomatik siler
   * @param {string} msg - Hata mesajı
   */
  function showError(msg) {
    setError(msg);
    setTimeout(() => setError(""), 3000);
  }

  /**
   * Öğrenci listesini ve arama işlemini yönetir
   * Arama kutusu değiştikçe veya component mount olduğunda çalışır
   */
  useEffect(() => {
    let url = "http://localhost:8080/api/v3/students";
    if (search.trim() !== "") {
      url = `http://localhost:8080/api/v3/students/search?q=${encodeURIComponent(
        search
      )}`;
    }
    fetch(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then(async (res) => {
        if (!res.ok) {
          const data = await res.json().catch(() => ({}));
          throw new Error(data.message || "Listeleme başarısız!");
        }
        return res.json();
      })
      .then((data) => setStudents(data))
      .catch((err) => showError(err.message));
  }, [token, search]);

  /**
   * Öğrenci silme işlemini yönetir
   * @param {number} id - Silinecek öğrenci id'si
   */
  function handleDelete(id) {
    if (window.confirm("Bu öğrenciyi silmek istediğine emin misin?")) {
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
          setStudents((prev) => prev.filter((s) => s.id !== id));
        })
        .catch((err) => showError(err.message));
    }
  }

  /**
   * Yeni öğrenci ekleme işlemini yönetir
   * @param {Event} e - Form submit event'i
   */
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
      .then((data) => {
        setStudents((prev) => [...prev, data]);
        setShowAddForm(false);
        setNewStudent({ name: "", surname: "", number: "" });
      })
      .catch((err) => showError(err.message));
  }

  /**
   * Öğrenci güncelleme işlemini yönetir
   * @param {Event} e - Form submit event'i
   * @param {number} id - Güncellenecek öğrenci id'si
   */
  function handleEditSubmit(e, id) {
    e.preventDefault();
    fetch(`http://localhost:8080/api/v3/students/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(editStudent),
    })
      .then(async (res) => {
        if (!res.ok) {
          const data = await res.json().catch(() => ({}));
          throw new Error(data.message || "Güncelleme başarısız!");
        }
        return res.json();
      })
      .then((data) => {
        setStudents((prev) => prev.map((s) => (s.id === id ? data : s)));
        setEditId(null);
      })
      .catch((err) => showError(err.message));
  }

  // Bileşenin render edilen kısmı
  return (
    <div className="container mt-5">
      {/* Başlık ve ekle butonu */}
      <div className="d-flex flex-row justify-content-between align-items-center mb-4">
        <h2 className="fw-bold">Students</h2>
        {isAdmin && (
          <button
            className="btn btn-success"
            onClick={() => setShowAddForm(true)}
          >
            Add Student
          </button>
        )}
      </div>
      {/* Hata mesajı */}
      {error && <div className="alert alert-danger">{error}</div>}

      {/* Arama kutusu */}
      <div className="row mb-3">
        <div className="col-12 col-md-6">
          <input
            type="text"
            className="form-control"
            placeholder="Search by name, surname or number"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
      </div>

      {/* Ekleme Formu (sadece adminler için) */}
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
              <button className="btn btn-success" type="submit">
                Save
              </button>
              <button
                className="btn btn-secondary ms-2"
                type="button"
                onClick={() => setShowAddForm(false)}
              >
                Cancel
              </button>
            </div>
          </div>
        </form>
      )}

      {/* Öğrenci tablosu */}
      <table className="table table-bordered table-striped">
        <thead>
          <tr>
            <th className="bg-dark text-white">Name</th>
            <th className="bg-dark text-white">Surname</th>
            <th className="bg-dark text-white">Number</th>
            {isAdmin && (
              <th
                className="bg-dark text-white text-center"
                style={{ width: "140px" }}
              >
                Actions
              </th>
            )}
          </tr>
        </thead>
        <tbody>
          {/* Öğrenci satırlarını render eder */}
          {students.map((student) => (
            <tr key={student.id}>
              {/* Eğer düzenleme modundaysa inputlar gösterilir */}
              {editId === student.id ? (
                <>
                  <td>
                    <input
                      className="form-control"
                      value={editStudent.name}
                      onChange={(e) =>
                        setEditStudent({ ...editStudent, name: e.target.value })
                      }
                    />
                  </td>
                  <td>
                    <input
                      className="form-control"
                      value={editStudent.surname}
                      onChange={(e) =>
                        setEditStudent({
                          ...editStudent,
                          surname: e.target.value,
                        })
                      }
                    />
                  </td>
                  <td>
                    <input
                      className="form-control"
                      value={editStudent.number}
                      onChange={(e) =>
                        setEditStudent({
                          ...editStudent,
                          number: e.target.value,
                        })
                      }
                    />
                  </td>
                  <td
                    className="text-center"
                    style={{ width: "140px", whiteSpace: "nowrap" }}
                  >
                    <button
                      className="btn btn-success btn-sm me-2"
                      onClick={(e) => handleEditSubmit(e, student.id)}
                    >
                      Save
                    </button>
                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={() => setEditId(null)}
                    >
                      Cancel
                    </button>
                  </td>
                </>
              ) : (
                <>
                  {/* Normal modda öğrenci bilgileri gösterilir */}
                  <td>{student.name}</td>
                  <td>{student.surname}</td>
                  <td>{student.number}</td>
                  {isAdmin && (
                    <td
                      className="text-center"
                      style={{ width: "140px", whiteSpace: "nowrap" }}
                    >
                      <button
                        className="btn btn-primary btn-sm me-2"
                        onClick={() => {
                          setEditId(student.id);
                          setEditStudent({
                            name: student.name,
                            surname: student.surname,
                            number: student.number,
                          });
                        }}
                      >
                        Edit
                      </button>
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => handleDelete(student.id)}
                      >
                        Delete
                      </button>
                    </td>
                  )}
                </>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

// StudentList bileşenini dışa aktarır
export default StudentList;
