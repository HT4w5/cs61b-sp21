package gitlet;

import java.io.File;
import java.io.Serializable;
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

    // Constructor
    private Index() {
    }

    // Public methods
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

    // Private members
    private TreeMap<String, String> indexMap_;
}
