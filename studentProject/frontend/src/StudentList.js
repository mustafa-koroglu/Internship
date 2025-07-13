import React, { useEffect, useState } from "react";

// Yeni öğrenci eklerken kullanılacak başlangıç formu
const initialForm = { name: "", surname: "", number: "" };

// Öğrenci listesini ve işlemlerini yöneten ana React component'i
const StudentList = () => {
  // students: Öğrenci listesini tutar
  // loading: Yükleniyor mu?
  // showModal: Modal açık mı?
  // form: Form verileri
  // editId: Düzenlenen öğrenci id'si
  // searchTerm: Arama kutusundaki değer
  // searchResults: Arama sonuçları
  // isSearching: Arama işlemi devam ediyor mu?
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState(initialForm);
  const [editId, setEditId] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [isSearching, setIsSearching] = useState(false);

  // Component yüklendiğinde öğrencileri getirir
  useEffect(() => {
    fetchStudents();
  }, []);

  // Tüm öğrencileri backend'den çeker
  const fetchStudents = () => {
    fetch("http://localhost:8080/api/v3/students")
      .then((res) => res.json())
      .then((data) => {
        setStudents(data);
        setLoading(false);
      });
  };

  // Arama kutusuna yazıldıkça backend'den arama yapar
  const handleSearch = (searchValue) => {
    const value = searchValue.trim();
    setSearchTerm(value);

    if (value === "") {
      setSearchResults([]);
      setIsSearching(false);
      return;
    }

    setIsSearching(true);
    // Eğer sadece rakamlardan oluşuyorsa numaraya göre ara
    const isNumber = /^\d+$/.test(value);
    const endpoint = isNumber
      ? `http://localhost:8080/api/v3/students/search/number?number=${encodeURIComponent(
          value
        )}`
      : `http://localhost:8080/api/v3/students/search?q=${encodeURIComponent(
          value
        )}`;
    fetch(endpoint)
      .then((res) => res.json())
      .then((data) => {
        setSearchResults(data);
        setIsSearching(false);
      })
      .catch((error) => {
        console.error("Arama hatası:", error);
        setIsSearching(false);
      });
  };

  // Arama sonuçlarını temizler
  const clearSearch = () => {
    setSearchTerm("");
    setSearchResults([]);
    setIsSearching(false);
  };

  // Form input değişikliklerini yönetir
  const handleInputChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  // Ekleme veya güncelleme işlemini yönetir
  const handleSubmit = (e) => {
    e.preventDefault();
    if (editId === null) {
      // Yeni öğrenci ekle
      fetch("http://localhost:8080/api/v3/students", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      })
        .then((res) => res.json())
        .then(() => {
          fetchStudents();
          setShowModal(false);
          setForm(initialForm);
        });
    } else {
      // Öğrenci güncelle
      fetch(`http://localhost:8080/api/v3/students/${editId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      })
        .then((res) => res.json())
        .then(() => {
          fetchStudents();
          setShowModal(false);
          setForm(initialForm);
          setEditId(null);
        });
    }
  };

  // Öğrenci silme işlemini yönetir
  const handleDelete = (id) => {
    if (window.confirm("Silmek istediğinize emin misiniz?")) {
      fetch(`http://localhost:8080/api/v3/students/${id}`, {
        method: "DELETE",
      }).then(() => fetchStudents());
    }
  };

  // Düzenleme işlemini başlatır
  const handleEdit = (student) => {
    setForm({
      name: student.name,
      surname: student.surname,
      number: student.number,
    });
    setEditId(student.id);
    setShowModal(true);
  };

  // Yükleniyorsa loading göster
  if (loading) return <div>Loading...</div>;

  // Component'in ana render'ı
  return (
    <div className="container" style={{ marginTop: "40px" }}>
      {/* Başlık ve ekle butonu */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1 style={{ fontWeight: "bold" }}>Students</h1>
        <button
          className="btn btn-success"
          style={{ width: "150px", fontSize: "1.2rem" }}
          onClick={() => {
            setForm(initialForm);
            setEditId(null);
            setShowModal(true);
          }}
        >
          Add Student
        </button>
      </div>

      {/* Arama Kutusu */}
      <div className="row mb-4 justify-content-center">
        <div className="col-md-8">
          <div className="search-box position-relative">
            <input
              type="text"
              className="form-control search-input"
              placeholder="🔍 Öğrenci adı, soyadı veya numarası ile ara..."
              value={searchTerm}
              onChange={(e) => handleSearch(e.target.value)}
              style={{ fontSize: "1.2rem", paddingRight: "40px" }}
            />
            {searchTerm && (
              <button
                className="btn search-clear-btn"
                type="button"
                onClick={clearSearch}
                tabIndex={-1}
                style={{
                  position: "absolute",
                  right: 10,
                  top: "50%",
                  transform: "translateY(-50%)",
                  fontSize: "1.3rem",
                  color: "#888",
                  background: "none",
                  border: "none",
                  boxShadow: "none",
                }}
              >
                ✕
              </button>
            )}
          </div>
          {isSearching && <small className="text-muted">Aranıyor...</small>}
          {searchTerm && !isSearching && (
            <small className="text-muted">
              {searchResults.length} sonuç bulundu
            </small>
          )}
        </div>
      </div>

      {/* Öğrenci tablosu */}
      <table className="table table-bordered">
        <thead>
          <tr>
            <th
              style={{ background: "#000", color: "#fff", borderColor: "#000" }}
            >
              Name
            </th>
            <th
              style={{ background: "#000", color: "#fff", borderColor: "#000" }}
            >
              Surname
            </th>
            <th
              style={{ background: "#000", color: "#fff", borderColor: "#000" }}
            >
              Number
            </th>
            <th
              style={{
                background: "#000",
                color: "#fff",
                width: "110px",
                textAlign: "center",
                borderColor: "#000",
              }}
            >
              Actions
            </th>
          </tr>
        </thead>
        <tbody>
          {/* Arama varsa arama sonuçlarını, yoksa tüm öğrencileri göster */}
          {searchTerm && searchResults.length === 0 && !isSearching ? (
            <tr>
              <td colSpan="4" className="text-center text-muted">
                "{searchTerm}" için sonuç bulunamadı.
              </td>
            </tr>
          ) : (
            (searchTerm ? searchResults : students).map((student) => (
              <tr key={student.id}>
                <td>{student.name}</td>
                <td>{student.surname}</td>
                <td>{student.number}</td>
                <td
                  style={{
                    width: "110px",
                    textAlign: "center",
                    verticalAlign: "middle",
                  }}
                >
                  <div
                    style={{
                      display: "flex",
                      gap: "6px",
                      justifyContent: "center",
                    }}
                  >
                    <button
                      className="btn btn-primary"
                      style={{ minWidth: "48px", padding: "4px 12px" }}
                      onClick={() => handleEdit(student)}
                    >
                      Edit
                    </button>
                    <button
                      className="btn btn-danger"
                      style={{ minWidth: "60px", padding: "4px 12px" }}
                      onClick={() => handleDelete(student.id)}
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      {/* Öğrenci ekleme/düzenleme modalı */}
      {showModal && (
        <div
          className="modal show"
          style={{ display: "block", background: "rgba(0,0,0,0.5)" }}
        >
          <div className="modal-dialog">
            <form onSubmit={handleSubmit}>
              <div className="modal-content">
                <div className="modal-header">
                  <h5 className="modal-title">
                    {editId === null ? "Add Student" : "Edit Student"}
                  </h5>
                  <button
                    type="button"
                    className="btn-close"
                    onClick={() => setShowModal(false)}
                  ></button>
                </div>
                <div className="modal-body">
                  <input
                    className="form-control mb-2"
                    name="name"
                    placeholder="Name"
                    value={form.name}
                    onChange={handleInputChange}
                    required
                  />
                  <input
                    className="form-control mb-2"
                    name="surname"
                    placeholder="Surname"
                    value={form.surname}
                    onChange={handleInputChange}
                    required
                  />
                  <input
                    className="form-control mb-2"
                    name="number"
                    placeholder="Number"
                    value={form.number}
                    onChange={handleInputChange}
                    required
                  />
                </div>
                <div className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => setShowModal(false)}
                  >
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-success">
                    {editId === null ? "Add" : "Update"}
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default StudentList;
