package gitlet;

import java.io.File;
import java.util.*;

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
        if (f.isDirectory()) {
            throw new GitletException("Directories not supported");
        }

        Index index = Index.fromFilesystem();
        Head head = Head.fromFilesystem();
        Commit commit = Commit.fromObjects(head.getHash());

        boolean changed = false;
        // Unstage
        if (index.hasFile(filename)) {
            index.removeFile(filename);
            changed = true;
        }

        if (commit.hasFile(filename)) {
            changed = true;
            f.delete();
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
            } catch (ClassCastException ignored) {
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
        var commitFull = getFullObjectHash(commit);
        if (commitFull == null) {
            System.out.println("No commit with that id exists.");
            return;
        }

        Commit c = Commit.fromObjects(commitFull);
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

        Index index = Index.fromFilesystem();

        if (hasUntracked(index)) {
            System.out.println("There is an untracked file in the way; delete it, or add and " +
                    "commit it first.");
            return;
        }

        restoreCommit(newBranchHead);

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

        Set<String> staged = new TreeSet<>(indexFileSet);
        staged.removeAll(latestCommitFileSet);

        Set<String> removed = new TreeSet<>(latestCommitFileSet);
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

    public static void reset(String commit) {
        Index index = Index.fromFilesystem();
        if (hasUntracked(index)) {
            System.out.println("There is an untracked file in the way; delete it, or add and " +
                    "commit it first.");
            return;
        }

        var commitFull = getFullObjectHash(commit);
        if (commitFull == null) {
            System.out.println("No commit with that id exists.");
            return;
        }

        restoreCommit(commitFull);

        Head head = Head.fromFilesystem();
        head.setHash(commitFull);
        head.save();

        Branches branches = Branches.fromFilesystem();
        branches.setBranchHead(head.getBranch(), commitFull);
        branches.save();
    }

    public static void merge(String givenBranch) {
        Head head = Head.fromFilesystem();
        if (givenBranch.equals(head.getBranch())) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        Branches branches = Branches.fromFilesystem();
        String givenBranchHead = branches.getBranchHead(givenBranch);
        if (givenBranchHead == null) {
            System.out.println("A branch with that name does not exist.");
            return;
        }

        Index currentIndex = Index.fromFilesystem();
        if (hasUntracked(currentIndex)) {
            System.out.println("There is an untracked file in the way; delete it, or add and " +
                    "commit it first.");
            return;
        }

        String currentBranchHead = head.getHash();
        Commit currentCommit = Commit.fromObjects(currentBranchHead);
        if (currentIndex.changed(currentCommit)) {
            System.out.println("You have uncommitted changes.");
            return;
        }

        // Look for split point
        HashSet<String> currentAncestors = new HashSet<>();
        Queue<String> fringe = new LinkedList<>();

        // Search for all ancestors of current branch head
        fringe.add(currentBranchHead);
        while (!fringe.isEmpty()) {
            String hash = fringe.remove();
            Commit c = Commit.fromObjects(hash);
            currentAncestors.add(hash);
            String parent = c.getParent();
            String altParent = c.getAltParent();
            if (parent != null) {
                fringe.add(parent);
            }
            if (altParent != null) {
                fringe.add(altParent);
            }
        }

        // BFS for latest common ancestor
        String latestCommonAncestor = null;
        fringe = new LinkedList<>();
        fringe.add(givenBranchHead);
        while (!fringe.isEmpty()) {
            String hash = fringe.remove();
            if (currentAncestors.contains(hash)) {
                latestCommonAncestor = hash;
                break;
            }
            Commit c = Commit.fromObjects(hash);
            String parent = c.getParent();
            String altParent = c.getAltParent();
            if (parent != null) {
                fringe.add(parent);
            }
            if (altParent != null) {
                fringe.add(altParent);
            }
        }

        if (latestCommonAncestor == null) {
            throw new GitletException("No common ancestors");
        }

        // Fast-forward
        if (latestCommonAncestor.equals(currentBranchHead)) {
            restoreCommit(givenBranchHead);
            branches.setBranchHead(head.getBranch(), givenBranchHead);
            branches.save();
            head.setHash(givenBranchHead);
            head.save();
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        // Nothing to do
        if (latestCommonAncestor.equals(givenBranchHead)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        // Three-way merge
        boolean conflict = false;
        Commit givenCommit = Commit.fromObjects(givenBranchHead);
        Commit splitPointCommit = Commit.fromObjects(latestCommonAncestor);

        Index newIndex = Index.createEmpty();

        Set<String> commonFiles = new HashSet<>(givenCommit.filenameSet());
        Set<String> filesOnlyInGiven = new HashSet<>(commonFiles);
        Set<String> filesOnlyInCurrent = new HashSet<>(currentCommit.filenameSet());
        commonFiles.retainAll(currentCommit.filenameSet());

        filesOnlyInCurrent.removeAll(commonFiles);
        filesOnlyInGiven.removeAll(commonFiles);

        for (String f : filesOnlyInCurrent) {
            String fHash = currentCommit.getFile(f);
            String splitHash = splitPointCommit.getFile(f);
            if (splitHash == null) {
                newIndex.putFile(f, fHash);
            } else if (!fHash.equals(splitHash)) {
                Blob orig = Blob.fromObjects(fHash);
                Blob merged = Blob.fromMerge(f, orig, null);
                merged.save();
                newIndex.putFile(f, merged.getSHA1Hash());
                conflict = true;
            }
        }
        for (String f : filesOnlyInGiven) {
            String fHash = givenCommit.getFile(f);
            String splitHash = splitPointCommit.getFile(f);
            if (splitHash == null) {
                newIndex.putFile(f, fHash);
            } else if (!fHash.equals(splitHash)) {
                Blob orig = Blob.fromObjects(fHash);
                Blob merged = Blob.fromMerge(f, null, orig);
                merged.save();
                newIndex.putFile(f, merged.getSHA1Hash());
                conflict = true;
            }
        }

        for (String f : commonFiles) {
            String currentHash = currentCommit.getFile(f);
            String givenHash = givenCommit.getFile(f);
            if (currentHash.equals(givenHash)) {
                newIndex.putFile(f, currentHash);
            } else {
                String splitHash = splitPointCommit.getFile(f);
                if (splitHash.equals(currentHash)) {
                    newIndex.putFile(f, givenHash);
                } else if (splitHash.equals(givenHash)) {
                    newIndex.putFile(f, currentHash);
                } else {
                    // conflict
                    Blob currentBlob = Blob.fromObjects(currentHash);
                    Blob givenBlob = Blob.fromObjects(givenHash);
                    Blob merged = Blob.fromMerge(f, currentBlob, givenBlob);
                    merged.save();
                    newIndex.putFile(f, merged.getSHA1Hash());
                    conflict = true;
                }
            }
        }

        // Generate commit
        String msg = "Merged " + givenBranch + " into " + head.getBranch() + ".";
        Commit nc = Commit.fromIndex(newIndex, msg, currentBranchHead, givenBranchHead);
        nc.save();
        head.setHash(nc.getSHA1Hash());
        head.save();
        branches.setBranchHead(head.getBranch(), nc.getSHA1Hash());

        // Restore files
        restoreCommit(nc.getSHA1Hash());

        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private static boolean hasUntracked(Index index) {
        // Check for untracked files
        var indexFileSet = index.filenameSet();
        var currentFilesList = Utils.plainFilenamesIn(CWD);
        if (currentFilesList == null) {
            throw new GitletException("No a directory");
        }
        Set<String> untracked = new TreeSet<>(currentFilesList);
        untracked.removeAll(indexFileSet);

        return !untracked.isEmpty();
    }

    private static String getFullObjectHash(String hashPrefix) {
        var objects = Utils.plainFilenamesIn(OBJECTS_FOLDER);
        if (objects == null) {
            throw new GitletException("Missing objects");
        }
        String objectHash = null;
        for (var obj : objects) {
            if (obj.startsWith(hashPrefix)) {
                objectHash = obj;
            }
        }
        return objectHash;
    }

    private static void restoreCommit(String commit) {
        // Restore CWD
        var currentFilesList = Utils.plainFilenamesIn(CWD);
        if (currentFilesList == null) {
            throw new GitletException("CWD should be directory");
        }
        Commit c = Commit.fromObjects(commit);
        for (var n : currentFilesList) {
            File f = join(CWD, n);
            if (!f.delete()) {
                System.out.println("warning: file deletion failed: " + n);
            }
        }
        for (var e : c.entrySet()) {
            Blob b = Blob.fromObjects(e.getValue());
            b.restore();
        }
        // Modify index
        Index newIndex = Index.fromCommit(c);
        newIndex.save();
    }
}
