package gitlet;

import java.util.ArrayList;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Nicole Ni and Ina Nierotka
 */
public class Main {
    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> etc.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        if (wrongNumOfCommands(args)) {
            System.out.println("Incorrect operands.");
            return;
        }
        if (needRepo(args[0])) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        switch (args[0]) {
            default:
                System.out.println("No command with that name exists.");
                break;
            case "init":
                Repository.init();
                break;
            case "add":
                Repository.add(args[1]);
                break;
            case "commit":
                Repository.commit(args[1], null);
                break;
            case "checkout":
                checkoutHelper(args);
                break;
            case "log":
                Repository.log();
                break;
            case "global-log":
                Repository.globalLog();
                break;
            case "branch":
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                Repository.rmBranch(args[1]);
                break;
            case "find":
                Repository.find(args[1]);
                break;
            case "rm":
                Repository.remove(args[1]);
                break;
            case "status":
                Repository.status();
                break;
            case "reset":
                Repository.reset(args[1]);
                break;
            case "merge":
                Repository.merge(args[1]);
                break;
        }
    }

    /**Heper method that deals with call the failure cases,
     * specifically incorrect number of commands.
     * @param args is the command lines.
     * @return return true if number of commands is wrong.
     */
    private static boolean wrongNumOfCommands(String[] args) {
        ArrayList<String> oneArg = oneArgCreator();
        ArrayList<String> twoArgs = twoArgCreator();
        if (oneArg.contains(args[0]) && args.length != 1
                || twoArgs.contains(args[0]) && args.length != 2) {
            return true;
        }
        return false;
    }

    /**Return a list of commands that has one argument.
     * @return ArrayList<String> oneArg.
     */
    private static ArrayList<String> oneArgCreator() {
        ArrayList<String> oneArg = new ArrayList<>();
        oneArg.add("init");
        oneArg.add("log");
        oneArg.add("global-log");
        oneArg.add("status");
        return oneArg;
    }

    /**Return a list of commands that has two arguments.*/
    private static ArrayList<String> twoArgCreator() {
        ArrayList<String> twoArgs = new ArrayList<>();
        twoArgs.add("add");
        twoArgs.add("rm");
        twoArgs.add("find");
        twoArgs.add("branch");
        twoArgs.add("rm-branch");
        twoArgs.add("reset");
        twoArgs.add("merge");
        return twoArgs;
    }

    /**Helper method that deal with the error when need to initialize
     * a repo first.
     * @param firstArg is the first word of the command lines.
     * @return boolean indicating if the command call
     * need to call init first.
     */
    private static boolean needRepo(String firstArg) {
        ArrayList<String> needsGitletRepo = new ArrayList<>();
        needsGitletRepo.add("add");
        needsGitletRepo.add("commit");
        needsGitletRepo.add("rm");
        needsGitletRepo.add("log");
        needsGitletRepo.add("global-log");
        needsGitletRepo.add("find");
        needsGitletRepo.add("status");
        needsGitletRepo.add("checkout");
        needsGitletRepo.add("branch");
        needsGitletRepo.add("rm-branch");
        needsGitletRepo.add("reset");
        needsGitletRepo.add("merge");
        if (needsGitletRepo.contains(firstArg)) {
            if (!Repository.checkGitlet()) {
                return true;
            }
        }
        return false;
    }

    /**Helper method that checks the failure cases with git checkout.
     * @param args is the command line that is passed in.
     */
    private static void checkoutHelper(String[] args) {
        if (args.length == 3) {
            if (args[1].equals("--")) {
                Repository.checkoutFile(args[2]);
            } else {
                System.out.println("Incorrect operands.");
            }
        } else if (args.length == 4) {
            if (args[2].equals("--")) {
                Repository.checkoutFileFromCommit(args[1], args[3]);
            } else {
                System.out.println("Incorrect operands.");
            }
        } else {
            Repository.checkoutBranch(args[1]);
        }
    }

}
