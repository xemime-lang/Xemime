package net.zero918nobita.xemime.resolver;

import net.zero918nobita.xemime.ast.Symbol;

import java.util.ArrayList;

public class Ruminator {
    /** 未解決シンボルの再解決 */
    public static void ruminate(ArrayList<Symbol> symbols, Resolver resolver) throws SemanticError {
        for (Symbol sym : symbols)
            if (!resolver.referVar(sym.getLocation(), sym))
                // Semantic Error - シンボルの参照の解決に失敗しました。
                throw new SemanticError(sym.getLocation(), 2, sym);
    }
}
