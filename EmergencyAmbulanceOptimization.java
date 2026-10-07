import java.util.*;

public class EmergencyAmbulanceOptimization {

    // ==========================================
    // MASTER VARIABLES (Shared by all algorithms)
    // ==========================================
    static int[][] costMatrix = {
        {0, 15, 25, 35}, // Row 0: From Hospital
        {15, 0, 30, 28}, // Row 1: From Location B
        {25, 30, 0, 20}, // Row 2: From Location C
        {35, 28, 20, 0}  // Row 3: From Location D
    };

    static String[] locations = {
        "Hospital",
        "Emergency Location B",
        "Emergency Location C",
        "Emergency Location D"
    };

    // Tracking variables for Divide & Conquer
    static int dcMinTotalCost = Integer.MAX_VALUE;
    static String dcBestRoute = "";

    // Tracking variables for Backtracking
    static int btBestCost = Integer.MAX_VALUE;
    static String btBestRoute = "";


    // ==========================================
    // 1. GREEDY ROUTE OPTIMIZATION
    // ==========================================
    public static String greedyEAROP(int[][] dist) {
        int n = dist.length;
        boolean[] visited = new boolean[n];
        visited[0] = true; 
        int current = 0;
        int totalCost = 0;
        StringBuilder path = new StringBuilder(locations[0]);

        for (int i = 0; i < n - 1; i++) {
            int nextNode = -1;
            int minCost = Integer.MAX_VALUE;
            
            for (int j = 0; j < n; j++) {
                if (!visited[j] && dist[current][j] < minCost && dist[current][j] != 0) {
                    minCost = dist[current][j];
                    nextNode = j;
                }
            }
            visited[nextNode] = true;
            totalCost += minCost;
            current = nextNode;
            path.append(" -> ").append(locations[current]);
        }
        
        totalCost += dist[current][0]; // Return to Hospital
        return "Greedy Ambulance Route: " + path.toString() + " -> " + locations[0] + " | Total Cost: " + totalCost;
    }


    // ==========================================
    // 2. DIVIDE AND CONQUER ROUTE OPTIMIZATION
    // ==========================================
    public static String divideAndConquerEAROP(int[][] dist) {
        int n = dist.length;
        boolean[] visited = new boolean[n];
        visited[0] = true; 
        
        dcMinTotalCost = Integer.MAX_VALUE;
        dcBestRoute = "";
        StringBuilder startingPath = new StringBuilder("Hospital");
        
        divideAndConquerHelper(0, visited, 0, dist, n, startingPath);
        return "Divide & Conquer Route: " + dcBestRoute + " | Minimum Cost: " + dcMinTotalCost;
    }

    private static int divideAndConquerHelper(int pos, boolean[] visited, int currentCost, int[][] dist, int n, StringBuilder path) {
        if (allVisited(visited)) {
            int returnCost = dist[pos][0];
            int totalCost = currentCost + returnCost;
            if (totalCost < dcMinTotalCost) {
                dcMinTotalCost = totalCost;
                dcBestRoute = path.toString() + " -> Hospital";
            }
            return totalCost;
        }

        int localMin = Integer.MAX_VALUE;
        for (int i = 1; i < n; i++) {
            if (!visited[i]) {
                visited[i] = true;
                int travelCost = dist[pos][i];
                String locName = (i == 1) ? "B" : (i == 2) ? "C" : "D";
                int originalLength = path.length();
                path.append(" -> ").append(locName);
                
                int branchCost = divideAndConquerHelper(i, visited, currentCost + travelCost, dist, n, path);
                localMin = Math.min(localMin, branchCost);
                
                visited[i] = false;
                path.setLength(originalLength); 
            }
        }
        return localMin;
    }

    private static boolean allVisited(boolean[] visited) {
        for (boolean v : visited) {
            if (!v) return false;
        }
        return true;
    }


    // ==========================================
    // 3. DYNAMIC PROGRAMMING ROUTE OPTIMIZATION
    // ==========================================
    public static String dynamicProgrammingEAROP(int[][] dist) {
        int n = dist.length;
        int VISITED_ALL = (1 << n) - 1;
        int[][] memo = new int[1 << n][n];
        String[][] paths = new String[1 << n][n];

        for (int[] row : memo) {
            Arrays.fill(row, -1);
        }

        int totalCost = dynamicProgrammingEAROPHelper(0, 1, dist, memo, VISITED_ALL, paths);
        String route = "Hospital" + paths[1][0];

        return "Dynamic Programming Route: " + route + " | Total Cost: " + totalCost;
    }

    private static int dynamicProgrammingEAROPHelper(int pos, int mask, int[][] dist, int[][] memo, int VISITED_ALL, String[][] paths) {
        if (mask == VISITED_ALL) {
            paths[mask][pos] = " -> " + locations[0];
            return dist[pos][0];
        }
        if (memo[mask][pos] != -1) {
            return memo[mask][pos];
        }

        int ans = Integer.MAX_VALUE;
        String bestPath = "";

        for (int nxt = 0; nxt < dist.length; nxt++) {
            if ((mask & (1 << nxt)) == 0) {
                int newCost = dist[pos][nxt] + dynamicProgrammingEAROPHelper(nxt, mask | (1 << nxt), dist, memo, VISITED_ALL, paths);
                if (newCost < ans) {
                    ans = newCost;
                    bestPath = " -> " + locations[nxt] + paths[mask | (1 << nxt)][nxt];
                }
            }
        }
        paths[mask][pos] = bestPath;
        return memo[mask][pos] = ans;
    }


    // ==========================================
    // 4. BACKTRACKING ROUTE OPTIMIZATION
    // ==========================================
    public static String backtrackingEAROP(int[][] dist) {
        int n = dist.length;
        boolean[] visited = new boolean[n];
        visited[0] = true; 
        btBestCost = Integer.MAX_VALUE;
        btBestRoute = "";

        StringBuilder path = new StringBuilder(locations[0]);
        earopBacktracking(0, dist, visited, n, 1, 0, path);

        return "Backtracking Route: " + btBestRoute + " | Total Cost: " + btBestCost;
    }

    private static int earopBacktracking(int pos, int[][] dist, boolean[] visited, int n, int count, int cost, StringBuilder path) {
        if (cost >= btBestCost) return Integer.MAX_VALUE;

        if (count == n) {
            int total = cost + dist[pos][0];
            if (total < btBestCost) {
                btBestCost = total;
                btBestRoute = path + " -> " + locations[0];
            }
            return total;
        }

        int best = Integer.MAX_VALUE;
        for (int next = 1; next < n; next++) {
            if (!visited[next]) {
                visited[next] = true;
                int lengthBefore = path.length();
                path.append(" -> ").append(locations[next]);

                int result = earopBacktracking(next, dist, visited, n, count + 1, cost + dist[pos][next], path);
                best = Math.min(best, result);

                path.setLength(lengthBefore);
                visited[next] = false;
            }
        }
        return best;
    }


    // ==========================================
    // 5. SORTING AND SEARCHING
    // ==========================================
    public static void insertionSort(int[] responseTimes) {
        for (int i = 1; i < responseTimes.length; i++) {
            int key = responseTimes[i];
            int j = i - 1;
            while (j >= 0 && responseTimes[j] > key) {
                responseTimes[j + 1] = responseTimes[j];
                j--;
            }
            responseTimes[j + 1] = key;
        }
    }

    public static int binarySearch(int[] arr, int target) {
        int low = 0;
        int high = arr.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] == target) return mid;
            if (arr[mid] < target) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }


    // ==========================================
    // MASTER DRIVER METHOD
    // ==========================================
    public static void main(String[] args) {
        System.out.println("--- EMERGENCY AMBULANCE ROUTING OPTIMIZATION ---");
        System.out.println(greedyEAROP(costMatrix));
        System.out.println(divideAndConquerEAROP(costMatrix));
        System.out.println(dynamicProgrammingEAROP(costMatrix));
        System.out.println(backtrackingEAROP(costMatrix));
        
        System.out.println("\n--- SORTING AND SEARCHING DEMO ---");
        int[] emergencyResponseTimes = {8, 3, 5, 1, 9, 2};
        int targetTime = 5;
        System.out.println("Original Array: " + Arrays.toString(emergencyResponseTimes));
        insertionSort(emergencyResponseTimes);
        System.out.println("Sorted Array:   " + Arrays.toString(emergencyResponseTimes));
        int resultIndex = binarySearch(emergencyResponseTimes, targetTime);
        System.out.println("Target time (" + targetTime + " mins) found at index: " + resultIndex);
    }
}