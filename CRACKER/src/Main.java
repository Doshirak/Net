import java.io.IOException;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

class Chunk {
    public int index;
    public boolean occupied;
    public boolean finished;
    public int client;

    Chunk(int index) {
        this.index = index;
        occupied = false;
        finished = false;
    }
}

class ClientRecord {
    public InetAddress address;
    public int port;

    ClientRecord(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public boolean equals (Object o) {
        if (o == this)
            return true;
        if (o.getClass() != ClientRecord.class)
            return false;
        ClientRecord a = (ClientRecord) o;
        return (a.address == address && a.port == port);
    }
}

public class Main {
    public static char genome[] = {'A', 'C', 'G', 'T'};
    public final static int chunkCount = 1000000;
    public final static int chunkSize = 1000000;
    private static int bufferSize = 100;
    public final static int codeLength = 3;
    public final static String addClient = "ADD";
    public final static String removeClient = "RMV";
    public final static String sendHash = "HSH";
    public final static String stopClient = "END";
    public final static String getChunk = "GET";
    public final static String sendChunk = "SND";
    public final static String finishChunk = "FIN";
    public final static String findKey = "FND";

    public static String toHex(byte hash[]) {
        StringBuilder builder = new StringBuilder();
        for (byte b: hash) {
            builder.append((String.format("%02x", b&0xff)));
        }
        return builder.toString();
    }

    public static byte[] toByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private static void finish(DatagramSocket socket, Set<ClientRecord> clients) throws IOException {
        for (ClientRecord client: clients) {
            DatagramPacket sendPacket = new DatagramPacket(stopClient.getBytes(), stopClient.getBytes().length, client.address, client.port);
            socket.send(sendPacket);
        }
        System.exit(0);
    }

    public static void main(String args[]) {
        try {
            if (args.length < 1) {
                System.out.println("usage: java Main <password>");
                System.exit(1);
            }
            String password = args[0];
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            System.out.println("hash is: " + toHex(hash));

            Chunk chunks[] = new Chunk[chunkCount];
            Stack<Chunk> chunkStack = new Stack<Chunk>();
            for (int i = chunkCount - 1; i >= 0; --i) {
                chunks[i] = new Chunk(i);
                chunkStack.push(chunks[i]);
            }

            DatagramSocket socket = new DatagramSocket();
            System.out.println("socket port: " + socket.getLocalPort());
            Set<ClientRecord> clients = new HashSet<ClientRecord>();

            while (true) {

                if (chunkStack.empty()) {
                    finish(socket, clients);
                }

                byte buffer[] = new byte[bufferSize];
                DatagramPacket receivePacket = new DatagramPacket(buffer, bufferSize);
                socket.receive(receivePacket);

                InetAddress address = receivePacket.getAddress();
                int port = receivePacket.getPort();
                String code = new String(receivePacket.getData(), 0, codeLength);
                String message = new String(receivePacket.getData(), codeLength, receivePacket.getData().length - codeLength);
                DatagramPacket sendPacket;

                switch (code) {
                    case addClient: {
                        message = sendHash + toHex(hash);
                        sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, address, port);
                        socket.send(sendPacket);
                        break;}
                    case removeClient: {
                        int index = Integer.parseInt(message.trim());
                        if (index != -1) {
                            chunks[index].occupied = false;
                            chunkStack.push(chunks[index]);
                            System.out.println("chunk - " + chunkStack.peek().index);
                        }
                        System.out.println("remove client: " + address + " " + port);
                        break;}
                    case getChunk:
                        System.out.println("chunk - " + chunkStack.peek().index);
                        Chunk chunk = chunkStack.pop();
                        chunk.occupied = true;
                        System.out.println("starting chunk " + chunk.index);
                        message = sendChunk + chunk.index;
                        sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, address, port);
                        socket.send(sendPacket);
                        break;
                    case finishChunk:
                        System.out.println("finish chunk " + message);
                        int i = Integer.parseInt(message.trim());
                        chunks[i].occupied = false;
                        chunks[i].finished = true;
                        break;
                    case findKey:
                        System.out.println("find key: " + message);
                        finish(socket, clients);
                        break;
                    default:
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
