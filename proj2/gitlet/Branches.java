package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;

import static gitlet.Utils.*;

public class Branches {
    // Static
    public static final File BRANCHES = join(Repository.GITLET_DIR, "branches");

    public static class BranchInfo implements Serializable {
        String head_;

        BranchInfo(String head) {
            head_ = head;
        }
    }

    public static Branches fromFilesystem() {
        if (!BRANCHES.exists()) {
            throw new GitletException("branches file missing");
        }
        Branches b = new Branches();
        b.branchMap_ = readObject(BRANCHES);
        return b;
    }

    public static Branches createEmpty() {
        Branches b = new Branches();
        b.branchMap_ = new TreeMap<>();
        return b;
    }

    // Public methods
    public void save() {
        writeObject(BRANCHES, branchMap_);
    }

    public boolean createBranch(String name, String head) {
        if (branchMap_.containsKey(name)) {
            return false;
        }
        branchMap_.put(name, new BranchInfo(head));
        return true;
    }

    public boolean getBranchHead(String name, String head) {
        if (!branchMap_.containsKey(name)) {
            return false;
        }
        branchMap_.put(name, new BranchInfo(head));
        return true;
    }

    public boolean hasBranch(String name) {
        return branchMap_.containsKey(name);
    }

    public String getBranchHead(String name) {
        var br = branchMap_.get(name);
        if (br == null) {
            return null;
        } else {
            return br.head_;
        }
    }

    public void setBranchHead(String name, String head) {
        branchMap_.put(name, new BranchInfo(head));
    }

    // Private members
    private TreeMap<String, BranchInfo> branchMap_;
}
