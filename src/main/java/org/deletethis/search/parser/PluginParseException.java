package org.deletethis.search.parser;

public class PluginParseException extends Exception {
    private static final long serialVersionUID = 1113211512231569730L;

    private final ErrorCode errorCode;

    private static String append(ErrorCode errorCode, String message) {
        if(message == null)
            return errorCode.toString();
        else
            return errorCode.toString() + ": " + message;
    }

    public PluginParseException(ErrorCode errorCode) {
        super(errorCode.toString());
        this.errorCode = errorCode;
    }

    public PluginParseException(ErrorCode errorCode, String message) {
        super(append(errorCode, message));
        this.errorCode = errorCode;
    }

    public PluginParseException(ErrorCode errorCode, String message, Throwable throwable) {
        super(append(errorCode, message), throwable);
        this.errorCode = errorCode;
    }

    public PluginParseException(ErrorCode errorCode, Throwable throwable) {
        super(errorCode.toString(), throwable);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
