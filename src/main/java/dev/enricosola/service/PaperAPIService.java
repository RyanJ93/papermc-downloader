package dev.enricosola.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import dev.enricosola.exception.PaperAPIException;
import com.fasterxml.jackson.databind.*;
import dev.enricosola.entity.Version;
import dev.enricosola.entity.Build;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.List;
import java.net.URI;

public class PaperAPIService {
    private static final String BUILD_LIST_ENDPOINT = "https://fill.papermc.io/v3/projects/paper/versions/%s/builds";
    private static final String VERSION_LIST_ENDPOINT = "https://fill.papermc.io/v3/projects/paper/versions";

    /**
     * Fetches a list of Minecraft server versions from the PaperMC API.
     *
     * @return a {@link TreeMap} with version IDs as keys and corresponding {@link Version} objects as values.
     * @throws PaperAPIException if there is an issue with parsing the API response or the API request fails.
     */
    public TreeMap<String, Version> fetchVersions() {
        try {
            TreeMap<String, Version> versions = new TreeMap<>();
            String json = this.sendJSONRequest(PaperAPIService.VERSION_LIST_ENDPOINT);
            new ObjectMapper().readTree(json).get("versions").forEach(innerJsonNode -> {
                String versionId = innerJsonNode.at("/version/id").asText();
                List<Integer> buildList = new ObjectMapper().convertValue(innerJsonNode.at("/builds"), new TypeReference<>() {});
                versions.put(versionId, new Version(versionId, buildList));
            });
            return versions;
        } catch (JsonProcessingException ex) {
            throw new PaperAPIException("Unable to parse the response from the PaperMC API.", ex);
        }
    }

    /**
     * Fetches a list of builds for a specific Minecraft server version from the PaperMC API.
     *
     * @param versionId The ID of the Minecraft server version for which the builds are to be retrieved.
     * @return A {@link TreeMap} where the keys are build IDs (integers) and the values are {@link Build} objects
     *         containing details of each build, including the build ID and download URL.
     * @throws PaperAPIException If there is an issue with parsing the API response or the API request fails.
     */
    public TreeMap<Integer, Build> fetchBuild(String versionId) {
        try {
            TreeMap<Integer, Build> builds = new TreeMap<>();
            String json = this.sendJSONRequest(String.format(PaperAPIService.BUILD_LIST_ENDPOINT, versionId));
            new ObjectMapper().readTree(json).forEach(buildNode -> {
                String downloadUrl = buildNode.at("/downloads/server:default/url").asText();
                int buildId = buildNode.at("/id").asInt();
                builds.put(buildId, new Build(buildId, downloadUrl));
            });
            return builds;
        } catch (JsonProcessingException ex) {
            throw new PaperAPIException("Unable to parse the response from the PaperMC API.", ex);
        }
    }

    /**
     * Downloads a specific PaperMC build to the given local file system path.
     *
     * @param build The {@link Build} object containing details of the build to be downloaded, including its download URL.
     * @param destinationPath A string representing the local file path where the downloaded build will be saved.
     * @throws PaperAPIException If an HTTP request error or I/O operation failure occurs during the download process.
     * @throws PaperAPIException If the response status code is not 200 (OK) when retrieving the build.
     */
    public void downloadBuild(Build build, String destinationPath) {
        URI uri = URI.create(build.getDownloadUrl());
        Path destination = Path.of(destinationPath);
        this.cleanupDownload(destination);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).build();
            HttpResponse<Path> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofFile(destination));
            if ( httpResponse.statusCode() != 200 ) {
                throw new PaperAPIException("Non-Ok response received from the PaperMC API: " + httpResponse.statusCode());
            }
        } catch (IOException | InterruptedException ex) {
            throw new PaperAPIException("An error occurred while sending the request to " + uri, ex);
        }
    }

    /**
     * Sends a GET request to the specified URL expecting a JSON response.
     *
     * @param url the URL to which the GET request will be sent.
     * @return the response body as a JSON string.
     * @throws PaperAPIException if an IOException or InterruptedException occurs during the request execution.
     * @throws PaperAPIException if the response status code is not 200 (OK).
     */
    private String sendJSONRequest(String url) {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest
                    .newBuilder()
                    .header("Accept", "application/json")
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() != 200) {
                throw new PaperAPIException("Non-Ok response received from the PaperMC API: " + httpResponse.statusCode());
            }
            return httpResponse.body();
        } catch (IOException | InterruptedException ex) {
            throw new PaperAPIException("An error occurred while sending the request to " + url, ex);
        }
    }

    /**
     * Deletes the specified file if it exists in the filesystem. This method is typically
     * used to clean up a previously downloaded file that may need to be replaced or deleted.
     *
     * @param destination the path of the file to be deleted.
     * @throws PaperAPIException if an I/O error occurs while attempting to delete the file.
     */
    private void cleanupDownload(Path destination) {
        if (Files.exists(destination)) {
            try {
                Files.delete(destination);
            } catch (IOException ex) {
                throw new PaperAPIException("Unable to delete previously downloaded file.", ex);
            }
        }
    }
}
