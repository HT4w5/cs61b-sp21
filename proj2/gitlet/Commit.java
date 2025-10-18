package gitlet;


import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;
import java.util.TreeMap;
import java.io.ByteArrayOutputStream;

/**
 * Represents a gitlet commit object.
 *
 * @author John Doe
 */
public class Commit extends GitletObject<Commit.Data> {
    // Constructors

    /**
     * Read commit from filesystem by SHA-1 hash
     *
     * @param sha1 SHA-1 hash
     */
    private Commit(String sha1) {
        super(sha1);
    }

    /**
     * Create new Commit
     */
    private Commit() {
        super();
    }

    // Public methods
    public void setParent(String parent, String altParent) {
        data_.parent_ = parent;
        data_.altParent_ = altParent;
    }

    public void setParent(String parent) {
        data_.parent_ = parent;
    }

    public void setMessage(String msg) {
        data_.msg_ = msg;
    }

    // Static
    public static class Data implements Serializable, ToBytesConvertible {
        public Instant timestamp_;
        public String msg_;
        /* maps filename to blob hash */
        public TreeMap<String, String> files_;
        public String parent_;
        public String altParent_;

        public Data() {
            files_ = new TreeMap<>();
        }

        @Override
        public byte[] toByteArray() {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            try {
                result.write(timestamp_.toString().getBytes(StandardCharsets.UTF_8));
                result.write(msg_.getBytes(StandardCharsets.UTF_8));
                var fileSet = files_.entrySet();
                for (var e : fileSet) {
                    result.write(e.getKey().getBytes(StandardCharsets.UTF_8));
                    result.write(e.getValue().getBytes(StandardCharsets.UTF_8));
                }
                if (parent_ != null) {
                    result.write(parent_.getBytes(StandardCharsets.UTF_8));
                }
                if (altParent_ != null) {
                    result.write(altParent_.getBytes(StandardCharsets.UTF_8));
                }
            } catch (IOException e) {
                System.out.println("Failed to convert Commit.Data into byte array");
                System.exit(0);
            }
            return result.toByteArray();
        }
    }

    public static Commit createEmpty() {
        Commit c = new Commit();
        c.data_ = new Commit.Data();
        c.data_.timestamp_ = Instant.now();
        c.data_.parent_ = null;
        c.data_.altParent_ = null;
        return c;
    }

    public static Commit createFromIndex(Index i) {
        var entries = i.entrySet();
        var c = createEmpty();
        entries.forEach(entry -> {
            c.data_.files_.put(entry.getKey(), entry.getValue());
        });
        return c;
    }

    // Private members
}
