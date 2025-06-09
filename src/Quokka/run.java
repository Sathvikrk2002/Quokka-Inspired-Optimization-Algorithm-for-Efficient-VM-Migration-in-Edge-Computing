package Quokka;

import static Code.Run.task;
import static Code.Run.VM;
import static Code.Run.P;
import static Code.Run.C;
import static Code.Run.B;
import static Code.Run.M;
import static Code.Run.I;
import static Code.Run.VM_Migration;
import static Code.Run.th;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class run {
    public static double resource_utilization, resource_utilization_C, load, resource_availability, migration_cost = 1.0, energy, st, p;
    public static ArrayList<Integer> task_time = new ArrayList<>();
    public static ArrayList<Integer> task_assign = new ArrayList<>();
    public static ArrayList<Integer> VM_Migration_update = new ArrayList<>();

    public static void callmain() throws IOException {
        int g = 0;
        st = System.nanoTime(); // start time
        task_assign = new ArrayList<>();
        VM_Migration_update = new ArrayList<>(VM_Migration); // Clone VM_Migration to keep track of migration changes

        while(g < Code.Run.max_itr) {
            assign_task_time(g); // Assign random task times
            VM_task_assign(g); // Assign tasks to VMs
            p = System.nanoTime() - st;  // Time elapsed in nanoseconds
            load_calculation(st); // Calculate load after task assignments

            if(load > th) { // If load exceeds threshold, trigger optimization
                quokka_algm.main(null); // Handle VM migration optimization
            }

            // Decrease task time for each assigned task
            for (int i = 0; i < task_assign.size(); i++) {
                if (task_assign.get(i) > 0)
                    task_assign.set(i, (task_assign.get(i) - 1)); // Reduce task time
            }
            g++; // Increment iteration count
            compute_parameter(); // Update parameters based on current state
        }

        // Store performance metrics after all iterations
        Code.Run.Load.add(load);
        Code.Run.Migration_cost.add(migration_cost);
        Code.Run.Energy_consumption.add(energy);
        Code.Run.Resource_availability.add(resource_availability);
    }

    // Assign random times to each task (from 1 to max_itr)
    public static void assign_task_time(int g) {
        if (g == 0) { // Only assign tasks at the beginning
            for (int i = 0; i < task; i++) {
                task_time.add((int) (Math.random() * Code.Run.max_itr + 1));
            }
        }
    }

    // Assign tasks to VMs in round-robin fashion
    public static void VM_task_assign(int g) {
        if (g == 0) { // Initialize task assignments for VMs
            for (int i = 0; i < VM; i++) {
                task_assign.add(0); // Initialize with no tasks assigned
            }
        }

        // Assign tasks to available VMs
        for (int i = 0; i < task_assign.size(); i++) {
            if (task_time.size() > 0 && task_assign.get(i) == 0) {
                task_assign.set(i, task_time.get(0)); // Assign first task to free VM
                task_time.remove(0); // Remove task from unassigned list
            }
        }
    }

    // Calculate system load based on resource utilization
    public static void load_calculation(double st) {
        resource_utilization = 0.0;
        load = 0.0;
        double N = 300, time, m = Code.Run.PM; // N = Normalizing factor

        // Iterate through each VM and calculate resource utilization
        for (int i = 0; i < VM; i++) {
            if (task_assign.get(i) > 0) { // Only consider VMs that have tasks assigned
                resource_utilization += ((P.get(i) / Collections.max(P)) + 
                                         (C.get(i) / Collections.max(C)) + 
                                         (B.get(i) / Collections.max(B)) + 
                                         (M.get(i) / Collections.max(M)) + 
                                         (I.get(i) / Collections.max(I)));
            }
        }
        resource_utilization = resource_utilization / N; // Normalize utilization
        resource_utilization_C = resource_utilization / m;
        time = (System.nanoTime() - st) / 1000000; // Time since process started
        load = resource_utilization / time; // Calculate load
    }

    // Compute additional performance parameters like migration cost and energy consumption
    public static void compute_parameter() {
        double m = (double) Code.Run.PM, n = (double) Code.Run.VM, c = Math.random() * 1 + 0.5, Wmax = 0.5;
        double alpha = Math.random(), beta = Math.random(), gamma = Math.random(), delta = Math.random();

        double M = 0.0; // Count the number of migrations
        for (int j = 0; j < VM_Migration.size(); j++) {
            if (VM_Migration.get(j) != VM_Migration_update.get(j)) {
                M++; // Increment migration count if VM migration occurred
            }
        }

        double T = System.nanoTime() - st;
        double power_consumed = (p * Wmax) + ((1 - p) * Wmax * resource_utilization_C);
        resource_availability = 1 - resource_utilization; // Calculate remaining resource availability
        migration_cost = (1.0 / m) * (M / (c * n)); // Calculate migration cost
        energy = (1 / T) * power_consumed; // Calculate energy consumption
    }
}
