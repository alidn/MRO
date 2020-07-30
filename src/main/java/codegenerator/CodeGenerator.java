package codegenerator;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeVariableName;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;

public class CodeGenerator {
    private CodeGenerator() {
    }

    public static MethodSpec methodFromQuery(Query query) {
        if (query.getMetadata().getQueryType().equals(Metadata.QueryType.QUERY_TYPE)) {
            if (query.getMetadata().getResultType().equals(Metadata.ResultType.MANY)) {
                return methodFromSimpleQuery(query);
            } else if (query.getMetadata().getResultType().equals(Metadata.ResultType.ONE)) {
                return methodFromQueryForObject(query);
            }
        } else if (query.getMetadata().getQueryType().equals(Metadata.QueryType.RETURNING_EXECUTE)) {
            if (query.getMetadata().getResultType().equals(Metadata.ResultType.MANY)) {
                return executeReturningMany(query);
            } else if (query.getMetadata().getResultType().equals(Metadata.ResultType.ONE)) {
                return methodFromQueryForObject(query);
            } else if (query.getMetadata().getResultType().equals(Metadata.ResultType.AFFECTED)) {
                return methodFromQueryReturningAffected(query);
            }
        }
        return null;
    }

    private static MethodSpec methodFromQueryReturningAffected(Query query) {
        String returnType = query.getMetadata().getReturnType();

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(query.getMetadata().getName())
                .addModifiers(Modifier.PUBLIC)
                .addStatement(getParamsListAsStr(query.getParams()))
                .returns(TypeVariableName.get(returnType))
                .addParameters(getParamSpecs(query.getParams()));

        methodBuilder.addStatement("int affectedRows = this.jdbcTemplate.update($S, params)",
                query.getQuery())
                .addStatement("return affectedRows");

        return methodBuilder.build();
    }

    private static String executeReturningOne(Query query) {
        return null;
    }

    private static MethodSpec executeReturningMany(Query query) {
        return null;
    }

    private static MethodSpec methodFromQueryForObject(Query query) {
        String returnType = query.getMetadata().getReturnType();

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(query.getMetadata().getName())
                .addModifiers(Modifier.PUBLIC)
                .addStatement(getParamsListAsStr(query.getParams()))
                .returns(TypeVariableName.get(returnType))
                .addParameters(getParamSpecs(query.getParams()))
                .addParameter(TypeVariableName.get("RowMapper<" + returnType + ">"),
                        "rowMapper");

        methodBuilder.beginControlFlow("try")
                .addStatement("return this.jdbcTemplate.queryForObject($S, params, rowMapper)",
                        query.getQuery())
                .nextControlFlow("catch ($T e)", Exception.class)
                .addStatement("return null")
                .endControlFlow();

        return methodBuilder.build();
    }

    public static MethodSpec methodFromSimpleQuery(Query query) {
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

        return methodBuilder.build();
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
