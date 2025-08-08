package aut.ap.Cli;


import aut.ap.model.User;
import aut.ap.service.UserService;
import aut.ap.service.EmailService;

import java.util.*;


public class Commands {
    private final UserService userService;
    private final EmailService emailService;
    private final Scanner scanner = new Scanner(System.in);
    private User currentUser;

    public Commands(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    public void start() {
        while (true) {
            try {
                System.out.println("[L]ogin, [S]ign up, [E]xit:");
                String input = scanner.nextLine().trim();
                UserCommands command = UserCommands.fromInput(input);

                switch (command) {
                    case Login -> login();
                    case Sign_up -> signUp();
                    case Exit -> {
                        System.out.println("Exiting program. Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid command. Try again.");
                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                return;
            }
        }
    }




    private void login() {
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            userService.login(email, password);
            currentUser = userService.findByEmail(email);
            showMainMenu();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }





    private void signUp() {
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            userService.register(name, email, password);
            System.out.println("Your new account is created. Go ahead and login!");
            start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }




    private void showMainMenu() {
        System.out.println("Welcome back, " + currentUser.getName() + "!");
        userService.showUnreadEmails(currentUser);

        while (true) {
            System.out.println("[S]end, [V]iew, [R]eply, [F]orward, [L]Logout:");
            String input = scanner.nextLine().trim();
            UserCommands command = UserCommands.fromInput(input);

            switch (command) {
                case Send -> sendEmail();
                case View -> viewEmails();
                case Reply -> replyToEmail();
                case Forward -> forwardEmail();
                case Logout -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid command. Try again.");
            }
        }
    }





    private void sendEmail() {
        System.out.print("Recipient(s): ");
        String[] recipientEmails = scanner.nextLine().trim().split(",");

        Set<User> recipients = new HashSet<>();
        for (String email : recipientEmails) {
            try {
                User recipient = userService.findByEmail(email.trim());
                recipients.add(recipient);
            } catch (Exception e) {
                System.out.println("Invalid recipient: " + email.trim());
            }
        }




        if (recipients.isEmpty()) {
            System.out.println("No valid recipients found.");
            return;
        }

        System.out.print("Subject: ");
        String subject = scanner.nextLine().trim();
        System.out.print("Body: ");
        String body = scanner.nextLine().trim();

        emailService.sendEmail(currentUser, subject, body, List.copyOf(recipients));
    }




    private void viewEmails() {
        System.out.println("[A]ll emails, [U]nread emails, [S]ent emails, Read by [C]ode");
        String input = scanner.nextLine().trim();
        EmailViewType viewType = EmailViewType.fromInput(input);

        switch (viewType) {
            case Code -> {
                System.out.print("Enter email code: ");
                String code = scanner.nextLine().trim();
                emailService.viewEmailByCode(currentUser, code);
            }
            case All, Unread, Sent -> emailService.viewEmails(currentUser, viewType.name());
            default -> System.out.println("Invalid view option.");
        }
    }




    private void replyToEmail() {
        System.out.print("Code: ");
        String code = scanner.nextLine().trim();
        System.out.print("Body: ");
        String body = scanner.nextLine().trim();

        emailService.replyToEmail(currentUser, code, body);
    }




    private void forwardEmail() {
        System.out.print("Code: ");
        String code = scanner.nextLine().trim();
        System.out.print("Recipient(s): ");
        String[] recipientEmails = scanner.nextLine().trim().split(",");

        Set<User> recipients = new HashSet<>();
        for (String email : recipientEmails) {
            try {
                User recipient = userService.findByEmail(email.trim());
                recipients.add(recipient);
            } catch (Exception e) {
                System.out.println("Invalid recipient: " + email.trim());
            }
        }

        if (recipients.isEmpty()) {
            System.out.println("No valid recipients found.");
            return;
        }

        emailService.forwardEmail(currentUser, code, List.copyOf(recipients));
    }
}
