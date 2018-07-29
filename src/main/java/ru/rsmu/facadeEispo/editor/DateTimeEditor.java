package ru.rsmu.facadeEispo.editor;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leonid.
 */
public class DateTimeEditor extends PropertyEditorSupport implements PropertyEditor {

    /**
     * date format using dashes for day, month, and year.
     *
     */
    final static SimpleDateFormat DASHES_DAY_MONTH_YEAR = new SimpleDateFormat("dd-MM-yyyy");

    /**
     * date format using dashes for day, month, and year.
     *
     */
    final static SimpleDateFormat DOTS_DAY_MONTH_YEAR = new SimpleDateFormat("dd.MM.yyyy");
    /**
     * date format using dashes for year, month, and day.
     *
     */
    final static SimpleDateFormat DASHES_YEAR_MONTH_DAY = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * the default format to use for retrieving dates.
     */
    final static SimpleDateFormat DEFAULT_DATE_FORMAT = DOTS_DAY_MONTH_YEAR;

    /**
     * date format using slashes for month, day, and year.
     */
    final static SimpleDateFormat SLASHES_MONTH_DAY_YEAR = new SimpleDateFormat("MM/dd/yyyy");

    /**
     * date format using dashes for day, month, and year.
     */
    final static SimpleDateFormat DASHES_DAY_MONTH_YEAR_TIME = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");

    /**
     * date format using dashes for year, month, and day.
     */
    final static SimpleDateFormat DASHES_YEAR_MONTH_DAY_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * date format using dashes for year, month, and day.
     */
    final static SimpleDateFormat HOUR_MINUTE_TIME = new SimpleDateFormat("HH:mm");

    /**
     * map used to determine which format to try to use to parse the date based on a pattern.
     */
    final static Map<Pattern, SimpleDateFormat> PATTERN_FORMATS;

    static {
        PATTERN_FORMATS = new LinkedHashMap<Pattern, SimpleDateFormat>();
        // Add the formats in the order that you want them tried, starting with the one that matches the default.
        PATTERN_FORMATS.put(Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{4}"), DOTS_DAY_MONTH_YEAR);
        PATTERN_FORMATS.put( Pattern.compile( "\\d{2}:\\d{2}" ), HOUR_MINUTE_TIME );
        PATTERN_FORMATS.put(Pattern.compile("\\d{2}-\\d{2}-\\d{4}"), DASHES_DAY_MONTH_YEAR);
        PATTERN_FORMATS.put(Pattern.compile("\\d{4}-\\d{2}-\\d{2}"), DASHES_YEAR_MONTH_DAY);
        PATTERN_FORMATS.put(Pattern.compile("\\d{2}/\\d{2}/\\d{4}"), SLASHES_MONTH_DAY_YEAR);
        PATTERN_FORMATS.put(Pattern.compile("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}\\.\\d{1,3}"), DASHES_DAY_MONTH_YEAR_TIME);
        PATTERN_FORMATS.put(Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{1,3}"), DASHES_YEAR_MONTH_DAY_TIME);
    }

    private SimpleDateFormat suggestedFormat;

    public DateTimeEditor() {
    }

    public DateTimeEditor( boolean useTimeFormat ) {
        if ( useTimeFormat ) {
            this.suggestedFormat = HOUR_MINUTE_TIME;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getAsText() {
        final Date date = (Date) getValue();
        if ( date == null ) {
            return "";
        }
        else if ( suggestedFormat == null ) {
            return DEFAULT_DATE_FORMAT.format(date);
        }
        return suggestedFormat.format(date);
    }

    /** {@inheritDoc} */
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        for (final Map.Entry<Pattern, SimpleDateFormat> patternFormatEntry : PATTERN_FORMATS.entrySet()) {
            try {
                final Pattern pattern = patternFormatEntry.getKey();
                final Matcher matcher = pattern.matcher(text);
                if (matcher.matches()) {
                    final SimpleDateFormat format = patternFormatEntry.getValue();
                    final Date date = format.parse(text);
                    if ( suggestedFormat != null && format.equals( suggestedFormat ) ) {
                        Time time = new Time( date.getTime() );
                        setValue( time );
                    }
                    else {
                        setValue( date );
                    }
                    return;
                }
            } catch (final ParseException e) {
                // If we can't parse using a format, we'll simply ignore that for now and see if any of the other formats might
                // apply.
            }
        }

        throw new IllegalArgumentException("Could not extract a valid date from " + text);
    }
}
