// Öğrenci listesini ve işlemlerini yöneten ana bileşen.
import React, { useEffect, useState } from "react";

/**
 * StudentList bileşeni, öğrenci listesini, arama, ekleme, silme ve güncelleme işlemlerini yönetir.
 * Admin ve user rolleri için farklı buton ve işlemler sunar.
 *
 * Öğrenci onay sistemi entegrasyonu:
 * - Admin: Tüm öğrencileri görür, onaylama ve görünürlük kontrolü yapabilir
 * - User: Sadece onaylanmış ve görünür öğrencileri görür
 * - Farklı endpoint'ler kullanır (admin: /students, user: /students/verified)
 * - Onaylama butonu (✓): Onaysız öğrencileri onaylar
 * - Görünürlük butonu (👁️): Öğrencileri gizler/gösterir
 *
 * İş kuralları:
 * - Admin tüm öğrencileri görür ve yönetebilir
 * - User sadece verified=true VE view=true olan öğrencileri görür
 * - Manuel eklenen öğrenciler otomatik onaylanır
 * - CSV'den okunan öğrenciler admin onayı bekler
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
    verified: false, // Öğrenci onay sistemi alanı
    view: false, // Öğrenci görünürlük alanı
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
   * Başarı mesajını ekranda 3 saniye gösterip otomatik siler
   * @param {string} msg - Başarı mesajı
   */
  function showSuccess(msg) {
    setError(""); // Hata mesajını temizle
    setTimeout(() => {
      setError(msg);
      setTimeout(() => setError(""), 3000);
    }, 100);
  }

  /**
   * Öğrenci listesini API'den çeker
   *
   * Bu metod, kullanıcı rolüne göre farklı endpoint'ler kullanır:
   * - Admin: /students (tüm öğrenciler)
   * - User: /students/verified (sadece onaylanmış ve görünür öğrenciler)
   *
   * Öğrenci onay sistemi:
   * - Admin tüm öğrencileri görür (onaylı/onaysız, görünür/gizli)
   * - User sadece verified=true VE view=true olan öğrencileri görür
   */
  function fetchStudents() {
    // Admin tüm öğrencileri, user sadece onaylanmış ve görünür öğrencileri görür
    const endpoint = isAdmin ? "/students" : "/students/verified";
    const url = `http://localhost:8080/api/v3${endpoint}`;

    console.log("Fetching URL:", url);
    console.log("Token:", token);
    console.log("Is Admin:", isAdmin);

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

  /**
   * Arama işlemini gerçekleştirir
   *
   * Bu metod, kullanıcı rolüne göre farklı arama endpoint'leri kullanır:
   * - Admin: /students/search (tüm öğrencilerde arama)
   * - User: /students/verified/search (sadece onaylanmış öğrencilerde arama)
   *
   * Öğrenci onay sistemi:
   * - Admin tüm öğrencilerde arama yapabilir
   * - User sadece onaylanmış ve görünür öğrencilerde arama yapabilir
   */
  function handleSearch() {
    if (!search.trim()) {
      fetchStudents();
      return;
    }

    // Admin tüm öğrencilerde, user sadece onaylanmış öğrencilerde arama yapar
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

  /**
   * Öğrenci onaylama işlemini yönetir (sadece admin)
   *
   * Bu metod, admin tarafından öğrenci onaylama işlemini gerçekleştirir.
   * Onaylanan öğrenci artık kullanıcılara gösterilebilir hale gelir.
   *
   * İş kuralları:
   * - Sadece admin kullanabilir
   * - verified = true yapılır
   * - view durumu değişmez (admin manuel olarak ayarlayabilir)
   *
   * @param {number} id - Onaylanacak öğrenci id'si
   */
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
      .then((data) => {
        setStudents((prev) => prev.map((s) => (s.id === id ? data : s)));
        showSuccess("Öğrenci başarıyla onaylandı!");
      })
      .catch((err) => showError(err.message));
  }

  /**
   * Öğrenci görünürlük durumunu değiştirir (sadece admin)
   *
   * Bu metod, admin tarafından öğrencinin kullanıcılara gösterilip
   * gösterilmeyeceğini kontrol etmek için kullanılır.
   *
   * İş kuralları:
   * - Sadece admin kullanabilir
   * - view = true/false yapılır
   * - verified durumu değişmez
   *
   * @param {number} id - Öğrenci id'si
   * @param {boolean} currentView - Mevcut görünürlük durumu
   */
  function handleToggleVisibility(id, currentView) {
    const newView = !currentView;

    fetch(
      `http://localhost:8080/api/v3/students/${id}/visibility?view=${newView}`,
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
      .then((data) => {
        setStudents((prev) => prev.map((s) => (s.id === id ? data : s)));
        showSuccess(
          `Öğrenci ${newView ? "görünür" : "gizli"} olarak ayarlandı!`
        );
      })
      .catch((err) => showError(err.message));
  }

  /**
   * Yeni öğrenci ekleme işlemini yönetir
   *
   * Bu metod, admin tarafından yeni öğrenci ekleme işlemini gerçekleştirir.
   * Manuel eklenen öğrenciler otomatik olarak onaylanır ve görünür olur.
   *
   * İş kuralları:
   * - Sadece admin kullanabilir
   * - Manuel eklenen öğrenciler: verified=true, view=true
   * - CSV'den okunan öğrenciler bu endpoint kullanılmaz
   *
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
        showSuccess("Öğrenci başarıyla eklendi!");
      })
      .catch((err) => showError(err.message));
  }

  /**
   * Öğrenci güncelleme işlemini yönetir
   *
   * Bu metod, admin tarafından öğrenci güncelleme işlemini gerçekleştirir.
   * Öğrencinin tüm alanları (onay durumu dahil) güncellenebilir.
   *
   * Güncellenebilir alanlar:
   * - name, surname, number: Temel bilgiler
   * - verified, view: Onay ve görünürlük durumu
   *
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
        showSuccess("Öğrenci başarıyla güncellendi!");
      })
      .catch((err) => showError(err.message));
  }

  /**
   * Öğrenci silme işlemini yönetir
   *
   * Bu metod, admin tarafından öğrenci silme işlemini gerçekleştirir.
   * Silme işlemi öncesi kullanıcıdan onay alınır.
   *
   * İş kuralları:
   * - Sadece admin kullanabilir
   * - Silme işlemi geri alınamaz
   * - Kullanıcı onayı gerekli
   *
   * @param {number} id - Silinecek öğrenci id'si
   */
  function handleDelete(id) {
    if (!window.confirm("Bu öğrenciyi silmek istediğinizden emin misiniz?")) {
      return;
    }

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
        return res.json();
      })
      .then(() => {
        setStudents((prev) => prev.filter((s) => s.id !== id));
        showSuccess("Öğrenci başarıyla silindi!");
      })
      .catch((err) => showError(err.message));
  }

  // Component mount olduğunda öğrenci listesini çeker
  useEffect(() => {
    fetchStudents();
  }, [isAdmin]);

  // Arama değiştiğinde otomatik arama yapar (debounce ile)
  useEffect(() => {
    const timeoutId = setTimeout(() => {
      handleSearch();
    }, 300); // 300ms gecikme ile arama yapar

    return () => clearTimeout(timeoutId);
  }, [search]);

  return (
    <div className="container mt-4">
      <h2 className="mb-4">Öğrenci Listesi</h2>

      {/* Hata/Başarı mesajı */}
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

      {/* Arama ve ekleme butonları */}
      <div className="row mb-3">
        <div className="col-md-6">
          {/* Arama kutusu - otomatik arama yapar */}
          <input
            type="text"
            className="form-control"
            placeholder="Öğrenci ara..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <div className="col-md-6 text-end">
          {/* Yeni öğrenci ekleme butonu - sadece admin */}
          {isAdmin && (
            <button
              className="btn btn-primary"
              onClick={() => setShowAddForm(!showAddForm)}
            >
              {showAddForm ? "İptal" : "Yeni Öğrenci Ekle"}
            </button>
          )}
        </div>
      </div>

      {/* Yeni öğrenci ekleme formu - sadece admin */}
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
            {/* Admin için ek sütunlar - onay durumu ve görünürlük */}
            {isAdmin && (
              <>
                <th className="bg-dark text-white text-center">Verified</th>
                <th className="bg-dark text-white text-center">View</th>
                <th
                  className="bg-dark text-white text-center"
                  style={{ width: "200px" }}
                >
                  Actions
                </th>
              </>
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
                  {/* Admin için düzenleme formunda onay durumu kontrolü */}
                  {isAdmin && (
                    <>
                      <td className="text-center">
                        <input
                          type="checkbox"
                          className="form-check-input"
                          checked={editStudent.verified}
                          onChange={(e) =>
                            setEditStudent({
                              ...editStudent,
                              verified: e.target.checked,
                            })
                          }
                        />
                      </td>
                      <td className="text-center">
                        <input
                          type="checkbox"
                          className="form-check-input"
                          checked={editStudent.view}
                          onChange={(e) =>
                            setEditStudent({
                              ...editStudent,
                              view: e.target.checked,
                            })
                          }
                        />
                      </td>
                    </>
                  )}
                  <td
                    className="text-center"
                    style={{ width: "200px", whiteSpace: "nowrap" }}
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
                  {/* Admin için onay durumu ve işlem butonları */}
                  {isAdmin && (
                    <>
                      {/* Onay durumu gösterimi */}
                      <td className="text-center">
                        <span
                          className={`badge ${
                            student.verified ? "bg-success" : "bg-warning"
                          }`}
                        >
                          {student.verified ? "Onaylı" : "Onaysız"}
                        </span>
                      </td>
                      {/* Görünürlük durumu gösterimi */}
                      <td className="text-center">
                        <span
                          className={`badge ${
                            student.view ? "bg-success" : "bg-secondary"
                          }`}
                        >
                          {student.view ? "Görünür" : "Gizli"}
                        </span>
                      </td>
                      {/* İşlem butonları */}
                      <td
                        className="text-center"
                        style={{ width: "200px", whiteSpace: "nowrap" }}
                      >
                        {/* Düzenleme butonu */}
                        <button
                          className="btn btn-primary btn-sm me-1"
                          onClick={() => {
                            setEditId(student.id);
                            setEditStudent({
                              name: student.name,
                              surname: student.surname,
                              number: student.number,
                              verified: student.verified,
                              view: student.view,
                            });
                          }}
                        >
                          Edit
                        </button>
                        {/* Onaylama butonu - sadece onaysız öğrenciler için */}
                        {!student.verified && (
                          <button
                            className="btn btn-success btn-sm me-1"
                            onClick={() => handleApprove(student.id)}
                            title="Onayla"
                          >
                            ✓
                          </button>
                        )}
                        {/* Görünürlük kontrol butonu */}
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
                        {/* Silme butonu */}
                        <button
                          className="btn btn-danger btn-sm"
                          onClick={() => handleDelete(student.id)}
                        >
                          Delete
                        </button>
                      </td>
                    </>
                  )}
                </>
              )}
            </tr>
          ))}
        </tbody>
      </table>

      {/* Öğrenci bulunamadı mesajı */}
      {students.length === 0 && (
        <div className="text-center text-muted mt-4">
          <p>Öğrenci bulunamadı.</p>
        </div>
      )}
    </div>
  );
}

export default StudentList;
