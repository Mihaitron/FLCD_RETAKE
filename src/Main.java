public class Main
{
    public static void main(String[] args)
    {
        SymbolTable st = new SymbolTable();

        System.out.println("== ADD POSITIONS ==");
        System.out.println(st.add("ab"));
        System.out.println(st.add("ba"));
        System.out.println(st.add("c"));
        System.out.println(st.add("1"));
        System.out.println(st.add("e"));

        System.out.println("== SEARCH POSITIONS == ");
        System.out.println(st.search("ab"));
        System.out.println(st.search("ba"));
        System.out.println(st.search("c"));
        System.out.println(st.search("1"));
        System.out.println(st.search("e"));
    }
}
