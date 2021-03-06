package net.zero918nobita.xemime.ast;

import net.zero918nobita.xemime.entity.Attr;
import net.zero918nobita.xemime.entity.Closure;
import net.zero918nobita.xemime.interpreter.Main;

import java.util.HashMap;

/**
 * 属性定義式を表すノードです。
 * @author kodai Matsumoto
 */

public class AttrDeclarationNode extends Node {
    private Symbol name;
    private HashMap<Symbol, Node> member;

    public AttrDeclarationNode(int location, Symbol name, HashMap<Symbol, Node> member) {
        super(location);
        this.name = name;
        this.member = member;
    }

    @Override
    public Node run() throws Exception {
        if (member.containsKey(Symbol.intern("attach"))) {
            Node attach = member.get(Symbol.intern("attach")).run();
            if (attach instanceof Closure) {
                Main.defValue(name, new Attr(attach.getLocation(), (Closure) attach));
            } else {
                throw new FatalException(attach.getLocation(), 21);
            }
        }
        return Main.getValueOfSymbol(name);
    }
}
