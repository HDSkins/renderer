package de.hdskins.skinrenderer.command;

import com.github.derrop.simplecommand.map.CommandExecutionResponse;
import com.github.derrop.simplecommand.map.CommandMap;
import com.github.derrop.simplecommand.map.DefaultCommandMap;
import de.hdskins.skinrenderer.RenderContext;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;

public final class SkinRenderCommand {

    private static final String PROMPT = Ansi.ansi()
            .fgRed().a("SkinRenderer")
            .fgDefault().a("@")
            .fgBrightYellow().a(System.getProperty("user.name"))
            .fgDefault().a(" > ")
            .toString();

    private final RenderContext renderContext;

    private SkinRenderCommand(CommandMap commandMap) {
        this.renderContext = new RenderContext(true);
        this.renderContext.start();

        commandMap.registerSubCommands(new RenderCommand(this));
    }

    public RenderContext getRenderContext() {
        return this.renderContext;
    }

    public static void main(String[] args) {
        AnsiConsole.systemInstall();

        CommandMap commandMap = new DefaultCommandMap();
        commandMap.registerDefaultHelpCommand();

        SkinRenderCommand command = new SkinRenderCommand(commandMap);

        LineReader lineReader = LineReaderBuilder.builder().build();

        try {
            String line;
            while (!Thread.interrupted() && (line = lineReader.readLine(PROMPT)) != null) {
                CommandExecutionResponse response = commandMap.dispatchConsoleCommand(line);
                if (response == CommandExecutionResponse.COMMAND_NOT_FOUND) {
                    System.out.println("That command doesn't exist, use 'help' to get a list of all commands");
                }
            }
        } catch (UserInterruptException ignored) {
        }

        AnsiConsole.systemUninstall();

        command.getRenderContext().interrupt();

    }

}
