package com.example.backend.utility;

import java.util.ArrayList;
import java.util.List;

public class IpParseUtil {

    // IP girişini parse eder ve tüm IP adreslerini döner (IPv4 ve IPv6)
    public static List<String> parseIpInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String trimmedInput = input.trim();
        List<String> result = new ArrayList<>();

        // IPv4 tekil IP kontrolü
        if (IpValidationUtil.isValidIpv4(trimmedInput)) {
            result.add(trimmedInput);
            return result;
        }

        // IPv6 tekil IP kontrolü
        if (IpValidationUtil.isValidIpv6(trimmedInput)) {
            result.add(trimmedInput);
            return result;
        }

        // IPv4 CIDR kontrolü - olduğu gibi kaydet, açma
        if (IpValidationUtil.isValidIpv4Cidr(trimmedInput)) {
            result.add(trimmedInput);
            return result;
        }

        // IPv6 CIDR kontrolü - olduğu gibi kaydet
        if (IpValidationUtil.isValidIpv6Cidr(trimmedInput)) {
            result.add(trimmedInput);
            return result;
        }

        // IPv4 aralığı kontrolü - olduğu gibi kaydet, açma
        if (IpValidationUtil.isValidIpv4Range(trimmedInput)) {
            result.add(trimmedInput);
            return result;
        }

        // IPv6 aralığı kontrolü - olduğu gibi kaydet
        if (IpValidationUtil.isValidIpv6Range(trimmedInput)) {
            result.add(trimmedInput);
            return result;
        }

        return result;
    }

    // IP girişinin tipini belirler
    public static IpInputType getInputType(String input) {
        if (input == null || input.trim().isEmpty()) {
            return IpInputType.INVALID;
        }

        String trimmedInput = input.trim();

        if (IpValidationUtil.isValidIpv4(trimmedInput)) {
            return IpInputType.SINGLE_IPV4;
        }

        if (IpValidationUtil.isValidIpv6(trimmedInput)) {
            return IpInputType.SINGLE_IPV6;
        }

        if (IpValidationUtil.isValidIpv4Cidr(trimmedInput)) {
            return IpInputType.CIDR_IPV4;
        }

        if (IpValidationUtil.isValidIpv6Cidr(trimmedInput)) {
            return IpInputType.CIDR_IPV6;
        }

        if (IpValidationUtil.isValidIpv4Range(trimmedInput)) {
            return IpInputType.IP_RANGE_IPV4;
        }

        if (IpValidationUtil.isValidIpv6Range(trimmedInput)) {
            return IpInputType.IP_RANGE_IPV6;
        }

        return IpInputType.INVALID;
    }


    // IP giriş tipini tanımlayan enum

    public enum IpInputType {
        SINGLE_IPV4,     // Tekil IPv4 adresi (örn: 192.168.1.1)
        SINGLE_IPV6,     // Tekil IPv6 adresi (örn: 2001:db8::1)
        CIDR_IPV4,       // IPv4/mask formatı (örn: 192.168.1.0/24)
        CIDR_IPV6,       // IPv6/mask formatı (örn: 2001:db8::/32)
        IP_RANGE_IPV4,   // IPv4-IPv4 aralığı (örn: 192.168.1.1-192.168.1.10)
        IP_RANGE_IPV6,   // IPv6-IPv6 aralığı (örn: 2001:db8::1-2001:db8::10)
        INVALID          // Geçersiz format
    }

    // IP girişinin açıklamasını oluşturur
    public static String generateDescription(String input) {
        IpInputType type = getInputType(input);

        switch (type) {
            case SINGLE_IPV4:
                return "Tekil IPv4 adresi: " + input;
            case SINGLE_IPV6:
                return "Tekil IPv6 adresi: " + input;
            case CIDR_IPV4:
                return "IPv4 CIDR subnet: " + input;
            case CIDR_IPV6:
                return "IPv6 CIDR subnet: " + input;
            case IP_RANGE_IPV4:
                return "IPv4 aralığı: " + input;
            case IP_RANGE_IPV6:
                return "IPv6 aralığı: " + input;
            default:
                return "Geçersiz IP formatı: " + input;
        }
    }
}
