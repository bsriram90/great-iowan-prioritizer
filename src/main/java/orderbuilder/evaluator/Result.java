package orderbuilder.evaluator;

import orderbuilder.model.differenceMatrix.TestTraceDifferenceMatrix;

import java.util.List;

/**
 * Created by Sriram on 03-04-2017.
 */
public class Result implements Comparable {

    private String id;
    private List<String> order;
    private Float score;

    public Result(String id, List<String> order) {
        this.id = id;
        this.order = order;
    }

    public Result(String id, List<String> order, List<String> referenceResult, TestTraceDifferenceMatrix diff) {
        this(id, order);
        this.score = generateScore(referenceResult, diff);
    }

    private Float generateScore(List<String> referenceResult, TestTraceDifferenceMatrix diff) {
        return CorrelationScore.customCorrelationScore(order, referenceResult);
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

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Result)) {
            return false;
        }
        Result r = (Result) obj;
        return r.getId().equals(this.getId());
    }

    @Override
    public int compareTo(Object o) {
        return this.getScore().compareTo(((Result) o).getScore());
    }
}
