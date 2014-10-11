import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
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
            while (Main.run) {
                Thread.sleep(100);
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                node.socket.receive(receivePacket);
                String received = new String(receiveData);
                if (received.substring(0, Node.commandLength).equals(Node.child)) {
                    InetAddress address = receivePacket.getAddress();
                    int port = receivePacket.getPort();
                    Node child = new RemoteNode(address, port, node.socket);
                    node.addChild(child);
                } else if (received.substring(0, Node.commandLength).equals(Node.parent)) {
                    String message[] = received.split(" ");
                    InetAddress address = InetAddress.getByName(message[1]);
                    int port = Integer.parseInt(message[2].trim());
                    Node parent = new RemoteNode(address, port, node.socket);
                    node.addParent(parent);
                } else if (received.substring(0, Node.commandLength).equals(Node.removeParent)) {
                    node.removeParent();
                } else if (received.substring(0, Node.commandLength).equals(Node.removeChild)) {
                    InetAddress address = receivePacket.getAddress();
                    int port = receivePacket.getPort();
                    Node child = new RemoteNode(address, port, node.socket);
                    node.removeChild(child);
                } else if (received.substring(0, Node.commandLength).equals(Node.message)) {
                    String message = received.substring(Node.message.length() + 1);
                    InetAddress address = receivePacket.getAddress();
                    int port = receivePacket.getPort();
                    Node sender = new RemoteNode(address, port, node.socket);
                    if (node.parentNode != null) {
                        if (!sender.equals(node.parentNode)) {
                            node.parentNode.sendMessage(message);
                        }
                    }
                    for (Node child: node.children) {
                        if (!sender.equals(child)) {
                            child.sendMessage(message);
                        }
                    }
                    System.out.println(Main.hr);
                    System.out.println(message);
                    System.out.println(Main.hr);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
