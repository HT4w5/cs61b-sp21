package gitlet;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author John Doe
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                if (Repository.repoExists()) {
                    System.out.println("A Gitlet version-control system already exists in the " +
                            "current directory.");
                    System.exit(0);
                }
                Repository.init();
                break;
            case "add":
                checkRepoExistence();
                if (args.length != 2) {
                    errorOperandIncorrect();
                }
                Repository.add(args[1]);
                break;
            case "commit":
                checkRepoExistence();
                if (args.length != 2) {
                    errorOperandIncorrect();
                }
                if (args[1].isEmpty()) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                Repository.commit(args[1]);
                break;
            case "rm":
                checkRepoExistence();
                if (args.length != 2) {
                    errorOperandIncorrect();
                }
                Repository.rm(args[1]);
                break;
            case "log":
                checkRepoExistence();
                if (args.length != 1) {
                    errorOperandIncorrect();
                }
                Repository.log();
                break;
            case "global-log":
                checkRepoExistence();
                if (args.length != 1) {
                    errorOperandIncorrect();
                }
                Repository.globalLog();
                break;
            case "find":
                checkRepoExistence();
                if (args.length != 2) {
                    errorOperandIncorrect();
                }
                Repository.find(args[1]);
                break;
            case "checkout":
                checkRepoExistence();
                switch (args.length) {
                    case 2:
                        break;
                    case 3:
                        if (!args[1].equals("--")) {
                            errorOperandIncorrect();
                        }
                        Repository.checkoutFileFromHead(args[2]);
                        break;
                    case 4:
                        if (!args[2].equals("--")) {
                            errorOperandIncorrect();
                        }
                        Repository.checkoutFile(args[1], args[3]);
                        break;
                    default:
                        errorOperandIncorrect();
                }
                break;
            case "status":
                checkRepoExistence();
                if (args.length != 1) {
                    errorOperandIncorrect();
                }
                Repository.status();
                break;
            case "branch":
                checkRepoExistence();
                if (args.length != 2) {
                    errorOperandIncorrect();
                }
                if (args[1].isEmpty()) {
                    errorOperandIncorrect();
                }
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                checkRepoExistence();
                if (args.length != 2) {
                    errorOperandIncorrect();
                }
                if (args[1].isEmpty()) {
                    errorOperandIncorrect();
                }
                Repository.rmBranch(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    public static void checkRepoExistence() {
        if (!Repository.repoExists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    public static void errorOperandIncorrect() {
        System.out.println("Incorrect operands.");
        System.exit(0);
    }
}
