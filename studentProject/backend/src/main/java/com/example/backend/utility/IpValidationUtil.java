package com.example.backend.utility;

import java.util.regex.Pattern;

public class IpValidationUtil {

    // IPv4 regex pattern
    private static final Pattern IPV4_PATTERN = Pattern.compile(
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );

    // CIDR regex pattern (IP/mask)
    private static final Pattern CIDR_PATTERN = Pattern.compile(
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)/([0-9]|[1-2][0-9]|3[0-2])$"
    );

    // IP range regex pattern (IP-IP)
    private static final Pattern IP_RANGE_PATTERN = Pattern.compile(
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)-((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );

    /**
     * Tekil IPv4 adresini doğrular
     */
    public static boolean isValidIpv4(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }
        return IPV4_PATTERN.matcher(ip.trim()).matches();
    }

    /**
     * CIDR formatını doğrular (IP/mask)
     */
    public static boolean isValidCidr(String cidr) {
        if (cidr == null || cidr.trim().isEmpty()) {
            return false;
        }
        return CIDR_PATTERN.matcher(cidr.trim()).matches();
    }

    /**
     * IP aralığı formatını doğrular (IP-IP)
     */
    public static boolean isValidIpRange(String ipRange) {
        if (ipRange == null || ipRange.trim().isEmpty()) {
            return false;
        }
        return IP_RANGE_PATTERN.matcher(ipRange.trim()).matches();
    }

    /**
     * IP adresinin geçerli olup olmadığını kontrol eder
     */
    public static boolean isValidIpInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        String trimmedInput = input.trim();
        
        // Tekil IP kontrolü
        if (isValidIpv4(trimmedInput)) {
            return true;
        }
        
        // CIDR kontrolü
        if (isValidCidr(trimmedInput)) {
            return isValidSubnet(trimmedInput);
        }
        
        // IP aralığı kontrolü
        if (isValidIpRange(trimmedInput)) {
            return isValidIpRangeLogic(trimmedInput);
        }
        
        return false;
    }

    /**
     * CIDR subnet'inin geçerli olup olmadığını kontrol eder
     */
    private static boolean isValidSubnet(String cidr) {
        try {
            String[] parts = cidr.split("/");
            String ip = parts[0];
            int mask = Integer.parseInt(parts[1]);
            
            // Mask değeri 0-32 arasında olmalı
            if (mask < 0 || mask > 32) {
                return false;
            }
            
            // IP adresi geçerli olmalı
            if (!isValidIpv4(ip)) {
                return false;
            }
            
            // Network adresi kontrolü
            return isNetworkAddress(ip, mask);
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * IP aralığının mantıksal olarak geçerli olup olmadığını kontrol eder
     */
    private static boolean isValidIpRangeLogic(String ipRange) {
        try {
            String[] parts = ipRange.split("-");
            String startIp = parts[0];
            String endIp = parts[1];
            
            if (!isValidIpv4(startIp) || !isValidIpv4(endIp)) {
                return false;
            }
            
            // Başlangıç IP'si bitiş IP'sinden küçük olmalı
            return ipToLong(startIp) <= ipToLong(endIp);
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * IP adresinin network adresi olup olmadığını kontrol eder
     */
    private static boolean isNetworkAddress(String ip, int mask) {
        long ipLong = ipToLong(ip);
        long networkMask = (0xFFFFFFFFL << (32 - mask)) & 0xFFFFFFFFL;
        return (ipLong & networkMask) == ipLong;
    }

    /**
     * IP adresini long değerine çevirir
     */
    public static long ipToLong(String ip) {
        String[] parts = ip.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = result << 8 | Integer.parseInt(parts[i]);
        }
        return result & 0xFFFFFFFFL;
    }

    /**
     * Long değerini IP adresine çevirir
     */
    public static String longToIp(long ip) {
        return String.format("%d.%d.%d.%d",
            (ip >> 24) & 0xFF,
            (ip >> 16) & 0xFF,
            (ip >> 8) & 0xFF,
            ip & 0xFF
        );
    }
}
