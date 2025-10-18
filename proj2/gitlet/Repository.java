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
        Commit ic = Commit.createInitial();
        ic.save();

        // Create head file
        Head head = Head.createEmpty(DEFAULT_BRANCH);
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
        // Create blob
        Blob blob = Blob.fromFileName(filename);

        String oldBlobHash = index.getFile(filename);
        if (oldBlobHash != null && oldBlobHash.equals(blob.getSHA1Hash())) {
            return;
        }
        // TODO: compare to head commit
        blob.save();
        index.putFile(filename, blob.getSHA1Hash());
    }

}
