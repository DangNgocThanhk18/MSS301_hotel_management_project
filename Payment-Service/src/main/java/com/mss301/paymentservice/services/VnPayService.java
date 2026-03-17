// src/main/java/com/mss301/paymentservice/services/VnPayService.java
package com.mss301.paymentservice.services;

import com.mss301.paymentservice.configs.VnPayConfig;
import com.mss301.paymentservice.pojos.Payment;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class VnPayService {

    public String generatePaymentUrl(Payment payment, HttpServletRequest request) {
        try {
            log.info("=== BẮT ĐẦU TẠO VNPay URL ===");
            log.info("Payment ID: {}, Amount: {}", payment.getId(), payment.getAmount());

            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_CurrCode = "VND";

            // Amount = số tiền * 100 (theo yêu cầu VNPay)
            long amount = payment.getAmount().longValue() * 100;

            // Timezone GMT+7 (Việt Nam)
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
            Calendar calendar = Calendar.getInstance(timeZone);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            formatter.setTimeZone(timeZone);

            String vnp_CreateDate = formatter.format(calendar.getTime());
            calendar.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(calendar.getTime());

            String vnp_TxnRef = String.valueOf(payment.getId());
            String vnp_OrderInfo = "Thanh toan dat phong #" + payment.getReservationId();
            String vnp_IpAddr = getClientIpAddress(request);

            // Tạo params
            Map<String, String> vnp_Params = new TreeMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", VnPayConfig.vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.put("vnp_OrderType", "170000");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", VnPayConfig.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            // Tạo hash data
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();

                if (fieldValue != null && !fieldValue.isEmpty()) {
                    String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString());

                    if (hashData.length() > 0) {
                        hashData.append('&');
                    }
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(encodedValue);

                    if (query.length() > 0) {
                        query.append('&');
                    }
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(encodedValue);
                }
            }

            String rawHashData = hashData.toString();
            log.debug("Raw hash data: {}", rawHashData);

            String vnp_SecureHash = hmacSHA512(VnPayConfig.vnp_HashSecret, rawHashData);
            log.debug("Secure hash: {}", vnp_SecureHash);

            String paymentUrl = VnPayConfig.vnp_Url + "?" + query +
                    "&vnp_SecureHashType=SHA512&vnp_SecureHash=" + vnp_SecureHash;

            log.info("Payment URL generated successfully");
            return paymentUrl;

        } catch (Exception e) {
            log.error("Error generating VNPay URL", e);
            throw new RuntimeException("Lỗi tạo VNPay URL: " + e.getMessage());
        }
    }

    public boolean validateVnPayReturn(Map<String, String> params) {
        try {
            String vnp_SecureHash = params.get("vnp_SecureHash");
            if (vnp_SecureHash == null || vnp_SecureHash.isEmpty()) {
                log.warn("Missing vnp_SecureHash");
                return false;
            }

            Map<String, String> paramsForHash = new TreeMap<>(params);
            paramsForHash.remove("vnp_SecureHash");
            paramsForHash.remove("vnp_SecureHashType");

            StringBuilder hashData = new StringBuilder();
            for (Map.Entry<String, String> entry : paramsForHash.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    if (hashData.length() > 0) {
                        hashData.append('&');
                    }
                    hashData.append(entry.getKey());
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII.toString()));
                }
            }

            String rawHashData = hashData.toString();
            String calculatedHash = hmacSHA512(VnPayConfig.vnp_HashSecret, rawHashData);

            boolean isValid = calculatedHash.equalsIgnoreCase(vnp_SecureHash);
            log.info("VNPay hash validation: {}", isValid ? "SUCCESS" : "FAILED");

            return isValid;

        } catch (Exception e) {
            log.error("Error validating VNPay return", e);
            return false;
        }
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            log.error("Error in HMAC-SHA512", ex);
            return "";
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        try {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
                return xForwardedFor.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
}