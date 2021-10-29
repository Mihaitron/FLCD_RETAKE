import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class Scanner
{
    private List<Entry<String, Integer>> pif = new ArrayList<>();
    private SymbolTable st = new SymbolTable();
    private Set<String> tokens = new HashSet<>();
    private Set<Character> separators = new HashSet<>();

    public Scanner() throws IOException
    {
        String tokens_string = Files.readString(Path.of("resources/tokens.in"));
        String[] tokens_split = tokens_string.split("\r\n");

        tokens.addAll(Arrays.asList(tokens_split));
        separators.add(';');
        separators.add('(');
        separators.add(')');
        separators.add(',');
    }

    private Entry<String, Integer> detect(String program, Integer index)
    {
        String token = "";
        Character character = program.charAt(index);

        if (separators.contains(character))
        {
            token += character;
            return new SimpleEntry<>(token, index);
        }

        if (character == '"')
        {
            do
            {
                token += character;
                index++;
                character = program.charAt(index);
            }
            while (character != '"');
            token += character;
            index++;
        }
        else
        {
            while (!Character.isWhitespace(character) && !separators.contains(character) && index < program.length())
            {
                token += character;
                index++;
                if (index < program.length())
                    character = program.charAt(index);
            }
        }

        if (token.equals(""))
            return new SimpleEntry<>(token, index);
        else
            return new SimpleEntry<>(token, index - 1);
    }

    private Boolean isIdentifierOrConstant(String token)
    {
        return token.equals("true") ||
                token.equals("false") ||
                Pattern.matches("[-+]?(0|[1-9][1-90]*)", token) ||
                Pattern.matches("[A-Za-z][A-Za-z1-90_]*", token) ||
                Pattern.matches("\"[^\"]*\"", token);
    }

    public void scan(String filename) throws IOException
    {
        String program = Files.readString(Path.of("resources/" + filename)).replace("\r", "");
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
            {
                System.out.println("Lexical error at line: " + lineNr);
                break;
            }

            if (program.charAt(i) == '\n')
            {
                lineNr++;
            }
        }

        System.out.println("Program is lexically correct.");
    }
}
