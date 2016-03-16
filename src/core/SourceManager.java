package core;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static core.Constants.*;

/**
 * Created by huaijie on 10/29/15.
 */
public class SourceManager {
    private static final String TAG = "SourceManager";

    private static SourceManager ourInstance;


    public static SourceManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new SourceManager();
        }
        return ourInstance;
    }

    private SourceManager() {

    }


    public List<RevCommit> getTodayCommitLog(Git git) {
        ArrayList<RevCommit> revCommits = new ArrayList<>();
        try {
            PullResult pullResult = git.pull().setCredentialsProvider(getCredentialsProvider()).call();
            boolean pullSuccessful = pullResult.isSuccessful();

            if (pullSuccessful) {
                Iterable<RevCommit> pullIterable = git.log().call();
                for (RevCommit revCommit : pullIterable) {
                    Date now = new Date();
                    //可以方便地修改日期格式
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    String todayTime = dateFormat.format(now);
                    Date date = dateFormat.parse(todayTime);

                    //今天
                    Calendar todayCalendar = new GregorianCalendar();
                    todayCalendar.setTime(date);
                    todayCalendar.add(Calendar.DATE, -1);

                    //昨天
                    Calendar yesterdayCalendar = new GregorianCalendar();
                    yesterdayCalendar.setTime(date);
                    yesterdayCalendar.add(Calendar.DATE, -1);

                    if (revCommit.getCommitTime() >= yesterdayCalendar.getTimeInMillis() / 1000
                            &&revCommit.getCommitTime() < todayCalendar.getTimeInMillis() /1000
                            && !revCommit.getFullMessage().contains("modify version to")
                            && !revCommit.getFullMessage().contains("Merge branch")
                            && !revCommit.getFullMessage().contains("优化代码")) {
                        revCommits.add(revCommit);
                    }
                }
            }
        } catch (Exception e) {
            Logger.logInfo(TAG, e.getMessage());
        }
        return revCommits;
    }


    public List<RevCommit> getCommitLogByTime(Git git, Calendar start, Calendar end) {
        ArrayList<RevCommit> revCommits = new ArrayList<>();
        try {
            PullResult pullResult = git.pull().setCredentialsProvider(getCredentialsProvider()).call();
            boolean pullSuccessful = pullResult.isSuccessful();

            if (pullSuccessful) {
                Iterable<RevCommit> pullIterable = git.log().call();
                for (RevCommit revCommit : pullIterable) {
//                    Date now = new Date();
//                    //可以方便地修改日期格式
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
//                    String todayTime = dateFormat.format(now);
//                    Date date = dateFormat.parse(todayTime);
//
//                    //今天
//                    Calendar todayCalendar = new GregorianCalendar();
//                    todayCalendar.setTime(date);
//                    todayCalendar.add(Calendar.DATE, -1);
//
//                    //昨天
//                    Calendar yesterdayCalendar = new GregorianCalendar();
//                    yesterdayCalendar.setTime(date);
//                    yesterdayCalendar.add(Calendar.DATE, -1);

                    if (revCommit.getCommitTime() >= start.getTimeInMillis() / 1000
                            &&revCommit.getCommitTime() < end.getTimeInMillis() /1000
                            && !revCommit.getFullMessage().contains("modify version to")
                            && !revCommit.getFullMessage().contains("Merge branch")
                            && !revCommit.getFullMessage().contains("优化代码")) {
                        revCommits.add(revCommit);
                    }
                }
            }
        } catch (Exception e) {
            Logger.logInfo(TAG, e.getMessage());
        }
        return revCommits;
    }





    public CredentialsProvider getCredentialsProvider() {
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(USERNAME, PASSWORD);
        return credentialsProvider;
    }

    public int updateVersionCode(Git git, SourceBranch barnch) {
        File file = new File(WORK_DIRECTORY, VERSION_FILE);
        int appVersionCode = 0;
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String all = new String();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("versionCode")) {
                    String[] str = line.split(" +");
                    int versionCode = Integer.parseInt(line.split(" +")[2]);
                    line = line.replace(String.valueOf(versionCode), String.valueOf(versionCode + 1));
                    appVersionCode = versionCode + 1;
                }
                if (line.contains("versionName")) {
                    int versionName = Integer.parseInt(line.split(" +")[2].replace("\"", ""));

                    line = line.replace(String.valueOf(versionName), String.valueOf(versionName + 1));
                }
                line += "\n";
                all += line;
            }
            bufferedReader.close();
            fileReader.close();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(all);
            fileWriter.flush();
            fileWriter.close();
            git.add().addFilepattern(".").call();
            git.commit().setMessage("modify version to  " + appVersionCode + "\n").call();

            RefSpec spec = new RefSpec(barnch.toString() + ":" + barnch.toString());
            CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(USERNAME, PASSWORD);
            Iterable<PushResult> resultIterable = git.push().setRemote("origin").setCredentialsProvider(credentialsProvider).setRefSpecs(spec).call();
        } catch (Exception e) {
            Logger.logInfo(TAG, e.getMessage());
        }
        return appVersionCode;
    }

    public Git getGit() {
        Git mGit = null;
        File workFile = new File(WORK_DIRECTORY);
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(USERNAME, PASSWORD);
        if (workFile.exists()) {
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            repositoryBuilder.setMustExist(true);
            repositoryBuilder.setWorkTree(new File(WORK_DIRECTORY));
            Repository repository = null;
            try {
                repository = repositoryBuilder.build();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mGit = new Git(repository);
        } else {
            workFile.mkdirs();
            CloneCommand cc = new CloneCommand()
                    .setCredentialsProvider(credentialsProvider)
                    .setDirectory(workFile)
                    .setURI(GIT_URL);
            try {
                mGit = cc.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mGit;
    }

    public void switchBranch(Git mGit, SourceBranch branch) {
        boolean localBranchExit = false;
        try {

            //判断本地是否有对应的branch
            List<Ref> refList = mGit.branchList().call();
            for (Ref ref : refList) {
                if (ref.getName().contains(branch.toString())) {
                    localBranchExit = localBranchExit || true;
                } else {
                    localBranchExit = localBranchExit || false;
                }
            }
            mGit.reset().setMode(ResetCommand.ResetType.HARD).call();
            //如果本地存在切换,否则创建
            if (localBranchExit) {
                mGit.checkout().setName(branch.toString()).call();
            } else {
                mGit.checkout().setCreateBranch(true).setName(branch.toString()).setStartPoint("origin/" + branch.toString()).call();
            }
            Logger.logInfo(TAG, "switch branch to [" + branch + "]");
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }


    public void checkSourceUpdate() {
        try {
            Git mGit = getGit();

            for (SourceBranch branch : SourceBranch.values()) {

                Logger.logInfo(TAG, "start check [" + branch + "] SourceUpdate...");
                ArrayList<RevCommit> arrayList = new ArrayList<>();
                switchBranch(mGit, branch);

                Iterable<RevCommit> revCommitIterable = mGit.log().setMaxCount(1).call();

                ObjectId lastId = null;
                for (RevCommit revCommit : revCommitIterable) {
                    lastId = revCommit.getId();
                }
                PullResult result = mGit.pull().setCredentialsProvider(getCredentialsProvider()).call();
                if (result.isSuccessful()) {
                    Iterable<RevCommit> updateRevCommits = mGit.log().not(lastId).call();
                    for (RevCommit revCommit : updateRevCommits) {
                        arrayList.add(revCommit);
                    }

//                    if (!arrayList.isEmpty()) {
//                        SourceBuilder.getInstance().buildSource();
//                    }
                    Logger.logInfo(TAG, "check" + branch + "SourceUpdate end...");
                } else {
                    Logger.logError(TAG, "check" + branch + "SourceUpdate failed...");
                    throw new Exception("checkSourceUpdate failed");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public boolean checkSourceUpdate(SourceBranch branch) {
//        Logger.logInfo(TAG, "check [" + branch + "] source update...");
//
//
//    }
}
