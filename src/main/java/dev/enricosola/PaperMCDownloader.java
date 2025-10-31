package dev.enricosola;

import dev.enricosola.command.DownloadCommand;
import picocli.CommandLine;

public class PaperMCDownloader {
    /**
     * Main method for the MCDownloader application.
     */
    static void main(String[] args) {
        int exitCode = new CommandLine(new DownloadCommand()).execute(args);
        System.exit(exitCode);
    }
}
