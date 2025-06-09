package Quokka;

import java.util.ArrayList;
import java.util.Collections;
import static Code.Run.VM;
import static Code.Run.PM;
import static Code.Run.VM_Migration;
import static Quokka.run.VM_Migration_update;

public class quokka_algm {
    public static ArrayList<Integer> Final_best = new ArrayList<>();

    public static void main(String[] args) {
        VM_Migration_update = (ArrayList<Integer>) VM_Migration.clone(); // Initial VM migration
        Final_best = new ArrayList<>();
        ArrayList<ArrayList<Integer>> Solution = new ArrayList<>();
        ArrayList<ArrayList<Integer>> Solution_update = new ArrayList<>();

        int Max_Generation = 50; // Increased generations for better exploration
        int N = 20; // Larger swarm size for better diversity
        int D = VM; // Problem dimension
        int G = 5; // Update interval
        double explorationRate = 1.5; // Initial exploration weight

        // Initialize solutions with a hybrid initialization (Chaotic + Random)
        Solution = initialize_solution(N, D);

        int t = 0;
        ArrayList<Integer> explorers = new ArrayList<>();
        ArrayList<Integer> foragers = new ArrayList<>();
        ArrayList<Double> Fit = new ArrayList<>();

        while (t < Max_Generation) {
            if (t % G == 0) {
                explorers.clear();
                foragers.clear();
                Fit = fitness_QA.func(Solution);
                Final_best = Solution.get(Fit.indexOf(Collections.max(Fit)));
                VM_Migration_update = Final_best;

                // Dynamic group division based on fitness with added diversity mechanism
                dynamic_grouping(N, Fit, explorers, foragers);
            }

            // Update solutions dynamically
            Solution_update = update_solution(N, D, Fit, explorers, foragers, Solution, explorationRate);
            Solution = better_solution(Solution, Solution_update);

            // Adaptive exploration rate with dynamic decay
            explorationRate *= 0.98; // Gradually decrease exploration over generations
            t++;
        }

        Fit = fitness_QA.func(Solution); // Final fitness evaluation
    }

    // Hybrid initialization (Chaotic + Random)
    private static ArrayList<ArrayList<Integer>> initialize_solution(int N, int D) {
        ArrayList<ArrayList<Integer>> solutions = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            ArrayList<Integer> temp = new ArrayList<>();
            for (int j = 0; j < D; j++) {
                // Hybrid initialization: Chaotic + Random
                int a = (int) ((Math.sin(i * Math.random()) + 1) * PM / 2 + 1); // Chaotic part
                int b = (int) (Math.random() * PM + 1); // Random part
                temp.add(Math.max(a, b)); // Max of chaotic and random for diversity
            }
            solutions.add(temp);
        }
        return solutions;
    }

    private static void dynamic_grouping(int N, ArrayList<Double> Fit, ArrayList<Integer> explorers, ArrayList<Integer> foragers) {
        ArrayList<Double> sortedFit = new ArrayList<>(Fit);
        Collections.sort(sortedFit, Collections.reverseOrder());
        double threshold = sortedFit.get(N / 2); // Divide based on median fitness

        // Dynamically divide explorers and foragers based on fitness
        for (int i = 0; i < N; i++) {
            if (Fit.get(i) >= threshold) {
                explorers.add(i);
            } else {
                foragers.add(i);
            }

        }

        // Diversity Control: periodically reset explorers and foragers to avoid premature convergence
        if (explorers.size() < N / 2) {
            for (int i = 0; i < N / 4; i++) {
                explorers.add(i);
            }
        }
    }

    // Update solutions dynamically with adaptive mechanisms
    public static ArrayList<ArrayList<Integer>> update_solution(int N, int D, ArrayList<Double> Fit, ArrayList<Integer> explorers,
            ArrayList<Integer> foragers, ArrayList<ArrayList<Integer>> Solution, double explorationRate) {
        ArrayList<ArrayList<Integer>> updated_soln = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            ArrayList<Integer> temp;
            if (explorers.contains(i)) {
                temp = update_explorer(i, D, explorationRate, Fit, Solution);
            } else {
                temp = update_forager(i, D, Solution);
            }
            updated_soln.add(temp);
        }
        return updated_soln;
    }

    // Explorer update mechanism with dynamic exploration rate
    private static ArrayList<Integer> update_explorer(int i, int D, double explorationRate, ArrayList<Double> Fit,
            ArrayList<ArrayList<Integer>> Solution) {
        ArrayList<Integer> temp = new ArrayList<>();
        for (int j = 0; j < D; j++) {
            int e = Solution.get(i).get(j)
                    + (int) (explorationRate * Math.random() * (Fit.get(i) - Collections.min(Fit)));
            temp.add(Math.max(1, e % PM));
        }
        return temp;
    }

    // Forager update mechanism with random search
    private static ArrayList<Integer> update_forager(int i, int D, ArrayList<ArrayList<Integer>> Solution) {
        ArrayList<Integer> temp = new ArrayList<>();
        for (int j = 0; j < D; j++) {
            int f = Solution.get(i).get(j) + (int) (Math.random() * (PM - Solution.get(i).get(j)));
            temp.add(Math.max(1, f % PM));
        }
        return temp;
    }

    // Compare solutions and select the better one
    public static ArrayList<ArrayList<Integer>> better_solution(ArrayList<ArrayList<Integer>> Solution,
            ArrayList<ArrayList<Integer>> Solution_update) {
        ArrayList<ArrayList<Integer>> best_soln = new ArrayList<>();
        ArrayList<Double> soln_fit = fitness_QA.func(Solution);
        ArrayList<Double> soln_up_fit = fitness_QA.func(Solution_update);

        for (int i = 0; i < Solution.size(); i++) {
            if (soln_fit.get(i) > soln_up_fit.get(i)) {
                best_soln.add(Solution.get(i));
            } else {
                best_soln.add(Solution_update.get(i));
            }
        }
        return best_soln;
    }
}
