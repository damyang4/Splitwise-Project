package server;

import command.CommandExecutor;
import errorlogger.ErrorLogger;
import user.RegisteredUsers;
import user.UserExporter;
import user.UserLoader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int BUFFER_SIZE = 1024;
    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    private final int port;
    private final CommandExecutor commandExecutor;
    private boolean isServerWorking;
    private Selector selector;
    private ByteBuffer buffer;

    private final Map<SocketChannel, String> clientSessions = new ConcurrentHashMap<>();

    public Server(int port) {
        this.port = port;
        RegisteredUsers registeredUsers = UserLoader.loadFromFile();
        this.commandExecutor = new CommandExecutor(registeredUsers, clientSessions);
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            isServerWorking = true;

            while (isServerWorking) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }

                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    processSelectedKeys(keyIterator);

                } catch (IOException e) {
                    System.err.println("Error processing client request. Details have been logged.");
                    ErrorLogger.logError("Exception while processing selected channels", e);
                }
            }
        } catch (IOException e) {
            ErrorLogger.logError("Failed to start the server", e);
            throw new UncheckedIOException("Server startup failed. See logs for details.", e);
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void processSelectedKeys(Iterator<SelectionKey> keyIterator) throws IOException {
        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            keyIterator.remove();

            if (key.isReadable()) {
                handleClientRequest((SocketChannel) key.channel());
            } else if (key.isAcceptable()) {
                acceptClient(selector, key);
            }
        }
    }

    private void handleClientRequest(SocketChannel clientChannel) throws IOException {
        String clientInput = getClientInput(clientChannel);

        if (clientInput == null) {
            return;
        }

        if ("[server.Shutdown command issued]".equals(clientInput.strip())) { // Sanitize input
            System.out.println("Shutdown command received. Stopping the server...");
            stop();
            return;
        }

        String sessionToken = clientSessions.get(clientChannel);

        String output = commandExecutor.execute(sessionToken, clientInput, clientChannel);
        writeClientOutput(clientChannel, output);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        clientChannel.write(buffer);
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        try {
            buffer.clear();
            int readBytes = clientChannel.read(buffer);

            if (readBytes < 0) {
                System.out.println("Client disconnected");
                clientSessions.remove(clientChannel);
                clientChannel.close();
                return null;
            }

            buffer.flip();
            byte[] clientInputBytes = new byte[buffer.remaining()];
            buffer.get(clientInputBytes);

            return new String(clientInputBytes, StandardCharsets.UTF_8).strip();
        } catch (IOException e) {
            handleClientDisconnection(clientChannel);
            ErrorLogger.logError("Error reading client input", e);
            return null;
        }
    }

    private void handleClientDisconnection(SocketChannel clientChannel) throws IOException {
        if (clientChannel != null && clientChannel.isOpen()) {
            System.out.println("Client disconnected: " + clientChannel);
            clientSessions.remove(clientChannel);
            SelectionKey key = clientChannel.keyFor(selector);
            if (key != null) {
                key.cancel();
            }
            clientChannel.close();
        }
    }

    private void acceptClient(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    public void stop() {
        UserExporter exporter = new UserExporter(commandExecutor.getRegisteredUsers());
        exporter.saveRegisteredUsersToFile();

        isServerWorking = false;
//        selector.wakeup();
        clientSessions.keySet().forEach(clientChannel -> {
            try {
                if (clientChannel != null && clientChannel.isOpen()) {
                    clientChannel.close();
                }
            } catch (IOException e) {
                ErrorLogger.logError("Failed to close client connection", e);
            }
        });
        clientSessions.clear();

        if (selector != null && selector.isOpen()) {
            try {
                selector.close();
            } catch (IOException e) {
                ErrorLogger.logError("Failed to close selector", e);
            }
        }
        System.out.println("Server has been stopped successfully.");
    }

    public static void main(String[] args) {
        Server server = new Server(PORT);
        server.start();
    }
}
