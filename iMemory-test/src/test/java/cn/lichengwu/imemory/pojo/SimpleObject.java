package cn.lichengwu.imemory.pojo;

import org.msgpack.annotation.Message;

import java.io.Serializable;
import java.util.Map;

/**
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-14 9:55 PM
 */
@Message
public class SimpleObject implements Serializable {

    private static final long serialVersionUID = -2628490795076341668L;

    private int intField;

    private String stringField;

    private long longField;


    private Map<Integer, String> mapField;

    public int getIntField() {
        return intField;
    }

    public void setIntField(int intField) {
        this.intField = intField;
    }

    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    public long getLongField() {
        return longField;
    }

    public void setLongField(long longField) {
        this.longField = longField;
    }

    public Map<Integer, String> getMapField() {
        return mapField;
    }

    public void setMapField(Map<Integer, String> mapField) {
        this.mapField = mapField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SimpleObject object = (SimpleObject) o;

        if (intField != object.intField) {
            return false;
        }
        if (longField != object.longField) {
            return false;
        }
        if (mapField != null ? !mapField.equals(object.mapField) : object.mapField != null) {
            return false;
        }
        if (stringField != null ? !stringField.equals(object.stringField) : object.stringField != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = intField;
        result = 31 * result + (stringField != null ? stringField.hashCode() : 0);
        result = 31 * result + (int) (longField ^ (longField >>> 32));
        result = 31 * result + (mapField != null ? mapField.hashCode() : 0);
        return result;
    }
}
