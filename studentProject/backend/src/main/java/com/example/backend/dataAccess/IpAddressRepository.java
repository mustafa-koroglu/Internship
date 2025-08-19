package com.example.backend.dataAccess;

import com.example.backend.entities.IpAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// Liste ve Optional sınıfları için import
import java.util.List;
import java.util.Optional;

@Repository
public interface IpAddressRepository extends JpaRepository<IpAddress, Long> {

    Optional<IpAddress> findByIpAddress(String ipAddress);

    boolean existsByIpAddress(String ipAddress);

    List<IpAddress> findByIsActiveTrue();

    @Query("SELECT ip FROM IpAddress ip WHERE ip.isActive = true AND " +
            "(ip.ipAddress LIKE %:searchTerm% OR ip.description LIKE %:searchTerm%)")
    List<IpAddress> searchActiveIpAddresses(@Param("searchTerm") String searchTerm);

    @Query("SELECT ip FROM IpAddress ip WHERE ip.isActive = true ORDER BY ip.createdAt DESC")
    List<IpAddress> findAllActiveOrderByCreatedAtDesc();

    @Query("SELECT ip FROM IpAddress ip ORDER BY ip.createdAt DESC")
    List<IpAddress> findAllByOrderByCreatedAtDesc();
}
