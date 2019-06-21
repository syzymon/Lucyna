package pl.edu.mimuw.kd209238.searcher;

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

    private static Searcher searcher;

    private static String executeCommand(String args) {

        if (!args.isEmpty() && args.charAt(0) != '%') {
            try {
                return searcher.query(args);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String command = "", arg = "";
        String[] argsList = args.split(" ");

        if (argsList.length > 0)
            command = argsList[0];
        if (argsList.length > 1)
            arg = argsList[1];

        switch (command) {
            case "%lang":
                searcher.setLang(arg);
                break;
            case "%details":
                searcher.setDetails(arg);
                break;
            case "%limit":
                searcher.setLimit(Integer.valueOf(arg));
                break;
            case "%color":
                searcher.setColor(arg);
                break;
            default: {
                if (command.equals("%term") || command.equals("%phrase") || command.equals("%fuzzy"))
                    searcher.setQueryMode(command.replaceFirst("%", ""));
            }
        }
        return "";
    }

    public static void main(String[] args) {

        try {
            searcher = new Searcher(INDEX_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Terminal terminal = TerminalBuilder.builder()
                .jna(false)
                .jansi(true)
                .build()) {
            LineReader lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(new Completers.FileNameCompleter())
                    .build();

            terminal.writer()
                    .println("Searcher CLI");
            terminal.writer()
                    .println("Commands: %lang en/pl; %details on/off; %limit <n>; %color on/off; %term; %phrase; %fuzzy");

            while (true) {
                try {
                    String line = lineReader.readLine("> ");


                    String cmdResult = executeCommand(line);
                    if (!cmdResult.isEmpty())
                        terminal.writer().println(cmdResult);

//                    terminal.writer().println("\u001b[1m\u001B[31mRed Bold\u001B[0m\u001B[0m");
//                    if(!cmdResult.isEmpty())
//                        terminal.writer().println(cmdResult);
                } catch (UserInterruptException | EndOfFileException e) {
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("An error has occured", e);
        }
    }
}
