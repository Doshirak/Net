import sun.print.resources.serviceui_de;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class Container {
    static public String input;
}


public class Client {
    private static int bufferLength = 100;

    public static void main(String args[]) {
        try {
            if (args.length != 3) {
                System.out.println("usage: java Client <server_address> <server_port> <name>");
                System.exit(1);
            }
            System.out.println("to send message type: <username>:<message>");

            final InetAddress address = InetAddress.getByName(args[0]);
            final int port = Integer.parseInt(args[1]);
            final String name = args[2];
            final DatagramSocket socket = new DatagramSocket();

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String message = Protocol.removeClient + name;
                        DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length,
                                address, port);
                        socket.send(sendPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }));

            String hello = Protocol.addClient + name;
            DatagramPacket helloPacket = new DatagramPacket(hello.getBytes(), hello.getBytes().length,
                    address, port);
            socket.send(helloPacket);


            Thread userListener = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while(true) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                            Container.input = reader.readLine();
                            String message = Protocol.sendMessage + name + ">" + Container.input;
                            DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length,
                                    address, port);
                            socket.send(sendPacket);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            userListener.start();

            while (true) {
                byte buffer[] = new byte[bufferLength];
                DatagramPacket receivePacket = new DatagramPacket(buffer, bufferLength);
                socket.receive(receivePacket);

                String code = new String(receivePacket.getData(), 0, Protocol.codeLength);
                String message = new String(receivePacket.getData(), Protocol.codeLength, receivePacket.getLength() - Protocol.codeLength);

                switch (code) {
                    case Protocol.clientList: {
                        System.out.println("users in chat:\n" + message);
                        break;
                    }
                    case Protocol.addClient: {
                        System.out.println(message + " joined chat");
                        break;
                    }
                    case Protocol.removeClient: {
                        System.out.println(message + " leaved chat");
                        break;
                    }
                    case Protocol.sendMessage: {
                        System.out.println(message);
                        break;
                    }
                    case Protocol.nameUsed: {
                        System.out.println("this name is already used");
                        System.exit(1);
                        break;
                    }
                    case Protocol.wrongName: {
                        System.out.println("wrong name");
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
