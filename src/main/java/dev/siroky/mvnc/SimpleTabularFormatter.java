package dev.siroky.mvnc;

import java.util.ArrayList;
import java.util.List;

public class SimpleTabularFormatter {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String HORIZONTAL_DELIMITER_CHAR = "-";
    private static final int DEFAULT_MIN_SPACE_BETWEEN_COLUMNS = 5;

    private final List<TableColumn> columns;
    private final int minSpaceBetweenColumns;

    public SimpleTabularFormatter(List<TableColumn> columns, int minSpaceBetweenColumns) {
        this.columns = List.copyOf(columns);
        this.minSpaceBetweenColumns = minSpaceBetweenColumns;
    }

    public SimpleTabularFormatter(List<TableColumn> columns) {
        this(columns, DEFAULT_MIN_SPACE_BETWEEN_COLUMNS);
    }

    public String format() {
        var maxColumnLengths = maxLengthPerColumn(columns);
        var rows = createRows(columns, maxColumnLengths);
        var resultBuilder = new StringBuilder();

        var headerRow = formatRow(rows.get(0), minSpaceBetweenColumns);
        resultBuilder.append(headerRow).append(LINE_SEPARATOR);
        resultBuilder
                .append(HORIZONTAL_DELIMITER_CHAR.repeat(headerRow.length()))
                .append(LINE_SEPARATOR);

        for (var rowNr = 1; rowNr < rows.size(); rowNr++) {
            resultBuilder.append(formatRow(rows.get(rowNr), minSpaceBetweenColumns));
            resultBuilder.append(LINE_SEPARATOR);
        }

        return resultBuilder.toString().trim();
    }

    private static List<TableRow> createRows(List<TableColumn> columns, List<Integer> maxColumnLengths) {
        var rows = new ArrayList<TableRow>();
        // add header row first
        var headerValues = new ArrayList<String>();
        for (TableColumn column : columns) {
            headerValues.add(column.header());
        }
        rows.add(new TableRow(headerValues, maxColumnLengths));

        // add rows with the actual values
        var nrOfValueRows = determineNumberOfValueRows(columns);
        for (var rowNr = 0; rowNr < nrOfValueRows; rowNr++) {
            var values = new ArrayList<String>();
            for (TableColumn column : columns) {
                values.add(column.values().get(rowNr));
            }
            rows.add(new TableRow(values, maxColumnLengths));
        }
        return rows;
    }

    private static int determineNumberOfValueRows(List<TableColumn> columns) {
        return columns.stream().mapToInt(c -> c.values().size()).max().orElse(0);
    }

    private List<Integer> maxLengthPerColumn(List<TableColumn> columns) {
        return columns.stream()
                .mapToInt(col -> {
                    int maxValueLength =
                            col.values().stream().mapToInt(String::length).max().orElse(0);
                    return Math.max(col.header().length(), maxValueLength);
                })
                .boxed()
                .toList();
    }

    private static String formatRow(TableRow row, int minSpaceBetweenColumns) {
        var rowBuilder = new StringBuilder();
        for (int colIdx = 0; colIdx < row.values().size(); colIdx++) {
            var textSize = row.maxTextSizes().get(colIdx);
            var value = row.values().get(colIdx);
            rowBuilder.append(value);
            // fill in with spaces to up to the max size for each column
            rowBuilder.append(" ".repeat(textSize - value.length() + minSpaceBetweenColumns));
        }
        return rowBuilder.toString().trim();
    }

    private record TableRow(List<String> values, List<Integer> maxTextSizes) {}
}
