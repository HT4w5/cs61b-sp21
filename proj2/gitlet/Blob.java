package gitlet;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.io.File;

import static gitlet.Utils.*;

public class Blob extends GitletObject<Blob.Data> {

    // Constructors
    private Blob(String sha1) {
        super(sha1);
    }

    private Blob() {
        super();
    }

    // Static
    public static class Data implements Serializable {
        public String filename_;
        public byte[] content_;
    }

    /**
     * Create new blob from file in current working directory
     *
     * @param filename name of file
     * @return new Blob
     */
    public static Blob fromFileName(String filename) {
        Blob b = new Blob();
        File f = join(Repository.CWD, filename);
        if (!f.exists()) {
            throw new GitletException("file doesn't exist");
        }
        if (f.isDirectory()) {
            throw new GitletException("directories not supported");
        }
        // Save file data
        b.data_.filename_ = filename;
        b.data_.content_ = readContents(f);
        return b;
    }

    /**
     * Read existing blob from filesystem by SHA-1 hash
     *
     * @param sha1 SHA-1 hash hex-string of blob
     * @return Blob
     */
    public static Blob fromSHA1Hash(String sha1) {
        return new Blob(sha1);
    }

    // Public methods

    /**
     * Restore blob to file in current working directory
     * regardless of existing files
     */
    public void restore() {
        File f = join(Repository.CWD, data_.filename_);
        writeContents(f, (Object) data_.content_);
    }
}
