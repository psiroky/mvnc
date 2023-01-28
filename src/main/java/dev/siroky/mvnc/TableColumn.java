package dev.siroky.mvnc;

import java.util.List;

public record TableColumn(String header, List<String> values) {

    public TableColumn(String header, List<String> values) {
        this.header = header;
        this.values = List.copyOf(values);
    }
}
