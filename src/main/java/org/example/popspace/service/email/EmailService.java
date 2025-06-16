package org.example.popspace.service.email;


import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.EntryEmail.Reservation;
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

    public void sendEnterNotification(Reservation reservation, String PopupName, String PopupLocation, String endTime) {
        log.info("reservation = {}", reservation);
        log.info("popupName = {}", PopupName);
        log.info("popupLocation = {}", PopupLocation);
        log.info("endTime = {}", endTime);
        String title = "[popspace]🎉 " + PopupName + " 입장안내 - 입장순서가 도착했어요!";
        String content = """
        <body style="font-family: 'Apple SD Gothic Neo', Arial, sans-serif; background-color: #f7f7f7; padding: 20px;">
            <div style="max-width: 600px; margin: auto; background: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                <h2 style="color: #1D9D8B;">🎉 입장 순서가 도착했어요!</h2>
                <p style="font-size: 16px; color: #333;">지금 바로 입장하실 수 있습니다.</p>
                <table style="width: 100%%; font-size: 16px; margin-top: 20px;">
                    <tr>
                        <td style="padding: 10px 0; font-weight: bold;">팝업명</td>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <td style="padding: 10px 0; font-weight: bold;">예약 일시</td>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <td style="padding: 10px 0; font-weight: bold;">입장 마감</td>
                        <td style="color: #DB4437; font-weight: bold;">%s까지</td>
                    </tr>
                    <tr>
                        <td style="padding: 10px 0; font-weight: bold;">현장 위치</td>
                        <td>%s</td>
                    </tr>
                </table>
                <p style="margin-top: 30px; color: #DB4437; font-size: 14px;">
                    ⚠ 입장 마감 시간 이후에는 입장이 제한되며, 노쇼로 간주될 수 있습니다.
                </p>
                <p style="text-align: center; font-size: 14px; color: #999; margin-top: 20px;">
                    <b>PopSpace 드림.</b>
                </p>
            </div>
        </body>
        """.formatted(
                PopupName,
                reservation.getReserveDate(),
                endTime,
                PopupLocation
        );
        mailSend(reservation.getEmail(), title, content);
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