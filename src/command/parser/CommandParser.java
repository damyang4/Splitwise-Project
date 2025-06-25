package command.parser;

import java.util.Arrays;
import java.util.List;

public class CommandParser {
    public static ParsedUserInput parse(String input) {
        List<String> tokens = Arrays.stream(input.split(" "))
                .map(String::strip)
                .filter(word -> !word.isEmpty())
                .toList();

        return new ParsedUserInput(tokens.getFirst(),
                                 tokens.subList(1, tokens.size()).toArray(String[]::new));
    }
}
