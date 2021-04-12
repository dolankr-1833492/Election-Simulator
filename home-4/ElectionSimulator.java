//Katie Dolan
//Section AA 
//This class ElectionSimulator simulates an election and given state population and 
//elector data figures out the minimum popular vote necessary to win an election. 
import java.util.*;
import java.io.*;
public class ElectionSimulator {
   //The pre-calculated arguments and states. 
   Map<Arguments, Set<State>> solutions;
   //The given US States.
    List<State> allstates;
    public ElectionSimulator(List<State> states) {
        solutions = new HashMap<Arguments, Set<State>>();
        allstates = states;
    }
    //post: simulates the election in order to figure out the minimum popular vote
    //using the available states, and will return the states that win with the 
    //least amount of popular vote.
    public Set<State> simulate() {
        return simulate(minElectoralVotes(allstates), 0);
    }

    //Will recursively simulate the the election, and return the states that win with the 
    //least amount of popular vote.
    private Set<State> simulate(int TotalElectoralVotes, int index) {
        if(solutions.containsKey(new Arguments(TotalElectoralVotes, index))) {
            return solutions.get(new Arguments(TotalElectoralVotes, index));
        }
        if(TotalElectoralVotes <= 0) {
            return new HashSet<State>();         
        } 
        if(index > allstates.size() - 1) {
            return null;
        }
        else {                    
            Set<State> withState = simulate(TotalElectoralVotes - 
            allstates.get(index).electoralVotes, index + 1);
            if(withState == null) {
                return null;
            }
            Set<State> containsall = new HashSet<State>(withState);
            containsall.add(allstates.get(index));
            Set<State> withOutState = simulate(TotalElectoralVotes, index + 1);            
            if(withOutState != null && minPopularVotes(withOutState) < 
                minPopularVotes(containsall)) {
                containsall = withOutState;
            }
            solutions.put(new Arguments(TotalElectoralVotes, index), containsall);       
            return containsall;
        }   
    }

    public static int minElectoralVotes(List<State> states) {
        int total = 0;
        for (State state : states) {
            total += state.electoralVotes;
        }
        return total / 2 + 1;
    }

    public static int minPopularVotes(Set<State> states) {
        int total = 0;
        for (State state : states) {
            total += state.popularVotes / 2 + 1;
        }
        return total;
    }

    private static class Arguments implements Comparable<Arguments> {
        public final int electoralVotes;
        public final int index;

        public Arguments(int electoralVotes, int index) {
            this.electoralVotes = electoralVotes;
            this.index = index;
        }

        public int compareTo(Arguments other) {
            int cmp = Integer.compare(this.electoralVotes, other.electoralVotes);
            if (cmp == 0) {
                cmp = Integer.compare(this.index, other.index);
            }
            return cmp;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (!(o instanceof Arguments)) {
                return false;
            }
            Arguments other = (Arguments) o;
            return this.electoralVotes == other.electoralVotes && this.index == other.index;
        }

        public int hashCode() {
            return Objects.hash(electoralVotes, index);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        List<State> states = new ArrayList<>(51);
        try (Scanner input = new Scanner(new File("data/1828.csv"))) {
            while (input.hasNextLine()) {
                states.add(State.fromCsv(input.nextLine()));
            }
        }
        Set<State> result = new ElectionSimulator(states).simulate();
        System.out.println(result);
        System.out.println(minPopularVotes(result) + " votes");
    }
}
