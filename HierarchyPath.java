
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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


