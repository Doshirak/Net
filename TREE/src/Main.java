import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

class Node {
    static int TIMEOUT = 100;
    private InetAddress address;
    private int port;
    private DatagramSocket socket;
    public Node(InetAddress address, int port) throws IOException
    {
        this.address = address;
        if (!address.isReachable(TIMEOUT)) {
            throw new IOException("wrong net address " + address);
        }
        this.port = port;
    }
    public void send(String msg) {

    }
}

public class Main
{
    private static Node parent;
    private static DatagramSocket socket;
    private static String socketErr = "Socket error occurred: ";
    private static String addressEr = "Net address error occurred: ";

    public static void main(String args[])
    {
        // args check
        if (2 != args.length)
        {
            System.err.println("usage: java Main [net] [port]");
            System.exit(1);
        }
        // init
        InetAddress parentAddress;
        int parentPort;
        try {
            parentAddress = InetAddress.getByName(args[0]);
            parentPort = Integer.parseInt(args[1]);
            parent = new Node(parentAddress, parentPort);
        } catch (IOException e) {
            System.out.println(addressEr + e);
        }
        try
        {
            // init socket
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            // add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread()
            {
                public void run()
                {
                }
            });
            // listening
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            while (true)
            {
            }
        } catch (IOException e)
        {
            System.out.println(socketErr + e);
        }
    }
}
