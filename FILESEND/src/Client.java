import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    static int SIZE = 1024;

    public static void main (String args[]) {
        try {
            if (args.length != 3) {
                System.out.println("usage: java Client <address> <port> <filename>");
                System.exit(1);
            }

            InetAddress address = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);
            File file = new File(args[2]);

            Socket socket = new Socket(address, port);
            FileInputStream stream = new FileInputStream(file);

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            output.writeBytes(file.getName() + "\n");

            int b;
            while ((b = stream.read()) != -1) {
                output.write(b);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
