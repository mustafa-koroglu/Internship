package com.example.backend.service.concretes;

import com.example.backend.dataAccess.IpAddressRepository;
import com.example.backend.entities.IpAddress;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.service.abstracts.IpAddressService;
import com.example.backend.response.IpAddressResponse;
import com.example.backend.utility.IpParseUtil;
import com.example.backend.utility.IpValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IpAddressManager implements IpAddressService {
    private final IpAddressRepository ipAddressRepository;

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

        for (String ip : ipAddresses) {
            if (ipAddressRepository.existsByIpAddress(ip)) {
                throw new IllegalArgumentException("IP adresi zaten mevcut: " + ip);
            }
        }

        String finalDescription = description != null ? description : IpParseUtil.generateDescription(ipInput);

        IpAddress firstIp = null;
        for (String ip : ipAddresses) {
            IpAddress ipAddress = new IpAddress(ip, finalDescription);
            IpAddress saved = ipAddressRepository.save(ipAddress);
            if (firstIp == null) {
                firstIp = saved;
            }
            log.info("IP adresi kaydedildi: {}", ip);
        }

        log.info("{} IP adresi basariyla kaydedildi", ipAddresses.size());
        return mapToResponse(firstIp);
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
        if (!ipAddressRepository.existsById(id)) {
            throw new ResourceNotFoundException("IP adresi bulunamadi: " + id);
        }
        ipAddressRepository.deleteById(id);
        log.info("IP adresi silindi, ID: {}", id);
    }

    @Override
    public boolean existsByIpAddress(String ipAddress) {
        return ipAddressRepository.existsByIpAddress(ipAddress);
    }

    private IpAddressResponse mapToResponse(IpAddress ipAddress) {
        return new IpAddressResponse(
                ipAddress.getId(),
                ipAddress.getIpAddress(),
                ipAddress.getDescription(),
                ipAddress.getIsActive(),
                ipAddress.getCreatedAt(),
                ipAddress.getUpdatedAt()
        );
    }
}

