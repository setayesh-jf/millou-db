package aut.ap;
import aut.ap.Cli.Commands;
import aut.ap.service.EmailService;
import aut.ap.service.UserService;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService();
        EmailService emailService = new EmailService();
        Commands commands = new Commands(userService, emailService);

        commands.start();
    }
}