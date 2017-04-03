package orderbuilder.evaluator;

import java.util.List;

/**
 * Created by Sriram on 03-04-2017.
 */
public class Result {

    private String id;
    private List<String> order;

    public Result(String id, List<String> order) {
        this.id = id;
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getOrder() {
        return order;
    }

    public void setOrder(List<String> order) {
        this.order = order;
    }
}
