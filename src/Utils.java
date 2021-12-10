import java.util.List;
import java.util.Map.Entry;

public class Utils
{
    public static boolean listContainsValue(List<Entry<List<String>, List<List<String>>>> list, Entry<List<String>, List<List<String>>> value)
    {
        for (Entry<List<String>, List<List<String>>> item : list)
            if (listEquals(item.getKey(), value.getKey()) && listListEquals(item.getValue(), value.getValue()))
                return true;
        return false;
    }

    public static boolean listEquals(List<String> list1, List<String> list2)
    {
        if (list1.size() != list2.size())
            return false;

        for (int i = 0; i < list1.size(); i++)
            if (!list1.get(i).equals(list2.get(i)))
                return false;
        return true;
    }

    public static boolean listListEquals(List<List<String>> list1, List<List<String>> list2)
    {
        if (list1.size() != list2.size())
            return false;
        for (int i = 0; i < list1.size(); i++)
            if (!listEquals(list1.get(i), list2.get(i)))
                return false;
        return true;
    }

    public static boolean listListContainsList(List<List<String>> C, List<String> list)
    {
        for (List<String> item : C)
            if (listEquals(item, list))
                return true;
        return false;
    }

    public static boolean stateEqualsState(Entry<List<String>, List<List<String>>> state1, Entry<List<String>, List<List<String>>> state2)
    {
        if (state1 == null)
            return false;
        if (state2 == null)
            return false;

        return listEquals(state1.getKey(), state2.getKey()) && listListEquals(state1.getValue(), state2.getValue());
    }
}
