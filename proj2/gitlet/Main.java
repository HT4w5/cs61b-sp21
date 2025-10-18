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
                Repository.initRepo();
                break;
            case "add":
                checkRepoExistence();
                if (args.length != 2) {
                    errorOperandIncorrect();
                }
                Repository.addFile(args[1]);
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
