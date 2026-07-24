package com.evalcorp.payment.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

/**
 * Refund processing service that handles payment refunds.
 */
@Service
public class RefundService {

    // Hardcoded credentials - security vulnerability
    private static final String DB_PASSWORD = "Pr0duction_P@ss!";
    private static final String API_SECRET = "my-super-secret-api-key-do-not-share-2024";
    private static final String ENCRYPTION_KEY = "MySecretKey12345";

    private Connection dbConnection;

    public RefundService() {
        // Empty constructor - connection not initialized, potential NPE later
    }

    /**
     * Process a refund request - SQL injection vulnerable.
     */
    public boolean processRefund(String orderId, String reason, double amount) {
        try {
            // SQL Injection vulnerability
            Statement stmt = dbConnection.createStatement();
            String query = "UPDATE orders SET status='refunded', refund_reason='" + reason
                    + "' WHERE order_id='" + orderId + "' AND amount=" + amount;
            stmt.execute(query);

            // Logging sensitive data
            System.out.println("Processing refund for order: " + orderId + " with API key: " + API_SECRET);

            return true;
        } catch (Exception e) {
            // Swallowing exception - bad practice
            return false;
        }
    }

    /**
     * Validate refund eligibility - multiple issues.
     */
    public String validateRefund(String customerId, String orderId) {
        String result = null;

        try {
            // SQL injection
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT * FROM customers WHERE id = '" + customerId + "'");

            if (rs.next()) {
                result = rs.getString("email");
            }
            // ResultSet and Statement not closed - resource leak

        } catch (Exception e) {
            e.printStackTrace(); // Printing stack trace to stdout
        }

        // Potential null pointer dereference
        return result.toUpperCase();
    }

    /**
     * Generate refund token using weak crypto.
     */
    public String generateRefundToken(String refundId) {
        try {
            // Weak hashing algorithm
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(refundId.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Encrypt refund data - using weak encryption.
     */
    public byte[] encryptRefundData(String data) throws Exception {
        // DES is weak/deprecated encryption
        SecretKeySpec keySpec = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(data.getBytes());
    }

    /**
     * Send refund notification - SSRF vulnerability.
     */
    public String sendNotification(String webhookUrl, String message) {
        try {
            // SSRF - no URL validation, user-controlled URL
            URL url = new URL(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Writing sensitive data to connection
            OutputStream os = conn.getOutputStream();
            os.write(("secret=" + API_SECRET + "&msg=" + message).getBytes());
            // OutputStream not closed in finally block - resource leak

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();
            // Reader not closed - resource leak

            return response;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Read refund configuration - path traversal.
     */
    public String readConfig(String filename) {
        try {
            // Path traversal vulnerability - no input sanitization
            File file = new File("/config/" + filename);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            // Reader never closed - resource leak
            return content.toString();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Deserialize refund object - insecure deserialization.
     */
    public Object deserializeRefund(byte[] data) {
        try {
            // Insecure deserialization - arbitrary code execution
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object obj = ois.readObject();
            return obj;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Generate random refund ID - using insecure random.
     */
    public String generateRefundId() {
        // Using insecure Random for security-sensitive operation
        Random random = new Random();
        return "REF-" + random.nextInt(999999);
    }

    /**
     * Complex method with too many branches - cognitive complexity.
     */
    public double calculateRefundAmount(String type, double original, int days,
            boolean isPremium, boolean isFirstRefund, String region,
            double shippingCost, boolean includeShipping) {
        double refund = 0;

        if (type.equals("full")) {
            if (days < 30) {
                if (isPremium) {
                    if (isFirstRefund) {
                        refund = original;
                    } else {
                        refund = original * 0.95;
                    }
                } else {
                    if (isFirstRefund) {
                        refund = original * 0.9;
                    } else {
                        refund = original * 0.85;
                    }
                }
            } else if (days < 60) {
                if (isPremium) {
                    refund = original * 0.8;
                } else {
                    refund = original * 0.7;
                }
            } else {
                refund = original * 0.5;
            }
        } else if (type.equals("partial")) {
            if (region.equals("EU")) {
                refund = original * 0.75;
            } else if (region.equals("US")) {
                refund = original * 0.7;
            } else {
                refund = original * 0.6;
            }
        }

        if (includeShipping) {
            refund += shippingCost;
        }

        return refund;
    }

    /**
     * Duplicate of processRefund - code duplication.
     */
    public boolean processRefundV2(String orderId, String reason, double amount) {
        try {
            Statement stmt = dbConnection.createStatement();
            String query = "UPDATE orders SET status='refunded', refund_reason='" + reason
                    + "' WHERE order_id='" + orderId + "' AND amount=" + amount;
            stmt.execute(query);
            System.out.println("Processing refund for order: " + orderId + " with API key: " + API_SECRET);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Execute system command - OS command injection.
     */
    public String generateReport(String reportName) {
        try {
            // Command injection vulnerability
            Process process = Runtime.getRuntime().exec("bash -c 'cat /reports/" + reportName + "'");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Dead code - unreachable method.
     */
    private void unusedMethod() {
        int x = 5;
        int y = 10;
        int z = x + y;
        System.out.println(z);
    }
}
