package core;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static core.Constants.*;


/**
 * Created by huaijie on 10/24/15.
 */
public class EmailSender {
    private static final String TAG = "EmailSender";

    private static EmailSender ourInstance = new EmailSender();

    public static EmailSender getInstance() {
        return ourInstance;
    }

    private EmailSender() {
    }


    public void send(SourceBranch branch, String content, int versionCode) {
        Logger.logInfo(TAG, "start send email...");
        MultiPartEmail email = new MultiPartEmail();
        email.setHostName("smtp.qiye.163.com");
        email.setSslSmtpPort("25");
        email.setAuthenticator(new DefaultAuthenticator("fenghuibin@ismartv.cn", "Hope0Dies"));
        email.setSSLOnConnect(true);

        // Create the attachment
        EmailAttachment attachment = new EmailAttachment();
        File attachmentFile = new File(WORK_DIRECTORY, APK_DIRECTORY);
        String md5Code = Utils.getMd5ByFile(attachmentFile);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        content += "\nAPK MD5: " + md5Code;
        content += "\nAPK VERSION: " + versionCode;
        content += "\nBUILD TIME: " + dateFormat.format(new Date());

        attachment.setPath(attachmentFile.getAbsolutePath());
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription("Picture of John");

        attachment.setName("IsmartvVoice_" + branch.getTitle() + "_" + versionCode + ".apk");
        try {
            email.setFrom("fenghuibin@ismartv.cn");
            email.setSubject(branch.getTitle().toUpperCase() + "_视云语音识别客户端更新_" + versionCode);
            email.setMsg(content);
            email.addTo("zhengwenkai@ismartv.cn");
            email.addTo("zhangshaoqing@ismartv.cn");
            email.addCc("pengzonghu@ismartv.cn");
            email.addCc("jiazhaobin@ismartv.cn");
            email.addCc("stonewang@ismartv.cn");
            email.addCc("zhangtianxi@ismartv.cn");
//            email.addCc("yujiang@ismartv.cn");
//            email.addCc("wangdan@ismartv.cn");
            email.addCc("koujia@ismartv.cn");
            email.addCc("liwei@ismartv.cn");
            email.addCc("hanjie@ismartv.cn");
//            email.addCc("wuguojun@ismartv.cn");
//            email.addCc("qianguangzhao@ismartv.cn");
            email.addCc("liuqiang@ismartv.cn");

//            email.addTo("fenghuibin@ismartv.cn");
            email.attach(attachment);
            email.send();
        } catch (EmailException e) {
            Logger.logError(TAG, "send email error: " + e.getMessage());
        }
        Logger.logInfo(TAG, "send email end...");
    }
}
