package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.Date;
import gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date date;
    private String parentID;
    private String commitID;
    private Map<String, String> fileToBlobMap; // Map to store filename to blob ID mapping

    /* TODO: fill in the rest of this class. */

    public Commit(String message, String parentID, Map<String, String> fileToBlobMap) {
        this.message = message;
        this.parentID = parentID;
        this.fileToBlobMap = fileToBlobMap;
        this.date = new Date(); // Initialize date with the current date and time
        this.commitID = Utils.sha1(Utils.serialize(this)); // Generate a unique commit ID using serialization
    }

    public String getCommitID() {
        return commitID;
    }

    public String getMessage() {
        return message;
    }

    public String getParentID() {
        return parentID;
    }

    public Map<String, String> getFileToBlobMap() {
        return fileToBlobMap;
    }

    public Date getDate() {
        return date;
    }

    public static Commit readCommit(String commitID) {
        File commitFile = Utils.join(Repository.OBJECTS_DIR, commitID);
        return Utils.readObject(commitFile, Commit.class);
    }

    public static String getCurrentCommitID() {
        return Utils.readContentsAsString(Repository.HEAD_FILE);
    }

    public static Commit getCurrentCommit() {
        String currentCommitID = getCurrentCommitID();
        return readCommit(currentCommitID);
    }


}
