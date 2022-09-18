package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  Repository class includes set up of all storage files in .gitlet
 *  directory, as well as the functionality of all Gitlet functions.
 *  Some helper functions are stored in here as well.
 *  @author Nicole Ni and Ina Nierotka
 */
public class Repository {

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory. In it are three directories for the staging
     * areas and commit history, as well all pointers stored as text files. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * The stageAdd directory. Contains files of blobs stored
     * by each call to add, cleared with each commit. */
    public static final File STAGE_ADD = join(GITLET_DIR, "stageAdd");
    /**
     * The stageDelete directory.
     * Used for the remove command. */
    public static final File STAGE_DEL = join(GITLET_DIR, "stageDelete");
    /**
     * The allCommits directory. Stores each commit as a file.
     * Once a file is added it is never changed. */
    public static final File COMMIT_HST = join(GITLET_DIR, "allCommits");
    /** The blobStorage directory. Holds all blobs once committed. */
    public static final File BLOBS = join(COMMIT_HST, "blobStorage");
    /** The branchStorage directory. Holds all branch pointer files. */
    public static final File BRANCHES = join(GITLET_DIR, "branchStorage");

    /**
     * The Head file. Serves as a pointer to the head branch,
     * text is sha1 code of the commit. */
    public static final File HEAD = join(BRANCHES, "HEAD");
    /**
     * The Main file. Serves as a pointer to the main branch,
     * text is sha1 code of the commit. */
    public static final File MAIN = join(BRANCHES, "main");

    /**
     * Init method takes no parameters and returns void. Used to
     * set up the .gitlet directory within a working directory.
     * Returns and prints error message if a .gitlet directory
     * already exists within the CWD.
     */
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already"
                    + " exists in the current directory.");
            return;
        }
        /*make the gitlet directory folder, create sub-folders
         * of staging areas and storage of commit */
        GITLET_DIR.mkdir();
        STAGE_ADD.mkdir();
        STAGE_DEL.mkdir();
        COMMIT_HST.mkdir();
        BRANCHES.mkdir();
        BLOBS.mkdir();

        /** create the commit object */
        Commit initCommit = new Commit("initial commit", null);

        /** get hash codes, add to hash map */
        String initSHA1 = sha1(serialize(initCommit));

        /** store in allCommits */
        File initCommitStorage = new File(COMMIT_HST, initSHA1);
        try {
            initCommitStorage.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Utils.writeObject(initCommitStorage, initCommit);

        /** adjust pointers */
        try {
            HEAD.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            MAIN.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Utils.writeObject(MAIN, initSHA1);
        Utils.writeObject(HEAD, MAIN);
    }

    /**
     * Checks if the current working directory has a .gitlet directory,
     * Used in main to check for incorrect operations. Takes in nothing
     * and returns a boolean indicating if repository has been initialized.
     */
    public static boolean checkGitlet() {
        return GITLET_DIR.exists();
    }

    /**
     * Adds a file to the staging area, creating a blob of the file
     * from the file name and storing it. Method returns void,
     * and prints error message if no file of the given name exists in the CWD.
     * File does not get added if it is A) unmodified from the most recent
     * commit or B) has already been added since the most recent commit
     * and has not been modified since.
     * @param fileName is a String of the name of the file being added.
     */
    public static void add(String fileName) {
        File file = join(CWD, fileName);
        File inRm = join(STAGE_DEL, fileName);
        if (inRm.exists()) {
            File blobFile = readObject(inRm, File.class);
            Blob toRestore = readObject(blobFile, Blob.class);
            writeContents(file, toRestore.getContent());
            inRm.delete();
            return;
        } else {
            if (!file.exists()) {
                System.out.println("File does not exist.");
                return;
            }
        }

        /** create blob of the file as is */
        Blob newBlob = new Blob(file);
        String blobSHA1 = sha1(serialize(newBlob));

        /** check if blob exists in most recent commit, return if true */
        File headBranch = readObject(HEAD, File.class);
        String headSHA1 = readObject(headBranch, String.class);
        File recentCommitFile = join(COMMIT_HST, headSHA1);
        Commit recentCommit = readObject(recentCommitFile, Commit.class);
        TreeMap<String, File> blobFiles = recentCommit.getBlobs();
        Set keys = blobFiles.keySet();
        for (Iterator i = keys.iterator(); i.hasNext();) {
            String name = (String) i.next();
            File iFile = blobFiles.get(name);
            if (iFile.getName().equals(blobSHA1)) {
                return;
            }
        }


        /** check if blobs in stage area are of same file, rewrite if true */
        for (File i: STAGE_ADD.listFiles()) {
            Blob iBlob = (Blob) readObject(i, Blob.class);
            if (iBlob.getFileName().equals(fileName)) {
                i.delete();
                break;
            }
        }
        /** writes blob to a file in STAGE_ADD */
        File stageFile = join(STAGE_ADD, fileName);
        writeObject(stageFile, newBlob);
    }

    /**
     * Method creates a commit, stores all the blobs in the staging
     * area within it, clears the staging directory,moves the pointers,
     * then stores the commit in the COMMIT_HST folder. Aborts if no
     * files are in the staging area. Case of no message is handled in
     * the Main.java class.
     * @param message This is a string with the log message passed in,
     *                which gets stored within the created commit.
     * @param otherParentSHA1 is null unless the commit is called through
     *                        merge, in which case it is added to the
     *                        created commit object.
     */
    public static void commit(String message, String otherParentSHA1) {
        if (message.length() == 0) {
            System.out.println("Please enter a commit message.");
        }
        /** failure case: STAGE_ADD empty */
        if (otherParentSHA1 == null) {
            if (STAGE_ADD.listFiles().length == 0
                    && STAGE_DEL.listFiles().length == 0) {
                System.out.println("No changes added to the commit.");
                return;
            }
        }
        /** create new commit */
        File headBranch = readObject(HEAD, File.class);
        String headSHA1 = Utils.readObject(headBranch, String.class);
        Commit newCommit = new Commit(message, headSHA1);
        newCommit.setSecondParentHash(otherParentSHA1);
        String commitSha1 = sha1(serialize(newCommit));

        /** add files from staging area to commit,
         * then clear the staging directory */
        for (File i: STAGE_ADD.listFiles()) {
            Blob iBlob = readObject(i, Blob.class);
            String iBlobSHA1 = sha1(serialize(iBlob));
            File f = join(BLOBS, iBlobSHA1);
            writeObject(f, iBlob);
            newCommit.addBlob(i.getName(), f);
            i.delete();
        }
        /** add tracked files */
        File prevCommitFile = join(COMMIT_HST, headSHA1);
        Commit prevCommit = readObject(prevCommitFile, Commit.class);
        TreeMap<String, File> prevBlobs = prevCommit.getBlobs();
        Set keys = prevBlobs.keySet();
        for (Iterator i = keys.iterator(); i.hasNext();) {
            String fileName = (String) i.next();
            if (!newCommit.getBlobs().containsKey(fileName)) {
                File rmTracker = join(STAGE_DEL, fileName);
                if (!rmTracker.exists()) {
                    File trackedBlob = prevBlobs.get(fileName);
                    newCommit.addBlob(fileName, trackedBlob);
                } else {
                    rmTracker.delete();
                }
            }
        }
        for (File i: STAGE_DEL.listFiles()) {
            i.delete();
        }
        /** rewrite pointers */
        writeObject(headBranch, commitSha1);

        /** store to commit history directory in .gitlet */
        File commitStorage = new File(COMMIT_HST, commitSha1);
        try {
            commitStorage.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Utils.writeObject(commitStorage, newCommit);
    }

    /**
     * Method takes in a file name and rewrites the file in the
     * CWD to be exactly how it was in the most recent commit.
     * Prints error message if file does not exist in the most
     * recent commit. Aborts if no change to the file has been made
     * since the most recent commit. Returns void.
     * @param file This is a string of the name of the file to check out.
     */
    public static void checkoutFile(String file) {
        /** create filepath to file in CWD */
        File toRewrite = join(CWD, file);
        String cwdSHA1 = sha1(serialize(toRewrite));
        /**
         * create filepath to most recent commit (HEAD),
         * pulls list of all blobs within it */
        File headBranch = readObject(HEAD, File.class);
        String headSHA1 = readObject(headBranch, String.class);
        File recentCommitFile = new File(COMMIT_HST, headSHA1);
        Commit recentCommit = readObject(recentCommitFile, Commit.class);
        TreeMap<String, File> commitBlobs = recentCommit.getBlobs();

        Set keys = commitBlobs.keySet();

        for (Iterator i = keys.iterator(); i.hasNext();) {
            String name = (String) i.next();
            File b = commitBlobs.get(name);
            Blob blob = readObject(b, Blob.class);
            if (blob.getFileName().equals(file)) {
                if (blob.getContentSHA1().equals(cwdSHA1)) {
                    return;
                }
                writeContents(toRewrite, blob.getContent());
                return;
            }
        }
        System.out.println("File does not exist in that commit.");
    }

    /**
     * Method takes in a file name and commit id. Rewrites the
     * file in the CWD to be exactly how it was in the given commit.
     * Prints error message if file does not exist in the most recent
     * commit OR if no commit with the given id exists. Aborts if no
     * change to the file has been made since that commit. Returns void.
     * @param file This is a string of the name of the file to check out.
     * @param commitID This is a string of the commit id to check out from.
     */
    public static void checkoutFileFromCommit(String commitID, String file) {
        /** create filepath to file in the CWD */
        File toRewrite = join(CWD, file);
        String cwdSHA1 = sha1(serialize(toRewrite));

        if (commitID.length() < 10) {
            for (File f: COMMIT_HST.listFiles()) {
                String prevCommitID = f.getName();
                if (prevCommitID.startsWith(commitID)) {
                    commitID = prevCommitID;
                }
            }
        }
        File commitFile = new File(COMMIT_HST, commitID);

        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        /** write commit & pull list of files */
        Commit commit = readObject(commitFile, Commit.class);
        TreeMap<String, File> commitBlobs = commit.getBlobs();

        Set keys = commitBlobs.keySet();

        for (Iterator i = keys.iterator(); i.hasNext();) {
            String name = (String) i.next();
            File b = commitBlobs.get(name);
            Blob blob = readObject(b, Blob.class);
            if (blob.getFileName().equals(file)) {
                if (blob.getContentSHA1().equals(cwdSHA1)) {
                    return;
                }
                writeContents(toRewrite, blob.getContent());
                return;
            }
        }
        System.out.println("File does not exist in that commit.");
    }

    /**
     * Method takes in a branch name and rewrites files in the CWD
     * to match the commit at the head of the given branch. Method aborts
     * and prints error messages if A) there is no branch with the given
     * name B) the given name is the current branch and C) there is an
     * untracked file that is in the commit being checked out. Deletes
     * any tracked files not in the checked out commit, and clears the
     * staging area. Returns void.
     * @param branchName This is the name of the branch we want to check
     *                   out from.
     */
    public static void checkoutBranch(String branchName) {
        File branch = join(BRANCHES, branchName);
        if (!branch.exists()) {
            System.out.println("No such branch exists.");
            return;
        }
        String branchCommitName = readObject(branch, String.class);
        File headBranch = readObject(HEAD, File.class);
        String currentCommitName = readObject(headBranch, String.class);
        if (branchName.equals(headBranch.getName())) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        File branchCommitFile = join(COMMIT_HST, branchCommitName);
        Commit branchCommit = readObject(branchCommitFile, Commit.class);
        TreeMap<String, File> checkoutBlobs = branchCommit.getBlobs();

        File currCommitFile = join(COMMIT_HST, currentCommitName);
        Commit currCommit = readObject(currCommitFile, Commit.class);
        TreeMap<String, File> trackedBlobs = currCommit.getBlobs();

        Set checkoutNames = checkoutBlobs.keySet();
        Set trackedNames = trackedBlobs.keySet();
        for (Iterator i = checkoutNames.iterator(); i.hasNext();) {
            String checkoutName = (String) i.next();
            if (!trackedBlobs.containsKey(checkoutName)) {
                File fileInCWD = join(CWD, checkoutName);
                if (fileInCWD.exists()) {
                    System.out.println("There is an untracked file in the way;"
                            + " delete it, or add and commit it first.");
                    return;
                }
            }
        }


        for (Iterator i = trackedNames.iterator(); i.hasNext();) {
            String name = (String) i.next();
            if (!checkoutBlobs.containsKey(name)) {
                File toDelete = join(CWD, name);
                toDelete.delete();
            }
        }
        Set checkoutFileNames = checkoutBlobs.keySet();
        for (Iterator j = checkoutFileNames.iterator(); j.hasNext();) {
            String name = (String) j.next();
            File blobFile = checkoutBlobs.get(name);
            File toRewrite = join(CWD, name);
            Blob blob = readObject(blobFile, Blob.class);
            writeContents(toRewrite, blob.getContent());
        }
        for (File file: STAGE_ADD.listFiles()) {
            file.delete();
        }
        writeObject(HEAD, branch);
    }

    /**
     * Method prints information about the head commit, then each parent
     * down the branch. Takes in no parameters and returns void.
     */
    public static void log() {
        File headBranch = readObject(HEAD, File.class);
        String headValue = readObject(headBranch, String.class);
        File first = new File(COMMIT_HST, headValue);
        logPrinter(first);
    }

    /**
     * Helper method for log. Takes in the file of the head commit,
     * then prints the information about the commit, then checks the
     * parent and recursively calls the helper function on the file
     * of the parent commit. Returns void.
     * @param first This is a file of a commit, stored within
     *              the COMMIT_HST directory in .gitlet.
     */
    public static void logPrinter(File first) {
        String commitID = first.getName();
        Commit current = readObject(first, Commit.class);
        System.out.println("===");
        System.out.println("commit " + commitID);
        System.out.println("Date: "
                + Commit.getSimpleDateFormat().format(current.getTimeStamp()));
        System.out.println(current.getMessage());
        System.out.println();
        if (current.getParentHash() != null) {
            File parentFile = new File(
                    Repository.COMMIT_HST, current.getParentHash());
            logPrinter(parentFile);
        }
    }

    /**
     * Method prints information about every commit, in any order.
     * Takes in no parameters, returns void.
     */
    public static void globalLog() {
        List<String> commitSha1s = plainFilenamesIn(COMMIT_HST);
        for (String commitSha1: commitSha1s) {
            File currCommit = new File(COMMIT_HST, commitSha1);
            Commit curr = readObject(currCommit, Commit.class);
            System.out.println("===");
            System.out.println("commit " + commitSha1);
            System.out.println("Date: "
                    + Commit.getSimpleDateFormat().format(curr.getTimeStamp()));
            System.out.println(curr.getMessage());
            System.out.println();
        }
    }

    /** Method prints commit ID's with the same message as a given string,
     * returns void.
     * @param message This string is the message each commit is compared to.
     */
    public static void find(String message) {
        boolean foundAny = false;
        for (String commitFileName: plainFilenamesIn(COMMIT_HST)) {
            File commitFile = join(COMMIT_HST, commitFileName);
            Commit commit = readObject(commitFile, Commit.class);
            if (commit.getMessage().equals(message)) {
                System.out.println(commitFile.getName());
                foundAny = true;
            }
        }
        if (!foundAny) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * Method creates a new branch, pointing to the current head.
     * Returns void. This method does not change which branch user is
     * working on. Prints error message if branch with given name exists.
     * @param branchName This string is the name of what we want to
     *                   call the new branch.
     */
    public static void branch(String branchName) {
        File newBranch = join(BRANCHES, branchName);
        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        try {
            newBranch.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File headBranch = readObject(HEAD, File.class);
        writeContents(newBranch, readContents(headBranch));
    }

    /**
     * Method deletes the branch with the given name. Any commits
     * under the branch should not be touched. Aborts & prints error
     * messages if A) no branch with the given name exists and B)
     * the branch name passed in is the name of the current branch.
     * @param branchName This is the name of the branch we want to delete.
     */
    public static void rmBranch(String branchName) {
        File newBranch = join(BRANCHES, branchName);
        if (!newBranch.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        File headBranchFile = readObject(HEAD, File.class);
        String currBranch = readObject(headBranchFile, String.class);
        if (branchName.equals(headBranchFile.getName())) {
            System.out.println("Cannot remove the current branch.");
        }
        newBranch.delete();
    }

    /**
     * Method takes in a file name. If the file is staged for addition,
     * it is removed from the stage add file. If it is tracked in the current
     * commit, it is removed from the current working directory, no longer
     * gets tracked, and added in the stage for removal file. Otherwise,
     * an error message is printed.
     * @param fileName This is a string of the name of the file to remove.
     */
    public static void remove(String fileName) {
        File toRemove = join(STAGE_ADD, fileName);
        if (toRemove.exists()) {
            toRemove.delete();
            return;
        }
        File headBranch = readObject(HEAD, File.class);
        String headSHA1 = readObject(headBranch, String.class);
        File recentCommitFile = join(COMMIT_HST, headSHA1);
        Commit currCommit = readObject(recentCommitFile, Commit.class);
        TreeMap<String, File> trackedBlobs = currCommit.getBlobs();

        if (trackedBlobs.containsKey(fileName)) {
            File rm = join(STAGE_DEL, fileName);
            writeObject(rm, trackedBlobs.get(fileName));

            File toDelete = join(CWD, fileName);
            toDelete.delete();
        } else {
            System.out.println("No reason to remove the file.");
        }

    }

    /**
     * Method prints information about all the branches, what files
     * are staged for addition, removal, which tracked files have been
     * modified and not staged for commit (changes in CWD of files not in
     * previous folders), and then any other non-tracked file.
     * No files are printed if there has been no changes since the most
     * recent commit. Takes in no parameters and returns void.
     */
    public static void status() {
        File headBranch = readObject(HEAD, File.class);
        String headBranchName = headBranch.getName();
        System.out.println("=== Branches ===");
        System.out.println("*" + headBranchName);
        for (String name : plainFilenamesIn(BRANCHES)) {
            if (name.equals("HEAD")) {
                continue;
            }
            if (name.equals(headBranchName)) {
                continue;
            }
            System.out.println(name);
        }
        ArrayList<ArrayList<String>> helper = statusHelper();

        System.out.println();
        System.out.println("=== Staged Files ===");
        for (String n: helper.get(0)) {
            System.out.println(n);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String n: helper.get(1)) {
            System.out.println(n);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String n: helper.get(2)) {
            System.out.println(n + " (modified)");
        }
        for (String n: helper.get(3)) {
            System.out.println(n + " (deleted)");
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        for (String n: helper.get(4)) {
            System.out.println(n);
        }
        System.out.println();
    }

    /**
     * Helper method to status, finds what file names get printed
     * in each section of the status. Takes in no parameters.
     * @return an array list of five array lists of strings. One
     * for staged, one for removed, two for modified (one for changed
     * and one for deleted), and one for untracked.
     */
    public static ArrayList<ArrayList<String>> statusHelper() {
        ArrayList<String> staged = new ArrayList<>();
        ArrayList<String> removed = new ArrayList<>();
        ArrayList<String> modified = new ArrayList<>();
        ArrayList<String> deleted = new ArrayList<>();
        ArrayList<String> untracked = new ArrayList<>();
        ArrayList<String> inCommitUnchanged = new ArrayList<>();
        for (String fName : plainFilenamesIn(STAGE_ADD)) {
            File f = join(STAGE_ADD, fName);
            Blob b = readObject(f, Blob.class);
            String bName = b.getFileName();
            staged.add(bName);
        }
        for (String fName : plainFilenamesIn(STAGE_DEL)) {
            File f = join(STAGE_DEL, fName);
            removed.add(f.getName());
        }
        File currBranch = readObject(HEAD, File.class);
        String commitSHA1 = readObject(currBranch, String.class);
        File currCommitFile = join(COMMIT_HST, commitSHA1);
        Commit currCommit = readObject(currCommitFile, Commit.class);
        TreeMap<String, File> blobs = currCommit.getBlobs();
        Set blobNames = blobs.keySet();
        for (Iterator i = blobNames.iterator(); i.hasNext(); ) {
            String blobID = (String) i.next();
            File blobFile = blobs.get(blobID);
            Blob b = readObject(blobFile, Blob.class);
            String fileName = b.getFileName();
            if (staged.contains(fileName) || removed.contains(fileName)) {
                continue;
            }
            File inCWD = join(CWD, fileName);
            if (!inCWD.exists()) {
                deleted.add(fileName);
                continue;
            }
            byte[] currContent = readContents(inCWD);
            String currSHA1 = sha1(currContent);
            if (!currSHA1.equals(b.getContentSHA1())) {
                modified.add(fileName);
            }
            inCommitUnchanged.add(fileName);
        }
        for (String fName : plainFilenamesIn(CWD)) {
            if (!modified.contains(fName) && !staged.contains(fName)
                    && !removed.contains(fName)
                    && !inCommitUnchanged.contains(fName)) {
                untracked.add(fName);
            }
        }
        ArrayList<ArrayList<String>> toReturn = new ArrayList<>();
        toReturn.add(staged);
        toReturn.add(removed);
        toReturn.add(modified);
        toReturn.add(deleted);
        toReturn.add(untracked);
        return toReturn;
    }

    /**
     * Takes in a commit ID and moves current branch head to that ID,
     * as well as checking out the entire commit. Returns void.
     * @param commitID This string is either the full or abbreviated
     *                 commit ID to check out.
     */
    public static void reset(String commitID) {
        if (commitID.length() < 10) {
            for (File f: COMMIT_HST.listFiles()) {
                String prevCommitID = f.getName();
                if (prevCommitID.startsWith(commitID)) {
                    commitID = prevCommitID;
                }
            }
        }
        File commitFile = new File(COMMIT_HST, commitID);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        File currBranch = readObject(HEAD, File.class);
        File tempBranch = join(BRANCHES, "temp");
        writeObject(tempBranch, commitID);
        checkoutBranch(tempBranch.getName());
        tempBranch.delete();
        writeObject(currBranch, commitID);
        writeObject(HEAD, currBranch);
    }

    /** Main method of merge,this part checks for fail cases
     * and call mergePart2 to find the common ancestor.
     * @param otherBranch is the given branch to merge with. */
    public static void merge(String otherBranch) {
        if (STAGE_ADD.listFiles().length != 0
                || STAGE_DEL.listFiles().length != 0) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        File otherBranchFile = join(BRANCHES, otherBranch);
        if (!otherBranchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        File currBranchFile = readObject(HEAD, File.class);
        if (currBranchFile.getName().equals(otherBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        String otherCommitID = readObject(otherBranchFile, String.class);
        File otherCommitFile = join(COMMIT_HST, otherCommitID);
        Commit otherCommit = readObject(otherCommitFile, Commit.class);
        TreeMap<String, File> otherBlobs = otherCommit.getBlobs();
        String currCommitID = readObject(currBranchFile, String.class);
        File currCommitFile = join(COMMIT_HST, currCommitID);
        Commit currCommit = readObject(currCommitFile, Commit.class);
        TreeMap<String, File> currBlobs = currCommit.getBlobs();
        Set otherNames = otherBlobs.keySet();
        for (Iterator i = otherNames.iterator(); i.hasNext(); ) {
            String fileName = (String) i.next();
            if (!currBlobs.containsKey(fileName)) {
                File fileInCWD = join(CWD, fileName);
                if (fileInCWD.exists()) {
                    System.out.println("There is an untracked file in the way;"
                            + " delete it, or add and commit it first.");
                    return;
                }
            }
        }
        mergePart2(currBranchFile.getName(), otherBranch);
    }

    /** Merge helper: this part finds the parent.
     * @param otherBranch is the given branch
     * @param currBranch is the current branch. */
    public static void mergePart2(String currBranch, String otherBranch) {
        File currBranchFile = join(BRANCHES, currBranch);
        String currCommitID = readObject(currBranchFile, String.class);
        File currCommitFile = join(COMMIT_HST, currCommitID);
        Commit currCommit = readObject(currCommitFile, Commit.class);

        File otherBranchFile = join(BRANCHES, otherBranch);
        String otherCommitID = readObject(otherBranchFile, String.class);
        File otherCommitFile = join(COMMIT_HST, otherCommitID);
        Commit otherCommit = readObject(otherCommitFile, Commit.class);

        ArrayList<String> currParents = new ArrayList<String>();
        ArrayList<String> otherParents = new ArrayList<String>();
        currParents.add(currCommitID);
        otherParents.add(otherCommitID);

        Commit currPointer = currCommit;
        while (!currPointer.getMessage().equals("initial commit")) {
            File parentCommitFile = join(COMMIT_HST,
                    currPointer.getParentHash());
            currParents.add(currPointer.getParentHash());
            if (currPointer.getSecondParentHash() != null) {
                currParents.add(currPointer.getSecondParentHash());
            }
            currPointer = readObject(parentCommitFile, Commit.class);
        }
        Commit otherPointer = otherCommit;
        while (!otherPointer.getMessage().equals("initial commit")) {
            File parentCommitFile = join(COMMIT_HST,
                    otherPointer.getParentHash());
            otherParents.add(otherPointer.getParentHash());
            if (otherPointer.getSecondParentHash() != null) {
                otherParents.add(otherPointer.getSecondParentHash());
            }
            otherPointer = readObject(parentCommitFile, Commit.class);
        }
        Commit parentCommit = null;
        for (String n: currParents) {
            if (otherParents.contains(n)) {
                File parentCommitFile = join(COMMIT_HST, n);
                Commit otherParentCommit = readObject(parentCommitFile, Commit.class);
                if (parentCommit != null) {
                    int comparison = parentCommit.getTimeStamp().compareTo(
                            otherParentCommit.getTimeStamp());
                    if (comparison > 0) {
                        continue;
                    }
                }
                parentCommit = otherParentCommit;
            }
        }
        if (parentCommit.equals(otherCommit)) {
            System.out.println("Given branch is an "
                    +
                    "ancestor of the current branch.");
            return;
        }
        if (parentCommit.equals(currCommit)) {
            checkoutBranch(otherBranch);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        mergePart3(parentCommit, currCommit, otherCommit);
        boolean mergeConflict = mergePart3(parentCommit,
                currCommit, otherCommit);
        String commitMessage = "Merged " + otherBranch
                + " into " + currBranch + ".";
        commit(commitMessage, otherCommitID);
        File newHeadBranchFile = readObject(HEAD, File.class);
        String newCommitID = readObject(newHeadBranchFile, String.class);
        File newCommitFile = join(COMMIT_HST, newCommitID);
        Commit newCommit = readObject(newCommitFile, Commit.class);
        newCommit.setSecondParentHash(otherCommitID);
        if (mergeConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /** merge helper: this part gets all versions of the files.
     * @param parent is the split point commit
     * @param current is the commit we are currently on
     * @param other is the commit from the otherBranch.
     * @return a boolean that returns the result of calling merge4. */
    public static boolean mergePart3(Commit parent, Commit current,
                                     Commit other) {
        HashMap<String, Blob[]> fileVersions = new HashMap<>();
        TreeMap<String, File> parentBlobs = parent.getBlobs();
        TreeMap<String, File> currBlobs = current.getBlobs();
        TreeMap<String, File> otherBlobs = other.getBlobs();
        Set<String> parentFiles = parentBlobs.keySet();
        for (Iterator i = parentFiles.iterator(); i.hasNext();) {
            String fileName = (String) i.next();
            File pBlobFile = parentBlobs.get(fileName);
            Blob pBlob = readObject(pBlobFile, Blob.class);
            Blob[] blobs = {pBlob, null, null};
            fileVersions.put(fileName, blobs);
        }
        Set<String> currFiles = currBlobs.keySet();
        for (Iterator i = currFiles.iterator(); i.hasNext();) {
            String fileName = (String) i.next();
            File cBlobFile = currBlobs.get(fileName);
            Blob cBlob = readObject(cBlobFile, Blob.class);
            if (fileVersions.containsKey(fileName)) {
                Blob[] blobs = fileVersions.get(fileName);
                blobs[1] = cBlob;
            } else {
                Blob[] blobs = {null, cBlob, null};
                fileVersions.put(fileName, blobs);
            }
        }
        Set<String> otherFiles = otherBlobs.keySet();
        for (Iterator i = otherFiles.iterator(); i.hasNext();) {
            String fileName = (String) i.next();
            File oBlobFile = otherBlobs.get(fileName);
            Blob oBlob = readObject(oBlobFile, Blob.class);
            if (fileVersions.containsKey(fileName)) {
                Blob[] blobs = fileVersions.get(fileName);
                blobs[2] = oBlob;
            } else {
                Blob[] blobs = {null, null, oBlob};
                fileVersions.put(fileName, blobs);
            }
        }
        return mergePart4(fileVersions);
    }

    /** Merge helper: checks all the merging rules and perform merging.
     * @param fileVersions is a hashmap that stores split point, current,
     * and parent blob contents.
     * @return returns a boolean, true if there's a merge conflict. */
    public static boolean mergePart4(HashMap<String, Blob[]> fileVersions) {
        boolean mergeConflict = false;
        Set<String> fileNames = fileVersions.keySet();
        for (Iterator i = fileNames.iterator(); i.hasNext();) {
            String fileName = (String) i.next();
            Blob[] blobs = fileVersions.get(fileName);
            Blob pBlob = blobs[0];
            Blob cBlob = blobs[1];
            Blob oBlob = blobs[2];
            if (pBlob == null) {
                // rule 4
                if (oBlob == null) {
                    continue;
                } else if (cBlob == null) {
                    // rule 5
                    writeAndStage(oBlob);
                    continue;
                }
                // rule 3b if no parents
                if (cBlob.getContentSHA1().equals(oBlob.getContentSHA1())) {
                    mergePart5(fileName, cBlob, oBlob);
                    mergeConflict = true;
                    continue;
                }
            } else {
                if (oBlob != null) {
                    if (cBlob != null) {
                        // rule 2 (parent and other same, head different)
                        if (pBlob.getContentSHA1().equals(
                                oBlob.getContentSHA1())
                                && !cBlob.getContentSHA1().equals(
                                        pBlob.getContentSHA1())) {
                            continue;
                        }
                        // rule 1 (parent and head same)
                        if (pBlob.getContentSHA1().equals(
                                cBlob.getContentSHA1())) {
                            writeAndStage(oBlob);
                            continue;
                        }
                        // rule 3a (other and head are the same, modified or not)
                        if (cBlob.getContentSHA1().equals(
                                oBlob.getContentSHA1())) {
                            continue;
                        }
                        // rule 3b, only reach if all three exist and are not equal
                        mergePart5(fileName, cBlob, oBlob);
                        mergeConflict = true;
                        continue;
                    }
                    // rule 7 (head not existent, parent and other same)
                    if (oBlob.getContentSHA1().equals(pBlob.getContentSHA1())) {
                        continue;
                    }
                } else {
                    // rule 6 (in parent, not in other)
                    if (cBlob != null) {
                        // rule 6 (current and parent same, not in other)
                        if (pBlob.getContentSHA1().equals(
                                cBlob.getContentSHA1())) {
                            remove(fileName);
                        } else {
                            // rule 3b
                            mergePart5(fileName, cBlob, null);
                            mergeConflict = true;
                            continue;
                        }
                    }
                }
            }
        }
        return mergeConflict;
    }

    /**
     * Helper method: writes the passed in blob into the CWD and
     * Stages the file for addition without calling add(). Returns void.
     * @param newBlob This is the blob of the contents we want to rewrite
     *                into the file in the CWD.
     */
    private static void writeAndStage(Blob newBlob) {
        String fileName = newBlob.getFileName();
        File inCWD = join(CWD, fileName);
        writeContents(inCWD, newBlob.getContent());
        Blob toStage = new Blob(inCWD);
        File stageFile = join(STAGE_ADD, fileName);
        writeObject(stageFile, toStage);
    }

    /** Merge helper: deals with merge conflict and
     * print header and divider.
     * @param fileName is the fileName
     * @param cBlob is the current blob
     * @param oBlob is the other blob.*/
    public static void mergePart5(String fileName, Blob cBlob, Blob oBlob) {
        String headline = "<<<<<<< HEAD\n";
        String divider = "=======\n";
        String end = ">>>>>>>\n";
        byte[] currContent = new byte[] {};
        byte[] otherContent = new byte[] {};
        if (cBlob != null) {
            currContent = cBlob.getContent();
        }
        if (oBlob != null) {
            otherContent = oBlob.getContent();
        }
        File inCWD = join(CWD, fileName);
        writeContents(inCWD, headline, currContent, divider, otherContent, end);
        Blob toStage = new Blob(inCWD);
        File stageFile = join(STAGE_ADD, fileName);
        writeObject(stageFile, toStage);
    }
}
