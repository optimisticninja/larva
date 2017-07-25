package com.runbox.clazz.entry.constant;

import com.runbox.clazz.reader.ConstantReader;

import javax.json.JsonObjectBuilder;

public class LongConstant extends Constant {

    public LongConstant(ConstantReader reader) {
        super(reader, TYPE_LONG);
    }
    
    public LongConstant(ConstantReader reader, long value) {
        super(reader, TYPE_LONG); this.value = value;
    }

    private long value = 0;

    public LongConstant value(long value) {
        this.value = value;
        return this;
    }

    public long value() {
        return value;
    }

    @Override
    public JsonObjectBuilder toJson() {
        return super.toJson().add("value", value);
    }
}
