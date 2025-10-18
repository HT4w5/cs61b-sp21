package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;

import static gitlet.Utils.*;

public class Head implements Serializable {
    // Static
    public static final File HEAD = join(Repository.GITLET_DIR, "head");

    public static Head fromFilesystem() {
        if (!HEAD.exists()) {
            throw new GitletException("head file missing");
        }
        return readObject(HEAD, Head.class);
    }

    public static Head createEmpty() {
        return new Head();
    }

    // Public methods
    public void save() {
        writeObject(HEAD, this);
    }

    public String getBranch() {
        return branch_;
    }

    public void setBranch(String branch) {
        branch_ = branch;
    }

    public String getHash() {
        return hash_;
    }

    public void setHash(String hash) {
        hash_ = hash;
    }

    public void set(String branchName, String headHash) {
        branch_ = branchName;
        hash_ = headHash;
    }

    // Private members
    private String branch_;
    private String hash_;
}
