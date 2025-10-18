package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;

import static gitlet.Utils.join;
import static gitlet.Utils.readObject;

public class Index implements Serializable {
    // Static
    public static final File INDEX = join(Repository.CWD, "index");

    // Constructor
    private Index() {
    }

    // Public methods
    public Index fromFilesystem() {
        if (!INDEX.exists()) {
            throw new GitletException("index file missing");
        }
        return readObject(INDEX, Index.class);
    }

    public Index createEmpty() {
        Index i = new Index();
        i.indexMap_ = new TreeMap<>();
        return i;
    }

    public void putEntry(String filename, String blobHash) {
        indexMap_.put(filename, blobHash);
    }

    public void removeEntry(String filename) {
        indexMap_.remove(filename);
    }

    public Set<Map.Entry<String, String>> entrySet() {
        return indexMap_.entrySet();
    }

    private TreeMap<String, String> indexMap_;
}
