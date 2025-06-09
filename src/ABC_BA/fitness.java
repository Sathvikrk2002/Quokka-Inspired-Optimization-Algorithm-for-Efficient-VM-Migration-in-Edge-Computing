package ABC_BA;

import static Code.Run.VM_Migration;
import static ABC_BA.run.energy;
import static ABC_BA.run.load;
import static ABC_BA.run.migration_cost;
import static ABC_BA.run.p;
import static ABC_BA.run.resource_availability;
import static ABC_BA.run.resource_utilization;
import static ABC_BA.run.resource_utilization_C;
import static ABC_BA.run.st;
import java.util.ArrayList;

public class fitness {
    public static ArrayList<Double> func(ArrayList<ArrayList<Integer>> migration) {
        ArrayList<Double> fit = new ArrayList();
        double m = (double)Code.Run.PM, n = (double)Code.Run.VM, c = 1.0, Wmax = 1.0;
        double alpha = Math.random(), beta = Math.random(), gamma = Math.random(), delta = Math.random();
        for( int i = 0; i < migration.size(); i++) {
            double M =0.0;      //no. of migrations of VM
            for( int j = 0; j < VM_Migration.size(); j++) {
                if(VM_Migration.get(j) != migration.get(i).get(j))
                    M++;
            }
            double T = System.nanoTime() - st;
            double power_consumed = (p * Wmax) + ((1-p) * Wmax * resource_utilization_C);
            resource_availability = 1 - resource_utilization;
            migration_cost = (1.0 / m) * (M / (c*n));
            energy = (1 / T) * power_consumed;
            double f = (alpha * resource_availability) + (beta * (1-load)) + (gamma * (1-migration_cost)) + (delta * (1-energy));
            fit.add(f);
        }
        return fit;
    }
    
    public static ArrayList<Double> bee_func(ArrayList<Integer> v1) {
        double e = 0, g;
        ArrayList<Double> f = new ArrayList();
        ArrayList<Double> fit = new ArrayList();
                
        //f(x) value           
        for(int i = 0; i < v1.size(); i++) {
            e += Math.pow(v1.get(i), 2);     //b=sq(x1)+sq(x2)
            f.add(e);
        }
        
        //Fitness Value            
        if(e >= 0)
            g = 1 / (1 + e);
        else
            g = 1 + Math.abs(e);
        
        fit.add(g);
        return fit;
    }
}