package gitlet;

import java.io.File;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;

import static gitlet.Utils.*;
import static gitlet.Utils.readObject;

public class Index {
    // Static
    public static final File INDEX = join(Repository.GITLET_DIR, "index");

    public static Index fromFilesystem() {
        if (!INDEX.exists()) {
            throw new GitletException("index file missing");
        }
        Index i = new Index();
        i.indexMap_ = readObject(INDEX);
        return i;
    }

    public static Index createEmpty() {
        Index i = new Index();
        i.indexMap_ = new TreeMap<>();
        return i;
    }

    public static Index fromCommit(Commit c) {
        var fileSet = c.entrySet();
        Index i = createEmpty();
        for (var e : fileSet) {
            i.indexMap_.put(e.getKey(), e.getValue());
        }
        return i;
    }

    // Constructor
    private Index() {
    }

    // Public methods
    public int numFiles() {
        return indexMap_.size();
    }

    public boolean changed(Commit c) {
        if (indexMap_.size() != c.numFiles()) {
            return true;
        }

        for (var e : indexMap_.entrySet()) {
            String file = c.getFile(e.getKey());
            if(file == null) {
                return true;
            }
            if (!file.equals(e.getValue())) {
                return true;
            }
        }
        return false;
    }

    public void save() {
        writeObject(INDEX, indexMap_);
    }

    public boolean hasFile(String filename) {
        return indexMap_.containsKey(filename);
    }

    public void putFile(String filename, String blobHash) {
        indexMap_.put(filename, blobHash);
    }

    public void removeFile(String filename) {
        indexMap_.remove(filename);
    }

    public String getFile(String filename) {
        return indexMap_.get(filename);
    }

    public Set<Map.Entry<String, String>> entrySet() {
        return indexMap_.entrySet();
    }

    public Set<String> filenameSet() {
        return indexMap_.keySet();
    }

    // Private members
    private TreeMap<String, String> indexMap_;
}
