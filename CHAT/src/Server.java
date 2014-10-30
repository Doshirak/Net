import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

class ClientRecord {
    public InetAddress address;
    public int port;
    public String name;


    ClientRecord(InetAddress address, int port, String name) {
        this.address = address;
        this.port = port;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o.getClass() != ClientRecord.class)
            return false;
        ClientRecord a = (ClientRecord) o;
        return (a.address.equals(address) && a.port == port && this.name.equals(name));
    }
}

public class Server {
    private static int bufferSize = 100;

    public static void show (Map<String, ClientRecord> map) {
        System.out.println("---------------");
        for (ClientRecord client: map.values()) {
            System.out.println(client.address + " " + client.port + " " + client.name);
        }
        System.out.println("---------------");
    }

    public static void main(String args[]) {
        try {
            if (args.length != 1) {
                System.out.println("usage: java Server <port>");
                System.exit(1);
            }

            int ownPort = Integer.parseInt(args[0]);
            DatagramSocket socket = new DatagramSocket(ownPort);
            Map<String, ClientRecord> clients = new HashMap<>();

            while (true) {
                byte buffer[] = new byte[bufferSize];
                DatagramPacket receivePacket = new DatagramPacket(buffer, bufferSize);
                socket.receive(receivePacket);

                InetAddress address = receivePacket.getAddress();
                int port = receivePacket.getPort();

                String data = new String(receivePacket.getData());
                String code = new String(receivePacket.getData(), 0, Protocol.codeLength);
                String message = new String(receivePacket.getData(), Protocol.codeLength, receivePacket.getLength() - Protocol.codeLength);

                switch (code) {
                    case Protocol.addClient: {
                        ClientRecord client = new ClientRecord(address, port, message);
                        if (clients.get(message) == null) {
                            StringBuilder list = new StringBuilder(Protocol.clientList);
                            for (ClientRecord receiver: clients.values()) {
                                DatagramPacket sendPacket = new DatagramPacket(data.getBytes(), data.getBytes().length,
                                        receiver.address, receiver.port);
                                socket.send(sendPacket);
                                list.append(receiver.name).append("\n");
                            }
                            clients.put(message, client);
                            message = list.toString();
                            DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length,
                                    client.address, client.port);
                            socket.send(sendPacket);
                            System.out.println("add client: " + address + " " + port + " " );
                            System.out.println(message + " joined chat");
                            show(clients);
                        } else {
                            DatagramPacket sendPacket = new DatagramPacket(Protocol.nameUsed.getBytes(), Protocol.nameUsed.getBytes().length,
                                    client.address, client.port);
                            socket.send(sendPacket);
                        }
                        break;
                    }
                    case Protocol.removeClient: {
                        ClientRecord client = new ClientRecord(address, port, message);
                        if (clients.get(message) != null) {
                            if (clients.get(message).equals(client)){
                                clients.remove(message);
                                for (ClientRecord receiver: clients.values()) {
                                    DatagramPacket sendPacket = new DatagramPacket(data.getBytes(), data.getBytes().length,
                                            receiver.address, receiver.port);
                                    socket.send(sendPacket);
                                }
                                System.out.println("remove client: " + address + " " + port + " " );
                                System.out.println(message + " leave chat");
                                show(clients);
                            }
                        }
                        break;
                    }
                    case Protocol.sendMessage: {
                        String sender = message.split(">")[0].trim();
                        ClientRecord client = clients.get(sender);
                        if (message.contains(":")){
                            String names = message.split(":")[0].trim();
                            String receiver = names.split(">")[1].trim();
                            message = Protocol.sendMessage + sender + ">" + receiver + message.substring(names.length());
                            if (clients.get(sender) != null &&
                                    clients.get(receiver) != null) {
                                System.out.println("sending from " + sender + " to " + receiver + " '" + message + "'");
                                ClientRecord receiverRecord = clients.get(receiver);
                                DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length,
                                        receiverRecord.address, receiverRecord.port);
                                socket.send(sendPacket);
                                break;
                            }
                        }
                        if (client != null) {
                            DatagramPacket sendPacket = new DatagramPacket(Protocol.wrongName.getBytes(), Protocol.wrongName.getBytes().length,
                                    client.address, client.port);
                            socket.send(sendPacket);

                        }
//                        }
                        break;
                    }
                    default:
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
