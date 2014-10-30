import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Server {
    static String hr = "---------";
    static int SIZE = 256;

    public static void main (String args[]) {
        try {
            ServerSocket socket = new ServerSocket(0);

            System.out.println(hr);
            System.out.println(socket.getLocalPort());
            System.out.println(hr);

            while(true) {
                Socket client = socket.accept();
                DataInputStream inputStream = new DataInputStream(client.getInputStream());
                DataOutputStream output = new DataOutputStream(client.getOutputStream());


                String fileName = "";
                char c;
                while((c = inputStream.readChar()) != '\n') {
                    fileName += c;
                }

                System.out.println("file name: " + fileName);

                File file = new File("serverFile");
                FileOutputStream stream = new FileOutputStream(file);

                int b;
                while((b = inputStream.read()) != -1) {
                    stream.write(b);
                }

                stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
