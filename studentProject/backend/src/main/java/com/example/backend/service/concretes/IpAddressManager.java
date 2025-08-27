package com.example.backend.service.concretes;

import com.example.backend.dataAccess.IpAddressRepository;
import com.example.backend.dataAccess.StudentsRepository;
import com.example.backend.entities.IpAddress;
import com.example.backend.entities.Student;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.service.abstracts.IpAddressService;
import com.example.backend.response.IpAddressResponse;
import com.example.backend.utility.IpParseUtil;
import com.example.backend.utility.IpValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Random;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class IpAddressManager implements IpAddressService {
    private final IpAddressRepository ipAddressRepository;
    private final StudentsRepository studentsRepository;

    @Override
    public List<IpAddressResponse> findAllActive() {
        log.info("Aktif IP adresleri getiriliyor...");
        List<IpAddress> ipAddresses = ipAddressRepository.findAllActiveOrderByCreatedAtDesc();
        return ipAddresses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<IpAddressResponse> findAll() {
        log.info("Tum IP adresleri (aktif ve pasif) getiriliyor...");
        List<IpAddress> ipAddresses = ipAddressRepository.findAllByOrderByCreatedAtDesc();
        return ipAddresses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public IpAddressResponse findById(Long id) {
        log.info("IP adresi getiriliyor, ID: {}", id);
        IpAddress ipAddress = ipAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IP adresi bulunamadi: " + id));
        return mapToResponse(ipAddress);
    }

    @Override
    public List<IpAddressResponse> search(String searchTerm) {
        log.info("IP adresleri araniyor: {}", searchTerm);
        List<IpAddress> ipAddresses = ipAddressRepository.searchActiveIpAddresses(searchTerm);
        return ipAddresses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public IpAddressResponse create(String ipInput, String description) {
        log.info("IP adresi olusturuluyor: {}", ipInput);

        if (!IpValidationUtil.isValidIpInput(ipInput)) {
            throw new IllegalArgumentException("Gecersiz IP formatı: " + ipInput);
        }
        
        List<String> ipAddresses = IpParseUtil.parseIpInput(ipInput);
        if (ipAddresses.isEmpty()) {
            throw new IllegalArgumentException("IP adresleri parse edilemedi: " + ipInput);
        }

        String ipAddress = ipAddresses.get(0);
        
        if (IpValidationUtil.isValidIpv4(ipAddress) || IpValidationUtil.isValidIpv6(ipAddress)) {
            if (!IpValidationUtil.isIpAssignableToStudent(ipAddress)) {
                throw new IllegalArgumentException("Bu IP adresi kaydedilemez");
            }
        }
        
        if (ipAddressRepository.existsByIpAddress(ipAddress)) {
            throw new IllegalArgumentException("Bu IP adresi zaten mevcut: " + ipAddress);
        }
        
        if (IpValidationUtil.isValidIpv4(ipAddress) || IpValidationUtil.isValidIpv6(ipAddress)) {
            List<IpAddress> existingIpAddresses = ipAddressRepository.findAll();
            List<String> existingIpStrings = existingIpAddresses.stream()
                    .map(IpAddress::getIpAddress)
                    .collect(Collectors.toList());
            
            if (IpValidationUtil.isIpInExistingRanges(ipAddress, existingIpStrings)) {
                throw new IllegalArgumentException("Bu IP adresi mevcut bir subnet veya aralık içinde bulunuyor: " + ipAddress);
            }
        }

        IpAddress ipAddressEntity = new IpAddress();
        ipAddressEntity.setIpAddress(ipAddress);
        ipAddressEntity.setDescription(description);
        ipAddressEntity.setIsActive(true);
        ipAddressEntity.setCreatedAt(LocalDateTime.now());

        IpAddress savedIpAddress = ipAddressRepository.save(ipAddressEntity);
        log.info("IP adresi basariyla olusturuldu: {}", savedIpAddress.getIpAddress());

        return mapToResponse(savedIpAddress);
    }

    @Override
    @Transactional
    public IpAddressResponse update(Long id, String ipInput, String description, Boolean isActive) {
        log.info("IP adresi guncelleniyor, ID: {}", id);

        IpAddress ipAddress = ipAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IP adresi bulunamadi: " + id));

        if (ipInput != null && !ipInput.equals(ipAddress.getIpAddress())) {
            if (!IpValidationUtil.isValidIpInput(ipInput)) {
                throw new IllegalArgumentException("Gecersiz IP formatı: " + ipInput);
            }

            if (IpValidationUtil.isValidIpv4(ipInput) || IpValidationUtil.isValidIpv6(ipInput)) {
                if (!IpValidationUtil.isIpAssignableToStudent(ipInput)) {
                    throw new IllegalArgumentException("Bu IP adresi kaydedilemez");
                }
            }

            if (ipAddressRepository.existsByIpAddress(ipInput)) {
                throw new IllegalArgumentException("IP adresi zaten mevcut: " + ipInput);
            }

            ipAddress.setIpAddress(ipInput);
        }

        if (description != null) {
            ipAddress.setDescription(description);
        }

        if (isActive != null) {
            ipAddress.setIsActive(isActive);
        }

        IpAddress updated = ipAddressRepository.save(ipAddress);
        log.info("IP adresi guncellendi: {}", updated.getIpAddress());
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("IP adresi siliniyor, ID: {}", id);
        
        IpAddress ipAddressToDelete = ipAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IP adresi bulunamadi: " + id));
        
        String ipAddressString = ipAddressToDelete.getIpAddress();
        
        if (isSubnetOrRange(ipAddressString)) {
            log.info("Subnet/Range siliniyor: {}. Bu aralıktaki atanmış IP adresleri de silinecek.", ipAddressString);
            
            List<IpAddress> allIpAddresses = ipAddressRepository.findAll();
            List<IpAddress> ipsToDelete = allIpAddresses.stream()
                    .filter(ip -> isIndividualIp(ip.getIpAddress()) && 
                                 IpValidationUtil.isIpInRange(ip.getIpAddress(), ipAddressString))
                    .collect(Collectors.toList());
            
            for (IpAddress ipToDelete : ipsToDelete) {
                ipAddressRepository.delete(ipToDelete);
                log.info("IP adresi silindi: {} (Ogrenci: {})", 
                        ipToDelete.getIpAddress(),
                        getStudentName(ipToDelete.getStudent()));
            }
            
            ipAddressRepository.delete(ipAddressToDelete);
            log.info("Subnet/Range silindi: {}", ipAddressString);
            log.info("Toplam {} IP adresi silindi", ipsToDelete.size());
        } else {
            ipAddressRepository.deleteById(id);
            log.info("Tekil IP adresi silindi: {}", ipAddressString);
        }
    }

    @Override
    public boolean existsByIpAddress(String ipAddress) {
        return ipAddressRepository.existsByIpAddress(ipAddress);
    }

    @Override
    @Transactional
    public void assignIpAddressesToStudent(Integer studentId, List<Long> ipAddressIds) {
        log.info("IP adresleri ogrenciye ataniyor, Student ID: {}, IP Count: {}", studentId, ipAddressIds.size());
        
        Student student = studentsRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Ogrenci bulunamadi: " + studentId));
        
        List<IpAddress> ipAddresses = ipAddressRepository.findAllById(ipAddressIds);
        if (ipAddresses.size() != ipAddressIds.size()) {
            throw new IllegalArgumentException("Bazı IP adresleri bulunamadi");
        }
        
        List<IpAddress> activeIpAddresses = ipAddresses.stream()
                .filter(IpAddress::getIsActive)
                .collect(Collectors.toList());
        
        if (activeIpAddresses.size() != ipAddresses.size()) {
            log.warn("Bazı IP adresleri pasif durumda, sadece aktif olanlar atanacak");
        }
        
        for (IpAddress ipAddress : activeIpAddresses) {
            if (ipAddress.getStudent() != null && ipAddress.getStudent().getId() != studentId) {
                Student existingStudent = ipAddress.getStudent();
                throw new IllegalArgumentException(
                    String.format("IP adresi %s zaten %s %s adlı öğrenciye atanmış", 
                        ipAddress.getIpAddress(), 
                        existingStudent.getName(), 
                        existingStudent.getSurname())
                );
            }
        }
        
        for (IpAddress ipAddress : activeIpAddresses) {
            ipAddress.setStudent(student);
            ipAddressRepository.save(ipAddress);
        }
        
        log.info("{} IP adresi ogrenciye basariyla atandi", activeIpAddresses.size());
    }

    @Override
    public List<IpAddressResponse> findUnassignedActive() {
        log.info("Atanmamış aktif IP adresleri getiriliyor");
        List<IpAddress> unassignedIpAddresses = ipAddressRepository.findByStudentIsNull();
        return unassignedIpAddresses.stream()
                .filter(IpAddress::getIsActive)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<IpAddressResponse> findUnassignedActiveIpv4() {
        log.info("Atanmamış aktif IPv4 adresleri getiriliyor");
        List<IpAddress> unassignedIpAddresses = ipAddressRepository.findByStudentIsNull();
        return unassignedIpAddresses.stream()
                .filter(IpAddress::getIsActive)
                .filter(ip -> isIpv4Format(ip.getIpAddress()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public IpAddressResponse assignRandomIpAddressToStudent(Integer studentId) {
        log.info("Ogrenciye rastgele IPv4 adresi ataniyor, Student ID: {}", studentId);
        
        Student student = studentsRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Ogrenci bulunamadi: " + studentId));
        
        List<IpAddress> unassignedIpAddresses = ipAddressRepository.findByStudentIsNull();
        List<IpAddress> availableIpAddresses = unassignedIpAddresses.stream()
                .filter(IpAddress::getIsActive)
                .collect(Collectors.toList());
        
        if (availableIpAddresses.isEmpty()) {
            throw new IllegalArgumentException("Atanabilir IP adresi bulunamadi");
        }
        
        List<IpAddress> availableIpAddressesForAssignment = new ArrayList<>();
        
        for (IpAddress ipAddress : availableIpAddresses) {
            String ipInput = ipAddress.getIpAddress();
            
            if (IpValidationUtil.isValidIpv4(ipInput)) {
                availableIpAddressesForAssignment.add(ipAddress);
            } else if (IpValidationUtil.isValidIpv4Cidr(ipInput)) {
                addCidrIpsToAvailableList(ipInput, availableIpAddressesForAssignment);
            } else if (IpValidationUtil.isValidIpv4Range(ipInput)) {
                addRangeIpsToAvailableList(ipInput, availableIpAddressesForAssignment);
            }
        }
        
        if (availableIpAddressesForAssignment.isEmpty()) {
            throw new IllegalArgumentException("Kullanilabilir IPv4 adresi bulunamadi");
        }
        
        Random random = new Random();
        IpAddress selectedIpAddress = availableIpAddressesForAssignment.get(random.nextInt(availableIpAddressesForAssignment.size()));
        
        selectedIpAddress.setStudent(student);
        IpAddress saved = ipAddressRepository.save(selectedIpAddress);
        
        log.info("IPv4 adresi {} ogrenciye basariyla atandi", saved.getIpAddress());
        return mapToResponse(saved);
    }

    private void addCidrIpsToAvailableList(String cidr, List<IpAddress> availableList) {
        List<String> subnetIps = parseIpv4Cidr(cidr);
        for (String ip : subnetIps) {
            if (!ipAddressRepository.existsByIpAddress(ip)) {
                IpAddress newIp = new IpAddress(ip, "Otomatik oluşturulan IPv4 adresi");
                newIp.setIsActive(true);
                newIp.setCreatedAt(LocalDateTime.now());
                availableList.add(newIp);
            }
        }
    }

    private void addRangeIpsToAvailableList(String ipRange, List<IpAddress> availableList) {
        List<String> rangeIps = parseIpv4Range(ipRange);
        for (String ip : rangeIps) {
            if (!ipAddressRepository.existsByIpAddress(ip)) {
                IpAddress newIp = new IpAddress(ip, "Otomatik oluşturulan IPv4 adresi");
                newIp.setIsActive(true);
                newIp.setCreatedAt(LocalDateTime.now());
                availableList.add(newIp);
            }
        }
    }

    private List<String> parseIpv4Cidr(String cidr) {
        List<String> ips = new ArrayList<>();
        
        try {
            String[] parts = cidr.split("/");
            String networkIp = parts[0];
            int mask = Integer.parseInt(parts[1]);
            
            if (mask < 0 || mask > 32) {
                return ips;
            }
            
            long networkLong = IpValidationUtil.ipToLong(networkIp);
            int hostBits = 32 - mask;
            long totalHosts = Math.min((1L << hostBits) - 2, 1000); // Network ve broadcast hariç, max 1000
            
            for (long i = 1; i <= totalHosts; i++) {
                long ipLong = networkLong + i;
                ips.add(IpValidationUtil.longToIp(ipLong));
            }
        } catch (Exception e) {
            log.error("CIDR parse hatasi: {}", e.getMessage());
        }
        
        return ips;
    }

    private List<String> parseIpv4Range(String ipRange) {
        List<String> ips = new ArrayList<>();
        
        try {
            String[] parts = ipRange.split("-");
            String startIp = parts[0].trim();
            String endIp = parts[1].trim();
            
            long startLong = IpValidationUtil.ipToLong(startIp);
            long endLong = IpValidationUtil.ipToLong(endIp);
            
            for (long ip = startLong; ip <= endLong; ip++) {
                ips.add(IpValidationUtil.longToIp(ip));
            }
        } catch (Exception e) {
            log.error("IP range parse hatasi: {}", e.getMessage());
        }
        
        return ips;
    }

    private boolean isSubnetOrRange(String ipAddress) {
        return IpValidationUtil.isValidIpv4Cidr(ipAddress) || 
               IpValidationUtil.isValidIpv6Cidr(ipAddress) ||
               IpValidationUtil.isValidIpv4Range(ipAddress) ||
               IpValidationUtil.isValidIpv6Range(ipAddress);
    }

    private boolean isIndividualIp(String ipAddress) {
        return IpValidationUtil.isValidIpv4(ipAddress) || IpValidationUtil.isValidIpv6(ipAddress);
    }

    private boolean isIpv4Format(String ipAddress) {
        return IpValidationUtil.isValidIpv4(ipAddress) || 
               IpValidationUtil.isValidIpv4Cidr(ipAddress) ||
               IpValidationUtil.isValidIpv4Range(ipAddress);
    }

    private String getStudentName(Student student) {
        return student != null ? student.getName() + " " + student.getSurname() : "Atanmamış";
    }

    private IpAddressResponse mapToResponse(IpAddress ipAddress) {
        boolean isAssigned = ipAddress.getStudent() != null;
        int assignedCount = calculateAssignedCount(ipAddress);
        
        return new IpAddressResponse(
                ipAddress.getId(),
                ipAddress.getIpAddress(),
                ipAddress.getDescription(),
                ipAddress.getIsActive(),
                ipAddress.getCreatedAt(),
                ipAddress.getUpdatedAt(),
                isAssigned,
                assignedCount
        );
    }

    private int calculateAssignedCount(IpAddress ipAddress) {
        boolean isAssigned = ipAddress.getStudent() != null;
        if (isIndividualIp(ipAddress.getIpAddress())) {
            return isAssigned ? 1 : 0;
        }
        
        if (isSubnetOrRange(ipAddress.getIpAddress())) {
            List<String> rangeIps = new ArrayList<>();
            if (IpValidationUtil.isValidIpv4Cidr(ipAddress.getIpAddress())) {
                rangeIps = parseIpv4Cidr(ipAddress.getIpAddress());
            } else if (IpValidationUtil.isValidIpv4Range(ipAddress.getIpAddress())) {
                rangeIps = parseIpv4Range(ipAddress.getIpAddress());
            }
            
            return (int) rangeIps.stream()
                    .filter(ip -> ipAddressRepository.existsByIpAddressAndStudentIsNotNull(ip))
                    .count();
        }
        
        return 0;
    }
}

