package dev.siroky.mvnc.rest;

public class MavenCentralClientException extends RuntimeException {
    public MavenCentralClientException(String message) {
        super(message);
    }

    public MavenCentralClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
