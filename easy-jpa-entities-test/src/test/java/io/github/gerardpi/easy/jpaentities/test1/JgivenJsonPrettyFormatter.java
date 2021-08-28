package io.github.gerardpi.easy.jpaentities.test1;

import com.google.common.base.Strings;
import com.tngtech.jgiven.format.ArgumentFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JgivenJsonPrettyFormatter implements ArgumentFormatter<String> {
    public static final String CANNOT_CONVERT_TO_JSON_OBJECT = "JSONArray cannot be converted to JSONObject";
    private static final Logger LOG = LoggerFactory.getLogger(JgivenJsonPrettyFormatter.class);
    private static final int INDENT_AMOUNT_SPACES = 10;
    private static final int SEPARATOR_SIZE = 70;
    private static final String INDENTATION =  String.join("", Collections.nCopies(INDENT_AMOUNT_SPACES, " "));
    private static final String SEPARATOR =  String.join("", Collections.nCopies(SEPARATOR_SIZE, "-"));

    @Override
    public String format(String argumentFormat, String... formattedArguments) {
        try {
            return formatToJsonObject(argumentFormat);
        } catch (JSONException e) {
            if (e.getMessage().endsWith(CANNOT_CONVERT_TO_JSON_OBJECT)) {
                try {
                    return formatToJsonArray(argumentFormat);
                } catch (JSONException e2) {
                    throw new IllegalStateException(e2);
                }
            }
            throw new IllegalStateException(e);
        }
    }

    private String formatToJsonArray(String argumentFormat) throws JSONException {
        LOG.info("Could not convert {} into JSONObject. Now trying to convert it into a JSONArray...", argumentFormat);
        JSONArray jsonArray = new JSONArray(argumentFormat);
        return indent(System.lineSeparator() + jsonArray);
    }

    private String formatToJsonObject(String argumentFormat) throws JSONException {
        if (Strings.isNullOrEmpty(argumentFormat)) {
            LOG.info("No JSON to format");
            return "";
        } else {
            JSONObject jsonObject = new JSONObject(argumentFormat);
            return indent(jsonObject.toString(INDENT_AMOUNT_SPACES));
        }
    }

    private String indent(String lines) {
        return System.lineSeparator()
                + INDENTATION + SEPARATOR + System.lineSeparator()
                + Stream.of(lines.split(System.lineSeparator()))
                .map(line -> INDENTATION + line)
                .collect(Collectors.joining(System.lineSeparator()))
                + System.lineSeparator()
                + INDENTATION + SEPARATOR + System.lineSeparator();
    }
}
