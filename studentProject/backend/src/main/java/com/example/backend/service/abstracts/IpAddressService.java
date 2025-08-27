// IP adresi service interface'inin paket tanımı
package com.example.backend.service.abstracts;

// Response sınıfı için import
import com.example.backend.response.IpAddressResponse;

// Liste sınıfı için import
import java.util.List;

// IP adresi işlemleri için service interface'i - iş mantığı katmanı
public interface IpAddressService {

    // Aktif IP adreslerini getiren metod
    List<IpAddressResponse> findAllActive();

    // Tüm IP adreslerini (aktif ve pasif) getiren metod
    List<IpAddressResponse> findAll();

    // ID'ye göre IP adresi getiren metod
    IpAddressResponse findById(Long id);

    // Arama terimi ile IP adreslerinde arama yapan metod
    List<IpAddressResponse> search(String searchTerm);

    // Yeni IP adresi oluşturan metod
    // ipInput: IP adresi girişi (tekil IP, CIDR, IP aralığı formatlarını destekler)
    // description: IP adresi açıklaması
    IpAddressResponse create(String ipInput, String description);

    // IP adresi güncelleyen metod
    // id: Güncellenecek IP adresinin ID'si
    // ipInput: Yeni IP adresi girişi
    // description: Yeni açıklama
    // isActive: Aktiflik durumu
    IpAddressResponse update(Long id, String ipInput, String description, Boolean isActive);

    // IP adresi silen metod
    void deleteById(Long id);

    // IP adresinin veritabanında var olup olmadığını kontrol eden metod
    boolean existsByIpAddress(String ipAddress);

    // Öğrenciye IP adresleri atayan metod
    void assignIpAddressesToStudent(Integer studentId, List<Long> ipAddressIds);

    // Atanmamış (öğrenciye atanmamış) aktif IP adreslerini getiren metod
    List<IpAddressResponse> findUnassignedActive();

    // Atanmamış (öğrenciye atanmamış) aktif IPv4 adreslerini getiren metod
    List<IpAddressResponse> findUnassignedActiveIpv4();

    // Öğrenciye otomatik IP adresi atayan metod (rastgele seçim)
    IpAddressResponse assignRandomIpAddressToStudent(Integer studentId);
}

