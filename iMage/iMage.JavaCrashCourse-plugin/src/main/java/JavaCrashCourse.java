import org.iMage.plugins.PluginForJmjrst;
import org.jis.Main;

import java.util.List;

public class JavaCrashCourse extends PluginForJmjrst {

    private static final String NAME = "Java Crash Course";
    private static final int NUMBER_OF_ATTRIBUTES = 1;

    /**
     * Source: Oracle Corp.
     */
    private static final String JAVA_8_LTS = "Java 8 (LTS)";
    private static final String JAVA_9 = "Java 9 (non‑LTS)";
    private static final String JAVA_10 = "Java 10 (non‑LTS)";
    private static final String JAVA_11_LTS = "Java 11 (LTS)";
    private static final String JAVA_12 = "Java 12 (non‑LTS)";
    private static final String JAVA_13 = "Java 13 (non‑LTS)";
    private static final String JAVA_14 = "Java 14 (non‑LTS)";

    private List<String> javaStableReleases;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getNumberOfParameters() {
        return NUMBER_OF_ATTRIBUTES;
    }

    @Override
    public void init(Main main) {
        javaStableReleases = List.of(JAVA_8_LTS, JAVA_9, JAVA_10, JAVA_11_LTS, JAVA_12, JAVA_13, JAVA_14);
    }

    @Override
    public void run() {

    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public void configure() {

    }
}
