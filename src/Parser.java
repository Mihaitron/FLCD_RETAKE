import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {

    private Grammar G;

    public Parser(Grammar g)
    {
        G = g;
    }

    public Map<List<String>, List<List<String>>> closure(List<String> nonterminals, List<List<String>> I)
    {
        List<List<String>> C;
        List<List<String>> CClone;
        C = new ArrayList<>(I);
        Map<List<String>, List<List<String>>> rez = new HashMap<>();

        do {
            CClone = new ArrayList<>(C);
            for (List<String> rule : C)
            {
                for (int i = 0; i < rule.size(); i++)
                {
                    if (rule.get(i).equals(".") && G.getNonterminals().contains(rule.get(i + 1)) && !listContainsLists(C, G.ruleForNonterminal(rule.get(i + 1)))) {
                        List<List<String>> rules = G.ruleForNonterminal(rule.get(i + 1));
                        C.addAll(G.ruleForNonterminal(rule.get(i + 1)));
                        for (int j = 0; j < rules.size(); j++)
                        {
                            nonterminals.add(rule.get(i + 1));
                            rules.get(j).add(0, ".");
                            C.add(rules.get(j));
                        }
                    }
                }
            }
        } while (listEquals(CClone, C));

        rez.put(nonterminals, C);
        return rez;
    }

    private boolean listEquals(List<List<String>> list1, List<List<String>> list2)
    {
        if (list1.size() != list2.size())
            return false;
        for (List<String> l : list1) {
            if (!list2.contains(list1))
                return false;
        }
        return true;
    }

    private boolean listContainsLists(List<List<String>> C, List<List<String>> lists)
    {
        for (List<String> list : lists)
        {
            if (!C.contains(list))
                return false;
        }
        return true;
    }
}
