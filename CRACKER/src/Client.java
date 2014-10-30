import java.io.IOException;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

class Hook implements Runnable {

    private DatagramSocket socket;
    private Integer index;
    private InetAddress address;
    private int port;

    Hook(DatagramSocket socket, Integer index, InetAddress address, int port) {
        this.socket = socket;
        this.index = index;
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            String message = Main.removeClient + index;
            DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, address, port);
            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Container {
    public static int index;
}

public class Client {
    private final static int bufferSize = 100;

    public static void main(String args[]) {
        try {
            if (args.length != 2) {
                System.out.println("usage: java Client <server_address> <server_port>");
                System.exit(1);
            }

            final InetAddress address = InetAddress.getByName(args[0]);
            final int port = Integer.parseInt(args[1]);
            final DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(1000);
            Container.index = -1;

//            Runtime.getRuntime().addShutdownHook(new Thread(new Hook(socket, index, address, port)));
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String message = Main.removeClient + Container.index;
                        DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, address, port);
                        socket.send(sendPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }));

            byte bytes[] = Main.addClient.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, address, port);
            socket.send(sendPacket);
            byte hash[] = {};

            while (true) {
                bytes = Main.getChunk.getBytes();
                sendPacket = new DatagramPacket(bytes, bytes.length, address, port);
                socket.send(sendPacket);

                byte buffer[] = new byte[bufferSize];
                DatagramPacket receivePacket = new DatagramPacket(buffer, bufferSize);
                socket.receive(receivePacket);
                String code = new String(receivePacket.getData(), 0, Main.codeLength);
                String message = new String(receivePacket.getData(), Main.codeLength, receivePacket.getLength() - Main.codeLength);


                switch (code) {
                    case Main.stopClient:
                        System.exit(0);
                        break;
                    case Main.sendHash: {
                        hash = Main.toByteArray(message.trim());
                        System.out.println("receive hash " + Main.toHex(hash));
                        break;}
                    case Main.sendChunk: {
                        int index = Integer.parseInt(message.trim());
                        Container.index = index;
                        System.out.println("starting chunk " + index);
                        Alphabet alphabet = new Alphabet(Main.genome);
                        for (int i = index * Main.chunkSize; i < (index + 1) * Main.chunkSize; ++i) {
                            String word = alphabet.toString(i);
                            if (word != null) {
                                MessageDigest md = MessageDigest.getInstance("MD5");
                                byte[] newHash = md.digest(word.getBytes("UTF-8"));

                                if (Arrays.equals(newHash, hash)) {
                                    System.out.println("find key in chunk " + index + " : " + word);
                                    String reply = Main.findKey + word;
                                    DatagramPacket replyPacket = new DatagramPacket(reply.getBytes(), reply.getBytes().length, address, port);
                                    socket.send(replyPacket);
                                    System.exit(0);
                                }
                            }
                        }

                        System.out.println("finish chunk " + index);
                        String reply = Main.finishChunk + index;
                        DatagramPacket replyPacket = new DatagramPacket(reply.getBytes(), reply.getBytes().length, address, port);
                        socket.send(replyPacket);
                        break;}
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
