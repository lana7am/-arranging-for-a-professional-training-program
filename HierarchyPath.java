
import java.util.List;


/**
 *
 * @author Lana
 */

public class HierarchyPath {
    
    public List<Integer> path;
    public int last;
    public float score;

    public HierarchyPath(List<Integer> path, int last, float score) {
        this.path = path;
        this.last = last;
        this.score = score;
    }
}


