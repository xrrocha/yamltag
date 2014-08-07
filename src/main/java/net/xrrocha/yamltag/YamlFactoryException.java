package net.xrrocha.yamltag;

import java.io.IOException;
import java.util.List;

public abstract class YamlFactoryException extends RuntimeException {
    public static final long serialVersionUID = 1L;

    public final static String NO_MESSAGE = "*** Error: no message";
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    
    public static class ClassLoadingException extends YamlFactoryException {
        public static final long serialVersionUID = 1L;
        
        public static final String FORMAT = "Error loading class '%1$s' by name";

        public ClassLoadingException(String className, Exception exception) {
            super(buildMessage(FORMAT, className), exception);
        }
    }
    
    public static class ResourceIOException extends YamlFactoryException {
        public static final long serialVersionUID = 1L;
        
        public static final String FORMAT = "I/O error retrieving resource '%1$s'";

        public ResourceIOException(String resourceName, IOException exception) {
            super(buildMessage(FORMAT, resourceName), exception);
        }
    }

    public YamlFactoryException(String message) {
        super(message);
    }

    public YamlFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public static String buildMessage(String format, Object... args) {
        if (format == null) {
            return NO_MESSAGE;
        }
        return String.format(format, args);
    }

    public static String joinMessages(List<String> errorMessages) {
        if (errorMessages == null) {
            return NO_MESSAGE;
        }
        StringBuilder sb = new StringBuilder();
        for (String errorMessage : errorMessages) {
            sb.append(LINE_SEPARATOR);
            sb.append("\t- ");
            sb.append(errorMessage);
        }
        return sb.toString();
    }
}