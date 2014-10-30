import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by rishavant on 06.10.14.
 */
public class MyTree {
    public static void main(String[] args) throws IOException {
        String daddy;
        if (args.length > 1) {
            daddy = args[1];
        } else {
            daddy = "IAMROOT";
        }
        Integer counter = 0;
        Integer port = Integer.parseInt(args[0]);
        DatagramSocket socket = new DatagramSocket(port);
        HashMap<InetAddress, Integer> map = new HashMap<InetAddress, Integer>();
        System.out.println();
        //Keep a socket open to listen to all the UDP traffic that is destined for this port
        if (daddy.equals("IAMROOT")) {
            System.out.println("root");

            byte[] sendData = ("IDAD ").getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), port);
            while (true) {
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);


                //Packet received
                System.out.println(socket.getClass().getName() + ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
                System.out.println(socket.getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));

                //See if the packet holds the right command (message)
                String message = new String(packet.getData()).trim();
                if (message.contains("ISON")) {
                    System.out.println("Received son" + packet.getAddress());
                    map.put(packet.getAddress(), counter);
                    ++counter;
                    sendPacket.setAddress(packet.getAddress());
                    socket.send(sendPacket);
                }
                if (message.contains("IDEAD")) {
                    System.out.println("Received idead" + packet.getAddress());
                    --counter;
                    map.remove(packet.getAddress());
                    System.out.println(packet.getAddress() + "with port: " + packet.getPort() + "is dead");
                }
                if (socket.isClosed()) {
                    DatagramSocket c = new DatagramSocket();
                    sendData = "YOUROOT".getBytes();
                    Iterator<Map.Entry<InetAddress, Integer>> entries = map.entrySet().iterator();
                    Map.Entry<InetAddress, Integer> entry1 = entries.next();
                    InetAddress root = entry1.getKey();

                    while (entries.hasNext()) {
                        Map.Entry<InetAddress, Integer> entry = entries.next();
                        sendData = ("IDEAD " + root).getBytes();
                        InetAddress key = entry.getKey();
                        sendPacket = new DatagramPacket(sendData, sendData.length, key, port);
                        c.send(sendPacket);
                    }
                    socket.close();
                }
            }
        } else {
            DatagramSocket dadsocket = new DatagramSocket(port, InetAddress.getByName(daddy));
            byte[] sendData = ("ISON " + InetAddress.getLocalHost().getHostAddress()).getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), port);
            dadsocket.send(sendPacket);
            sendData = ("IDAD " + InetAddress.getLocalHost().getHostAddress()).getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), port);
            while (true) {
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);
                String message = new String(packet.getData()).trim();
                if (message.contains("ISON")) {
                    System.out.println("Received son" + packet.getAddress());
                    map.put(packet.getAddress(), counter);
                    ++counter;
                    sendPacket.setAddress(packet.getAddress());
                    socket.send(sendPacket);
                }
                if (message.contains("IDEAD")) {
                    System.out.println("Received idead" + packet.getAddress());
                    if (daddy.equals(packet.getAddress())) {
                        System.out.println(packet.getAddress() + "with port: " + packet.getPort() + "is dead");
                        String[] words = message.split(" ");
                        daddy = words[1];
                        DatagramSocket c = new DatagramSocket();
                        sendData = ("ISON").getBytes();
                        sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(daddy), port);
                        c.send(sendPacket);
                    } else {
                        --counter;
                        map.remove(packet.getAddress());
                        System.out.println(packet.getAddress() + "with port: " + packet.getPort() + "is dead");
                    }
                }
                if (message.contains("YOUROOT")) {
                    System.out.println("Received root" + packet.getAddress());
                    System.out.println(packet.getAddress() + "with port: " + packet.getPort() + "is dead");
                }
                if (socket.isClosed()) {
                    DatagramSocket c = new DatagramSocket();
                    sendData = ("IDEAD "+InetAddress.getLocalHost().getHostAddress()).getBytes();
                    System.out.println("Received idead" + packet.getAddress());
                    sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(daddy), port);
                    c.send(sendPacket);

                    Iterator<Map.Entry<InetAddress, Integer>> entries = map.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry<InetAddress, Integer> entry = entries.next();
                        sendData = ("IDEAD " + daddy).getBytes();
                        System.out.println("Received idead" + packet.getAddress());
                        InetAddress key = entry.getKey();
                        sendPacket = new DatagramPacket(sendData, sendData.length, key, port);
                        c.send(sendPacket);
                    }
                    socket.close();
                }

            }
        }
    }
}