package pl.edu.mimuw.kd209238.indexer;

import org.jline.builtins.Completers;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.mimuw.kd209238.example.JLineExample;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CLI {
    private static Logger logger = LoggerFactory.getLogger(JLineExample.class);

    private static final Path INDEX_PATH =
            Paths.get("/home/syzymon/Pulpit/java/PO_2/Lucyna/src/test/resources/index");

    private static String executeCommand(String[] args) {
        String cmd_name = args[0], dir_name = null;
        if (args.length > 1)
            dir_name = args[1];

        try (IndexManager index = new IndexManager(INDEX_PATH)) {
            DocumentExtractor extractor = new DocumentExtractor();
            FilesManager files = new FilesManager(extractor, index);

            if (!cmd_name.equals("")) {
                return files.performOperation(cmd_name, dir_name);
            } else {
                Watcher w = new Watcher(files);
                w.initialize();
                w.processEvents();
            }
        } catch (IOException e) {
            // TODO: break program?
        }
        return "";
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

                    String cmdResult = executeCommand(input_args);

                    if(!cmdResult.isEmpty())
                        terminal.writer().println(cmdResult);
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
