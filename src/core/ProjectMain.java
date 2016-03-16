package core;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by huaijie on 10/29/15.
 */
public class ProjectMain {
    private static final String TAG = "Main";

    private static ProjectMain ourInstance = new ProjectMain();


    public static ProjectMain getInstance() {
        return ourInstance;
    }

    private ProjectMain() {
    }


    public void buildAll() {
        for (SourceBranch branch : SourceBranch.values()) {
            build(branch);
        }
    }


    public void build(SourceBranch branch) {
        SourceManager sourceManager = SourceManager.getInstance();
        Git git = sourceManager.getGit();
//
        sourceManager.checkSourceUpdate();
//
        SourceBuilder sourceBuilder = SourceBuilder.getInstance();
        EmailSender emailSender = EmailSender.getInstance();
//
        sourceManager.switchBranch(git, branch);

        int versionCode = sourceManager.updateVersionCode(git, branch);

        if (versionCode > 0) {

            if (sourceBuilder.buildSource()) {

                String msg = new String();


                Date now = new Date();
                //可以方便地修改日期格式
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                String todayTime = dateFormat.format(now);
                Date date = null;
                try {
                    date = dateFormat.parse(todayTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //今天
                Calendar todayCalendar = new GregorianCalendar();
                todayCalendar.setTime(date);
                todayCalendar.add(Calendar.DATE, -0);

                //昨天
                Calendar yesterdayCalendar = new GregorianCalendar();
                yesterdayCalendar.setTime(date);
                yesterdayCalendar.add(Calendar.DATE, -1);


                //now
                Calendar nowCalendar = new GregorianCalendar();
                nowCalendar.setTime(new Date());
                nowCalendar.add(Calendar.DATE, 0);

                msg += "================" +dateFormat.format(todayCalendar.getTime())+"修改================\n";
                for (RevCommit commit : sourceManager.getCommitLogByTime(git, todayCalendar, nowCalendar)) {
                    msg += commit.getAuthorIdent().getName() + "    =====>  " + commit.getFullMessage();
                }

                msg += "\n\n\n";

                msg += "================" +dateFormat.format(yesterdayCalendar.getTime())+"修改================\n";
                for (RevCommit commit : sourceManager.getCommitLogByTime(git, yesterdayCalendar, todayCalendar)) {
                    msg += commit.getAuthorIdent().getName() + "    =====>  " + commit.getFullMessage();
                }
                Logger.logInfo(TAG, msg);
                emailSender.send(branch, msg, versionCode);
            }
        }


    }
}
