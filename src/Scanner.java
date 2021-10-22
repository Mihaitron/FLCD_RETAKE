import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

public class Scanner
{
    private List<Entry<String, Integer>> pif = new ArrayList<>();
    private SymbolTable st = new SymbolTable();
    private Set<String> tokens = new HashSet<>();

    public Scanner() throws IOException
    {
        String tokens_string = Files.readString(Path.of("resources/tokens.in"));
        String[] tokens_split = tokens_string.split("\r\n");
        tokens.addAll(Arrays.asList(tokens_split));
    }

    private Entry<String, Integer> detect(String program, Integer index)
    {
        String token = "";

        while (!Character.isWhitespace(program.charAt(index)))
        {
            token += program.charAt(index);
            index++;
        }

        return new SimpleEntry<>(token, index);
    }

    private Boolean isIdentifierOrConstant(String raw)
    {
        String token = raw.replace(";", "");
        if (st.search(token) != -1)
            return true;

        if (raw.contains(";"))
            return true;

        return false;
    }

    public void scan(String filename) throws IOException
    {
        String program = Files.readString(Path.of("resources/" + filename));
        int lineNr = 1;

        for (int i = 0; i < program.length(); i++)
        {
            Entry<String, Integer> token_index = detect(program, i);
            String token = token_index.getKey();
            i = token_index.getValue();

            if (token.equals(""))
                continue;

            if (tokens.contains(token))
                pif.add(new SimpleEntry<>(token, 0));
            else if (isIdentifierOrConstant(token))
            {
                Integer index = st.search(token);

                pif.add(new SimpleEntry<>(token, index != -1 ? index : st.add(token)));
            }
            else
                System.out.println("Lexical error at line: " + lineNr);

            if (program.charAt(i) == '\n')
            {
                lineNr++;
            }
        }
    }
}
