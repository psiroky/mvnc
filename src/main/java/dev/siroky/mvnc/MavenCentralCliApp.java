package dev.siroky.mvnc;

import javax.inject.Inject;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import picocli.CommandLine;

@QuarkusMain
public class MavenCentralCliApp implements QuarkusApplication {

    private final CommandLine.IFactory factory;
    private final MvncCommand mvncCommand;

    @Inject
    public MavenCentralCliApp(CommandLine.IFactory factory, MvncCommand mvncCommand) {
        this.factory = factory;
        this.mvncCommand = mvncCommand;
    }

    public static void main(String... args) {
        Quarkus.run(MavenCentralCliApp.class, args);
    }

    @Override
    public int run(String... args) {
        return new CommandLine(mvncCommand, factory)
                .setExecutionExceptionHandler(new PrintExceptionMessageHandler())
                .execute(args);
    }
}
