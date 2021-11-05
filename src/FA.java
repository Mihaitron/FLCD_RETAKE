import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Triple<A, B, C>
{
    private A a;
    private B b;
    private C c;

    public Triple(A a, B b, C c)
    {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public A getA()
    {
        return a;
    }

    public void setA(A a)
    {
        this.a = a;
    }

    public B getB()
    {
        return b;
    }

    public void setB(B b)
    {
        this.b = b;
    }

    public C getC()
    {
        return c;
    }

    public void setC(C c)
    {
        this.c = c;
    }
}

public class FA
{
    private List<String> states = new ArrayList<>();
    private List<String> alphabet = new ArrayList<>();
    private String starting = "";
    private List<String> finalStates = new ArrayList<>();
    private List<Triple<String, String, String>> rules = new ArrayList<>();

    public void readFA(String file)
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader("resources/" + file));

            String states_line = reader.readLine();
            states.addAll(Arrays.asList(states_line.split(" ")));

            String alphabet_line = reader.readLine();
            alphabet.addAll(Arrays.asList(alphabet_line.split(" ")));

            starting = reader.readLine();

            String finals_line = reader.readLine();
            finalStates.addAll(Arrays.asList(finals_line.split(" ")));

            String rules_line = reader.readLine();
            while (rules_line != null)
            {
                String[] rule = rules_line.split("->");
                String[] lhs = rule[0].split(" ");

                rules.add(new Triple<>(lhs[0], lhs[1], rule[1].replace(" ", "")));

                rules_line = reader.readLine();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void printFA(String index)
    {
        if (index.equals("1"))
        {
            for (String state : states)
                System.out.print(state + " ");
            System.out.println();
        }

        if (index.equals("2"))
        {
            for (String a : alphabet)
                System.out.print(a + " ");
            System.out.println();
        }

        if (index.equals("3"))
            System.out.println(starting);

        if (index.equals("4"))
        {
            for (String final_state : finalStates)
                System.out.print(final_state);
            System.out.println();
        }

        if (index.equals("5"))
        {
            for (Triple<String, String, String> rule : rules)
                System.out.println(rule.getA() + " " + rule.getB() + " -> " + rule.getC());
            System.out.println();
        }

        if (index.equals("6"))
            System.out.println("Is DFA? " + isDFA());
    }

    private Boolean isDFA()
    {
        for (int i = 0; i < rules.size() - 1; i++)
            for (int j = i + 1; j < rules.size(); j++)
                if (rules.get(i).getA().equals(rules.get(j).getA()) && rules.get(i).getB().equals(rules.get(j).getB()))
                    return false;
        return true;
    }

    private Triple<String, String, String> getRule(String state, String transition_alphabet)
    {
        for (Triple<String, String, String> rule : rules)
        {
            if (rule.getA().equals(state) && rule.getB().equals(transition_alphabet))
                return rule;
        }
        return null;
    }

    public Boolean verifySequenceAccepted(String sequence)
    {
        String[] sequence_split = sequence.split("");
        String current_state = starting;

        for (int i = 0; i < sequence_split.length; i++)
        {
            if (!alphabet.contains(sequence_split[i]))
                return false;

            Triple<String, String, String> rule = getRule(current_state, sequence_split[i]);
            if (rule == null)
                return false;

            current_state = rule.getC();
        }

        return true;
    }
}
