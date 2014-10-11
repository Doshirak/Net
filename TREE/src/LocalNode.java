import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

class LocalNode implements Node {
    public DatagramSocket socket;
    public Node parentNode;
    public Set<Node> children = new HashSet<Node>();

    public LocalNode(DatagramSocket socket, Node parentNode) {
        this.socket = socket;
        this.parentNode = parentNode;
    }


    @Override
    public InetAddress getAddress() {
        return null;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public void addParent(Node node) throws IOException {
        parentNode = node;
        parentNode.addChild(this);
        System.out.println("add parent: " + node.getAddress() + " " + node.getPort());
    }

    @Override
    public void addChild(Node node) {
        children.add(node);
        System.out.println("add child: " + node.getAddress() + " " + node.getPort());
    }

    @Override
    public void removeParent() {
        parentNode = null;
        System.out.println("remove parent");

    }

    @Override
    public void removeChild(Node node) {
        children.remove(node);
        System.out.println("remove child: " + node.getAddress() + " " + node.getPort());
    }

    @Override
    public void sendMessage(String message) throws IOException {
        System.out.println(Main.hr);
        System.out.println("message to send: " + message);
        System.out.println(Main.hr);
        if (parentNode != null) {
            parentNode.sendMessage(message);
        }
        for (Node node : children) {
            node.sendMessage(message);
        }
    }
}