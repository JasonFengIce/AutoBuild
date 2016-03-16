package core;

/**
 * Created by huaijie on 10/24/15.
 */
public enum SourceBranch {
    master;
//    sharp,
//    sharp_2$1,
//    master_2$1,
//    tencent;

    @Override
    public String toString() {
        return super.toString().replace("$", ".");
    }

    public String getTitle() {
        if (this.toString().equals("master_2.1")) {
            return "sharp_2.1";
        } else {
            return this.toString();
        }
    }
}
