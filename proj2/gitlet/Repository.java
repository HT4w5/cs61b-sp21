package gitlet;

import java.io.File;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 *
 * @author John Doe
 */
public class Repository {

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECTS_FOLDER = join(GITLET_DIR, "objects");
    public static final String DEFAULT_BRANCH = "master";


    // Static
    public static boolean repoExists() {
        return GITLET_DIR.exists();
    }

    public static void initRepo() {
        if (repoExists()) {
            throw new GitletException("Repo exists");
        }
        if (!GITLET_DIR.mkdir() || !OBJECTS_FOLDER.mkdir()) {
            throw new GitletException("Failed to init repo");
        }

        // Create index file
        Index index = Index.createEmpty();
        index.save();

        // Create initial commit
        Commit ic = Commit.initial();
        ic.save();

        // Create head file
        Head head = Head.createEmpty();
        head.setBranch(DEFAULT_BRANCH);
        head.setHash(ic.getSHA1Hash());
        head.save();

        // Create branches file
        Branches branches = Branches.createEmpty();
        branches.createBranch(DEFAULT_BRANCH, ic.getSHA1Hash());
        branches.save();
    }

    public static void addFile(String filename) {
        if (filename.equals(".gitlet")) {
            throw new GitletException("Can't add .gitlet directory");
        }

        Index index = Index.fromFilesystem();
        Head head = Head.fromFilesystem();
        // Create blob
        Blob blob = Blob.fromFileName(filename);

        String indexBlobHash = index.getFile(filename);
        if (indexBlobHash != null && indexBlobHash.equals(blob.getSHA1Hash())) {
            return;
        }
        // Compare to head commit
        Commit hc = Commit.fromObjects(head.getHash());
        String headCommitBlobHash = hc.getFile(filename);
        if (headCommitBlobHash != null && headCommitBlobHash.equals(blob.getSHA1Hash())) {
            index.removeFile(filename);
            return;
        }

        blob.save();
        index.putFile(filename, blob.getSHA1Hash());
        index.save();
    }

    public static void commit(String msg) {
        Index index = Index.fromFilesystem();
        Head head = Head.fromFilesystem();
        Branches branches = Branches.fromFilesystem();


        // Compare changes
        Commit prev = Commit.fromObjects(head.getHash());
        if (!index.changed(prev)) {
            System.out.println("No changes added to the commit.");
            return;
        }

        // Create commit
        Commit nc = Commit.fromIndex(index, msg, head.getHash(), null);
        head.setHash(nc.getSHA1Hash());
        branches.setBranchHead(head.getBranch(), nc.getSHA1Hash());
        nc.save();
        head.save();
        branches.save();
    }

}
