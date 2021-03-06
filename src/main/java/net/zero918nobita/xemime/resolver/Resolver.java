package net.zero918nobita.xemime.resolver;

import net.zero918nobita.xemime.ast.Node;
import net.zero918nobita.xemime.ast.Symbol;
import net.zero918nobita.xemime.parser.FatalError;
import net.zero918nobita.xemime.type.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

/** 変数の参照の解決 */
public class Resolver {
    private Stack<Scope> scope = new Stack<>();
    private HashSet<Symbol> substs = new HashSet<>();
    private StaticTypeChecker stc = new StaticTypeChecker();
    private ArrayList<Symbol> postponedSymbols = new ArrayList<>();

    public Resolver() {
        scope.add(new Scope(null));
    }

    public void declareVar(Symbol symbol, Node node) throws FatalError, SemanticError, TypeError {
        scope.peek().defVar(stc.check(this, node), symbol);
    }

    public void declareVar(Type type, Symbol symbol) {
        scope.peek().defVar(type, symbol);
    }

    public void assignVar(int location, Symbol symbol, Node node) throws FatalError, SemanticError, TypeError {
        if (!scope.peek().hasVariable(symbol)) throw new SemanticError(location, 51, symbol);
        Type type_of_variable = getTypeOfVariable(symbol);
        Type type_of_value = stc.check(this, node);
        if (!type_of_variable.equals(type_of_value) &&
                !(type_of_variable instanceof AnyType) &&
                !(type_of_variable instanceof ArrayType && ((ArrayType)type_of_variable).getType() instanceof AnyType) &&
                !(type_of_variable instanceof ArrayType &&
                        ((ArrayType)type_of_variable).getType() instanceof DoubleType &&
                        type_of_value instanceof ArrayType &&
                        ((ArrayType)type_of_value).getType() instanceof IntType))
            throw new TypeError(location, 87,
                    "代入式が不正です。変数の型と代入される値の型が一致しません。\n" +
                            "変数の型: " + type_of_variable + ", 値の型: " + type_of_value);
    }

    public boolean referVar(int location, Symbol sym) throws SemanticError {
        return scope.peek().referVar(location, sym);
    }

    public Type getTypeOfNode(Node node) throws FatalError, SemanticError, TypeError {
        return stc.check(this, node);
    }

    public Type getTypeOfVariable(Symbol sym) throws SemanticError {
        return scope.peek().getTypeOfVariable(sym);
    }

    public void addScope() {
        scope.add(new Scope(scope.peek()));
    }

    public void removeScope() {
        postpone(scope.peek().getPostponedSymbols());
        scope.pop();
    }

    public void finishResolving() {
        postpone(scope.peek().getPostponedSymbols());
    }

    public void defineAttr(Symbol symbol) throws Exception {
        if (substs.contains(symbol)) throw new Exception(symbol + "型はすでに定義されています。");
        substs.add(symbol);
    }

    public boolean hasAttr(Symbol symbol) {
        return substs.contains(symbol);
    }

    private void postpone(ArrayList<Symbol> symbols) {
        postponedSymbols.addAll(symbols);
    }

    public ArrayList<Symbol> getPostponedSymbols() {
        return postponedSymbols;
    }
}
