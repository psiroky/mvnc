package dev.siroky.mvnc;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleTabularFormatterTest {

    @Test
    void formatEmptyTableJustWithHeader() {
        var versionCol = new TableColumn("Version", List.of());
        var releasedOnCol = new TableColumn("Released on", List.of());
        SimpleTabularFormatter formatter = new SimpleTabularFormatter(List.of(versionCol, releasedOnCol));

        String result = formatter.format();

        assertThat(result)
                .isEqualTo("""
                Version     Released on
                -----------------------""");
    }

    @Test
    void formatTableWithSingleRow_valuesShorterThanHeaders() {
        var versionCol = new TableColumn("Version", List.of("3.8.7"));
        var releasedOnCol = new TableColumn("Released on", List.of("24-Dec-2022"));
        SimpleTabularFormatter formatter = new SimpleTabularFormatter(List.of(versionCol, releasedOnCol));

        String result = formatter.format();

        assertThat(result)
                .isEqualTo(
                        """
                Version     Released on
                -----------------------
                3.8.7       24-Dec-2022""");
    }

    @Test
    void formatTableWithSingleRow_valuesLongerThanHeaders() {
        var versionCol = new TableColumn("Version", List.of("4.0.0-alpha-3"));
        var releasedOnCol = new TableColumn("Released on", List.of("12-Dec-2022"));
        SimpleTabularFormatter formatter = new SimpleTabularFormatter(List.of(versionCol, releasedOnCol));

        String result = formatter.format();

        assertThat(result)
                .isEqualTo(
                        """
                Version           Released on
                -----------------------------
                4.0.0-alpha-3     12-Dec-2022""");
    }

    @Test
    void formatTableWithMultipleRows_valuesShorterThanHeaders() {
        var versionCol = new TableColumn("Version", List.of("3.8.7", "3.8.6"));
        var releasedOnCol = new TableColumn("Released on", List.of("24-Dec-2022", "06-Jun-2022"));
        SimpleTabularFormatter formatter = new SimpleTabularFormatter(List.of(versionCol, releasedOnCol));

        String result = formatter.format();

        assertThat(result)
                .isEqualTo(
                        """
                Version     Released on
                -----------------------
                3.8.7       24-Dec-2022
                3.8.6       06-Jun-2022""");
    }

    @Test
    void formatTableWithMultipleRows_valuesLongerThanHeaders() {
        var versionCol = new TableColumn("Version", List.of("3.8.7", "4.0.0-alpha-3"));
        var releasedOnCol = new TableColumn("Released on", List.of("24-Dec-2022", "12-Dec-2022"));
        SimpleTabularFormatter formatter = new SimpleTabularFormatter(List.of(versionCol, releasedOnCol));

        String result = formatter.format();

        assertThat(result)
                .isEqualTo(
                        """
                Version           Released on
                -----------------------------
                3.8.7             24-Dec-2022
                4.0.0-alpha-3     12-Dec-2022""");
    }
}
