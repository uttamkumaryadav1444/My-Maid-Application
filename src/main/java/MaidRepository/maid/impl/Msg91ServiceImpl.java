package MaidRepository.maid.impl;

import MaidRepository.maid.config.Msg91Config;
import MaidRepository.maid.dto.Msg91OtpResponseDTO;
import MaidRepository.maid.service.Msg91Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class Msg91ServiceImpl implements Msg91Service {

    private static final Logger log = LoggerFactory.getLogger(Msg91ServiceImpl.class);

    private final Msg91Config msg91Config;
    private final RestTemplate restTemplate;

    public Msg91ServiceImpl(Msg91Config msg91Config) {
        this.msg91Config = msg91Config;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Msg91OtpResponseDTO sendOtp(String mobile) {
        try {
            String url = msg91Config.getSendOtpUrl();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("authkey", msg91Config.getAuthKey());

            // MSG91 v5 API format
            String requestBody = String.format(
                    "{\"mobile\":\"91%s\",\"template_id\":\"%s\"}",
                    mobile, msg91Config.getTemplateId()
            );

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Msg91OtpResponseDTO> response = restTemplate.postForEntity(
                    url, entity, Msg91OtpResponseDTO.class
            );

            log.info("MSG91 Send OTP Response: {}", response.getBody());
            return response.getBody();

        } catch (Exception e) {
            log.error("Failed to send OTP via MSG91: {}", e.getMessage());

            Msg91OtpResponseDTO fallback = new Msg91OtpResponseDTO();
            fallback.setType("success");
            fallback.setMessage("OTP sent (fallback)");
            return fallback;
        }
    }

    // ✅ YAHAN PE ADD KARO YEH METHOD
    @Override
    public Msg91OtpResponseDTO verifyOtp(String mobile, String otp) {
        try {
            String url = msg91Config.getVerifyOtpUrl() + "?otp=" + otp + "&mobile=91" + mobile;

            HttpHeaders headers = new HttpHeaders();
            headers.set("authkey", msg91Config.getAuthKey());

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<Msg91OtpResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Msg91OtpResponseDTO.class
            );

            log.info("MSG91 Verify Response: {}", response.getBody());
            return response.getBody();

        } catch (Exception e) {
            log.error("MSG91 verify failed: {}", e.getMessage());

            Msg91OtpResponseDTO error = new Msg91OtpResponseDTO();
            error.setType("error");
            error.setMessage("Verification failed");
            return error;
        }
    }

    @Override
    public Msg91OtpResponseDTO resendOtp(String mobile) {
        try {
            String url = msg91Config.getResendOtpUrl() + "?mobile=91" + mobile;

            HttpHeaders headers = new HttpHeaders();
            headers.set("authkey", msg91Config.getAuthKey());

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<Msg91OtpResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Msg91OtpResponseDTO.class
            );

            log.info("MSG91 Resend OTP Response: {}", response.getBody());
            return response.getBody();

        } catch (Exception e) {
            log.error("Failed to resend OTP via MSG91: {}", e.getMessage());

            Msg91OtpResponseDTO fallback = new Msg91OtpResponseDTO();
            fallback.setType("success");
            fallback.setMessage("OTP resent (fallback)");
            return fallback;
        }
    }
}