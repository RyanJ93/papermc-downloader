package dev.enricosola.command;

import dev.enricosola.service.PaperAPIService;
import java.util.concurrent.Callable;
import dev.enricosola.entity.Version;
import dev.enricosola.entity.Build;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.util.TreeMap;

@Command(
        name = "download",
        mixinStandardHelpOptions = true,
        version = "0.0.1",
        description = "Download Paper Minecraft server."
)
public class DownloadCommand implements Callable<Integer> {
    private static final String VERSION_ENV_VAR_NAME = "MC_VERSION";
    private static final String BUILD_ENV_VAR_NAME = "MC_BUILD";

    private static final String DEFAULT_OUTPUT = "./papermc.jar";

    @Option(names = {"-p", "--mc-version"}, description = "Paper server version to download (latest by default).")
    private String MCVersion = null;

    @Option(names = {"-b", "--mc-build"}, description = "Paper server build number to download (latest by default).")
    private Integer MCBuild = null;

    @Option(names = {"-o", "--out"}, description = "Path where the paper JAR file will be downloaded.")
    private String output = DownloadCommand.DEFAULT_OUTPUT;

    @Option(names = {"-d", "--dry-run"}, description = "If set no real download will be performed.")
    private boolean dryRun = false;

    /**
     * Executes the process of downloading a specified version and build of the PaperMC server.
     * Retrieves the target version and build, logs the download process, and initiates
     * the download unless the "--dry-run" argument is present.
     *
     * @return An Integer value indicating the execution result, typically 0 for a successful execution.
     */
    @Override
    public Integer call() {
        Version version = this.getVersion();
        Build build = this.getBuild(version);
        IO.println("Downloading PaperMC server version " + version.getId() + " build " + build.getId() + "...");
        IO.println(String.format("Saving to \"%s\"...", this.output));
        if (this.dryRun) {
            IO.println("Dry-run on, no real download will be performed!");
        } else {
            new PaperAPIService().downloadBuild(build, this.output);
        }
        IO.println("Download completed!");
        return 0;
    }

    /**
     * Retrieves the appropriate {@link Version} to be used based on the provided configuration or environment variables.
     * If the version is not explicitly defined, it fetches the available versions from the PaperMC API and selects
     * the first version. In case of an invalid or unrecognized version, the application will terminate with an error message.
     *
     * @return The selected {@link Version} object representing the Minecraft server version.
     */
    private Version getVersion() {
        String selectedVersion = this.MCVersion == null || this.MCVersion.isBlank() ?
                System.getenv(DownloadCommand.VERSION_ENV_VAR_NAME) :
                this.MCVersion;
        TreeMap<String, Version> versions = new PaperAPIService().fetchVersions();
        Version version = selectedVersion == null || selectedVersion.isBlank() ?
                versions.firstEntry().getValue() : versions.get(selectedVersion);
        if ( version == null ) {
            IO.println("Unknown Minecraft server version, aborting.");
            System.exit(1);
        }
        return version;
    }

    /**
     * Retrieves the appropriate {@link Build} for the given Minecraft {@link Version}.
     *
     * @param version The {@link Version} object representing the Minecraft server version for which the build is to be retrieved.
     * @return The selected {@link Build} object containing build details such as ID and download URL.
     */
    private Build getBuild(Version version) {
        try {
            Integer selectedBuild = this.MCBuild;
            if (selectedBuild == null) {
                String selectedBuildStr = System.getenv(DownloadCommand.BUILD_ENV_VAR_NAME);
                selectedBuild = selectedBuildStr == null || selectedBuildStr.isBlank() ? null : Integer.parseInt(selectedBuildStr);
            }
            TreeMap<Integer, Build> builds = new PaperAPIService().fetchBuild(version.getId());
            Build build = selectedBuild == null ? builds.firstEntry().getValue() : builds.get(selectedBuild);
            if ( build == null ) {
                IO.println("Unknown Minecraft server version, aborting.");
                System.exit(1);
            }
            return build;
        } catch (NumberFormatException ex) {
            IO.println("Invalid Minecraft server build number, aborting.");
            System.exit(1);
        }
        return null;
    }
}
