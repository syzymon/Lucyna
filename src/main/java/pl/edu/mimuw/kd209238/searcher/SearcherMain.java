package pl.edu.mimuw.kd209238.searcher;

import org.jline.builtins.Completers;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

import static pl.edu.mimuw.kd209238.common.ConfigConstants.INDEX_PATH;

public class SearcherMain {
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

                } catch (UserInterruptException | EndOfFileException e) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("An error has occured: " + e);
        }
    }
}
