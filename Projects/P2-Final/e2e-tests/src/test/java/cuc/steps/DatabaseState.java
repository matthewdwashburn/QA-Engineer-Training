package cuc.steps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Restores the shared database to its seeded baseline before every scenario, by
 * invoking the {@code db-reset} compose service rather than opening the SQLite
 * file directly.
 *
 * <p>Without a reset, the suite is only green on a freshly initialised database.
 * Several scenarios leave rows behind on purpose — the validation-recovery
 * scenario ends by successfully submitting the very expense an earlier step
 * asserts is absent — so the second run fails on data the first run created.
 * Others leave rows behind only when they fail partway, which turns one failure
 * into a permanently poisoned database.
 *
 * <p>The reset runs in a container because it reseeds through the same code the
 * db-init image ships, against the Postgres service on the compose network.
 * Driving it from the host would mean duplicating that script and publishing
 * the database port to whoever runs the suite.
 *
 * <p>The reset itself is still data-only — see {@code database/reset_db.py}.
 * Both applications open a fresh connection per request, so neither caches rows
 * across the reset and neither needs restarting.
 */
final class DatabaseState {

    /** Overrides the reset command, e.g. for a non-containerised stack. */
    private static final String RESET_COMMAND_PROPERTY = "e2e.reset.command";

    // exec into the already-running db-tools sidecar: no container create/start
    // per scenario. -T disables TTY allocation, required when not interactive.
    private static final String DEFAULT_RESET_COMMAND =
            "docker compose exec -T db-tools python /app/dbsetup/reset_db.py";

    private static final int TIMEOUT_SECONDS = 120;

    private DatabaseState() {
    }

    static void resetToSeedBaseline() {
        String command = System.getProperty(RESET_COMMAND_PROPERTY, DEFAULT_RESET_COMMAND);

        // Split on whitespace: the command is a fixed list of bare arguments, so
        // there is nothing quoted to preserve.
        ProcessBuilder builder = new ProcessBuilder(List.of(command.trim().split("\\s+")));
        builder.directory(repositoryRoot().toFile());
        builder.redirectErrorStream(true);

        try {
            Process process = builder.start();
            String output = new String(process.getInputStream().readAllBytes());

            if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new IllegalStateException("Database reset timed out after "
                        + TIMEOUT_SECONDS + "s. Command: " + command);
            }

            if (process.exitValue() != 0) {
                throw new IllegalStateException("Database reset failed (exit "
                        + process.exitValue() + "). Command: " + command
                        + System.lineSeparator() + output);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to run the database reset: " + command, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while resetting the database.", e);
        }
    }

    /** Compose resolves its files relative to the working directory. */
    private static Path repositoryRoot() {
        Path fromModule = Path.of("..");
        if (Files.isRegularFile(fromModule.resolve("compose.yaml"))) {
            return fromModule;
        }

        Path current = Path.of("");
        if (Files.isRegularFile(current.resolve("compose.yaml"))) {
            return current;
        }

        throw new IllegalStateException("Cannot locate compose.yaml from "
                + Path.of("").toAbsolutePath() + ".");
    }
}
