package gitlet;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gitlet.Branch.getCurrentBranch;
import static gitlet.Branch.setCurrentBranch;
import static gitlet.Commit.*;
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
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
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
        System.out.println("当前工作目录");
        if (GITLET_DIR.exists()) {
            printErrorWithExit("A Gitlet version-control system already exists in the current directory.");
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

    public static void printCWD() {
        System.out.println("当前工作目录: " + CWD.getPath());
        System.out.println("测试");
    }
    public static void add(String fileName) {
        // 如果文件不存在报错并退出
        File fileToAdd = join(CWD, fileName);
        if (!fileToAdd.exists()) {
            printCWD();
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

    public static void commit(String message) {
        // 读取暂存区中的文件列表
        List<String> stagedFiles = plainFilenamesIn(ADDSTAGE_DIR);
        if (stagedFiles.isEmpty()) {
            printErrorWithExit("No files staged for commit.");
        }
        // 创建blob的映射
        Map<String, String> blobMap = new HashMap<>();
        for (String fileName : stagedFiles) {
            File stagedFile = join(ADDSTAGE_DIR, fileName);
            Blob blob = new Blob(stagedFile);
            blob.saveBlob();
            blobMap.put(fileName, blob.getBlobID());
        }
        // 获取当前分支的提交ID
        String currentBranch = readContentsAsString(HEAD_FILE).split("/")[2];//按照"/"分割成length为3的数组即
        Branch branch = readObject(join(HEADS_DIR, currentBranch), Branch.class);
        String currentCommitID = branch.getCommitID();
        // 创建新的提交对象
        Commit newCommit = new Commit(message, currentCommitID, blobMap);
        String newCommitID = newCommit.getCommitID();
        File newCommitFile = join(OBJECTS_DIR, newCommitID);
        writeObject(newCommitFile, newCommit);
        // 更新当前分支指向新的commit
        branch.setCommitID(newCommitID);
        writeObject(join(HEADS_DIR, currentBranch), branch);
        // 清空暂存区
        clearDirectory(ADDSTAGE_DIR);
    }

    //清空目录文件
    private static void clearDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
    }

    public static void rm(String fileName) {
        File file = join(CWD, fileName);
        File stagedFile = join(ADDSTAGE_DIR, fileName);
        // 检查文件是否被暂存
        if (stagedFile.exists()) {
            // 文件已经被暂存，取消暂存
            stagedFile.delete();
        } else {
            // 检查文件是否被当前提交跟踪
            Commit currentCommit = getCurrentCommit();
            String fileBlobID = currentCommit.getFileToBlobMap().get(fileName);

            if (fileBlobID != null) {
                // 文件被当前提交跟踪，将其暂存以进行删除
                File removeStageFile = join(REMOVESTAGE_DIR, fileName);
                writeContents(removeStageFile, fileBlobID);

                // 从工作目录中删除文件（如果它还存在）
                if (file.exists()) {
                    file.delete();
                }
            } else {
                // 文件既没有被暂存，也没有被当前提交跟踪
                printErrorWithExit("No reason to remove the file.");
            }
        }
    }

    public static void log() {
        String currentCommitID = getCurrentCommitID();
        while (currentCommitID != null) {
            Commit currentCommit = readObject(join(OBJECTS_DIR, currentCommitID), Commit.class);
            System.out.println("===");
            System.out.println("commit " + currentCommitID);
            System.out.println("Date: " + currentCommit.getDate());
            System.out.println(currentCommit.getMessage());
            System.out.println();
            currentCommitID = currentCommit.getParentID();
        }
    }

    public static void globalLog() {
        File[] files = join(OBJECTS_DIR).listFiles();
        for (File file : files) {
            if (file.isFile()) {
                Commit commit = readObject(file, Commit.class);
                System.out.println("===");
                System.out.println("commit " + commit.getCommitID());
                System.out.println("Date: " + commit.getDate());
                System.out.println(commit.getMessage());
                System.out.println();
            }
        }
    }

    public static void find(String message) {
        File[] files = join(OBJECTS_DIR).listFiles();
        for (File file : files) {
            if (file.isFile()) {
                Commit commit = readObject(file, Commit.class);
                if (commit.getMessage().equals(message)) {
                    System.out.println(commit.getCommitID());
                }
            }
        }
    }

    public static void status() {
        System.out.println("=== Branches ===");
        File[] branches = join(HEADS_DIR).listFiles();
        for (File branch : branches) {
            String branchName = branch.getName();
            if (branchName.equals(getCurrentBranch().getName())) {
                System.out.println("*" + branchName);
            } else {
                System.out.println(branchName);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        File[] stagedFiles = join(ADDSTAGE_DIR).listFiles();
        for (File file : stagedFiles) {
            System.out.println(file.getName());
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        File[] removedFiles = join(REMOVESTAGE_DIR).listFiles();
        for (File file : removedFiles) {
            System.out.println(file.getName());
        }
    }

    public static void checkout(String fileName) {
        Commit currentCommit = getCurrentCommit();
        String fileBlobID = currentCommit.getFileToBlobMap().get(fileName);
        File file = join(CWD, fileName);
        if (fileBlobID != null) {
            String fileContent = readContentsAsString(join(OBJECTS_DIR, fileBlobID));
            writeContents(file, fileContent);
        } else {
            printErrorWithExit("File does not exist in that commit.");
        }
    }

    public static void checkout(String commitID, String fileName) {
        //判断commitID是否存在
        if (!join(OBJECTS_DIR, commitID).exists()) {
            printErrorWithExit("No commit with that id exists.");
        }
        Commit commit = readObject(join(OBJECTS_DIR, commitID), Commit.class);
        String fileBlobID = commit.getFileToBlobMap().get(fileName);
        File file = join(CWD, fileName);
        if (fileBlobID != null) {
            String fileContent = readContentsAsString(join(OBJECTS_DIR, fileBlobID));
            writeContents(file, fileContent);
        }else {
            printErrorWithExit("File does not exist in that commit.");
        }
    }
    //切换分支
    public static void checkoutbranch(String branchName) {
        // 检查分支是否存在
        File branchFile = join(HEADS_DIR, branchName);
        if (!branchFile.exists()) {
            printErrorWithExit("No such branch exists.");
        }

        // 检查分支是否为当前分支
        if (branchName.equals(getCurrentBranch().getName())) {
            printErrorWithExit("No need to checkout the current branch.");
        }

        // 检查是否有未跟踪的文件会被覆盖
        Commit commit = readObject(branchFile, Commit.class);
        Map<String, String> fileToBlobMap = commit.getFileToBlobMap();
        for (String fileName : fileToBlobMap.keySet()) {
            File file = join(CWD, fileName);
            if (file.exists() && !isStaged(fileName)) {
                printErrorWithExit("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }

        // 检出分支
        checkoutcommitID(commit.getCommitID());
        setCurrentBranch(branchName);
    }

    public static void checkoutcommitID(String commitID) {
        // 判断commitID是否存在
        if (!join(OBJECTS_DIR, commitID).exists()) {
            printErrorWithExit("No commit with that id exists.");
        }
        Commit commit = readObject(join(OBJECTS_DIR, commitID), Commit.class);
        Map<String, String> fileToBlobMap = commit.getFileToBlobMap();
        for (String fileName : fileToBlobMap.keySet()) {
            File file = join(CWD, fileName);
            if (file.exists() && !isStaged(fileName)) {
                printErrorWithExit("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
        for (String fileName : fileToBlobMap.keySet()) {
            File file = join(CWD, fileName);
            String fileBlobID = fileToBlobMap.get(fileName);
            String fileContent = readContentsAsString(join(OBJECTS_DIR, fileBlobID));
            writeContents(file, fileContent);
        }
        clearDirectory(ADDSTAGE_DIR);
        clearDirectory(REMOVESTAGE_DIR);
    }

    private static boolean isStaged(String fileName) {
        File stagedFile = join(ADDSTAGE_DIR, fileName);
        return stagedFile.exists();
    }

    //使用给定名称创建一个新分支，并将其指向当前的头提交
    public static void branch(String branchName) {
        File branchFile = join(HEADS_DIR, branchName);
        if (branchFile.exists()) {
            printErrorWithExit("A branch with that name already exists.");
        }
        writeContents(branchFile, getCurrentBranch().getCommitID());
        setCurrentBranch(branchName);
    }

    //删除具有给定名称的分支
    public static void rmbranch(String branchName) {
        File branchFile = join(HEADS_DIR, branchName);
        if (!branchFile.exists()) {
            printErrorWithExit("A branch with that name does not exist.");
        }
        if (branchName.equals(getCurrentBranch().getName())) {
            printErrorWithExit("Cannot remove the current branch.");
        }
        branchFile.delete();
    }

    //检查给定提交跟踪的所有文件。删除该提交中不存在的跟踪文件。还将当前分支的头移动到该提交节点
    public static void reset(String commitID) {
        if (!join(OBJECTS_DIR, commitID).exists()) {
            printErrorWithExit("No commit with that id exists.");
        }
        checkoutcommitID(commitID);
        setCurrentBranch(getCurrentBranch().getName());
    }
}
