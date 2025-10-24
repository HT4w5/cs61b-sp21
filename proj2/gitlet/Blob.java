package gitlet;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static gitlet.Utils.*;

public class Blob extends GitletObject<Blob.Data> {

    // Constructors
    private Blob(String sha1) {
        super(sha1);
    }

    private Blob() {
        super();
        data_ = new Blob.Data();
    }

    // Static
    public static class Data implements Serializable, ToBytesConvertible {
        public String filename_;
        public byte[] content_;

        @Override
        public byte[] toByteArray() {
            byte[] filenameBytes = filename_.getBytes(StandardCharsets.UTF_8);
            int len = filenameBytes.length + content_.length;
            byte[] result = new byte[len];
            System.arraycopy(filenameBytes, 0, result, 0, filenameBytes.length);
            System.arraycopy(content_, 0, result, filenameBytes.length, content_.length);
            return result;
        }
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

    public static Blob fromMerge(String filename, Blob first, Blob second) {
        Blob b = new Blob();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write("<<<<<<< HEAD\n".getBytes(StandardCharsets.UTF_8));
            if (first != null) {
                baos.write(first.getContent());
            }
            baos.write("=======\n".getBytes(StandardCharsets.UTF_8));
            if (second != null) {
                baos.write(second.getContent());
            }
            baos.write(">>>>>>>".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Save data
        b.data_.content_ = baos.toByteArray();
        b.data_.filename_ = filename;
        return b;
    }


    /**
     * Read existing blob from filesystem by SHA-1 hash
     *
     * @param sha1 SHA-1 hash hex-string of blob
     * @return Blob
     */
    public static Blob fromObjects(String sha1) {
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

    public byte[] getContent() {
        return data_.content_;
    }

    public String getFilename() {
        return data_.filename_;
    }
}
