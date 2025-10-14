package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.join;
import static gitlet.Utils.readObject;
import static gitlet.Utils.writeObject;
import static gitlet.Utils.sha1;

public abstract class GitletObject<Data extends Serializable> {
    private static final File OBJECTS_FOLDER = join(Repository.GITLET_DIR, "objects");

    /**
     * Read an existing GitletObject from filesystem
     * @param sha1 SHA-1 hash hex-string
     */
    public GitletObject(String sha1) {
        // Read data from filesystem
        File f = join(OBJECTS_FOLDER, sha1);
        if (!f.exists()) {
            throw new GitletException("object" + sha1 + " not found in filesystem");
        }
        data_ = readObject(f);

        // Validate SHA-1
        computeSha1();
        if (sha1_.equals(sha1)) {
            throw new GitletException("object SHA-1 mismatch");
        }
    }

    public GitletObject() {}

    public void save() {
        computeSha1();
        File f = join(OBJECTS_FOLDER, sha1_);
        if (f.exists()) {
            throw new GitletException("object present in filesystem");
        }
        writeObject(f, data_);
    }

    // Private / Protected members
    /* serializable class representing all data stored in filesystem */
    protected Data data_;
    private String sha1_;

    private void computeSha1() {
        sha1_ = sha1(data_.toString());
    }
}
