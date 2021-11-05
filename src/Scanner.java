import java.io.BufferedWriter;
import java.io.FileWriter;
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
    private FA integerFA = new FA();
    private FA stringFA = new FA();
    private FA identifierFA = new FA();

    public Scanner() throws IOException
    {
        String tokens_string = Files.readString(Path.of("resources/tokens.in"));
        String[] tokens_split = tokens_string.split("\r\n");
        integerFA.readFA("integer_fa.in");
        stringFA.readFA("string_fa.in");
        identifierFA.readFA("identifier_fa.in");

        tokens.addAll(Arrays.asList(tokens_split));
        separators.add(';');
        separators.add('(');
        separators.add(')');
        separators.add(',');
        separators.add('[');
        separators.add(']');
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

                if (index >= program.length())
                    break;

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
            return new SimpleEntry<>("" + character, index);
        else
            return new SimpleEntry<>(token, index - 1);
    }

    private Boolean isIdentifierOrConstant(String token)
    {
//        return token.equals("true") ||
//                token.equals("false") ||
//                Pattern.matches("[-+]?(0|[1-9][1-90]*)", token) ||
//                Pattern.matches("[A-Za-z][A-Za-z1-90_]*", token) ||
//                Pattern.matches("\"[^\"]*\"", token);
        return token.equals("true") ||
                token.equals("false") ||
                integerFA.verifySequenceAccepted(token) ||
                stringFA.verifySequenceAccepted(token) ||
                identifierFA.verifySequenceAccepted(token);
    }

    public void scan(String filename) throws IOException
    {
        String program = Files.readString(Path.of("resources/" + filename)).replace("\r", "");
        int line_nr = 1;

        for (int i = 0; i < program.length(); i++)
        {
            Entry<String, Integer> token_index = detect(program, i);
            String token = token_index.getKey();
            i = token_index.getValue();

            if (token.trim().isEmpty())
            {
                if (token.equals("\n"))
                    line_nr++;
                continue;
            }

            if (tokens.contains(token))
                pif.add(new SimpleEntry<>(token, 0));
            else if (isIdentifierOrConstant(token))
            {
                Integer index = st.search(token);

                pif.add(new SimpleEntry<>(token, index != -1 ? index : st.add(token)));
            }
            else
            {
                System.out.println("Lexical error at line: " + line_nr);
                return;
            }
        }

        System.out.println("Program is lexically correct.");
        writePIF();
        writeST();
    }

    private void writePIF() throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter("out_files/PIF.out"));

        String pif_string = "";
        for (Entry entry : pif)
            pif_string += entry.getKey() + " -> " + entry.getValue() + "\n";

        writer.append("===== PIF =====\n");
        writer.append(pif_string);

        writer.close();
    }

    private void writeST() throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter("out_files/ST.out"));

        writer.append("===== ST =====\n");
        writer.append(st.toString());

        writer.close();
    }
}
