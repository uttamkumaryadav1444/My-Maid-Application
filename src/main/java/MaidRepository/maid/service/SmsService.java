package MaidRepository.maid.service;

public interface SmsService {
    void sendSms(String mobile, String message);
    void sendOtpSms(String mobile, String otp);
}