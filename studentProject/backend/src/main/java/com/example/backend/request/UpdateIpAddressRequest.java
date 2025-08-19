// IP adresi güncelleme request sınıfının paket tanımı
package com.example.backend.request;

// Lombok import'u - getter, setter metodlarını otomatik oluşturur
import lombok.Data;

// Validation import'ları - veri doğrulama için
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Lombok annotation'ı - getter, setter, toString metodlarını otomatik oluşturur
@Data
// IP adresi güncelleme isteği için DTO (Data Transfer Object) sınıfı
public class UpdateIpAddressRequest {

    // IP adresi girişi alanı - zorunlu alan
    @NotBlank(message = "IP adresi boş olamaz")
    // @NotBlank: Bu alan boş veya sadece boşluk karakterlerinden oluşamaz
    @Size(max = 50, message = "IP adresi 50 karakterden uzun olamaz")
    // @Size: Bu alan maksimum 50 karakter olabilir
    // IP adresi girişi (tekil IP, CIDR, IP aralığı formatlarını destekler)
    private String ipInput;

    // IP adresi açıklaması alanı - opsiyonel alan
    @Size(max = 500, message = "Açıklama 500 karakterden uzun olamaz")
    // @Size: Bu alan maksimum 500 karakter olabilir
    // IP adresi için açıklama metni
    private String description;

    // IP adresinin aktif olup olmadığını belirten alan - varsayılan değer true
    // Bu alan güncelleme sırasında IP adresinin aktif/pasif durumunu değiştirmek için kullanılır
    private Boolean isActive = true;
}
