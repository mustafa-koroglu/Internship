package com.example.backend.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class IpParseUtil {

    /**
     * IP girişini parse eder ve tüm IP adreslerini döner
     */
    public static List<String> parseIpInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String trimmedInput = input.trim();
        Set<String> uniqueIps = new TreeSet<>();

        // Tekil IP kontrolü
        if (IpValidationUtil.isValidIpv4(trimmedInput)) {
            uniqueIps.add(trimmedInput);
            return new ArrayList<>(uniqueIps);
        }

        // CIDR kontrolü
        if (IpValidationUtil.isValidCidr(trimmedInput)) {
            List<String> cidrIps = parseCidr(trimmedInput);
            uniqueIps.addAll(cidrIps);
            return new ArrayList<>(uniqueIps);
        }

        // IP aralığı kontrolü
        if (IpValidationUtil.isValidIpRange(trimmedInput)) {
            List<String> rangeIps = parseIpRange(trimmedInput);
            uniqueIps.addAll(rangeIps);
            return new ArrayList<>(uniqueIps);
        }

        return new ArrayList<>();
    }

    /**
     * CIDR formatındaki IP'yi parse eder
     */
    private static List<String> parseCidr(String cidr) {
        List<String> ips = new ArrayList<>();
        
        try {
            String[] parts = cidr.split("/");
            String networkIp = parts[0];     // CIDR ifadesini IP ve maske kısmına ayırır.
            int mask = Integer.parseInt(parts[1]);

            long networkLong = IpValidationUtil.ipToLong(networkIp); // bit seviyesinde matematiksel işlem yapmak
            long networkMask = (0xFFFFFFFFL << (32 - mask)) & 0xFFFFFFFFL;
            long broadcastMask = ~networkMask & 0xFFFFFFFFL;

            long startIp = networkLong & networkMask; // başlangıç adresi
            long endIp = startIp | broadcastMask; // brodcast adresi

            // Network ve broadcast adreslerini hariç tut
            for (long ip = startIp + 1; ip < endIp; ip++) {
                ips.add(IpValidationUtil.longToIp(ip));
            }

        } catch (Exception e) {
            // Hata durumunda boş liste döner
        }

        return ips;
    }

    /**
     * IP aralığını parse eder
     */
    private static List<String> parseIpRange(String ipRange) {
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
            // Hata durumunda boş liste döner
        }

        return ips;
    }

    /**
     * IP girişinin tipini belirler
     */
    public static IpInputType getInputType(String input) {
        if (input == null || input.trim().isEmpty()) {
            return IpInputType.INVALID;
        }

        String trimmedInput = input.trim();

        if (IpValidationUtil.isValidIpv4(trimmedInput)) {
            return IpInputType.SINGLE_IP;
        }

        if (IpValidationUtil.isValidCidr(trimmedInput)) {
            return IpInputType.CIDR;
        }

        if (IpValidationUtil.isValidIpRange(trimmedInput)) {
            return IpInputType.IP_RANGE;
        }

        return IpInputType.INVALID;
    }

    /**
     * IP giriş tipini tanımlayan enum
     */
    public enum IpInputType {
        SINGLE_IP,   // Tekil IP adresi
        CIDR,        // IP/mask formatı
        IP_RANGE,    // IP-IP aralığı
        INVALID      // Geçersiz format
    }

    /**
     * IP girişinin açıklamasını oluşturur
     */
    public static String generateDescription(String input) {
        IpInputType type = getInputType(input);
        
        switch (type) {
            case SINGLE_IP:
                return "Tekil IP adresi: " + input;
            case CIDR:
                return "CIDR subnet: " + input;
            case IP_RANGE:
                return "IP aralığı: " + input;
            default:
                return "Geçersiz IP formatı: " + input;
        }
    }
}
