package dev.siroky.mvnc;

import picocli.CommandLine;

import java.net.URL;
import java.util.Properties;

public class PicocliVersionProvider implements CommandLine.IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        URL url = getClass().getResource("/mvnc-version.properties");
        if (url == null) {
            return new String[]{"Unknown"};
        }
        Properties properties = new Properties();
        try (var propsStream = url.openStream()) {
            properties.load(propsStream);
            return new String[]{
                    properties.getProperty("appName") + " " + properties.getProperty("version")
            };
        }
    }
}
