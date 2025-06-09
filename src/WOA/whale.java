package WOA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import static Code.Run.PM;
import static Code.Run.VM;
import static Code.Run.VM_Migration;
import static WOA.run.VM_Migration_update;
import static WOA.run.energy;
import static WOA.run.load;
import static WOA.run.migration_cost;
import static WOA.run.p;
import static WOA.run.resource_availability;
import static WOA.run.resource_utilization;
import static WOA.run.resource_utilization_C;
import static WOA.run.st;

public class whale {
    public static ArrayList<Integer> Leader_pos = new ArrayList();
    public static ArrayList<Integer> X_rand = new ArrayList();
    public static ArrayList<Integer> BEST = new ArrayList();
    public static ArrayList<Double> Fitness = new ArrayList<Double>();
    public static ArrayList<Double> Convergence_curve = new ArrayList<Double>();
    public static int INPUT_NEURONS;
    public static int OUTPUT_NEURONS;
    public static ArrayList<ArrayList<Integer>> Positions=new ArrayList<>();
    public static void Whale() throws IOException {
        ArrayList<ArrayList<Double>> Circularkernal=new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Integer>> initial_soln =new ArrayList<>();
        int SearchAgents_no=10,Max_iter=10,lb=0,ub=1,dim=10,fcbj=0;
        for(int i=0;i<VM;i++) {
            Leader_pos.add(0);
        }
        double Leader_score = new Double(Double.POSITIVE_INFINITY); 
        Positions=initialization(SearchAgents_no,VM,Positions);
        initial_soln = (ArrayList<ArrayList<Integer>>) Positions.clone();
        Fitness=new ArrayList<>();
        for(int k=0;k<SearchAgents_no;k++) {
            Fitness.add(Fitnes(Positions.get(k)));
        }
        int m = Fitness.indexOf(Collections.max(Fitness));
        VM_Migration_update = Positions.get(m);
        for(int i=0;i<Max_iter;i++) {
            Convergence_curve.add(0.0);
        }
        int t=0;
        while (t< Max_iter)
        {
            Fitness=new ArrayList<>();
            ArrayList<Double> Fit = new ArrayList<Double>();
            ArrayList<Integer> solution = new ArrayList();
            for(int k=0;k<SearchAgents_no;k++) {
                
                solution=Positions.get(k);
                Fit.add(Fitnes(solution));
                
                //  % Update the leader
                if (Fit.get(k)<Leader_score)
                {
                    Leader_score=Fit.get(k); //% Update alpha
                    Leader_pos.set(k, Positions.get(k).get(k));
                }
            }
            int a=2-t*((2)/Max_iter);
            int a2=-1+t*((-1)/Max_iter);
            
            //% Update the Position of search agents 
            for(int i=0;i<Positions.size();i++) {
                int r1=1; //% r1 is a random number in [0,1]
                int r2=1; // % r2 is a random number in [0,1]
                int A=2*a*r1-a; 
                int C=2*r2;     
                int b=1;            
                int  l=(a2-1) * 1 + 1;
                int p = 1;  
                for(int j=0;j<Positions.get(i).size();j++) {
                    Random rd = new Random();
                    if(p<0.5) {
                        if (Math.abs(A)>=1) {
                            X_rand.add(Positions.get(i).get(j));
                            double D_X_rand=Math.abs(C*X_rand.get(j)-Positions.get(i).get(j));
                            Positions.get(i).set(j, (int)Math.abs(X_rand.get(j)-A*D_X_rand));
                        }
                        else if(Math.abs(a)<1) {
                            double D_Leader=Math.abs(C*Leader_pos.get(j)-Positions.get(i).get(j));
                            Positions.get(i).set(j, (int)Math.abs(Leader_pos.get(j)-A*D_Leader));
                        }
                    }
                    else if(p>=0.5) {
                        double distance2Leader=(Math.abs(Leader_pos.get(j)-Positions.get(i).get(j)));//Conventinal
                        Positions.get(i).set(j, (int)Math.abs(distance2Leader*Math.exp(b*l)*Math.cos(l*2*Math.PI)+Leader_pos.get(j)));
                    }
                }
                for(int k=0;k<Positions.get(i).size();k++) {
                    if(Positions.get(i).get(k)>ub) {
                        Positions.get(i).set(i, ub);
                    }
                    else if(Positions.get(i).get(k)<lb) {
                        Positions.get(i).set(i, lb);
                    }
                }
            }
            t=t+1;
            Fitness=Fit;   
        }
        double max=0;
        max=Collections.max(Fitness);
        
        for(int i=0;i<Positions.size();i++) {
            if(Fitness.get(i) == max) {
                int e=0;
                e=Fitness.indexOf(Fitness.get(i));
                BEST=initial_soln.get(e);
            } 
        }
    }

    public static double Fitnes(ArrayList<Integer> migration) {
        double m = (double)Code.Run.PM, n = (double)Code.Run.VM, c = 1.0, Wmax = 1.0;
        double alpha = Math.random(), beta = Math.random(), gamma = Math.random(), delta = Math.random();
        double M =0.0;      //no. of migrations of VM
            for( int j = 0; j < VM_Migration.size(); j++) {
                if(VM_Migration.get(j) != migration.get(j))
                    M++;
            }
            double T = System.nanoTime() - st;
            double power_consumed = (p * Wmax) + ((1-p) * Wmax * resource_utilization_C);
            resource_availability = 1 - resource_utilization;
            migration_cost = (1.0 / m) * (M / (c*n));
            energy = (1 / T) * power_consumed;//
            double f = (alpha * resource_availability) + (beta * (1-load)) + (gamma * (1-migration_cost)) + (delta * (1-energy));
        return f;
    }
    
    public static ArrayList<ArrayList<Integer>> initialization(int SearchAgents_no,int VM,ArrayList<ArrayList<Integer>>  Positions) {
        Random r=new Random();
        Positions=new ArrayList<>();
        for(int i=0;i<SearchAgents_no;i++) {
            ArrayList<Integer> tem=new ArrayList();
            for(int j=0;j<VM;j++) {
                int p = r.nextInt(PM)+1;
                tem.add(p);
            }
            Positions.add(tem);
        }
        return Positions;
    }
}
