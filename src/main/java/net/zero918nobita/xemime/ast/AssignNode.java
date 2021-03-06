package net.zero918nobita.xemime.ast;

import net.zero918nobita.xemime.NodeType;
import net.zero918nobita.xemime.Recognizable;
import net.zero918nobita.xemime.entity.Address;
import net.zero918nobita.xemime.interpreter.Main;
import net.zero918nobita.xemime.lexer.TokenType;

/**
 * 代入演算子を表すノードです。
 * @author Kodai Matsumoto
 */

public class AssignNode extends ExprNode implements Recognizable {
    public AssignNode(int location, Symbol symbol, Node node) {
        super(location, TokenType.ASSIGN, symbol, node);
    }

    @Override
    public NodeType recognize() {
        return NodeType.ASSIGN_NODE;
    }

    public Node run() throws Exception {
        Symbol sym = (Symbol)lhs;

        // Fatal Exception - シンボルが宣言されていません
        if (!Main.hasSymbol(sym)) throw new FatalException(getLocation(), 6);

        Node code;
        if (rhs instanceof Symbol) {
            code = ((Symbol) rhs).getAddress();
            Main.defAddress(sym, (Address)code);
        } else {
            code = rhs.run();
            if (code instanceof Address) {
                Main.setAddress(sym, (Address) code);
            } else {
                Main.setValue(sym, code);
            }
        }
        return code;
    }
}
