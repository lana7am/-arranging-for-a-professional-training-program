
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author Lana
 */


  public class main {

    public final Hashtable<Integer, Employee> employees;
    public Hashtable<Integer, List<Integer>> hierarchy;

    public main(Hashtable<Integer, Employee> employees) {
        this.employees = employees;
        generateHierarchy();
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        
        // create a hashtable for employee objects
        
        Hashtable<Integer, Employee> employees = new Hashtable<>();
         
        //read from file [ID of parent : Name : ID : Evaluation Score]
        Scanner read = new Scanner(new File("Employee.txt.txt"));

        while(read.hasNext()){
        
            String line = read.nextLine();
            int IDp = line.indexOf(":");           
            int name = line.indexOf(":" , IDp+1);
            int ID = line.indexOf(":" , name+1);
                    
            Employee newEmployee = new Employee( Integer.parseInt(line.substring(0 , IDp)),line.substring(IDp+1 , name)
                    ,Integer.parseInt(line.substring(name+1 , ID)) , Float.parseFloat(line.substring(ID+1 , line.length() ) )  );
                        
                //entering the employees into the hashtable
                //let the Evaluation Score be the key 
                employees.put(newEmployee.id, newEmployee);
                
        }//while 
                    
        // creating a class object                    
        main employeeHierarchy = new main(employees);

        // Getting the output result for Brutal Force Algorithm
        //optimal subset 
        Map<Float, List<Integer>> OPsubsetBF = employeeHierarchy.BrutalForce();
        
        // 
        float maxEvaluationScoreBF =  (float) OPsubsetBF.keySet().toArray()[0];
        List<Integer> outputBF = OPsubsetBF.get(maxEvaluationScoreBF);
        
        //printing brute force results
        System.out.println("\nBrutal Force");
        System.out.println("max sum of the evaluation-based ratings = "+maxEvaluationScoreBF);
        for (int i : outputBF) {
            System.out.println(employees.get(i).id + " " + employees.get(i).name);
        }

        // Getting the output result for Dynamic Programming Algorithm
        Map<Float, List<Integer>> OPsubsetDP = employeeHierarchy.DP();

        float maxEvaluationScoreDP =  (float) OPsubsetDP.keySet().toArray()[0];
        List<Integer> outputDP = OPsubsetDP.get(maxEvaluationScoreDP);

        //print the DP results
        System.out.println("\ndynamic programming");
        System.out.println("max sum of the evaluation-based ratings = "+maxEvaluationScoreDP);
        for (int i : outputDP) {
            System.out.println(employees.get(i).id + " " + employees.get(i).name);
        }
    }//main

    public void generateHierarchy() {
        
        Hashtable<Integer, List<Integer>> hierarchy = new Hashtable<>();

        for (int i = 1; i <= employees.size(); i ++) {
            if (hierarchy.containsKey(employees.get(i).parent)) {
                hierarchy.get(employees.get(i).parent).add(i);
            }
            else {
                hierarchy.put(employees.get(i).parent, new ArrayList<Integer>(List.of(i)));
            }
        }

        this.hierarchy = hierarchy;
    }

    public Hashtable<Integer, List<Integer>> getHierarchy() {
        return hierarchy;
    }

    public Map<Float, List<Integer>> BrutalForce() {
        //output of this method
        List<Integer> ids = new ArrayList<>();
        Map<Float, List<Integer>> output = new HashMap<>();
        float maximumEvalScore = 0;

        // create an array of zeros to store the status of employees (selected 1 / not selected 0)
        int[] StatusArray = new int[employees.size()+1];
        Arrays.fill(StatusArray, 0);

        /*Initialize a path (id = 0). A path contains status array of employees[0.1]
        and the visited employee ids*/
        
        Map<int[], List<Integer>> initialPath = new HashMap<>();
        initialPath.put(StatusArray.clone(), new ArrayList<>(List.of(0)));

        // Initialize a set of paths to find maximum evaluation score
        List<Map<int[], List<Integer>>> paths = new ArrayList<>();
        paths.add(initialPath);

        while (!paths.isEmpty()) {
            // Pop the first path and its status array
            Map<int[], List<Integer>> path = paths.remove(0);
            int[] statusArray = (int[]) path.keySet().toArray()[0];

            if (path.get(statusArray).isEmpty()) {
                //there is no more children in this path therefore calculate the score of this path
                float score = 0;
                for (int i = 0; i < statusArray.length; i ++) {
                    if (statusArray[i] == 1) {
                        score += employees.get(i).evaluation;
                    }
                }

                // Compare the score with maximum evaluation score and update
                //this compares the  maximum evaluation score of the current path with the the path before it
                if (score > maximumEvalScore) {
                    maximumEvalScore = score;
                    StatusArray = statusArray.clone();
                }
            }else { // if there is more children in this path
                // Pop the first employee id visited
                int nextId = path.get(statusArray).remove(0);

                if (hierarchy.containsKey(nextId)) {
                    // If the hierarchy contains id, it means the employee with this id has lower level employee/s.
                    List<Integer> nextIds1 = new ArrayList<>();
                    nextIds1.addAll(path.get(statusArray));
                    nextIds1.addAll(hierarchy.get(nextId)); // Add lower level employees to search in next iterations
                    int[] nextStatusArray1 = statusArray.clone();

                    // Create a new path with the new visited employee ids and add it to paths.
                    Map<int[], List<Integer>> newPath1 = new HashMap<>();
                    newPath1.put(nextStatusArray1, nextIds1);
                    paths.add(newPath1);

                    // Check whether the employee's immediate head is not selected. Then create a new status array with
                    // the employee selected and create a new path. Then add it to the paths.
                    if (nextId != 0 && statusArray[employees.get(nextId).parent] == 0) {
                        List<Integer> nextIds2 = new ArrayList<>();
                        nextIds2.addAll(path.get(statusArray));
                        nextIds2.addAll(hierarchy.get(nextId));
                        int[] nextStatusArray2 = statusArray.clone();
                        nextStatusArray2[nextId] = 1;

                        Map<int[], List<Integer>> newPath2 = new HashMap<>();
                        newPath2.put(nextStatusArray2, nextIds2);
                        paths.add(newPath2);
                    }
                }
                else {
                    // If the hierarchy doesn't contain id, it means the employee with this id doesn't have children
                    List<Integer> nextIds1 = new ArrayList<>();
                    nextIds1.addAll(path.get(statusArray));
                    int[] nextStatusArray1 = statusArray.clone();

                    Map<int[], List<Integer>> newPath1 = new HashMap<>();
                    newPath1.put(nextStatusArray1, nextIds1);
                    paths.add(newPath1);

                    // Check whether the employee's immediate head is not selected. Then create a new status array with
                    // the employee selected and create a new path. Then add it to the paths.
                    if (statusArray[employees.get(nextId).parent] == 0) {
                        List<Integer> nextIds2 = new ArrayList<>();
                        nextIds2.addAll(path.get(statusArray));
                        int[] nextStatusArray2 = statusArray.clone();
                        nextStatusArray2[nextId] = 1;

                        Map<int[], List<Integer>> newPath2 = new HashMap<>();
                        newPath2.put(nextStatusArray2, nextIds2);
                        paths.add(newPath2);
                    }
                }
            }
        }

        // Add ids of employees which are selected in the optimal solution.
        for (int i = 0; i <= employees.size(); i ++) {
            if (StatusArray[i] == 1) {
                ids.add(i);
            }
        }

        output.put(maximumEvalScore, ids);

        return output;
    }

    public Map<Float, List<Integer>> DP() {
        // Initialize outputs of the method
        List<Integer> ids = new ArrayList<>();
        Map<Float, List<Integer>> tree = new HashMap<>();
        float maximumEvalScore = 0;

        // Initialize children of the  head
        List<Integer> children = hierarchy.get(0);

        // Check for maximum scores of immediate children
        for (int child: children) {
            
            // Check for maximum scores if the current employee is invited
            
            Map<Float, List<Integer>> subTree1 = InvitedSubTreeList(child);
            float value1 = (float) subTree1.keySet().toArray()[0];

            // Check for maximum scores if the current employee is not invited
            
            Map<Float, List<Integer>> subTree2 = NotInvitedSubTreeList(child);
            float value2 = (float) subTree2.keySet().toArray()[0];

            // Compare two scores and update the maximum evaluation score with selected ids of employees
            if (value1 >= value2) {
                ids.addAll(subTree1.get(value1));
                maximumEvalScore += value1;
            } else {
                ids.addAll(subTree2.get(value2));
                maximumEvalScore += value2;
            }
        }

        tree.put(maximumEvalScore, ids);

        return tree;
    }

    public Map<Float, List<Integer>> InvitedSubTreeList(int root) {
        
        /* Initializing the output of the method which is a hashmap containing the max score and 
        and a list structure  containing employee ids as key*/
        
        List<Integer> ids = new ArrayList<>();
        ids.add(root);  // root id is added since root employee is invited
        Map<Float, List<Integer>> tree = new HashMap<>();
        float maximum = employees.get(root).evaluation; //since the root employee is invited we add the score right away

        // Check whether the root employee has children 
        if (hierarchy.containsKey(root)) {
            List<Integer> children = hierarchy.get(root);
            // Check for maximum scores of those children
            for (int child: children) {
                /* Since root employee is invited, children cannot be invited. threfore we check only for 
                cases when the children are not invited*/
                Map<Float, List<Integer>> subTree = NotInvitedSubTreeList(child);
                float value = (float) subTree.keySet().toArray()[0];
                ids.addAll(subTree.get(value));
                maximum += value;
            }
        }

        tree.put(maximum, ids);

        return tree;
    }

    public Map<Float, List<Integer>> NotInvitedSubTreeList(int root) {
        
        /* Initializing the output of the method which is a hashmap containing the max score and 
        and a list structure  containing employee ids as key*/
        List<Integer> ids = new ArrayList<>();
        Map<Float, List<Integer>> tree = new HashMap<>();
        float maximum = 0;

        // Check if the root employee has a children 
        if (hierarchy.containsKey(root)) {
            List<Integer> children = hierarchy.get(root);

            
            for (int i: children) {
                

                //first we will check for when current employee(root) is invited
                Map<Float, List<Integer>> invited = InvitedSubTreeList(i);
                float valueinvited = (float) invited.keySet().toArray()[0];

                //secondly we will check for when current employee(root) is NOT invited
                Map<Float, List<Integer>> notinvited = NotInvitedSubTreeList(i);
                float valueNotinvited = (float) notinvited.keySet().toArray()[0];

                // Compare the two scores and update the maximum evaluation score with selected ids of employees
                if (valueinvited >= valueNotinvited) {
                    ids.addAll(invited.get(valueinvited));
                    maximum += valueinvited;
                } else {
                    ids.addAll(notinvited.get(valueNotinvited));
                    maximum += valueNotinvited;
                }
            }
        }

        tree.put(maximum, ids);

        return tree;
    }

   
 }


