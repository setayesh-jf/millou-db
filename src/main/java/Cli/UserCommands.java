package Cli;


public enum UserCommands{
    Login("L", "Login"),
    Sign_up("S", "Sign up"),
    Exit("E", "Exit"),
    Send("S", "Send"),
    View("V", "View"),
    Reply("R", "Reply"),
    Forward("F", "Forward"),
    Logout("O", "Logout"),
    Invalid("", "");

    private final String shortCode;
    private final String fullCommand;

    UserCommands(String shortCode, String fullCommand) {
        this.shortCode = shortCode;
        this.fullCommand = fullCommand;
    }

    public static UserCommands fromInput(String input) {
        for (UserCommands command : values()) {
            if (command.shortCode.equalsIgnoreCase(input) || command.fullCommand.equalsIgnoreCase(input)) {
                return command;
            }
        }
        return Invalid;
    }
}