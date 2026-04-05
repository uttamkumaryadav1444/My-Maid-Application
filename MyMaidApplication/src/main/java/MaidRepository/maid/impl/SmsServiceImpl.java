package MaidRepository.maid.impl;

import MaidRepository.maid.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Override
    public void sendSms(String mobile, String message) {
        log.info(" SMS to {}: {}", mobile, message);
        // Yaha actual SMS integration aayega
    }

    @Override
    public void sendOtpSms(String mobile, String otp) {
        String message = "Your OTP is: " + otp;
        sendSms(mobile, message);
    }
}