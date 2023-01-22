# Maven Central CLI (mvnc)
Simple command line tool to search Maven Central.

## Building native binary
> **_Note:_** Native binary is created using GraalVM. You either need to have GraalVM installed locally and $GRAALVM_HOME
> environment variable set, or have local Docker environment working to let Quarkus download a GraalVM builder image.

Run the following Maven command to build a native `mvnc` binary:
```shell
$ mvn verify -Pnative
```
The resulting file is located under `target/` directory and called `mvnc-runner`.

## Example usage
Searching by `artifactId` only:
```shell
$ mvnc "maven-invoker-*" --limit 5
Group ID                     Artifact ID                           Version           Released on
------------------------------------------------------------------------------------------------
org.apache.maven.plugins     maven-invoker-plugin                  3.4.0             15-Dec-2022
com.github.hiwepy            maven-invoker-spring-boot-starter     2.0.0.RELEASE     18-Jun-2022
org.apache.maven.plugins     maven-invoker-plugin                  3.3.0             25-May-2022
org.apache.maven.plugins     maven-invoker-plugin                  3.2.2             14-Feb-2021
com.github.hiwepy            maven-invoker-spring-boot-starter     1.0.3.RELEASE     04-Jan-2020
```

Searching by `groupId` and `artifactId`:
```shell
$ mvnc "org.apache.maven.plugins:maven-surefire-plugin" --limit 5
Group ID                     Artifact ID               Version      Released on
-------------------------------------------------------------------------------
org.apache.maven.plugins     maven-surefire-plugin     3.0.0-M8     07-Jan-2023
org.apache.maven.plugins     maven-surefire-plugin     3.0.0-M7     03-Jun-2022
org.apache.maven.plugins     maven-surefire-plugin     3.0.0-M6     31-Mar-2022
org.apache.maven.plugins     maven-surefire-plugin     3.0.0-M5     10-Jun-2020
org.apache.maven.plugins     maven-surefire-plugin     3.0.0-M4     13-Nov-2019
```

## FAQ
**Q**: Why is the tool built of top of Quarkus, seems like an overkill?

**A**: The primary reason is that I wanted to play with and try Quarkus for CLI app development, together with
Picocli. There may be better ways to do this, but in the end I am quite happy with the whole Quarkus experience.
