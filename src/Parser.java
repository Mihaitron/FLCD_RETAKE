import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Parser {

    private Grammar G;
    private Map<String, Entry<List<String>, List<List<String>>>> statesMap;
    int stateNumber;
    Map<String, Map<String, String>> parsingTable;
    private List<String> action;
    private List<Map<String, Entry<List<String>, List<List<String>>>>> gotoColumn;

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
            for (List<String> rule : CClone)
            {
                for (int i = 0; i < rule.size() - 1; i++)
                {
                    String nextRule = rule.get(i + 1);
                    List<List<String>> nextRuleNonterminals = G.ruleForNonterminal(nextRule);

                    List<List<String>> dotNextRuleNonterminal = new ArrayList<>();
                    if (nextRuleNonterminals != null)
                    {
                        for (List<String> nextRuleNonterminal : nextRuleNonterminals) {
                            List<String> nextRuleNonterminalCopy = new ArrayList<>(nextRuleNonterminal);
                            nextRuleNonterminalCopy.add(0, ".");
                            dotNextRuleNonterminal.add(nextRuleNonterminalCopy);
                        }
                    }

                    if (rule.get(i).equals(".") && G.getNonterminals().contains(nextRule))
                    {
                        for (List<String> dotRule : dotNextRuleNonterminal)
                        {
                            if (!listListContainsList(C, dotRule))
                            {
                                I.getKey().add(nextRule);
                                C.add(dotRule);
                            }
                        }
                    }
                }
            }
        } while (!listListEquals(CClone, C));

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

            for (int j = 0; j < rule.size() - 1; j++) {
                String nextRule = rule.get(j + 1);

                if (rule.get(j).equals(".") && X.equals(nextRule)) {
                    newRule.add(nextRule);
                    newRule.add(".");
                    if (j + 2 < rule.size())
                        newRule.addAll(rule.subList(j + 2, rule.size()));
                    important = true;
                    break;
                } else
                    newRule.add(rule.get(j));
            }

            if (important) {
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
        rightSide.add(rule);
        Entry<List<String>, List<List<String>>> s0 = new SimpleEntry<>(nt, rightSide);
        List<Entry<List<String>, List<List<String>>>> rezCopy;
        parsingTable = new HashMap<>();
        statesMap = new HashMap<>();
        stateNumber = 0;

        rez.add(closure(s0));

        statesMap.put("s0", rez.get(0));
        stateNumber++;
        parsingTable.put("s0", parsingTableRow(rez.get(0)));

        boolean stop = false;
        while(!stop)
        {
            int addedStates = 0;
            rezCopy = new ArrayList<>(rez);
            for (int i = 0; i < rezCopy.size(); i++)
            {
                for (String terminal : G.getTerminals()) {
                    Entry<List<String>, List<List<String>>> sNew = goTo(rezCopy.get(i), terminal);
                    if (sNew != null && !listContainsValue(rez, sNew))
                    {
                        rez.add(sNew);

                        Map<String, String> tableRow = parsingTable.get("s" + i);
                        statesMap.put("s" + stateNumber, sNew);
                        tableRow.put(terminal, "s" + stateNumber);
                        parsingTable.replace("s" + i, tableRow);
                        stateNumber++;
                        parsingTable.put("s" + stateNumber, parsingTableRow(sNew));

                        addedStates++;
                    }
                }
                for (String nonterminal : G.getNonterminals()) {
                    Entry<List<String>, List<List<String>>> sNew = goTo(rezCopy.get(i), nonterminal);
                    if (sNew != null && !listContainsValue(rez, sNew))
                    {
                        rez.add(sNew);

                        Map<String, String> tableRow = parsingTable.get("s" + i);
                        statesMap.put("s" + stateNumber, sNew);
                        tableRow.put(nonterminal, "s" + stateNumber);
                        parsingTable.replace("s" + i, tableRow);
                        stateNumber++;
                        parsingTable.put("s" + stateNumber, parsingTableRow(sNew));

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

    private boolean listListContainsList(List<List<String>> C, List<String> list)
    {
        for (List<String> item : C)
            if (listEquals(item, list))
                return true;
        return false;
    }

    public Map<String, String> parsingTableRow(Entry<List<String>, List<List<String>>> state)
    {
        Map<String, String> rez = new HashMap<>();
        List<List<String>> rules = state.getValue();
        for (int i = 0; i < rules.size(); i++)
        {
            if (rules.get(i).get(rules.get(i).size() - 1).equals("."))
            {
                if (rules.get(i).size() == 2 && rules.get(i).get(0).equals("S"))
                    rez.put("action", "acc");
                else
                    rez.put("action", "reduce" + G.getProductionNumber(state.getKey().get(i), rules.get(i)));
            }
            else
            {
                rez.put("action", "shift");
            }
        }
        return rez;
    }
}
