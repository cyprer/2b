package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;

import static gitlet.Utils.join;

public class Blob implements Serializable {
    private String blobID;
    private String fileName;
    private byte[] content;

    public Blob(File file) {
        this.fileName = file.getName();
        this.content = Utils.readContents(file);
        this.blobID = Utils.sha1(Utils.serialize(this));
    }

    public String getBlobID() {
        return blobID;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getContent() {
        return content;
    }

    public  void saveBlob() {
        File Blobfile = join(Repository.OBJECTS_DIR,blobID);
        writeObject(Blobfile, this);
    }
}
