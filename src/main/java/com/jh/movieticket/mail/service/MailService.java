package com.jh.movieticket.mail.service;

import com.jh.movieticket.mail.exception.MailErrorCode;
import com.jh.movieticket.mail.exception.MailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;

    public void sendEmail(String toEmail, String title, String text) {

        SimpleMailMessage emailForm = createEmailForm(toEmail, title, text);

        try {
            javaMailSender.send(emailForm);
        } catch (RuntimeException e) {
            log.error("이메일 발송 실패 toEmail = {}, title = {}, text = {}", toEmail, title, text);
            throw new MailException(MailErrorCode.FAIL_SEND_EMAIL);
        }
    }

    // 이메일 데이터 세팅
    private SimpleMailMessage createEmailForm(String toEmail, String title, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);

        return message;
    }
}