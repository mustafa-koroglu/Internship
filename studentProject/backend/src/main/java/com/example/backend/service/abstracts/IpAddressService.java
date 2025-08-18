package com.example.backend.service.abstracts;

import com.example.backend.response.IpAddressResponse;

import java.util.List;

public interface IpAddressService {

    List<IpAddressResponse> findAllActive();

    List<IpAddressResponse> findAll();

    IpAddressResponse findById(Long id);

    List<IpAddressResponse> search(String searchTerm);

    IpAddressResponse create(String ipInput, String description);

    IpAddressResponse update(Long id, String ipInput, String description, Boolean isActive);

    void deleteById(Long id);

    boolean existsByIpAddress(String ipAddress);
}

