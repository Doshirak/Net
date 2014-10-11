/**
 * Created by doshirak on 15.09.14.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class Server
{
    public static void main(String args[]) throws IOException
    {
        if (1 > args.length)
        {
            System.out.println("usage: java Server [port]");
            System.exit(0);
        }
        Integer port = Integer.parseInt(args[0]);
        DatagramSocket serverSocket = new DatagramSocket(port);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        while (true)
        {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String received = new String(receivePacket.getData(),0,receivePacket.getLength());
            System.out.println(received);
            if (received.equals("ping"))
            {
                InetAddress IPAddress = receivePacket.getAddress();
                int senderPort = receivePacket.getPort();
                System.out.println("sender address:" + IPAddress + " " + senderPort);
                String quote = "pong";
                sendData = quote.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, senderPort);
                serverSocket.send(sendPacket);
            }
        }
    }
}
