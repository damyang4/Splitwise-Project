package command;

import java.nio.channels.SocketChannel;

public interface Command {
    String execute(String sessionToken, String[] args, SocketChannel clientChannel);
}
