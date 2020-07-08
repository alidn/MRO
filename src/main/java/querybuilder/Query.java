package querybuilder;

public class Query {
    private Metadata metadata;
    private String query;
    private Param[] params;

    static class Param {
        String name;
        int[] indices;
    }
}
