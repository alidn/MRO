package querybuilder;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeVariableName;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class CodeGenerator {
    private CodeGenerator() {
    }

    public static String methodFromQuery(Query query) {
        if (query.getMetadata().getQueryType().equals(Metadata.QueryType.QUERY_TYPE)) {
            if (query.getMetadata().getResultType().equals(Metadata.ResultType.MANY)) {
                return methodFromSimpleQuery(query);
            } else {
                return methodFromQueryForObject(query);
            }
        }
        return null;
    }

    private static String methodFromQueryForObject(Query query) {
        TypeVariableName typeVariableName = TypeVariableName.get("T");

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(query.getMetadata().getName())
                .addModifiers(Modifier.PUBLIC)
                .addStatement(getParamsListAsStr(query.getParams()))
                .addTypeVariable(typeVariableName)
                .returns(TypeVariableName.get("Iterable<T>"))
                .addParameters(getParamSpecs(query.getParams()))
                .addParameter(TypeVariableName.get("RowMapper<T>"),
                        "rowMapper")
                .addStatement("return this.jdbcTemplate.query($S, params, rowMapper)",
                        query.getQuery());

        methodBuilder.beginControlFlow("try")
                .addStatement("$T o = this.jdbcTemplate.queryForObject($S, params, rowMapper)",
                        typeVariableName, query.getQuery())
                .nextControlFlow("catch ($T e)", Exception.class)
                .addStatement("return null")
                .endControlFlow();

        return methodBuilder.build().toString();
    }

    public static String methodFromSimpleQuery(Query query) {
        TypeVariableName typeVariableName = TypeVariableName.get("T");

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(query.getMetadata().getName())
                .addModifiers(Modifier.PUBLIC)
                .addStatement(getParamsListAsStr(query.getParams()))
                .addTypeVariable(typeVariableName)
                .returns(TypeVariableName.get("Iterable<T>"))
                .addParameters(getParamSpecs(query.getParams()))
                .addParameter(TypeVariableName.get("RowMapper<T>"),
                        "rowMapper")
                .addStatement("return this.jdbcTemplate.query($S, params, rowMapper)",
                        query.getQuery());

        return methodBuilder.build().toString();
    }

    private static String getParamsListAsStr(ArrayList<Query.Param> params) {
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            list.append(params.get(i).getName());
            if (i != params.size() - 1) {
                list.append(", ");
            }
        }
        return "Object[] params = { " + list.toString() + " }";
    }

    private static ArrayList<ParameterSpec> getParamSpecs(ArrayList<Query.Param> params) {
        ArrayList<ParameterSpec> parameterSpecs = new ArrayList<>();
        // TODO: Remove duplicates
        for (Query.Param param : params) {
            parameterSpecs.add(ParameterSpec.builder(Object.class, param.getName()).build());
        }
        return parameterSpecs;
    }
}
