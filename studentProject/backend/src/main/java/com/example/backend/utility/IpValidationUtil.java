package com.example.backend.utility;

import java.util.regex.Pattern;
import java.util.List;

public class IpValidationUtil {

    // Regex patterns - static final olarak tanımlanmış
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

    // Network ve broadcast adresleri için sabitler
    private static final String[] RESERVED_IPV4_ADDRESSES = {
        "0.0.0.0", "127.0.0.0", "169.254.0.0", "224.0.0.0", "240.0.0.0", "255.255.255.255"
    };

    private static final String[] MULTICAST_PREFIXES = {
        "224.", "225.", "226.", "227.", "228.", "229.", "230.", "231.", "232.", "233.", 
        "234.", "235.", "236.", "237.", "238.", "239."
    };

    private static final String[] RESERVED_PREFIXES = {
        "240.", "241.", "242.", "243.", "244.", "245.", "246.", "247.", "248.", "249.", 
        "250.", "251.", "252.", "253.", "254.", "255."
    };

    // Temel IP doğrulama metodları
    public static boolean isValidIpv4(String ip) {
        return ip != null && !ip.trim().isEmpty() && IPV4_PATTERN.matcher(ip.trim()).matches();
    }

    public static boolean isValidIpv4Cidr(String cidr) {
        return cidr != null && !cidr.trim().isEmpty() && IPV4_CIDR_PATTERN.matcher(cidr.trim()).matches();
    }

    public static boolean isValidIpv4Range(String ipRange) {
        return ipRange != null && !ipRange.trim().isEmpty() && IPV4_RANGE_PATTERN.matcher(ipRange.trim()).matches();
    }

    public static boolean isValidIpv6Range(String ipRange) {
        return ipRange != null && !ipRange.trim().isEmpty() && IPV6_RANGE_PATTERN.matcher(ipRange.trim()).matches();
    }

    public static boolean isValidIpv6Cidr(String cidr) {
        if (cidr == null || cidr.trim().isEmpty() || !cidr.contains("/")) {
            return false;
        }

        String[] parts = cidr.split("/");
        if (parts.length != 2) {
            return false;
        }

        try {
            int mask = Integer.parseInt(parts[1]);
            return mask >= 0 && mask <= 128 && isValidIpv6(parts[0]);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidIpv6(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }

        String trimmedIp = ip.trim();

        // Özel IPv6 adresleri
        if (trimmedIp.equals("::1") || trimmedIp.equals("::")) {
            return true;
        }

        if (!trimmedIp.contains(":")) {
            return false;
        }

        // Çift iki nokta üst üste (::) kontrolü
        if (trimmedIp.contains("::")) {
            return isValidCompressedIpv6(trimmedIp);
        } else {
            return isValidFullIpv6(trimmedIp);
        }
    }

    private static boolean isValidCompressedIpv6(String ip) {
        // Sadece bir tane :: olmalı
        if (ip.indexOf("::") != ip.lastIndexOf("::")) {
            return false;
        }

        if (ip.equals("::")) {
            return true;
        }

        String[] parts = ip.split("::");
        if (parts.length != 2) {
            return false;
        }

        return isValidHexGroups(parts[0]) && isValidHexGroups(parts[1]);
    }

    private static boolean isValidFullIpv6(String ip) {
        String[] groups = ip.split(":");
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

    private static boolean isValidHexGroup(String group) {
        return group != null && !group.isEmpty() && group.length() <= 4 && 
               group.matches("[0-9a-fA-F]{1,4}");
    }

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

    // Ana IP doğrulama metodu
    public static boolean isValidIpInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        String trimmedInput = input.trim();

        // Sırayla tüm formatları kontrol et
        return isValidIpv4(trimmedInput) ||
               isValidIpv6(trimmedInput) ||
               (isValidIpv4Cidr(trimmedInput) && isValidIpv4Subnet(trimmedInput)) ||
               (isValidIpv6Cidr(trimmedInput) && isValidIpv6Subnet(trimmedInput)) ||
               (isValidIpv4Range(trimmedInput) && isValidIpv4RangeLogic(trimmedInput)) ||
               (isValidIpv6Range(trimmedInput) && isValidIpv6RangeLogic(trimmedInput));
    }

    // Subnet doğrulama metodları
    private static boolean isValidIpv4Subnet(String cidr) {
        try {
            String[] parts = cidr.split("/");
            String ip = parts[0];
            int mask = Integer.parseInt(parts[1]);

            return mask >= 0 && mask <= 32 && isValidIpv4(ip) && isIpv4NetworkAddress(ip, mask);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isValidIpv6Subnet(String cidr) {
        try {
            String[] parts = cidr.split("/");
            String ip = parts[0];
            int mask = Integer.parseInt(parts[1]);

            return mask >= 0 && mask <= 128 && isValidIpv6(ip);
        } catch (Exception e) {
            return false;
        }
    }

    // Range doğrulama metodları
    private static boolean isValidIpv4RangeLogic(String ipRange) {
        try {
            String[] parts = ipRange.split("-");
            String startIp = parts[0];
            String endIp = parts[1];

            return isValidIpv4(startIp) && isValidIpv4(endIp) && 
                   ipToLong(startIp) <= ipToLong(endIp);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isValidIpv6RangeLogic(String ipRange) {
        try {
            String[] parts = ipRange.split("-");
            String startIp = parts[0];
            String endIp = parts[1];

            return isValidIpv6(startIp) && isValidIpv6(endIp);
        } catch (Exception e) {
            return false;
        }
    }

    // Network adresi kontrolü
    private static boolean isIpv4NetworkAddress(String ip, int mask) {
        long ipLong = ipToLong(ip);
        long networkMask = (0xFFFFFFFFL << (32 - mask)) & 0xFFFFFFFFL;
        return (ipLong & networkMask) == ipLong;
    }

    // Network ve broadcast adresi kontrolü
    public static boolean isIpv4NetworkOrBroadcastAddress(String ip) {
        if (!isValidIpv4(ip)) {
            return false;
        }

        // Rezerve edilmiş adresler kontrolü
        for (String reserved : RESERVED_IPV4_ADDRESSES) {
            if (ip.equals(reserved)) {
                return true;
            }
        }

        // Private network adresleri kontrolü
        if (isPrivateNetworkAddress(ip)) {
            return true;
        }

        // CIDR subnet kontrolü
        return ip.endsWith(".0") || ip.endsWith(".0.0") || ip.endsWith(".0.0.0");
    }

    private static boolean isPrivateNetworkAddress(String ip) {
        // 10.x.x.x
        if (ip.startsWith("10.") && ip.endsWith(".0")) {
            return true;
        }
        
        // 172.16-31.x.x
        if (ip.startsWith("172.")) {
            String[] parts = ip.split("\\.");
            if (parts.length == 4) {
                try {
                    int secondOctet = Integer.parseInt(parts[1]);
                    return secondOctet >= 16 && secondOctet <= 31 && parts[3].equals("0");
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        
        // 192.168.x.x
        return ip.startsWith("192.168.") && ip.endsWith(".0");
    }

    // Broadcast adresi kontrolü
    public static boolean isIpv4BroadcastAddress(String ip) {
        if (!isValidIpv4(ip)) {
            return false;
        }

        // Genel broadcast adresi
        if (ip.equals("255.255.255.255")) {
            return true;
        }

        // Private network broadcast adresleri
        if (isPrivateBroadcastAddress(ip)) {
            return true;
        }

        // CIDR broadcast kontrolü
        return ip.endsWith(".255") || ip.endsWith(".255.255") || ip.endsWith(".255.255.255");
    }

    private static boolean isPrivateBroadcastAddress(String ip) {
        // 10.x.x.255
        if (ip.startsWith("10.") && ip.endsWith(".255")) {
            return true;
        }
        
        // 172.16-31.x.255
        if (ip.startsWith("172.")) {
            String[] parts = ip.split("\\.");
            if (parts.length == 4) {
                try {
                    int secondOctet = Integer.parseInt(parts[1]);
                    return secondOctet >= 16 && secondOctet <= 31 && parts[3].equals("255");
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        
        // 192.168.x.255
        return ip.startsWith("192.168.") && ip.endsWith(".255");
    }

    // Öğrenciye atanabilir IP kontrolü
    public static boolean isIpv4AssignableToStudent(String ip) {
        if (!isValidIpv4(ip)) {
            return false;
        }

        // Network veya broadcast adresi kontrolü
        if (isIpv4NetworkOrBroadcastAddress(ip) || isIpv4BroadcastAddress(ip)) {
            return false;
        }

        // Loopback adresi kontrolü
        if (ip.startsWith("127.")) {
            return false;
        }

        // Link-local adresi kontrolü
        if (ip.startsWith("169.254.")) {
            return false;
        }

        // Multicast adresi kontrolü
        for (String prefix : MULTICAST_PREFIXES) {
            if (ip.startsWith(prefix)) {
                return false;
            }
        }

        // Reserved adresi kontrolü
        for (String prefix : RESERVED_PREFIXES) {
            if (ip.startsWith(prefix)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isIpv6AssignableToStudent(String ip) {
        if (!isValidIpv6(ip)) {
            return false;
        }

        // Özel IPv6 adresleri kontrolü
        if (ip.equals("::1") || ip.equals("::")) {
            return false;
        }

        String lowerIp = ip.toLowerCase();
        
        // Link-local adresi kontrolü (fe80::/10)
        if (lowerIp.startsWith("fe80:")) {
            return false;
        }

        // Multicast adresi kontrolü (ff00::/8)
        if (lowerIp.startsWith("ff")) {
            return false;
        }

        // Unique local adresi kontrolü (fc00::/7)
        if (lowerIp.startsWith("fc") || lowerIp.startsWith("fd")) {
            return false;
        }

        // Documentation adresi kontrolü (2001:db8::/32)
        if (lowerIp.startsWith("2001:db8:")) {
            return false;
        }

        return true;
    }

    public static boolean isIpAssignableToStudent(String ip) {
        if (isValidIpv4(ip)) {
            return isIpv4AssignableToStudent(ip);
        } else if (isValidIpv6(ip)) {
            return isIpv6AssignableToStudent(ip);
        }
        return false;
    }

    // IP aralık kontrolü metodları
    public static boolean isIpInExistingRanges(String ipAddress, List<String> existingIps) {
        if (existingIps == null || existingIps.isEmpty()) {
            return false;
        }

        return existingIps.stream().anyMatch(existingIp -> isIpInRange(ipAddress, existingIp));
    }

    public static boolean isIpInRange(String ipAddress, String rangeOrSubnet) {
        if (!isValidIpv4(ipAddress) && !isValidIpv6(ipAddress)) {
            return false;
        }

        if (isValidIpv4Cidr(rangeOrSubnet)) {
            return isIpInCidrRange(ipAddress, rangeOrSubnet);
        }
        if (isValidIpv4Range(rangeOrSubnet)) {
            return isIpInIpRange(ipAddress, rangeOrSubnet);
        }
        if (isValidIpv6Cidr(rangeOrSubnet)) {
            return isIpInIpv6CidrRange(ipAddress, rangeOrSubnet);
        }
        if (isValidIpv6Range(rangeOrSubnet)) {
            return isIpInIpv6Range(ipAddress, rangeOrSubnet);
        }
        
        return false;
    }

    private static boolean isIpInCidrRange(String ipAddress, String cidr) {
        try {
            String[] parts = cidr.split("/");
            if (parts.length != 2) return false;

            String networkAddress = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);

            long networkLong = ipToLong(networkAddress);
            long ipLong = ipToLong(ipAddress);
            long subnetMask = (0xFFFFFFFFL << (32 - prefixLength)) & 0xFFFFFFFFL;

            return (networkLong & subnetMask) == (ipLong & subnetMask);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isIpInIpRange(String ipAddress, String ipRange) {
        try {
            String[] parts = ipRange.split("-");
            if (parts.length != 2) return false;

            String startIp = parts[0].trim();
            String endIp = parts[1].trim();

            long ipLong = ipToLong(ipAddress);
            long startLong = ipToLong(startIp);
            long endLong = ipToLong(endIp);

            return ipLong >= startLong && ipLong <= endLong;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isIpInIpv6CidrRange(String ipAddress, String cidr) {
        try {
            String[] parts = cidr.split("/");
            if (parts.length != 2) return false;

            String networkAddress = parts[0];
            return ipAddress.toLowerCase().startsWith(networkAddress.toLowerCase());
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isIpInIpv6Range(String ipAddress, String ipRange) {
        try {
            String[] parts = ipRange.split("-");
            if (parts.length != 2) return false;

            String startIp = parts[0].trim();
            String endIp = parts[1].trim();

            String lowerIp = ipAddress.toLowerCase();
            return lowerIp.equals(startIp.toLowerCase()) || lowerIp.equals(endIp.toLowerCase());
        } catch (Exception e) {
            return false;
        }
    }

    // IP dönüştürme metodları
    public static long ipToLong(String ipAddress) {
        String[] parts = ipAddress.split("\\.");
        if (parts.length != 4) return 0;

        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = result << 8 | Integer.parseInt(parts[i]);
        }
        return result;
    }

    public static String longToIp(long ip) {
        return String.format("%d.%d.%d.%d",
                (ip >> 24) & 0xFF,
                (ip >> 16) & 0xFF,
                (ip >> 8) & 0xFF,
                ip & 0xFF
        );
    }
}
