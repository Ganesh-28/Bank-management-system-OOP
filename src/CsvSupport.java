import java.util.ArrayList;
import java.util.List;

/** Small CSV utility supporting quoted fields, commas, quotes and newlines. */
final class CsvSupport {
    private CsvSupport() {}

    static String encode(String... fields) {
        var encoded = new ArrayList<String>(fields.length);
        for (String field : fields) {
            String value = field == null ? "" : field;
            if (value.contains(",") || value.contains("\"")
                    || value.contains("\n") || value.contains("\r")) {
                encoded.add("\"" + value.replace("\"", "\"\"") + "\"");
            } else {
                encoded.add(value);
            }
        }
        return String.join(",", encoded);
    }

    static List<String> decode(String line) {
        if (line == null) throw new IllegalArgumentException("CSV line cannot be null.");
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (quoted) {
                if (ch == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        quoted = false;
                    }
                } else {
                    field.append(ch);
                }
            } else if (ch == ',' ) {
                fields.add(field.toString());
                field.setLength(0);
            } else if (ch == '"' && field.isEmpty()) {
                quoted = true;
            } else {
                field.append(ch);
            }
        }
        if (quoted) throw new IllegalArgumentException("Unclosed quoted CSV field.");
        fields.add(field.toString());
        return fields;
    }
}
