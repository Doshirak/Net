import java.io.*;
import java.net.*;

class Client
{
    public static void main(String args[])
    {
        if (2 != args.length)
        {
            System.out.println("usage: java Client [ip address] [port]");
            System.exit(0);
        }
        try
        {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(args[0]);
            Integer port = Integer.parseInt(args[1]);
            byte[] sendData;
            byte[] receiveData = new byte[1024];
            String sentence = "Hello";
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String quote = new String(receivePacket.getData());
            System.out.println("FROM SERVER:" + quote);
            clientSocket.close();
        }
        catch (SocketException e)
        {
            System.out.println("Can't bind socket");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}