import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class Parser {

    private Grammar G;

    public Parser(Grammar g)
    {
        G = g;
    }

    public Entry<List<String>, List<List<String>>> closure(Entry<List<String>, List<List<String>>> I)
    {
        List<List<String>> C;
        List<List<String>> CClone;
        C = new ArrayList<>(I.getValue());

        do {
            CClone = new ArrayList<>(C);
            for (List<String> rule : C)
            {
                for (int i = 0; i < rule.size(); i++)
                {
                    String nextRule = rule.get(i + 1);
                    List<List<String>> nextRuleNonterminals = G.ruleForNonterminal(nextRule);

                    if (rule.get(i).equals(".") && G.getNonterminals().contains(nextRule) && !listContainsLists(C, nextRuleNonterminals))
                    {
                        C.addAll(nextRuleNonterminals);
                        for (List<String> nextRuleNonterminal : nextRuleNonterminals)
                        {
                            I.getKey().add(nextRule);
                            nextRuleNonterminal.add(0, ".");
                            C.add(nextRuleNonterminal);
                        }
                    }
                }
            }
        } while (listEquals(CClone, C));

        I.setValue(C);
        return I;
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
