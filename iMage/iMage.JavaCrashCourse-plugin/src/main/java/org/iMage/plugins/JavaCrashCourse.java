package org.iMage.plugins;

import org.jis.Main;

import javax.swing.JOptionPane;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class JavaCrashCourse extends PluginForJmjrst {

    private static final String NAME = "Java Crash Course";
    private static final boolean CONFIGURABLE = true;

    private static final String KEEPING_UPDATED = "Keeping updated";
    private static final String RUNNING_LATE = "Running late";

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
    private int numberOfParameters;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getNumberOfParameters() {
        return numberOfParameters;
    }

    @Override
    public void init(Main main) {
        javaStableReleases = List.of(JAVA_8_LTS, JAVA_9, JAVA_10, JAVA_11_LTS, JAVA_12, JAVA_13, JAVA_14);
        numberOfParameters = javaStableReleases.size();
        System.out.printf("Found %d Java versions since Java 8\n", numberOfParameters);
    }

    @Override
    public void run() {
        Random random = new Random();
        String release = javaStableReleases.get(random.nextInt() % getNumberOfParameters());

        switch (release) {
            case JAVA_14:
                System.out.println(KEEPING_UPDATED);
            case JAVA_8_LTS:
            case JAVA_9:
            case JAVA_10:
            case JAVA_11_LTS:
            case JAVA_12:
            case JAVA_13:
                System.out.println(RUNNING_LATE);
            default:
                System.out.println(String.format("%s(%d)", NAME, getNumberOfParameters()));
        }
    }

    @Override
    public boolean isConfigurable() {
        return CONFIGURABLE;
    }

    @Override
    public void configure() {
        String data = javaStableReleases.stream().collect(Collectors.joining("\n"));
        JOptionPane.showMessageDialog(null, data);
    }
}
