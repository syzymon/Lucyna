package pl.edu.mimuw.kd209238.example;

import java.io.IOException;

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

public class JLineExample {

	private static Logger logger = LoggerFactory.getLogger(JLineExample.class);

	public static void main(String[] args) {
		logger.info("Warming up...");
		try (Terminal terminal = TerminalBuilder.builder()
			.jna(false)
			.jansi(true)
			.build()) {
			LineReader lineReader = LineReaderBuilder.builder()
				.terminal(terminal)
				.completer(new Completers.FileNameCompleter())
				.build();

			terminal.writer()
				.println("Hello! This is a simple terminal that echoes (in bold!) everything you write");
			terminal.writer()
				.println("Also, try pressing the TAB key...");
			while (true) {
				String line = null;
				try {
					line = lineReader.readLine("> ");
					terminal.writer()
						.println(new AttributedStringBuilder().append("You've entered: ")
							.style(AttributedStyle.DEFAULT.bold())
							.append(line)
							.toAnsi());
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
