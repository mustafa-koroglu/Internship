package com.example.backend.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class IpParseUtil {

    // IP girişini parse eder ve tüm IP adreslerini döner (IPv4 ve IPv6)
    public static List<String> parseIpInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String trimmedInput = input.trim();
        Set<String> uniqueIps = new TreeSet<>();

        // IPv4 tekil IP kontrolü
        if (IpValidationUtil.isValidIpv4(trimmedInput)) {
            uniqueIps.add(trimmedInput);
            return new ArrayList<>(uniqueIps);
        }

        // IPv6 tekil IP kontrolü
        if (IpValidationUtil.isValidIpv6(trimmedInput)) {
            uniqueIps.add(trimmedInput);
            return new ArrayList<>(uniqueIps);
        }

        // IPv4 CIDR kontrolü - subnet'i tekil IP'lere çevir
        if (IpValidationUtil.isValidIpv4Cidr(trimmedInput)) {
            List<String> cidrIps = parseIpv4Cidr(trimmedInput);
            uniqueIps.addAll(cidrIps);
            return new ArrayList<>(uniqueIps);
        }

        // IPv6 CIDR kontrolü - subnet'i tekil IP'lere çevir
        if (IpValidationUtil.isValidIpv6Cidr(trimmedInput)) {
            List<String> cidrIps = parseIpv6Cidr(trimmedInput);
            uniqueIps.addAll(cidrIps);
            return new ArrayList<>(uniqueIps);
        }

        // IPv4 aralığı kontrolü - aralığı tekil IP'lere çevir
        if (IpValidationUtil.isValidIpv4Range(trimmedInput)) {
            List<String> rangeIps = parseIpv4Range(trimmedInput);
            uniqueIps.addAll(rangeIps);
            return new ArrayList<>(uniqueIps);
        }

        // IPv6 aralığı kontrolü - aralığı tekil IP'lere çevir
        if (IpValidationUtil.isValidIpv6Range(trimmedInput)) {
            List<String> rangeIps = parseIpv6Range(trimmedInput);
            uniqueIps.addAll(rangeIps);
            return new ArrayList<>(uniqueIps);
        }

        return new ArrayList<>();
    }

    // IPv4 CIDR formatındaki IP'yi parse eder
    private static List<String> parseIpv4Cidr(String cidr) {
        List<String> ips = new ArrayList<>();

        try {
            // CIDR'ı IP ve mask kısımlarına ayır
            String[] parts = cidr.split("/");
            String networkIp = parts[0];
            String mask = parts[1];

            // IPv4 için CIDR formatını koru
            ips.add(networkIp + "/" + mask);

        } catch (Exception e) {
        }

        return ips;
    }

    // IPv6 CIDR formatındaki IP'yi parse eder
    private static List<String> parseIpv6Cidr(String cidr) {
        List<String> ips = new ArrayList<>();

        try {
            // CIDR'ı IP ve mask kısımlarına ayır
            String[] parts = cidr.split("/");
            String networkIp = parts[0];
            String mask = parts[1];

            // IPv6 için CIDR formatını koru
            ips.add(normalizeIpv6(networkIp) + "/" + mask);

        } catch (Exception e) {
        }

        return ips;
    }

    // IPv4 aralığını parse eder
    private static List<String> parseIpv4Range(String ipRange) {
        List<String> ips = new ArrayList<>();

        try {
            // Aralığı başlangıç ve bitiş IP'lerine ayır
            String[] parts = ipRange.split("-");
            String startIp = parts[0].trim();
            String endIp = parts[1].trim();

            // Başlangıç ve bitiş IP'lerini long değerine çevir
            long startLong = IpValidationUtil.ipToLong(startIp);
            long endLong = IpValidationUtil.ipToLong(endIp);

            // Aralıktaki tüm IP'leri ekle (başlangıç ve bitiş dahil)
            for (long ip = startLong; ip <= endLong; ip++) {
                ips.add(IpValidationUtil.longToIp(ip));
            }

        } catch (Exception e) {
        }

        return ips;
    }

    //  IPv6 aralığını parse eder
    private static List<String> parseIpv6Range(String ipRange) {
        List<String> ips = new ArrayList<>();

        try {
            // Aralığı başlangıç ve bitiş IP'lerine ayır
            String[] parts = ipRange.split("-");
            String startIp = parts[0].trim();
            String endIp = parts[1].trim();

            // IPv6 için basit implementasyon - sadece başlangıç ve bitiş IP'lerini döner
            ips.add(normalizeIpv6(startIp));
            ips.add(normalizeIpv6(endIp));

        } catch (Exception e) {
        }

        return ips;
    }

    // IPv6 adresini normalize eder
    private static String normalizeIpv6(String ip) {
        // Basit IPv6 normalizasyonu - küçük harfe çevir ve boşlukları temizle
        return ip.toLowerCase().trim();
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
