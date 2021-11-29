
/**
 *
 * @author Lana
 */
public class Employee {
    
    public int id;
    public String name;
    public float evaluation;
    public int parent;

    public Employee(int parent, String name, int id, float evaluation) {
        this.name = name;
        this.id = id;
        this.evaluation = evaluation;
        this.parent = parent;
    }
}


