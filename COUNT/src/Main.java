import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class Main {
    private static final String HELLO = "IBORN";
    private static final String LIVE = "ILIVE";
    private static final String DEAD = "IDEAD";

    public static void main(String args[]) {
        try {

            if (3 != args.length) {
                System.err.println("usage: java Main [net] [port] [name]");
                System.exit(1);
            }

            final InetAddress IPAddress = InetAddress.getByName(args[0]);

            byte[] address = IPAddress.getAddress();
            if (address[address.length - 1] != 0) {
                System.err.println("first argument must be network address");
                System.exit(1);
            }
            final int port = Integer.parseInt(args[1]);
            final String name = args[2];
            Set<InetAddress> neighbours = new HashSet<>();
            Set<String> names = new HashSet<>();
            names.add(name);

            final DatagramSocket socket = new DatagramSocket(port);
            socket.setBroadcast(true);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    String msg = DEAD + " " + name;
                    DatagramPacket sendPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                            IPAddress, port);
                    try {
                        socket.send(sendPacket);
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
            });

            byte[] buf = (HELLO + " " + name).getBytes();
            DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, IPAddress, port);
            socket.send(sendPacket);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            while (true) {
                socket.receive(receivePacket);
                String received = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());

                System.out.println("receive: " + receivePacket.getAddress() + " " + received);
                String receivedName;
                try {
                    receivedName = received.substring((HELLO + " ").length());
                } catch (StringIndexOutOfBoundsException e) {
                    receivedName = "";
                }
                if (received.contains(HELLO)) {
                    if (!names.contains(receivedName)) {
                        neighbours.add(receivePacket.getAddress());
                        names.add(receivedName);
                        System.out.println("number of neighbours: " + neighbours.size());
                        buf = (LIVE + " " + name).getBytes();
                        sendPacket = new DatagramPacket(buf, buf.length, IPAddress, port);
                        socket.send(sendPacket);
                    }
                }
                if (received.contains(LIVE)) {
                    if (!names.contains(receivedName)) {
                        neighbours.add(receivePacket.getAddress());
                        names.add(receivedName);
                        System.out.println("number of neighbours: " + neighbours.size());
                    }
                }
                if (received.contains(DEAD)) {
                    if (!names.contains(receivedName)) {
                        neighbours.remove(receivePacket.getAddress());
                        names.remove(receivedName);
                        System.out.println("number of neighbours: " + neighbours.size());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
