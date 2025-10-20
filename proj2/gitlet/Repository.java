package gitlet;

import com.sun.source.tree.Tree;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

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

    public static void init() {
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

    public static void add(String filename) {
        if (filename.equals(".gitlet")) {
            throw new GitletException("Can't add .gitlet directory");
        }
        File f = join(CWD, filename);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        if (f.isDirectory()) {
            throw new GitletException("Directories not supported");
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

    public static void rm(String filename) {
        File f = join(CWD, filename);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        if (f.isDirectory()) {
            throw new GitletException("Directories not supported");
        }

        Index index = Index.fromFilesystem();
        Head head = Head.fromFilesystem();
        Branches branches = Branches.fromFilesystem();
        Commit commit = Commit.fromObjects(head.getHash());

        boolean changed = false;
        // Unstage
        if (index.hasFile(filename)) {
            index.removeFile(filename);
            changed = true;
        }

        if (commit.hasFile(filename)) {
            changed = true;
            if (!f.delete()) {
                throw new GitletException("Failed to delete file from CWD");
            }
        }

        if (!changed) {
            System.out.println("No reason to remove the file.");
        }

        index.save();
    }

    public static void log() {
        Head head = Head.fromFilesystem();

        Commit c = Commit.fromObjects(head.getHash());
        while (true) {
            System.out.print(c.toLogEntry());
            if (c.getParent() == null) {
                break;
            }
            c = Commit.fromObjects(c.getParent());
        }
    }

    public static void globalLog() {
        var objects = Utils.plainFilenamesIn(OBJECTS_FOLDER);
        if (objects == null) {
            throw new GitletException("Missing objects");
        }
        for (var obj : objects) {
            try {
                Commit c = Commit.fromObjects(obj);
                System.out.print(c.toLogEntry());

            } catch (IllegalArgumentException | ClassCastException e) {
            }
        }
    }

    public static void find(String msg) {
        var objects = Utils.plainFilenamesIn(OBJECTS_FOLDER);
        if (objects == null) {
            throw new GitletException("Missing objects");
        }
        boolean found = false;
        for (var obj : objects) {
            try {
                Commit c = Commit.fromObjects(obj);
                if (!msg.equals(c.getMsg())) {
                    continue;
                }
                System.out.println(c.getSHA1Hash());
            } catch (IllegalArgumentException | ClassCastException e) {
                continue;
            }
            found = true;
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void checkoutFile(String commit, String filename) {
        var objects = Utils.plainFilenamesIn(OBJECTS_FOLDER);
        if (objects == null) {
            throw new GitletException("Missing objects");
        }
        String commitFull = null;
        for (var obj : objects) {
            if (obj.startsWith(commit)) {
                commitFull = obj;
            }
        }
        if (commitFull == null) {
            System.out.println("No commit with that id exists.");
            return;
        }

        Commit c = Commit.fromObjects(commit);
        String blobHash = c.getFile(filename);
        if (blobHash == null) {
            System.out.println("File does not exist in that commit.");
            return;
        }

        Blob b = Blob.fromObjects(blobHash);
        b.restore();
    }

    public static void checkoutFileFromHead(String filename) {
        Head head = Head.fromFilesystem();
        checkoutFile(head.getHash(), filename);
    }

    public static void checkoutBranch(String name) {
        // Check if is current branch
        Head head = Head.fromFilesystem();
        if (name.equals(head.getBranch())) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

        // Check branch availability
        Branches branches = Branches.fromFilesystem();
        String newBranchHead = branches.getBranchHead(name);
        if (newBranchHead == null) {
            System.out.println("No such branch exists.");
            return;
        }

        // Check for untracked files
        Index index = Index.fromFilesystem();
        var indexFileSet = index.filenameSet();
        var currentFilesList = Utils.plainFilenamesIn(CWD);
        if (currentFilesList == null) {
            throw new GitletException("No a directory");
        }
        Set<String> untracked = new TreeSet<>(currentFilesList);
        untracked.removeAll(indexFileSet);

        if (!untracked.isEmpty()) {
            System.out.println("There is an untracked file in the way; delete it, or add and " +
                    "commit it first.");
            return;
        }

        // Restore CWD
        Commit newHead = Commit.fromObjects(newBranchHead);
        for (var n : currentFilesList) {
            File f = join(CWD, n);
            f.delete();
        }
        for (var e : newHead.entrySet()) {
            Blob b = Blob.fromObjects(e.getValue());
            b.restore();
        }

        // Modify index
        Index newIndex = Index.fromCommit(newHead);
        newIndex.save();

        // Modify head
        head.set(name, newBranchHead);
        head.save();
    }

    public static void status() {
        Branches branches = Branches.fromFilesystem();
        Head head = Head.fromFilesystem();
        Index index = Index.fromFilesystem();
        Commit latest = Commit.fromObjects(head.getHash());

        // Branches
        System.out.println("=== Branches ===");

        var branchSet = branches.branchSet();
        var currentBranch = head.getBranch();
        for (var b : branchSet) {
            if (b.equals(currentBranch)) {
                System.out.print("*");
            }
            System.out.println(b);
        }

        // Staged and removed files
        var indexFileSet = index.filenameSet();
        var latestCommitFileSet = latest.filenameSet();

        Set<String> staged = new TreeSet<String>(indexFileSet);
        staged.removeAll(latestCommitFileSet);

        Set<String> removed = new TreeSet<String>(latestCommitFileSet);
        removed.removeAll(indexFileSet);

        System.out.println();
        System.out.println("=== Staged Files ===");
        for (var f : staged) {
            System.out.println(f);
        }

        System.out.println();
        System.out.println("=== Removed Files ===");
        for (var f : removed) {
            System.out.println(f);
        }

        // Modifications
        var indexSet = index.entrySet();
        Set<String> output = new TreeSet<>();
        for (var e : indexSet) {
            File f = Utils.join(CWD, e.getKey());
            if (!f.exists()) {
                output.add(e.getKey() + " (deleted)");
                continue;
            }
            if (f.isDirectory()) {
                throw new GitletException("Directories not supported");
            }
            Blob b = Blob.fromFileName(e.getKey());
            if (!e.getValue().equals(b.getSHA1Hash())) {
                output.add(e.getKey() + " (modified)");
            }
        }

        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (var line : output) {
            System.out.println(line);
        }

        // Untracked
        var currentFilesList = Utils.plainFilenamesIn(CWD);
        if (currentFilesList == null) {
            throw new GitletException("No a directory");
        }
        Set<String> untracked = new TreeSet<>(currentFilesList);
        untracked.removeAll(indexFileSet);

        System.out.println();
        System.out.println("=== Untracked Files ===");
        for (var line : untracked) {
            System.out.println(line);
        }

        System.out.println();
    }

    public static void branch(String name) {
        Branches branches = Branches.fromFilesystem();
        Head head = Head.fromFilesystem();

        if (!branches.createBranch(name, head.getHash())) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        branches.save();
    }

    public static void rmBranch(String name) {
        Head head = Head.fromFilesystem();
        if (head.getBranch().equals(name)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }

        Branches branches = Branches.fromFilesystem();
        if (!branches.rmBranch(name)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }

        branches.save();
    }
}
