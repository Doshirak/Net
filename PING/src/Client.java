import java.io.*;
import java.net.*;

class Client
{
    public static void main(String args[]) throws Exception
    {
        if (2 > args.length) {
            System.out.println("usage: java Client [ip address] [port] [sends count]");
            System.exit(0);
        }

        DatagramSocket clientSocket = new DatagramSocket();
        clientSocket.setSoTimeout(1000);
        InetAddress IPAddress = InetAddress.getByName(args[0]);
        Integer port = Integer.parseInt(args[1]);
        Integer count = Integer.parseInt(args[2]);
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        long start;
        long end;
        String sentence = "ping";
        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sentence.getBytes(), sentence.getBytes().length, IPAddress, port );
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        for (int i = 0;i < count;++i)
        {
            try
            {
                clientSocket.send(sendPacket);
                start = System.currentTimeMillis();
                clientSocket.receive(receivePacket);
                end = System.currentTimeMillis();
                System.out.println("Packet received in " + (end - start) + "ms");
            }
            catch (SocketTimeoutException e)
            {
                System.out.println("Packet lost");
            }

        }
        clientSocket.close();
    }
}