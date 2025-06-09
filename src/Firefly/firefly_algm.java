package Firefly;

import static Code.Run.PM;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import static Code.Run.VM;
import static Firefly.run.VM_Migration_update;

public class firefly_algm {
    public static ArrayList<Integer>best=new ArrayList<>();
    public static void firefly() throws FileNotFoundException
    {
        Random r=new Random();
        ArrayList<ArrayList<Integer>> solution=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ArrayList<Integer> tem = new ArrayList();
            for (int j = 0; j < VM; j++) {
                int p = r.nextInt(PM)+1;
                tem.add(p);
            }
            solution.add(tem);
        }  
        ArrayList<Double> Fit=new ArrayList<Double> ();
        firefly_algm ff=new firefly_algm();
        for(int it=0;it<2;it++)
        {
            Fit=new ArrayList<Double> ();
            Fit = fitness.evaluate(solution);
            ff.updateSolution(Fit, solution);
        }
      
        int p=Fit.indexOf(Collections.max(Fit));
        best=solution.get(p);
        VM_Migration_update = best;
    }
    
    void updateSolution(ArrayList<Double> fit,ArrayList<ArrayList<Integer>> solution)
    {
        double gamma=1.0;
        double theeta=1.5;
        double m=1.0;
        double py=0.2;
        for(int i=0;i<solution.size()-1;i++) {
            for(int j=(i+1);j<solution.size();j++) {
                if(fit.get(j)>fit.get(i)) {
                    ArrayList<Integer> tem=new ArrayList<>();
                    for(int k=0;k<solution.get(i).size();k++) {
                        double distance=Math.sqrt(Math.pow((solution.get(i).get(k)-solution.get(j).get(k)),2));
                        double attractiveness=gamma*Math.exp(((-1)*theeta)*Math.pow(distance,m));
                        tem.add((int)(solution.get(i).get(k)+(attractiveness*distance)+(py*(Math.random()-0.5))));  
                    }
                    solution.remove(i);
                    solution.add(i,tem);
                }
            }
        }
    }
}
