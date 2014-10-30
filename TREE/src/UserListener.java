import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

class UserListener implements Runnable {
    Node node;

    public UserListener(Node node) {
        this.node = node;
    }

    @Override
    public void run() {
        Console console = System.console();
        if (console == null) {
            System.err.println("no console");
        }
        while (!Thread.currentThread().isInterrupted()) {
            String message;
            try {
                message = console.readLine();
                node.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}