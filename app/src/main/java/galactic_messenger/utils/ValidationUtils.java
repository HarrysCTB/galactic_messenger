package galactic_messenger.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ValidationUtils {
    public static String validateAndParseIPAddress(String ipAddress) {
        try {
            InetAddress.getByName(ipAddress);
            return ipAddress;
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address: " + ipAddress, e);
        }
    }

    public static int validateAndParsePort(String portStr) {
        try {
            int port = Integer.parseInt(portStr);
            if (port < 1024 || port > 65535) {
                throw new IllegalArgumentException(
                        "Invalid port number: " + portStr + " (must be between 1024 and 65535)");
            }
            return port;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port number: " + portStr, e);
        }
    }
}