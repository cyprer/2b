package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args == null || args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                gitlet.Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                gitlet.Repository.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                gitlet.Repository.commit(args[1]);
                break;
            case "rm":
                gitlet.Repository.rm(args[1]);
                break;
            case "log":
                gitlet.Repository.log();
                break;
            case "global-log":
                gitlet.Repository.globalLog();
                break;
            case "find":
                gitlet.Repository.find(args[1]);
                break;
            case "status":
                gitlet.Repository.status();
                break;
            case "checkout":
                if (args.length == 2) {
                    gitlet.Repository.checkoutbranch(args[1]);//java gitlet.Main checkout [branch name]
                }
                else if (args.length == 4 && args[2].equals("--")) {
                    gitlet.Repository.checkout(args[1], args[3]);//java gitlet.Main checkout [commit id] -- [filename]
                }
                else if (args.length == 3 && args[1].equals("--")) {
                    gitlet.Repository.checkout(args[2]);//java gitlet.Main checkout -- [filename]
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }
}
