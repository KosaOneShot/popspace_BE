package org.example.popspace.service.email;


import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendPinNumberToEmail(String email,String code){
        String title ="[popspace]비밀번호 재설정용 PinNumber";
        String content =
                "popspace을 사용해주셔서 감사합니다." +
                        "<br><br> " +
                        "인증 번호는 " + code + "입니다." +
                        "<br> "; // 이메일 내용
        mailSend(email, title, content);
    }

    // mail 양식 설정
    public void temporaryPasswordEmail(String email,String newPassword) {
        String title = "[popspace] 임시 비밀번호를 보내드립니다."; // 이메일 제목
        String content =
                "popspace을 사용해주셔서 감사합니다." +
                        "<br><br> " +
                        "임시 비밀번호는 " + newPassword + "입니다." +
                        "<br> " +
                        "보안을 위해 로그인 후에는 꼭 비밀번호를 변경해주세요!"; // 이메일 내용
        mailSend(email, title, content);
    }

    private void mailSend(String toMail, String title, String content) {
        MimeMessage message = javaMailSender.createMimeMessage(); // MimeMessage 객체 생성
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setTo(toMail); // 이메일 수신자 주소 설정
            helper.setSubject(title); // 이메일 주소 설정
            helper.setText(content, true); // 이메일의 내용
            javaMailSender.send(message);
        } catch (MailSendException sme)
        {
            log.error("존재하지 않는 이메일 : {}" ,toMail);
            throw new CustomException(ErrorCode.NOT_EXISTS_EMAIL);
        } catch (SendFailedException se)
        {
            log.error("메일 전송 실패: {}", se.getMessage());
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        } catch (MessagingException e) {
            log.error("이메일 전송 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
}