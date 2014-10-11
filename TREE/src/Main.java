import java.io.Console;
import java.io.IOException;
import java.net.*;


public class Main {
    static String hr = "---------------------";
    static boolean run = true;

    public static void main(String args[]) {
        try {
            final LocalNode node;
            DatagramSocket socket = new DatagramSocket();
            System.out.println(hr);
            System.out.println("current port: " + socket.getLocalPort());
            System.out.println(hr);

            if (2 == args.length) {
                InetAddress parentAddress = InetAddress.getByName(args[0]);
                int parentPort = Integer.parseInt(args[1]);
                Node parent = new RemoteNode(parentAddress, parentPort, socket);
                node = new LocalNode(socket, parent);
                node.addParent(parent);
            } else {
                node = new LocalNode(socket, null);
            }

            Thread socketListener = new Thread(new SocketListener(node));
            Thread userListener = new Thread(new UserListener(node));

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        run = false;
                        socketListener.join();
                        userListener.join();
                        if (node.parentNode != null)
                            node.parentNode.removeChild(node);
                        for (Node child : node.children) {
                            child.removeParent();
                            child.addParent(node.parentNode);
                        }
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            socketListener.start();
            userListener.start();

            socketListener.join();
            userListener.join();
        } catch (SocketException e) {
            System.out.println("can't bind socket");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.exit(1);
        }
    }
}
