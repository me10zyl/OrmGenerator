package top.javaw.mapping;

/**
 * Created by ZyL on 2016/9/29.
 */
public class MappingHandler {

    private Mapping[] mappings = new Mapping[]{
            new Mapping("Timestamp", "Date"),
            new Mapping("Float", "BigDecimal")
    };

    public MappingHandler() {
    }

    public MappingHandler(Mapping[] mappings) {
        this.mappings = mappings;
    }

    public String handle(String source){
        String target = ignorePackageName(source);
        for(Mapping mapping : mappings){
            if(mapping.getSource().equals(target)){
                target = mapping.getTarget();
            }
        }
        return target;
    }

    private String ignorePackageName(String clazz){
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
