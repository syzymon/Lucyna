package pl.edu.mimuw.kd209238.indexer;

import org.jline.builtins.Completers;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.mimuw.kd209238.example.JLineExample;

import java.io.IOException;
import java.nio.file.Paths;

public class CLI {

    private static Logger logger = LoggerFactory.getLogger(JLineExample.class);

    public static void execute_command(String[] args) {
        String cmd_name = args[0], dir_name = null;
        if (args.length > 1)
            dir_name = args[1];

    }

    public static void main(String[] args) {
        try (Terminal terminal = TerminalBuilder.builder()
                .jna(false)
                .jansi(true)
                .build()) {
            LineReader lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(new Completers.FileNameCompleter())
                    .build();

            terminal.writer()
                    .println("Indexer CLI");
            terminal.writer()
                    .println("Args: --purge, --add, --rm, --reindex, --list ");

            while (true) {
                try {
                    String line = lineReader.readLine("> ");
                    String[] input_args = line.split(" ");
                    execute_command(input_args);
                } catch (UserInterruptException e) {
                    break;
                } catch (EndOfFileException e) {
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("An error has occured", e);
        }
    }
}
