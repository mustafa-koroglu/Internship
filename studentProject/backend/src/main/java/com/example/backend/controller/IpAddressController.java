package com.example.backend.controller;

import com.example.backend.request.CreateIpAddressRequest;
import com.example.backend.request.UpdateIpAddressRequest;
import com.example.backend.request.AssignIpAddressRequest;
import com.example.backend.response.IpAddressResponse;
import com.example.backend.service.abstracts.IpAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import com.example.backend.entities.IpAddress;
import com.example.backend.dataAccess.IpAddressRepository;

@RestController
@RequestMapping("/api/v1/ip-addresses")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
@Validated
public class IpAddressController {

    private final IpAddressService ipAddressService;
    private final IpAddressRepository ipAddressRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<IpAddressResponse>> getAllIpAddresses() {
        log.info("IP yönetim sayfası için tüm aktif IP adresleri getiriliyor...");
        List<IpAddressResponse> ipAddresses = ipAddressService.findAllActive();
        return ResponseEntity.ok(ipAddresses);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<IpAddressResponse>> getAllIpAddressesIncludingInactive() {
        log.info("Tum IP adresleri (aktif ve pasif) getiriliyor...");
        List<IpAddressResponse> ipAddresses = ipAddressService.findAll();
        return ResponseEntity.ok(ipAddresses);
    }

    @GetMapping("/unassigned")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<IpAddressResponse>> getUnassignedIpAddresses() {
        log.info("Atanmamış aktif IP adresleri getiriliyor...");
        List<IpAddressResponse> ipAddresses = ipAddressService.findUnassignedActive();
        return ResponseEntity.ok(ipAddresses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IpAddressResponse> getIpAddressById(@PathVariable Long id) {
        log.info("IP adresi getiriliyor, ID: {}", id);
        try {
            IpAddressResponse ipAddress = ipAddressService.findById(id);
            return ResponseEntity.ok(ipAddress);
        } catch (Exception e) {
            log.error("IP adresi getirilirken hata: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<IpAddressResponse>> searchIpAddresses(@RequestParam String q) {
        log.info("IP adresleri araniyor: {}", q);
        String searchTerm = q.trim();
        if (searchTerm.isEmpty()) {
            return ResponseEntity.ok(ipAddressService.findAllActive());
        }
        List<IpAddressResponse> ipAddresses = ipAddressService.search(searchTerm);
        return ResponseEntity.ok(ipAddresses);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createIpAddress(@Valid @RequestBody CreateIpAddressRequest request) {
        log.info("IP adresi olusturuluyor: {}", request.getIpInput());
        try {
            IpAddressResponse created = ipAddressService.create(request.getIpInput(), request.getDescription());
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            log.error("IP adresi olusturulurken hata: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("IP adresi olusturulurken beklenmeyen hata: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("IP adresi olusturulamadi");
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateIpAddress(@PathVariable Long id, @Valid @RequestBody UpdateIpAddressRequest request) {
        log.info("IP adresi guncelleniyor, ID: {}", id);
        try {
            IpAddressResponse updated = ipAddressService.update(id, request.getIpInput(), 
                                                              request.getDescription(), request.getIsActive());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("IP adresi guncellenirken hata: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("IP adresi guncellenirken beklenmeyen hata: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("IP adresi guncellenemedi");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteIpAddress(@PathVariable Long id) {
        log.info("IP adresi siliniyor, ID: {}", id);
        try {
            ipAddressService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("IP adresi silinirken hata: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("IP adresi silinemedi");
        }
    }

    @GetMapping("/validate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> validateIpAddress(@RequestParam String ipInput) {
        log.info("IP adresi dogrulanıyor: {}", ipInput);
        try {
            if (!com.example.backend.utility.IpValidationUtil.isValidIpInput(ipInput)) {
                return ResponseEntity.badRequest().body("Gecersiz IP formatı");
            }

            List<String> ipAddresses = com.example.backend.utility.IpParseUtil.parseIpInput(ipInput);
            if (ipAddresses.isEmpty()) {
                return ResponseEntity.badRequest().body("IP adresleri parse edilemedi");
            }

            // Artık sadece 1 IP adresi olacak
            String ipAddress = ipAddresses.get(0);
            
            // Eğer tekil IP adresi ise, önce network/broadcast kontrolü yap
            if (com.example.backend.utility.IpValidationUtil.isValidIpv4(ipAddress) || 
                com.example.backend.utility.IpValidationUtil.isValidIpv6(ipAddress)) {
                
                // Network veya broadcast adresi kontrolü - önce yap
                if (!com.example.backend.utility.IpValidationUtil.isIpAssignableToStudent(ipAddress)) {
                    return ResponseEntity.badRequest().body("Bu IP adresi kaydedilemez");
                }
            }

            // Aynı IP adresi zaten var mı kontrol et
            if (ipAddressService.existsByIpAddress(ipAddress)) {
                return ResponseEntity.badRequest().body("Bu IP adresi zaten mevcut: " + ipAddress);
            }
            
            // Eğer tekil IP adresi ise, mevcut subnet veya range'lerle çakışma kontrolü yap
            if (com.example.backend.utility.IpValidationUtil.isValidIpv4(ipAddress) || 
                com.example.backend.utility.IpValidationUtil.isValidIpv6(ipAddress)) {
                
                // Mevcut tüm IP adreslerini al
                List<IpAddress> existingIpAddresses = ipAddressRepository.findAll();
                List<String> existingIpStrings = existingIpAddresses.stream()
                        .map(IpAddress::getIpAddress)
                        .collect(Collectors.toList());
                
                // IP adresinin mevcut subnet veya range içinde olup olmadığını kontrol et
                if (com.example.backend.utility.IpValidationUtil.isIpInExistingRanges(ipAddress, existingIpStrings)) {
                    return ResponseEntity.badRequest().body("Bu IP adresi mevcut bir subnet veya aralık içinde bulunuyor: " + ipAddress);
                }
            }

            String inputType = com.example.backend.utility.IpParseUtil.getInputType(ipInput).name();
            String inputTypeDescription = getInputTypeDescription(ipInput);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("ipCount", ipAddresses.size());
            response.put("ips", ipAddresses);
            response.put("inputType", inputType);
            response.put("inputTypeDescription", inputTypeDescription);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("IP adresi dogrulanirken hata: {}", e.getMessage());
            return ResponseEntity.badRequest().body("IP adresi dogrulanamadi");
        }
    }



    @GetMapping("/unassigned/ipv4")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUnassignedIpv4Addresses() {
        log.info("Atanmamış IPv4 adresleri getiriliyor");
        try {
            List<IpAddressResponse> unassignedIpAddresses = ipAddressService.findUnassignedActiveIpv4();
            return ResponseEntity.ok(unassignedIpAddresses);
        } catch (Exception e) {
            log.error("Atanmamış IPv4 adresleri getirilirken hata: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Atanmamış IPv4 adresleri getirilemedi");
        }
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignIpAddresses(@Valid @RequestBody AssignIpAddressRequest request) {
        log.info("IP adresleri ataniyor, Student ID: {}, IP Count: {}", request.getStudentId(), request.getIpAddressIds().size());
        try {
            ipAddressService.assignIpAddressesToStudent(request.getStudentId(), request.getIpAddressIds());
            return ResponseEntity.ok().body("IP adresleri basariyla atandi");
        } catch (IllegalArgumentException e) {
            log.error("IP adresi atama hatasi: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("IP adresi atama beklenmeyen hata: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("IP adresleri atanamadi");
        }
    }

    @PostMapping("/assign-random/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignRandomIpAddress(@PathVariable Integer studentId) {
        log.info("Ogrenciye rastgele IP adresi ataniyor, Student ID: {}", studentId);
        try {
            IpAddressResponse assignedIp = ipAddressService.assignRandomIpAddressToStudent(studentId);
            return ResponseEntity.ok().body(assignedIp);
        } catch (IllegalArgumentException e) {
            log.error("Rastgele IP atama hatasi: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Rastgele IP atama beklenmeyen hata: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("IP adresi atanamadi");
        }
    }

    private String getInputTypeDescription(String ipInput) {
        if (com.example.backend.utility.IpValidationUtil.isValidIpv4(ipInput)) {
            return "Tekil IPv4 Adresi";
        } 
        else if (com.example.backend.utility.IpValidationUtil.isValidIpv6(ipInput)) {
            return "Tekil IPv6 Adresi";
        } 
        else if (com.example.backend.utility.IpValidationUtil.isValidIpv4Cidr(ipInput)) {
            return "IPv4 CIDR Subnet";
        } 
        else if (com.example.backend.utility.IpValidationUtil.isValidIpv6Cidr(ipInput)) {
            return "IPv6 CIDR Subnet";
        } 
        else if (com.example.backend.utility.IpValidationUtil.isValidIpv4Range(ipInput)) {
            return "IPv4 Aralığı";
        } 
        else if (com.example.backend.utility.IpValidationUtil.isValidIpv6Range(ipInput)) {
            return "IPv6 Aralığı";
        }
        return "Bilinmeyen Format";
    }
}

