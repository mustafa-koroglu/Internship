package com.example.backend.utility;

import java.util.regex.Pattern;

public class IpValidationUtil {

    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );

    private static final Pattern IPV4_CIDR_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)/([0-9]|[1-2][0-9]|3[0-2])$"
    );
    private static final Pattern IPV4_RANGE_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)-((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );

    private static final Pattern IPV6_RANGE_PATTERN = Pattern.compile(
            "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}-([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$"
    );

    public static boolean isValidIpv4(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }
        // IPv4 regex pattern'ı ile eşleşme kontrolü
        return IPV4_PATTERN.matcher(ip.trim()).matches();
    }

    public static boolean isValidIpv4Cidr(String cidr) {
        if (cidr == null || cidr.trim().isEmpty()) {
            return false;
        }
        // IPv4 CIDR regex pattern'ı ile eşleşme kontrolü
        return IPV4_CIDR_PATTERN.matcher(cidr.trim()).matches();
    }

    public static boolean isValidIpv6Cidr(String cidr) {
        if (cidr == null || cidr.trim().isEmpty()) {
            return false;
        }

        String trimmedCidr = cidr.trim();

        // CIDR formatını kontrol et (IP/mask)
        if (!trimmedCidr.contains("/")) {
            return false;
        }

        String[] parts = trimmedCidr.split("/");
        if (parts.length != 2) {
            return false;
        }

        String ipPart = parts[0];
        String maskPart = parts[1];

        // Mask'ın sayısal olduğunu ve geçerli aralıkta olduğunu kontrol et
        try {
            int mask = Integer.parseInt(maskPart);
            if (mask < 0 || mask > 128) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        // IP kısmının geçerli IPv6 olduğunu kontrol et
        return isValidIpv6(ipPart);
    }

    public static boolean isValidIpv4Range(String ipRange) {
        if (ipRange == null || ipRange.trim().isEmpty()) {
            return false;
        }
        // IPv4 range regex pattern'ı ile eşleşme kontrolü
        return IPV4_RANGE_PATTERN.matcher(ipRange.trim()).matches();
    }

    public static boolean isValidIpv6Range(String ipRange) {
        if (ipRange == null || ipRange.trim().isEmpty()) {
            return false;
        }
        // IPv6 range regex pattern'ı ile eşleşme kontrolü
        return IPV6_RANGE_PATTERN.matcher(ipRange.trim()).matches();
    }

    public static boolean isValidIpv6(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }

        String trimmedIp = ip.trim();

        // Özel durumlar
        if (trimmedIp.equals("::1") || trimmedIp.equals("::")) {
            return true;
        }

        // IPv6 formatını kontrol et
        if (!trimmedIp.contains(":")) {
            return false;
        }

        // Çift iki nokta üst üste (::) kontrolü
        if (trimmedIp.contains("::")) {
            // Sadece bir tane :: olmalı
            if (trimmedIp.indexOf("::") != trimmedIp.lastIndexOf("::")) {
                return false;
            }

            // :: ile başlayıp biten durumlar
            if (trimmedIp.equals("::")) {
                return true;
            }

            // :: ile başlayan veya biten durumlar
            if (trimmedIp.startsWith("::") || trimmedIp.endsWith("::")) {
                return true;
            }

            // :: ortada olan durumlar
            String[] parts = trimmedIp.split("::");
            if (parts.length != 2) {
                return false;
            }

            // Her iki tarafta da geçerli hex grupları olmalı
            return isValidHexGroups(parts[0]) && isValidHexGroups(parts[1]);
        } else {
            // Tam IPv6 formatı (8 grup)
            String[] groups = trimmedIp.split(":");
            if (groups.length != 8) {
                return false;
            }

            for (String group : groups) {
                if (!isValidHexGroup(group)) {
                    return false;
                }
            }
            return true;
        }
    }

    // Hex grubunun geçerli olup olmadığını kontrol eder
    private static boolean isValidHexGroup(String group) {
        if (group == null || group.isEmpty()) {
            return false;
        }

        if (group.length() > 4) {
            return false;
        }

        return group.matches("[0-9a-fA-F]{1,4}");
    }

    // Hex gruplarının geçerli olup olmadığını kontrol eder (boş string kabul eder)
    private static boolean isValidHexGroups(String groups) {
        if (groups == null || groups.isEmpty()) {
            return true;
        }

        String[] groupArray = groups.split(":");
        for (String group : groupArray) {
            if (!isValidHexGroup(group)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidIpInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        String trimmedInput = input.trim();

        if (isValidIpv4(trimmedInput)) {
            return true;
        }

        if (isValidIpv6(trimmedInput)) {
            return true;
        }

        if (isValidIpv4Cidr(trimmedInput)) {
            return isValidIpv4Subnet(trimmedInput);
        }

        if (isValidIpv6Cidr(trimmedInput)) {
            return isValidIpv6Subnet(trimmedInput);
        }

        if (isValidIpv4Range(trimmedInput)) {
            return isValidIpv4RangeLogic(trimmedInput);
        }

        if (isValidIpv6Range(trimmedInput)) {
            return isValidIpv6RangeLogic(trimmedInput);
        }

        return false;
    }

    // IPv4 CIDR subnet'inin geçerli olup olmadığını kontrol eder
    private static boolean isValidIpv4Subnet(String cidr) {
        try {
            // CIDR'ı IP ve mask kısımlarına ayır
            String[] parts = cidr.split("/");
            String ip = parts[0];
            int mask = Integer.parseInt(parts[1]);

            if (mask < 0 || mask > 32) {
                return false;
            }

            if (!isValidIpv4(ip)) {
                return false;
            }

            // Network adresi kontrolü - IP'nin network adresi olup olmadığını kontrol eder
            return isIpv4NetworkAddress(ip, mask);

        } catch (Exception e) {
            return false;
        }
    }

    //  IPv6 CIDR subnet'inin geçerli olup olmadığını kontrol eder
    private static boolean isValidIpv6Subnet(String cidr) {
        try {
            // CIDR'ı IP ve mask kısımlarına ayır
            String[] parts = cidr.split("/");
            String ip = parts[0];
            int mask = Integer.parseInt(parts[1]);

            if (mask < 0 || mask > 128) {
                return false;
            }

            if (!isValidIpv6(ip)) {
                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    // IPv4 aralığının mantıksal olarak geçerli olup olmadığını kontrol eder
    private static boolean isValidIpv4RangeLogic(String ipRange) {
        try {
            // Aralığı başlangıç ve bitiş IP'lerine ayır
            String[] parts = ipRange.split("-");
            String startIp = parts[0];
            String endIp = parts[1];

            if (!isValidIpv4(startIp) || !isValidIpv4(endIp)) {
                return false;
            }

            return ipToLong(startIp) <= ipToLong(endIp);

        } catch (Exception e) {
            return false;
        }
    }

    // IPv6 aralığının mantıksal olarak geçerli olup olmadığını kontrol eder
    private static boolean isValidIpv6RangeLogic(String ipRange) {
        try {
            // Aralığı başlangıç ve bitiş IP'lerine ayır
            String[] parts = ipRange.split("-");
            String startIp = parts[0];
            String endIp = parts[1];

            if (!isValidIpv6(startIp) || !isValidIpv6(endIp)) {
                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    // IPv4 adresinin network adresi olup olmadığını kontrol eder
    private static boolean isIpv4NetworkAddress(String ip, int mask) {
        // IP adresini long değerine çevir
        long ipLong = ipToLong(ip);
        // Network mask'ını hesaplar
        long networkMask = (0xFFFFFFFFL << (32 - mask)) & 0xFFFFFFFFL;
        // IP'nin network adresi olup olmadığını kontrol eder
        return (ipLong & networkMask) == ipLong;
    }

    // IP adresini long değerine çevirir
    public static long ipToLong(String ip) {
        // IP adresini nokta ile ayırır
        String[] parts = ip.split("\\.");
        long result = 0;
        // Her octet'i long değerine ekler
        for (int i = 0; i < 4; i++) {
            // Sola 8 bit kaydırır ve yeni octet'i ekler
            result = result << 8 | Integer.parseInt(parts[i]);
        }
        // 32 bit unsigned integer olarak döndürür
        return result & 0xFFFFFFFFL;
    }

    // Long değerini IP adresine çevirir
    public static String longToIp(long ip) {
        // Long değerini 4 octet'e çevir ve nokta ile birleştir
        return String.format("%d.%d.%d.%d",
                (ip >> 24) & 0xFF,  // İlk octet (en yüksek 8 bit)
                (ip >> 16) & 0xFF,  // İkinci octet
                (ip >> 8) & 0xFF,   // Üçüncü octet
                ip & 0xFF           // Dördüncü octet (en düşük 8 bit)
        );
    }
}
