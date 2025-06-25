package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9999;
    private static final int BUFFER_SIZE = 1024;
    private static final String QUIT_COMMAND = "quit";
    private static final String SHUTDOWN_COMMAND = "shutdown";

    public void start() {
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            System.out.println("Connected to the server.");

            while (true) {
                System.out.print("$ ");
                String inputMessage = scanner.nextLine();

                if (QUIT_COMMAND.equals(inputMessage)) {
                    sendMessageToServer(socketChannel, directBuffer, "[client.Client is disconnecting]");
                    System.out.println("Disconnecting from the server...");
                    break;
                }

                if (SHUTDOWN_COMMAND.equals(inputMessage)) {
                    sendMessageToServer(socketChannel, directBuffer, "[server.Shutdown command issued]");
                    System.out.println("Server shutdown command sent. Disconnecting...");
                    break;
                }

                sendMessageToServer(socketChannel, directBuffer, inputMessage);
                String serverReply = readMessageFromServer(socketChannel, directBuffer);

                if (serverReply == null) {
                    System.out.println("Server closed the connection.");
                    return;
                }
                System.out.println(serverReply);
            }
        } catch (IOException e) {
            throw new RuntimeException("Network communication error", e);
        }
    }

    private void sendMessageToServer(SocketChannel socketChannel, ByteBuffer buffer, String message)
            throws IOException {
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
        socketChannel.write(buffer);
    }

    private String readMessageFromServer(SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
        buffer.clear();
        int bytesRead = socketChannel.read(buffer);

        if (bytesRead == -1) {
            return null;
        }

        buffer.flip();
        byte[] responseBytes = new byte[buffer.remaining()];
        buffer.get(responseBytes);

        return new String(responseBytes, java.nio.charset.StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}