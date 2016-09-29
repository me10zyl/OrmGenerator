package com.github.ormgenerator.mapping;

/**
 * Created by ZyL on 2016/9/29.
 */
public class Mapping {
    private String source;
    private String target;

    public Mapping(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
