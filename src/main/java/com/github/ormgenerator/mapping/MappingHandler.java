package com.github.ormgenerator.mapping;

/**
 * Created by ZyL on 2016/9/29.
 */
public class MappingHandler {

    private Mapping[] mappings = new Mapping[]{
            new Mapping("Timestamp", "Date"),
            new Mapping("Float", "BigDecimal"),
            new Mapping("Double", "BigDecimal")
    };

    public MappingHandler() {
    }

    public String handleMySQL(String columnClassName) {
        return handle(columnClassName, mappings);
    }

    public String handleOracle(String columnClassName, String columnTypeName, int scale, boolean isPrimaryKey) {
        String process = handle(columnClassName, mappings);
        if (scale == 0 && columnTypeName.equals("NUMBER")) {
            if (isPrimaryKey) {
                process = "Long";
            } else {
                process = "Integer";
            }
        }
        return process;
    }

    private String handle(String source, Mapping[] mappings) {
        String target = ignorePackageName(source);
        for (Mapping mapping : mappings) {
            if (mapping.getSource().equals(target)) {
                target = mapping.getTarget();
            }
        }
        return target;
    }

    private String ignorePackageName(String clazz) {
        String[] split = clazz.split("\\.");
        return split[split.length - 1];
    }

    public Mapping[] getMappings() {
        return mappings;
    }

    public void setMappings(Mapping[] mappings) {
        this.mappings = mappings;
    }
}
