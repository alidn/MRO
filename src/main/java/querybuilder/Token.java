package querybuilder;

public class Token {
    private final String value;
    private final Type type;

    public enum Type {
        SQL,
        EMPTY_LINE,
        NAME,
        QUERY_TYPE,
        RESULT_TYPE
    }

    public Token(String value, Type type) {
        this.value = value;
        this.type = type;
    }

    public static Token fromLine(String line) {
        if (line.length() == 0) {
            return new Token("", Type.EMPTY_LINE);
        } else if (line.matches("-- @name.* .*")) {
            return new Token(getCommentValue(line), Type.NAME);
        } else if (line.matches("-- @type.* .*")) {
            return new Token(getCommentValue(line), Type.QUERY_TYPE);
        } else if (line.matches("-- @result.* .*")) {
            return new Token(getCommentValue(line), Type.RESULT_TYPE);
        } else {
            return new Token(line, Type.SQL);
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

    @Override
    public String toString() {
        return "querybuilder.Token{" +
                "value='" + value + '\'' +
                ", type=" + type +
                '}';
    }
}
