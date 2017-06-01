package orderbuilder.evaluator;

/**
 * Created by Sriram on 28-05-2017.
 */
public class FileChange {
    String fileName;
    Long change;
    Change_type type;

    public FileChange(String name, Long value, Change_type type) {
        fileName = name;
        change = value;
        this.type = type;
    }

    public enum Change_type {
        add, delete, rename, modify;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getChange() {
        return change;
    }

    public void setChange(Long change) {
        this.change = change;
    }

    public Change_type getType() {
        return type;
    }

    public void setType(Change_type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "{" + fileName + "," + change + "," + type + "}";
    }
}
