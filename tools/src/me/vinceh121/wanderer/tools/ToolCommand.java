package me.vinceh121.wanderer.tools;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;

@Command(name = "tools", subcommands = { HelpCommand.class, CinematicCommand.class, WeatherCommand.class })
public class ToolCommand {
	public static void main(final String[] args) {
		System.exit(new CommandLine(new ToolCommand()).execute(args));
	}
}
