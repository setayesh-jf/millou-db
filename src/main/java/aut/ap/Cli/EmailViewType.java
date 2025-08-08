package aut.ap.Cli;


public enum EmailViewType {
    All("A", "All"),
    Unread("U", "Unread"),
    Sent("S", "Sent"),
    Code("C", "Code"),
    Invalid("", "");

    private final String shortCode;
    private final String fullName;

    EmailViewType(String shortCode, String fullName) {
        this.shortCode = shortCode;
        this.fullName = fullName;
    }

    public static EmailViewType fromInput(String input) {
        for (EmailViewType type : values()) {
            if (type.shortCode.equalsIgnoreCase(input) || type.fullName.equalsIgnoreCase(input)) {
                return type;
            }
        }
        return Invalid;
    }
}