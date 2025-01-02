package com.mobilegroup.core.exceptions;

public class MgException extends Exception{
    private final ErrorCode errorCode;

    /**
     * Constructor for MgException with an error code and message.
     *
     * @param errorCode The specific error code.
     * @param message   The error message.
     */
    public MgException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor for MgException with an error code, message, and a cause.
     *
     * @param errorCode The specific error code.
     * @param message   The error message.
     * @param cause     The underlying exception cause.
     */
    public MgException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Get the error code associated with this exception.
     *
     * @return The error code.
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Enum to define SDK-specific error codes.
     */
    public enum ErrorCode {
        BT_CONNECTION_FAILED,
        NETWORK_CONNECTION_FAILED,
        INVALID_TOKEN,
        UNAUTHORIZED_ACCESS,
        TIMEOUT_ERROR,
        UNKNOWN_ERROR,
        MISSING_PERMISSIONS,
        SEND_COMMAND_ERROR
    }

    @Override
    public String toString() {
        return "MgException{" +
                "errorCode=" + errorCode +
                ", message=" + getMessage() +
                '}';
    }
}
