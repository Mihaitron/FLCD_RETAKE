import java.util.ArrayList;
import java.util.List;

public class Parser {

    private Grammar G;

    public Parser(Grammar g)
    {
        G = g;
    }

    public List<List<String>> closure(List<List<String>> I)
    {
        List<List<String>> C = new ArrayList<>();
        List<List<String>> CClone;
        C = new ArrayList<>(I);

        do {
            CClone = new ArrayList<>(C);
            for (List<String> rule : C)
            {
                for (String elem : rule)
                {
                    if (G.getNonterminals().contains(elem) && !listContainsLists(C, G.ruleForNonterminal(elem))) {
                        C.addAll(G.ruleForNonterminal(elem));
                    }
                }
            }
        } while (listEquals(CClone, C));

        return C;
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
