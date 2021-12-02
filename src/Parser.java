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
        } while (listListEquals(CClone, C));

        I.setValue(C);
        return I;
    }

    public Entry<List<String>, List<List<String>>> goTo(Entry<List<String>, List<List<String>>> s, String X)
    {
        Entry<List<String>, List<List<String>>> rez = new SimpleEntry<>(new ArrayList<>(), new ArrayList<>());

        for (int i = 0; i < s.getValue().size(); i++)
        {
            List<String> rule = s.getValue().get(i);
            List<String> newRule = new ArrayList<>();
            boolean important = false;

            for (int j = 0; j < rule.size(); j++) {
                String nextRule = rule.get(j + 1);

                if (rule.get(i).equals(".") && X.equals(nextRule)) {
                    newRule.add(nextRule);
                    newRule.add(".");
                    j++;
                } else
                    newRule.add(rule.get(i));
            }

            if (important) {
                rez.getValue().add(rule);
                rez.getKey().add(s.getKey().get(i));
                rez.getValue().add(newRule);
                rez.getKey().add(s.getKey().get(i));
            }
        }
        if (rez.getValue().size() == 0)
            return null;

        return closure(rez);
    }

    public List<Entry<List<String>, List<List<String>>>> canCol()
    {
        List<Entry<List<String>, List<List<String>>>> rez = new ArrayList<>();
        List<String> nt = new ArrayList<>();
        nt.add("S1");
        List<List<String>>rightSide = new ArrayList<>();
        List<String> rule = new ArrayList<>();
        rule.add(".");
        rule.add("S");
        Entry<List<String>, List<List<String>>> s0 = new SimpleEntry<>(nt, rightSide);

        rez.add(closure(s0));
        boolean stop = false;
        while(!stop)
        {
            int addedStates = 0;
            for (Entry<List<String>, List<List<String>>> sk : rez)
            {
                for (String terminal : G.getTerminals()) {
                    Entry<List<String>, List<List<String>>> sNew = goTo(sk, terminal);
                    if (sNew != null && !listContainsValue(rez, sNew))
                    {
                        rez.add(sNew);
                        addedStates++;
                    }
                }
                for (String nonterminal : G.getNonterminals()) {
                    Entry<List<String>, List<List<String>>> sNew = goTo(sk, nonterminal);
                    if (sNew != null && !listContainsValue(rez, sNew))
                    {
                        rez.add(sNew);
                        addedStates++;
                    }
                }
            }
            if (addedStates == 0)
                stop = true;
        }

        return rez;
    }

    private boolean listContainsValue(List<Entry<List<String>, List<List<String>>>> list, Entry<List<String>, List<List<String>>> value)
    {
        for (Entry<List<String>, List<List<String>>> item : list)
            if (listEquals(item.getKey(), value.getKey()) && listListEquals(item.getValue(), value.getValue()))
                return true;
        return false;
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

    private boolean listListEquals(List<List<String>> list1, List<List<String>> list2)
    {
        if (list1.size() != list2.size())
            return false;
        for (int i = 0; i < list1.size(); i++)
            if (!listEquals(list1.get(i), list2.get(i)))
                return false;
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
