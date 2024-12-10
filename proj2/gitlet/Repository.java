package gitlet;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /** 目录结构是这样的:
     * - .gitlet(folder)
     *     - objects(folder)
     *       - commit(file)
     *       - blob(file)
     *     - refs(folder)
     *       - heads(folder)
     *         - master
     *         - other
     *     - HEAD(记录当前commit的id)
     *     - stage(folder)
     *       - addstage(folder)
     *       - rmemovestage(folder)
     */
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    public static final File STAGE_DIR = join(GITLET_DIR, "stage");
    public static final File ADDSTAGE_DIR = join(OBJECTS_DIR, "addstage");
    public static final File REMOVESTAGE_DIR = join(OBJECTS_DIR, "removestage");
    /* TODO: fill in the rest of this class. */
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();
        STAGE_DIR.mkdir();
        ADDSTAGE_DIR.mkdir();
        REMOVESTAGE_DIR.mkdir();
        // 创建初始提交
        Commit initialCommit = new Commit("initial commit", null, null);
        String initialCommitID = initialCommit.getCommitID();
        File initialCommitFile = join(OBJECTS_DIR, initialCommitID);
        writeObject(initialCommitFile, initialCommit);//这里writeboject方法会自动创建一个文件并写入内容

        // 创建master分支并指向初始提交
        Branch master = new Branch("master", initialCommitID);
        File masterFile = join(HEADS_DIR, "master");
        writeObject(masterFile, master);

        // 更新HEAD文件指向master分支
        writeContents(HEAD_FILE, "refs/heads/master");
    }

    public static void add(String fileName) {
        // 如果文件不存在报错并退出
        File fileToAdd = join(CWD, fileName);
        if (!fileToAdd.exists()) {
            printErrorWithExit("File does not exist.");
        }

        // 读取文件内容
        byte[] fileContent = readContents(fileToAdd);

        File stagedFile = join(ADDSTAGE_DIR, fileName);
        if (stagedFile.exists()) {
            // 文件已经在暂存区中，进一步比较文件内容是否完全一致
            byte[] stagedFileContent = readContents(stagedFile);
            if (Arrays.equals(fileContent, stagedFileContent)) {
                // 文件内容相同，无需再次添加
                printErrorWithExit("File already exists in staging area.");
            }
        }
        //创建新的blob对象储存文件
        Blob blob = new Blob(fileToAdd);
        blob.saveBlob();
        // 文件内容不同或文件不在暂存区中，将文件添加到暂存区
        writeContents(stagedFile, fileContent);
        // 如果文件在移除暂存区，则从移除暂存区删除
        File removeStageFile = join(REMOVESTAGE_DIR, fileName);
        if (removeStageFile.exists()) {
            removeStageFile.delete();
        }
    }

}
