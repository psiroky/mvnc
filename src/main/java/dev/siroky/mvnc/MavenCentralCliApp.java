package dev.siroky.mvnc;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import picocli.CommandLine;

import javax.inject.Inject;

@QuarkusMain
public class MavenCentralCliApp implements QuarkusApplication {

    private CommandLine.IFactory factory;
    private MainCommand listArtifactsCommand;

    @Inject
    public MavenCentralCliApp(CommandLine.IFactory factory, MainCommand listArtifactsCommand) {
        this.factory = factory;
        this.listArtifactsCommand = listArtifactsCommand;
    }

    public static void main(String... args) {
        Quarkus.run(MavenCentralCliApp.class, args);
    }

    @Override
    public int run(String... args) {
        return new CommandLine(listArtifactsCommand, factory).execute(args);
    }
}
