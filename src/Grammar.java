import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Map.Entry;

public class Grammar
{
    private Set<String> nonterminals = new HashSet<>();
    private Set<String> terminals = new HashSet<>();
    private String starting;
    private Map<String, List<List<String>>> rules = new HashMap<>();

    public void readGrammar(String file)
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader("resources/" + file));

            String states_line = reader.readLine();
            nonterminals.addAll(Arrays.asList(states_line.split(" ")));

            String alphabet_line = reader.readLine();
            terminals.addAll(Arrays.asList(alphabet_line.split(" ")));

            starting = reader.readLine();

            String rules_line = reader.readLine();
            while (rules_line != null)
            {
                String[] rule = rules_line.split("->");
                String nonterminal = rule[0].trim();
                List<String> productions = Arrays.stream(rule[1].split("\\|")).map(String::trim).collect(Collectors.toList());
                List<List<String>> productions_split = new ArrayList<>();

                for (String production : productions)
                    productions_split.add(Arrays.asList(production.split(" ")));

                rules.put(nonterminal, productions_split);

                rules_line = reader.readLine();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void printNonterminals()
    {
        nonterminals.forEach(s -> System.out.print(s + " "));
    }

    public void printTerminals()
    {
        terminals.forEach(s -> System.out.print(s + " "));
    }

    public void printProductions() {
        for (Entry<String, List<List<String>>> production : rules.entrySet())
        {
            String productions_str = "";
            for (int i = 0; i < production.getValue().size() - 1; i++)
            {
                for (int j = 0; j < production.getValue().get(i).size(); j++)
                    productions_str += production.getValue().get(i).get(j) + " ";
                productions_str += "| ";
            }
            for (int j = 0; j < production.getValue().get(production.getValue().size() - 1).size(); j++)
                productions_str += production.getValue().get(production.getValue().size() - 1).get(j) + " ";

            System.out.println(production.getKey() + " -> " + productions_str);
        }
    }

    public boolean isCFG()
    {
        for (Entry<String, List<List<String>>> entry : rules.entrySet())
        {
            if (!nonterminals.contains(entry.getKey()))
                return false;
            for (List<String> prod : entry.getValue())
            {
                for (String value : prod)
                {
                    if (!nonterminals.contains(value) && !terminals.contains(value))
                        return false;
                }
            }
        }
        return true;

    }

    public List<List<String>> ruleForNonterminal(String nonterminal)
    {
        return rules.get(nonterminal);
    }

    public Set<String> getNonterminals()
    {
        return nonterminals;
    }

    public Set<String> getTerminals() { return terminals; }

    public int getProductionNumber(String nonterminal, List<String> rule)
    {
        int prodNb = 1;
        for (Entry<String, List<List<String>>> prod : rules.entrySet())
        {
            for (List<String> list : prod.getValue())
            {
                if (listEquals(list, rule) && prod.getKey().equals(nonterminal))
                    return prodNb;
                prodNb++;
            }
        }
        return prodNb;
    }

    private boolean listEquals(List<String> list1, List<String> list2)
    {
        if (list1.size() != list2.size())
            return false;

        for (String item : list1)
            if (!list2.contains(item))
                return false;
        return true;
    }
}
