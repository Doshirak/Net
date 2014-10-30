import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class SocketListener implements Runnable {
    private LocalNode node;

    public SocketListener(LocalNode node) {
        this.node = node;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                node.socket.receive(receivePacket);
                String received = new String(receiveData);
                System.out.println(Main.hr);
                System.out.println(received);
                System.out.println(Main.hr);
                InetAddress address;
                int port;
                Node child;
                Node parent;
                String type = received.substring(0, Node.commandLength);
                switch (type) {
                    case Node.child:
                        address = receivePacket.getAddress();
                        port = receivePacket.getPort();
                        child = new RemoteNode(address, port, node.socket);
                        node.addChild(child);
                        break;
                    case Node.parent:
                        String tokens[] = received.split(" ");
                        address = InetAddress.getByName(tokens[1]);
                        port = Integer.parseInt(tokens[2].trim());
                        parent = new RemoteNode(address, port, node.socket);
                        node.addParent(parent);
                        break;
                    case Node.removeParent:
                        node.removeParent();
                        break;
                    case Node.removeChild:
                        address = receivePacket.getAddress();
                        port = receivePacket.getPort();
                        child = new RemoteNode(address, port, node.socket);
                        node.removeChild(child);
                        break;
                    case Node.message:
                        String message = received.substring(Node.message.length() + 1);
                        address = receivePacket.getAddress();
                        port = receivePacket.getPort();
                        Node sender = new RemoteNode(address, port, node.socket);
                        if (node.parentNode != null) {
                            if (!sender.equals(node.parentNode)) {
                                node.parentNode.sendMessage(message);
                            }
                        }
                        for (Node temp: node.children) {
                            if (!sender.equals(temp)) {
                                temp.sendMessage(message);
                            }
                        }
                        System.out.println(Main.hr);
                        System.out.println(message);
                        System.out.println(Main.hr);
                        break;
                    case Node.check:
                        DatagramSocket socket = new DatagramSocket();
                        address = receivePacket.getAddress();
                        port = receivePacket.getPort();
                        byte sendData[] = (Node.answer).getBytes();
                        DatagramPacket packet = new DatagramPacket(sendData, 0, sendData.length, address, port);
                        socket.send(packet);
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
