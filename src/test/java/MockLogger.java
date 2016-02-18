import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.SYNC;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * Tool to simulate a running Weblogic server. It streams mock log lines to a file on the disk.
 */
@ParametersAreNonnullByDefault
public class MockLogger {

    private static final String[] levels = {"VERBOSE", "DEBUG", "INFO", "WARN", "ERROR"};

    private static final String[] classes =
            {"com.amadeus.DataMap", "org.bigfoot.Toe", "java.util.Map", "com.fizzy.Twizzer", "org.whynot.Question",
                    "com.enterprise.Business"};

    private static final String[] nouns = {"file", "message", "id", "PNR", "reservation", "service", "class", "object"};

    private static final String[] participates = {"found", "deleted", "inserted", "retrieved", "read", "downloaded", "accessed", "returned"};

    private static final String[] verbs = {"cannot be", "was", "has been", "could not be"};

    private static final Random random = new Random();

    public static void main(String[] args) {
        // TODO: Use a command line lib? See http://commons.apache.org/proper/commons-cli/index.html
        streamMockLogsToFile("mock.log", 0);
    }

    /**
     * Returns a mock log line. Each part of the line is randomly generated.
     *
     * @return the mock log line
     */
    private static String mockLogLine() {
        String format = "####<%s> <%s> <%s> <> <%s;jsessionid=%s>%n";
        return String.format(format, LocalDateTime.now(), rand(levels), rand(classes), randMsg(), randSID());
    }

    /**
     * Writes mock lines to the file at the given path. It writes line by line. Speed depends on delayBetweenLines. The file is cleaned at the beginning of each call.
     *
     * @param path              the path to the target file
     * @param delayBetweenLines the delay between line writes, in milliseconds
     */
    private static void streamMockLogsToFile(String path, int delayBetweenLines) {
        Path p = Paths.get(path);
        try (BufferedWriter writer = Files.newBufferedWriter(p, UTF_8, TRUNCATE_EXISTING, SYNC)) {
            while (true) {
                writer.write(mockLogLine());
                writer.flush();
                // Thread.sleep(0) is not neutral. See http://stackoverflow.com/a/17494898
                if (delayBetweenLines > 0) {
                    Thread.sleep(delayBetweenLines);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String rand(String[] array) {
        return array[random.nextInt(array.length)];
    }

    private static String randMsg() {
        return String.format("The %s %s %s", rand(nouns), rand(verbs), rand(participates));
    }

    private static String randSID() {
        return random.ints(16, 0, 0xFFFF).mapToObj(Integer::toHexString).collect(Collectors.joining());
    }
}