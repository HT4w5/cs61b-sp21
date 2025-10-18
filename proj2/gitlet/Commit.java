package gitlet;


import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
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
        data_ = new Commit.Data();
        data_.timestamp_ = Instant.now();
    }

    // Public methods
    public int numFiles() {
        return data_.files_.size();
    }

    public Set<Map.Entry<String, String>> entrySet() {
        return data_.files_.entrySet();
    }

    public boolean hasFile(String filename) {
        return data_.files_.containsKey(filename);
    }

    public String getFile(String filename) {
        return data_.files_.get(filename);
    }

    public String getDateString() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern(LOG_DATE_PATTERN).withZone(ZoneId.systemDefault());
        return f.format(data_.timestamp_);
    }

    public String toLogEntry() {
        StringBuilder sb = new StringBuilder();
        sb.append("===\n");
        sb.append("commit ");
        sb.append(getSHA1Hash());
        if(data_.altParent_ != null) {
            sb.append("\nMerge: ");
            sb.append(data_.parent_, 0, 7);
            sb.append(" ");
            sb.append(data_.altParent_,0,7);
        }
        sb.append("\nDate: ");
        sb.append(getDateString());
        sb.append("\n");
        sb.append(data_.msg_);
        sb.append("\n\n");
        return sb.toString();
    }

    public String getParent() {
        return data_.parent_;
    }

    public String getMsg() {
        return data_.msg_;
    }

    public String getAltParent() {
        return data_.altParent_;
    }
    // Static
    private static final String LOG_DATE_PATTERN = "EEE MMM d HH:mm:ss yyyy Z";

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

    public static Commit fromIndex(Index i, String msg, String parent, String altParent) {
        var entries = i.entrySet();
        Commit c = new Commit();
        c.data_.msg_ = msg;
        c.data_.parent_ = parent;
        c.data_.altParent_ = altParent;
        entries.forEach(entry -> {
            c.data_.files_.put(entry.getKey(), entry.getValue());
        });
        return c;
    }

    public static Commit fromObjects(String sha1) {
        return new Commit(sha1);
    }

    public static Commit initial() {
        Commit c = new Commit();
        c.data_ = new Commit.Data();
        c.data_.msg_ = "initial commit";
        c.data_.timestamp_ = Instant.EPOCH;
        c.data_.parent_ = null;
        c.data_.altParent_ = null;
        return c;
    }

    // Private members
}
