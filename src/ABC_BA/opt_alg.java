package ABC_BA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import static Code.Run.VM;
import static Code.Run.PM;
import static ABC_BA.run.VM_Migration_update;

public class opt_alg {
    public static ArrayList<Integer> best = new ArrayList();
    public static void func() {
        int i,j = 0, Max_itr = 5;
        best = new ArrayList<>();
        ArrayList<Integer> t2 = new ArrayList();
        
        //INITIAL SOLUTION 
        ArrayList<ArrayList<Integer>> solution=new ArrayList<>();
        for(i = 0; i < 10; i++) {
            ArrayList<Integer> tem=new ArrayList();
            for(j = 0; j < VM; j++) {
                int r = (int)(Math.random() * PM + 1); 
                tem.add(r);
            }
            solution.add(tem);
        }
        
        int w = 0;
        while(w < Max_itr)                     
        {
            ArrayList<Double> fit=new ArrayList();
            fit = fitness.func(solution);
        
        //EMPLOYED BEE
            employed_bee(solution, fit, w);
            w++;
        }           //end of while loop
        VM_Migration_update = best;
    }

    private static void employed_bee(ArrayList<ArrayList<Integer>> solution, ArrayList<Double> fit, int w) {
        int i = 0, j, k = new Random().nextInt(solution.size());  //random solution index (size of solution)
            ArrayList<ArrayList<Integer>> v=new ArrayList<>();
            ArrayList<Double> fit1 = new ArrayList();
            while(i < solution.size()) {                            
                double pi = (Math.random()*2-1); int d = 0;      //pi => random no b/w -1 and 1
                ArrayList<Integer> v1=new ArrayList();
                for(j=0;j<solution.get(i).size();j++) {
                    d = (int) (solution.get(i).get(j) + (pi * (solution.get(i).get(j) - solution.get(k).get(j))));     //d=aij+pi(aij-akj)
                    v1.add(d);  //v1 = d
                } 
                fit1 = fitness.bee_func(v1);
                
    //X2:           
                if(fit1.get(0) > fit.get(0))
                    v.add(v1);
                else
                    v.add(solution.get(i));
                i++;
            }   //end of employed while loop
        
        ArrayList<Double> fit2=new ArrayList();
        fit2 = fitness.func(v);
        
    //Probability Values
        double h = 0;       
        for(i = 0; i < solution.size(); i++) {
            h += fit2.get(i);
        } 
        
        ArrayList<Double> p = new ArrayList();
        for(i = 0; i < solution.size(); i++) {
            double p1 = fit2.get(i) / h;      //p = Single data fitness / total fitness
            p.add(p1);
        } 
        
        //ONLOOKER BEE
            onlooker_bee(solution, fit2, v, w);
    }

    private static void onlooker_bee(ArrayList<ArrayList<Integer>> solution, ArrayList<Double> fit2, ArrayList<ArrayList<Integer>> v, int w) {
        int k = new Random().nextInt(solution.size());
        int i = 0, j;
        
        ArrayList<ArrayList<Integer>> u=new ArrayList<>();
        while(i < solution.size())                   
        {
            double pi=(Math.random()*2-1), e=0, g; int d;
            ArrayList<Integer> v2=new ArrayList();
            ArrayList<Double> f3=new ArrayList();
            ArrayList<Double> fit3=new ArrayList();
            for(j=0;j<solution.get(i).size();j++) 
            {
                d=(int) (solution.get(i).get(j)+pi*(solution.get(i).get(j)-solution.get(k).get(j)));
                v2.add(d);
            }
            fit3 = fitness.bee_func(v2); 
            
        //X3 Value             
            if(fit3.get(0)>fit2.get(0))
                u.add(v2);
            else
                u.add(v.get(i));
            i++;
        }                                     //end of Onlooker while loop
        
        ArrayList<Double> fit3=new ArrayList();
        fit3 = fitness.func(u);
        
    //Memorize best
        int l = fit3.indexOf(Collections.max(fit3));             // max of fit3
        best = u.get(l);
        scout_bee(solution, u, w);
    }

    private static void scout_bee(ArrayList<ArrayList<Integer>> solution, ArrayList<ArrayList<Integer>> u, int w) {
        //SCOUT BEE PHASE 
        ArrayList<Integer> t=new ArrayList();
        ArrayList<ArrayList<Integer>> t1 = new ArrayList<>();
        for(int i=0;i<solution.size();i++) {
            if(solution.get(i) == u.get(i))                 //checking equal rows of X and X3 (equal = 1)
                t.add(1);
            else
                t.add(0);
        }
        t1.add(t);
        
        int a1;
        for(int i=0;i<solution.size();i++) {
            for(int j=0;j<solution.get(i).size();j++) {
                if(t1.get(0).get(i) == (w+1)) {
                    int r = (int)(Math.random() * PM + 1); 
                    a1=i;
                    solution.get(a1).set(j,r);            
                }
            }
        }
    }
}