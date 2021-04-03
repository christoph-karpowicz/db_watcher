package com.dbw.cli;

import com.dbw.log.ErrorMessages;
import com.dbw.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShowLatestOperationsOption {
    private static final int MINUTE_IN_SECONDS = 60;
    private static final int HOUR_IN_SECONDS = MINUTE_IN_SECONDS * 60;

    private boolean isTime;
    private short value;

    public boolean isTime() {
        return isTime;
    }

    public void setIsTime(boolean isTime) {
        this.isTime = isTime;
    }

    public short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    public static ShowLatestOperationsOption create(String input) throws NumberFormatException {
        ShowLatestOperationsOption opt = new ShowLatestOperationsOption();
        if (StringUtils.isNumeric(input)) {
            opt.setIsTime(false);
            opt.setValue(Short.parseShort(input));
        } else {
            if (!isValid(input)) {
                throw new NumberFormatException(ErrorMessages.CLI_INVALID_LATEST_OP);
            }
            int minutesMultiplier = input.contains("m") ? MINUTE_IN_SECONDS : 1;
            int hoursMultiplier = input.contains("h") ? HOUR_IN_SECONDS : 1;
            int extractedNumber = Short.parseShort(input.substring(0, input.length() - 1));
            int timeInSeconds = extractedNumber * minutesMultiplier * hoursMultiplier;
            opt.setIsTime(true);
            opt.setValue((short)timeInSeconds);
        }
        return opt;
    }

    private static boolean isValid(String input) {
        Pattern validationPattern = Pattern.compile("^\\d+(s|m|h){1}$");
        Matcher validationMatcher = validationPattern.matcher(input.trim());
        return validationMatcher.matches();
    }
}
