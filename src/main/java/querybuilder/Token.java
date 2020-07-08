package querybuilder;

public class Token {
    private final String value;
    private final Type type;
    private final int lineNumber;

    public enum Type {
        SQL,
        EMPTY_LINE,
        NAME,
        QUERY_TYPE,
        RESULT_TYPE
    }

    public Token(String value, Type type, int lineNumber) {
        this.value = value;
        this.type = type;
        this.lineNumber = lineNumber;
    }

    public static Token fromLine(String line, int lineNumber) {
        if (line.length() == 0) {
            return new Token("", Type.EMPTY_LINE, lineNumber);
        } else if (line.matches("-- @name.* .*")) {
            return new Token(getCommentValue(line), Type.NAME, lineNumber);
        } else if (line.matches("-- @type.* .*")) {
            return new Token(getCommentValue(line), Type.QUERY_TYPE, lineNumber);
        } else if (line.matches("-- @result.* .*")) {
            return new Token(getCommentValue(line), Type.RESULT_TYPE, lineNumber);
        } else {
            return new Token(line, Type.SQL, lineNumber);
        }
    }

    private static String getCommentValue(String line) {
        String[] s = line.split("-- @.* ");
        return s[1];
    }

    public String getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    public int getLineNumber() { return lineNumber + 1; }

    @Override
    public String toString() {
        return "querybuilder.Token{" +
                "value='" + value + '\'' +
                ", type=" + type +
                '}';
    }
}
