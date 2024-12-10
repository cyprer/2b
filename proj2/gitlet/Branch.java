package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Repository.HEAD_FILE;
import static gitlet.Utils.*;

/** Represents a branch in a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Branch implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Branch class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /** The name of the branch. */
    private String name;
    /** The ID of the commit that this branch points to. */
    private String commitID;

    /** Initializes a new Branch with the given NAME and COMMIT_ID. */
    public Branch(String name, String commitID) {
        this.name = name;
        this.commitID = commitID;
    }

    /** Returns the name of the branch. */
    public String getName() {
        return name;
    }

    /** Returns the ID of the commit that this branch points to. */
    public String getCommitID() {
        return commitID;
    }

    /** Sets the commit that this branch points to to COMMIT_ID. */
    public void setCommitID(String commitID) {
        this.commitID = commitID;
    }
    public void saveBranch() {
        File branchFile = join(Repository.HEADS_DIR, name);
        writeObject(branchFile, this);
    }

    public static Branch getCurrentBranch(){
        String headRef = Utils.readContentsAsString(HEAD_FILE);
        return Utils.readObject(join(Repository.HEADS_DIR, headRef), Branch.class);
    }

    public static void setCurrentBranch(String branchName) {
        File branchFile = join(Repository.HEADS_DIR, branchName);
        if (!branchFile.exists()) {
            printErrorWithExit("A branch with that name does not exist.");
        }
        writeContents(HEAD_FILE, "refs/heads/" + branchName);
    }


}
