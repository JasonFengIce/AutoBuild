import core.ProjectMain;
import core.SourceBranch;

/**
 * Created by huaijie on 11/12/15.
 */
public class Main {
    public static void main(String[] argv) {
//        ProjectMain.getInstance().build(SourceBranch.tencent);
        ProjectMain.getInstance().build(SourceBranch.master);
    }
}
