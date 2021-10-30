import java.util.ArrayList;
import java.util.List;

public class SymbolTable
{
    private Integer capacity = 8;
    private Integer length = 0;
    private Float c1 = 0.5F;
    private Float c2 = 0.5F;
    private List<String> table;

    public SymbolTable()
    {
        table = new ArrayList<>();
        for (int i = 0; i < capacity; i++)
            table.add(null);
    }

    private Integer h(String k, Integer i)
    {
        Integer value = 0;
        for (int j = 0; j < k.length(); j++)
            value += ((int) k.charAt(j));

        Integer h_prime = value % capacity;
        Integer h = (int) (h_prime + c1 * i + c2 * i * i);

        return h % capacity;
    }

    public Integer search(String value)
    {
        Integer i = 0;
        Integer position = h(value, i);

        while (i < capacity && position < capacity && table.get(position) != null && !table.get(position).equals(value))
        {
            i++;
            position = h(value, i);
        }

        if (i < capacity && h(value, i) < capacity && table.get(h(value, i)) != null)
            return position;
        else
            return -1;
    }

    public Integer add(String value)
    {
        Integer i = 0;
        while (i < capacity && table.get(h(value, i)) != null && !table.get(h(value, i)).equals(value))
            i++;

        if (i >= capacity)
        {
            capacity *= 2;
            List<String> old_list = table;
            table = new ArrayList<>(capacity);

            for (int j = 0; j < capacity; j++)
                table.add(null);

            length = 0;
            for (int j = 0; j < capacity / 2; j++)
                if (old_list.get(j) != null)
                    add(old_list.get(j));
        }

        i = 0;
        while (i < capacity && table.get(h(value, i)) != null && !table.get(h(value, i)).equals(value))
            i++;

        Integer pos = h(value, i);
        table.set(pos, value);
        length++;

        return pos;
    }

    public Integer size()
    {
        return length;
    }

    public Boolean isEmpty()
    {
        return length == 0;
    }

    public String toString()
    {
        String st = "";
        for (Integer i = 0; i < capacity; i++)
            if (table.get(i) != null)
                st += table.get(i) + " -> " + i + "\n";

        return st;
    }
}
