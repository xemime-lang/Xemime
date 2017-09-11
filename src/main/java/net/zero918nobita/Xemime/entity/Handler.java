package net.zero918nobita.Xemime.entity;

import net.zero918nobita.Xemime.ast.Node;
import net.zero918nobita.Xemime.ast.Symbol;
import net.zero918nobita.Xemime.interpreter.Main;

import java.util.ArrayList;
import java.util.HashMap;

public class Handler extends Node {
    /**
     * メンバのリスト
     */
    private HashMap<Symbol, Address> members;

    public Handler(int n) {
        super(n);
        members = new HashMap<>();
    }

    public boolean hasMember(Symbol symbol) {
        return members.containsKey(symbol);
    }

    /**
     * 新たにメンバを追加、または既存のメンバを上書きする
     * @param key メンバの名称
     * @param obj メンバの値
     */
    public void setMember(Symbol key, Node obj) {
        if (obj instanceof Address) {
            members.put(key, (Address) obj);
        } else {
            members.put(key, Main.register(obj));
        }
    }

    /**
     * 既存のメンバの値を取得する
     * @param key メンバの名称
     * @return メンバの値
     */
    public Node getMember(Symbol key) {
        return Main.getValueOfReference(members.get(key));
    }

    public HashMap<Symbol, Address> getMembers() throws Exception {
        return members;
    }

    public Address getAddressOfMember(Symbol key) {
        return members.get(key);
    }

    @Override
    public Node message(int line, Symbol symbol) throws Exception {
        if (!hasMember(symbol)) throw new Exception(line + ": `" + symbol.getName() + "` というフィールドはありません");
        return getMember(symbol);
    }

    @Override
    public Node message(int line, Symbol symbol, ArrayList<Node> params) throws Exception {
        if (symbol.equals(Symbol.intern(0, "proto")))
            throw new Exception(line + ": protoフィールドはメソッドとして呼び出すことはできません");
        if (!hasMember(symbol)) throw new Exception(line + ": `" + symbol.getName() + "` というメソッドはありません");
        Node o = getMember(symbol);
        if (!(o instanceof Function)) throw new Exception(line + ": `" + symbol.getName() + "` はメソッドではありません");
        if (params == null) params = new ArrayList<>();
        params.add(0, this);
        return ((Function) o).call(getLocation(), params, Main.register(this));
    }
}