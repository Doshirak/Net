import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class RemoteNode implements Node{
    private InetAddress address;
    private int port;
    private DatagramSocket socket;
    static int TIMEOUT = 100;

    public RemoteNode (InetAddress address, int port, DatagramSocket socket) throws IOException {
        this.address = address;
        if (!address.isReachable(TIMEOUT)) {
            throw new IOException("wrong net address " + address);
        }
        this.port = port;
        this.socket = socket;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) {
            return true;
        }
        if (o.getClass() != LocalNode.class && o.getClass() != RemoteNode.class) {
            return false;
        }
        Node node = (Node)o;
        return (node.getAddress().equals(address) && node.getPort() == port);
    }

    @Override
    public InetAddress getAddress() {
        return address;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void addParent(Node node) throws IOException {
        byte sendData[] = (parent + " " + node.getAddress().getCanonicalHostName() + " " + node.getPort()).getBytes();
        DatagramPacket packet = new DatagramPacket(sendData, 0, sendData.length, address, port);
        socket.send(packet);
    }

    @Override
    public void addChild(Node node) throws IOException {
        byte sendData[] = child.getBytes();
        DatagramPacket packet = new DatagramPacket(sendData, 0, sendData.length, address, port);
        socket.send(packet);
    }

    @Override
    public void removeParent() throws IOException {
        byte sendData[] = (removeParent).getBytes();
        DatagramPacket packet = new DatagramPacket(sendData, 0, sendData.length, address, port);
        socket.send(packet);
    }

    @Override
    public void removeChild(Node node) throws IOException {
        byte sendData[] = (removeChild).getBytes();
        DatagramPacket packet = new DatagramPacket(sendData, 0, sendData.length, address, port);
        socket.send(packet);
    }

    @Override
    public void sendMessage(String message) throws IOException {
        byte sendData[] = (Node.message + " " + message).getBytes();
        DatagramPacket packet = new DatagramPacket(sendData, 0, sendData.length, address, port);
        socket.send(packet);
    }
}