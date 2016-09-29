package top.javaw.orm;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.VoidType;
import top.javaw.mapping.MappingHandler;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by ZyL on 2016/9/28.
 */
public class OrmGenerator extends OrmBase {

    private Options options = new Options();
    private MappingHandler mappingHandler;

    public static class Options {
        public boolean annotation;
        public boolean comment = true;
        public MappingHandler mappingHandler = new MappingHandler();
    }

    public OrmGenerator(String host, int port, String username, String password, String dbName, Database database, Options options) {
        this(host, port, username, password, dbName, database);
        this.options = options;
    }

    public OrmGenerator(String host, int port, String username, String password, String dbName, Database database) {
        super(host, port, username, password, dbName, database);
        mappingHandler = options.mappingHandler;
    }


    public String generateEntity(String tableName) {
        ClassOrInterfaceDeclaration clazz = new ClassOrInterfaceDeclaration(EnumSet.of(Modifier.PUBLIC), false, camelName(tableName, true));
        setJPAAnnotation(clazz, tableName);
        List<BodyDeclaration<?>> members = new ArrayList<BodyDeclaration<?>>();
        List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>();
        List<MethodDeclaration> getters = new ArrayList<MethodDeclaration>();
        List<MethodDeclaration> setters = new ArrayList<MethodDeclaration>();
        clazz.setMembers(members);
        try {
            connection = getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            resultSet = databaseMetaData.getColumns(connection.getCatalog(), null, tableName, "%");
            preparedStatement = connection.prepareStatement("select * from " + tableName);
            ResultSet pks = databaseMetaData.getPrimaryKeys(connection.getCatalog(), null, tableName);
            String pkColumnName = null;
            if (pks.next()) {
                pkColumnName = pks.getString("COLUMN_NAME");
            }
            int i = 1;
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                String typeName = mappingHandler.handle(preparedStatement.getMetaData().getColumnClassName(i));
                String document = resultSet.getString("REMARKS");
                String fieldName = camelName(columnName, false);
                FieldDeclaration field = getField(pkColumnName, columnName, fieldName, typeName, document);
                MethodDeclaration getter = getGetter(fieldName, typeName);
                MethodDeclaration setter = getSetter(fieldName);
                getters.add(getter);
                setters.add(setter);
                fields.add(field);
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            release();
        }
        members.addAll(fields);
        members.addAll(getters);
        members.addAll(setters);
        return clazz.toString();
    }

    private void setJPAAnnotation(ClassOrInterfaceDeclaration clazz, String tableName) {
        if (options.annotation) {
            List<AnnotationExpr> annotations = new ArrayList<AnnotationExpr>();
            NameExpr nameExpr = new NameExpr("Table");
            List<MemberValuePair> pairs = new ArrayList<MemberValuePair>();
            Expression expression = new NameExpr("\"" + tableName + "\"");
            MemberValuePair memberValuePair = new MemberValuePair("name", expression);
            pairs.add(memberValuePair);
            AnnotationExpr annotation = new NormalAnnotationExpr(nameExpr, pairs);
            AnnotationExpr annotation1 = new MarkerAnnotationExpr(new NameExpr("Entity"));
            annotations.add(annotation1);
            annotations.add(annotation);
            clazz.setAnnotations(annotations);
        }
    }

    private MethodDeclaration getSetter(String fieldName) {
        EnumSet<Modifier> modifiers = EnumSet.of(Modifier.PUBLIC);
        MethodDeclaration setter = new MethodDeclaration(modifiers, new VoidType(), "set" + firstLetterUppercase(fieldName));
        List<Statement> stmts = new ArrayList<Statement>();
        NameExpr thisExpr = new NameExpr("this." + fieldName);
        NameExpr param = new NameExpr(fieldName);
        AssignExpr expr = new AssignExpr(thisExpr, param, AssignExpr.Operator.assign);
        ExpressionStmt stmt = new ExpressionStmt(expr);
        stmts.add(stmt);
        BlockStmt body = new BlockStmt(stmts);
        setter.setBody(body);
        return setter;
    }

    private FieldDeclaration getField(String pkColumnName, String columnName, String fieldName, String typeName, String document) {
        EnumSet<Modifier> modifiers = EnumSet.of(Modifier.PRIVATE);
        ClassOrInterfaceType type = new ClassOrInterfaceType(typeName);
        VariableDeclarator variableDeclarator = new VariableDeclarator();
        VariableDeclaratorId variableDeclaratorId = new VariableDeclaratorId(fieldName);
        variableDeclarator.setId(variableDeclaratorId);
        FieldDeclaration fieldDeclaration = new FieldDeclaration(modifiers, type, variableDeclarator);
        Comment comment = new JavadocComment(document);
        if (options.comment) {
            fieldDeclaration.setComment(comment);
        }
        if (options.annotation) {
            List<AnnotationExpr> annotations = new ArrayList<AnnotationExpr>();
            if (columnName.equals(pkColumnName)) {
                AnnotationExpr annotation = new MarkerAnnotationExpr(new NameExpr("Id"));
                List<MemberValuePair> pairs = new ArrayList<MemberValuePair>();
                MemberValuePair memberValuePair = new MemberValuePair("strategy", new NameExpr("GenerationType.AUTO"));
                pairs.add(memberValuePair);
                AnnotationExpr annotation2 = new NormalAnnotationExpr(new NameExpr("GeneratedValue"), pairs);
                annotations.add(annotation);
                annotations.add(annotation2);
            }

            if (!columnName.toLowerCase().equals(fieldName.toLowerCase())) {
                NameExpr nameExpr = new NameExpr("Column");
                List<MemberValuePair> pairs = new ArrayList<MemberValuePair>();
                MemberValuePair memberValuePair = new MemberValuePair("name", new NameExpr("\"" + columnName.toUpperCase() + "\""));
                pairs.add(memberValuePair);
                AnnotationExpr annotation = new NormalAnnotationExpr(nameExpr, pairs);
                annotations.add(annotation);
            }
            fieldDeclaration.setAnnotations(annotations);
        }
        return fieldDeclaration;
    }

    private MethodDeclaration getGetter(String fieldName, String typeName) {
        EnumSet<Modifier> modifiers = EnumSet.of(Modifier.PUBLIC);
        ClassOrInterfaceType type = new ClassOrInterfaceType(typeName);
        MethodDeclaration getter = new MethodDeclaration(modifiers, type, "get" + firstLetterUppercase(fieldName));
        List<Statement> stmts = new ArrayList<Statement>();
        Expression expr = new NameExpr(fieldName);
        Statement stmt = new ReturnStmt(expr);
        stmts.add(stmt);
        BlockStmt body = new BlockStmt(stmts);
        getter.setBody(body);
        return getter;
    }

    private String firstLetterUppercase(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private String camelName(String name, boolean firstLetterUppercase) {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (name == null || name.length() == 0) {
            // 没必要转换
            return "";
        } else if (!name.contains("_")) {
            // 不含下划线
            //如果全是大写
            boolean allUpper = true;
            for (int i = 0; i < name.length(); i++) {
                char current = name.charAt(i);
                if (((int) current) < ((int) ('A')) || ((int) current) > ((int) ('Z'))) {
                    allUpper = false;
                    break;
                }
            }
            if (allUpper) {
                return name.toLowerCase();
            }
            //将首字母小写，其他原样
            String firstLetter = name.substring(0, 1).toUpperCase();
            firstLetter = firstLetterUppercase ? firstLetter : firstLetter.toLowerCase();
            return firstLetter + name.substring(1);
        }
        // 用下划线将原始字符串分割
        String camels[] = name.split("_");
        for (String camel : camels) {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.length() == 0) {
                continue;
            }
            // 处理真正的驼峰片段
            if (result.length() == 0) {
                // 第一个驼峰片段，首字母?写
                String firstLetter = camel.substring(0, 1);
                result.append(firstLetterUppercase ? firstLetter.toUpperCase() : firstLetter.toLowerCase());
                result.append(camel.substring(1).toLowerCase());
            } else {
                // 其他的驼峰片段，首字母大写
                result.append(camel.substring(0, 1).toUpperCase());
                result.append(camel.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }
}
