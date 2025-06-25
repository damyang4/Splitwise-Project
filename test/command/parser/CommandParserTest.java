package command.parser;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {

    @Test
    void testParseSingleWordCommand() {
        String input = "command";
        ParsedUserInput result = CommandParser.parse(input);

        assertEquals("command", result.name());
        assertArrayEquals(new String[]{}, result.args());
    }

    @Test
    void testParseCommandWithArguments() {
        String input = "command arg1 arg2";
        ParsedUserInput result = CommandParser.parse(input);

        assertEquals("command", result.name());
        assertArrayEquals(new String[]{"arg1", "arg2"}, result.args());
    }

    @Test
    void testParseCommandWithExtraSpaces() {
        String input = "   command   arg1   arg2   ";
        ParsedUserInput result = CommandParser.parse(input);

        assertEquals("command", result.name());
        assertArrayEquals(new String[]{"arg1", "arg2"}, result.args());
    }

    @Test
    void testParseCommandWithOnlySpaces() {
        String input = "        ";
        assertThrows(NoSuchElementException.class, () -> CommandParser.parse(input));
    }

    @Test
    void testParseEmptyString() {
        String input = "";
        assertThrows(NoSuchElementException.class, () -> CommandParser.parse(input));
    }

    @Test
    void testParseNullInput() {
        assertThrows(NullPointerException.class, () -> CommandParser.parse(null));
    }
}