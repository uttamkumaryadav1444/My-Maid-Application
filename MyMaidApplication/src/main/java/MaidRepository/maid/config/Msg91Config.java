package MaidRepository.maid.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Msg91Config {

    @Value("${msg91.authkey}")
    private String authKey;

    @Value("${msg91.template_id}")
    private String templateId;

    @Value("${msg91.url.send}")
    private String sendOtpUrl;

    @Value("${msg91.url.verify}")
    private String verifyOtpUrl;

    @Value("${msg91.url.resend}")
    private String resendOtpUrl;

    // Getters
    public String getAuthKey() { return authKey; }
    public String getTemplateId() { return templateId; }
    public String getSendOtpUrl() { return sendOtpUrl; }
    public String getVerifyOtpUrl() { return verifyOtpUrl; }
    public String getResendOtpUrl() { return resendOtpUrl; }
}