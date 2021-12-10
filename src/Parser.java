import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

public class Parser {

    private Grammar G;
    private Map<String, Entry<List<String>, List<List<String>>>> statesMap;
    int stateNumber;
    Map<String, Map<String, String>> parsingTable;

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
                            if (!Utils.listListContainsList(C, dotRule))
                            {
                                I.getKey().add(nextRule);
                                C.add(dotRule);
                            }
                        }
                    }
                }
            }
        } while (!Utils.listListEquals(CClone, C));

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

        Entry<List<String>, List<List<String>>> closureRez = closure(rez);
        return closureRez;
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
        parsingTable.put("s0", parsingTableRow(rez.get(0), "s0"));

        boolean stop = false;
        while(!stop)
        {
            int addedStates = 0;
            rezCopy = new ArrayList<>(rez);
            for (int i = 0; i < rezCopy.size(); i++)
            {
                for (String terminal : G.getTerminals()) {
                    Entry<List<String>, List<List<String>>> sNew = goTo(rezCopy.get(i), terminal);
                    if (sNew != null && !Utils.listContainsValue(rez, sNew))
                    {
                        rez.add(sNew);

                        Map<String, String> tableRow = parsingTable.get("s" + i);
                        statesMap.put("s" + stateNumber, sNew);
                        tableRow.put(terminal, "s" + stateNumber);
                        parsingTable.replace("s" + i, tableRow);
                        parsingTable.put("s" + stateNumber, parsingTableRow(sNew, "s" + stateNumber));
                        stateNumber++;

                        addedStates++;
                    }
                }
                for (String nonterminal : G.getNonterminals()) {
                    Entry<List<String>, List<List<String>>> sNew = goTo(rezCopy.get(i), nonterminal);
                    if (sNew != null && !Utils.listContainsValue(rez, sNew))
                    {
                        rez.add(sNew);

                        Map<String, String> tableRow = parsingTable.get("s" + i);
                        statesMap.put("s" + stateNumber, sNew);
                        tableRow.put(nonterminal, "s" + stateNumber);
                        parsingTable.replace("s" + i, tableRow);
                        parsingTable.put("s" + stateNumber, parsingTableRow(sNew, "s" + stateNumber));
                        stateNumber++;

                        addedStates++;
                    }
                }
            }
            if (addedStates == 0)
                stop = true;
        }

        return rez;
    }

    private Map<String, String> parsingTableRow(Entry<List<String>, List<List<String>>> state, String stateString)
    {
        Map<String, String> rez = new HashMap<>();
        List<List<String>> rules = state.getValue();
        for (int i = 0; i < rules.size(); i++)
        {
            if (rules.get(i).get(rules.get(i).size() - 1).equals("."))
            {
                if (rules.get(i).size() == 2 && rules.get(i).get(0).equals("S"))
                {
                    if (rez.size() != 0 && !rez.get("action").equals("acc"))
                    {
                        System.out.println("Conflict at row - " + stateString + " column - action!");
                        rez.put("action", "conflict");
                    }
                    else
                        rez.put("action", "acc");
                }
                else {
                    if (rez.size() != 0 && !rez.get("action").equals("reduce" + G.getProductionNumber(state.getKey().get(i), rules.get(i).subList(0, rules.get(i).size() - 1)))) {
                        System.out.println("Conflict at row - " + stateString + " column - action!");
                        rez.put("action", "conflict");
                    } else
                        rez.put("action", "reduce " + G.getProductionNumber(state.getKey().get(i), rules.get(i).subList(0, rules.get(i).size() - 1)));
                }
            }
            else
            {
                if (rez.size() != 0 && !rez.get("action").equals("shift"))
                {
                    System.out.println("Conflict at row - " + stateString + " column - action!");
                    rez.put("action", "conflict");
                }
                else
                    rez.put("action", "shift");
            }
        }
        return rez;
    }

    private void actionShift(Stack<String> alpha, Stack<String> beta, Stack<String> phi)
    {
        Entry<List<String>, List<List<String>>> newStateObj = goTo(statesMap.get(alpha.peek()), beta.peek());
        for (Entry<String, Entry<List<String>, List<List<String>>>> stateEntry : statesMap.entrySet())
        {
            String stateNumber = stateEntry.getKey();
            Entry<List<String>, List<List<String>>> stateObject = stateEntry.getValue();

            if (Utils.stateEqualsState(newStateObj, stateObject))
            {
                alpha.push(beta.pop());
                alpha.push(stateNumber);
                break;
            }
        }
    }

    private void actionReduce(Stack<String> alpha, Stack<String> beta, Stack<String> phi, String reduceNumber)
    {
        Entry<String, List<String>> production = G.getProductionByNumber(Integer.valueOf(reduceNumber));
        for (int i = 0; i < production.getValue().size(); i++)
        {
            alpha.pop();
            alpha.pop();
        }

        Entry<List<String>, List<List<String>>> newStateObj = goTo(statesMap.get(alpha.peek()), production.getKey());
        for (Entry<String, Entry<List<String>, List<List<String>>>> stateEntry : statesMap.entrySet())
        {
            String stateNumber = stateEntry.getKey();
            Entry<List<String>, List<List<String>>> stateObject = stateEntry.getValue();

            if (Utils.stateEqualsState(newStateObj, stateObject))
            {
                alpha.push(production.getKey());
                alpha.push(stateNumber);
                phi.push(reduceNumber);
                break;
            }
        }
    }

    public void parse(String w)
    {
        for (Entry<String, Map<String, String>> row : parsingTable.entrySet())
        {
            for (Entry<String, String> val : row.getValue().entrySet())
            {
                if (val.getValue().equals("conflict"))
                    return;
            }
        }
        String state = "s0";

        Stack<String> alpha = new Stack<>();
        alpha.push("$");
        alpha.push(state);

        Stack<String> beta = new Stack<>();
        beta.push("$");
        StringBuilder builder = new StringBuilder();
        builder.append(w);
        builder.reverse();
        for (String str : builder.toString().split(" "))
            beta.push(str);

        Stack<String> phi = new Stack<>();

        boolean end = false;

        String action = parsingTable.get(state).get("action");
        do {
            if (action.equals("shift") && beta.size() == 1)
            {
                System.out.println("error");
                end = true;
            }
            else if (action.equals("shift"))
            {
                actionShift(alpha, beta, phi);
            }
            else if (action.contains("reduce"))
            {
                actionReduce(alpha, beta, phi, action.split(" ")[1]);
            }
            else if (action.equals("acc"))
            {
                if (beta.size() > 1)
                    System.out.println("error");
                else
                {
                    System.out.println("success");
                    System.out.println(phi);
                }
                end = true;
            }
            state = alpha.peek();
            action = parsingTable.get(state).get("action");
        } while (!end);
    }
}
