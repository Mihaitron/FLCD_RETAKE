import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Map.Entry;

public class Grammar
{
    //this is the second time this is my line
    private Set<String> nonterminals = new HashSet<>();
    private Set<String> terminals = new HashSet<>();
    private String starting;
    private Map<String, List<String>> rules = new HashMap<>();

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

                rules.put(nonterminal, productions);

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
        for (Entry<String, List<String>> production : rules.entrySet())
        {
            String productions_str = "";
            for (int i = 0; i < production.getValue().size() - 1; i++)
                productions_str += production.getValue().get(i) + " | ";
            productions_str += production.getValue().get(production.getValue().size() - 1);

            System.out.println(production.getKey() + " -> " + productions_str);
        }
    }

    public void isCFG()
    {
        Boolean is_cfg = true;


    }
}
