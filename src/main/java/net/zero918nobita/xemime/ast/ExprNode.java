package net.zero918nobita.xemime.ast;

import net.zero918nobita.xemime.NodeType;
import net.zero918nobita.xemime.Recognizable;
import net.zero918nobita.xemime.entity.Address;
import net.zero918nobita.xemime.entity.Bool;
import net.zero918nobita.xemime.lexer.TokenType;
import org.jetbrains.annotations.NotNull;

import static net.zero918nobita.xemime.lexer.TokenType.AND;
import static net.zero918nobita.xemime.lexer.TokenType.OR;
import static net.zero918nobita.xemime.lexer.TokenType.XOR;

/**
 * 二項演算を表すノードです。<br>
 * parser パッケージの Parser.parse メソッドによってインスタンスが生成され、
 * 実行時には run メソッドで演算結果を返します。
 * @author Kodai Matsumoto
 */

public class ExprNode extends Node implements Recognizable {
    /** 演算子の種類 */
    private TokenType op;
    /** 左辺 */
    Node lhs;
    /** 右辺 */
    Node rhs;

    /**
     * 二項演算を表すノードを生成します。
     * @param location 行番号
     * @param operator 演算子の種類
     * @param lhs 左辺
     * @param rhs 右辺
     */
    public ExprNode(int location, TokenType operator, Node lhs, Node rhs) {
        super(location);
        op = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public NodeType recognize() {
        return NodeType.EXPR;
    }

    /** 二項演算 */
    public Node run() throws Exception {
        // 二項演算の結果
        Node result = null;
        // 左辺の評価結果
        Node e_lhs = lhs.run();
        // 右辺の評価結果
        Node e_rhs = rhs.run();

        // 演算子の種類によって処理を分岐させます
        if (op == AND) { // `&&`
            result = e_lhs.and(getLocation(), e_rhs);
        } else if (op == OR) { // `||`
            result = e_lhs.or(getLocation(), e_rhs);
        } else if (op == XOR) { // `^`
            result = e_lhs.xor(getLocation(), e_rhs);
        } else {
            switch (op) {
                case ADD: // `+`
                    result = e_lhs.add(getLocation(), e_rhs);
                    break;
                case SUB: // `-`
                    result = e_lhs.sub(getLocation(), e_rhs);
                    break;
                case MUL: // `*`
                    result = e_lhs.multiply(getLocation(), e_rhs);
                    break;
                case DIV: // `/`
                    result = e_lhs.divide(getLocation(), e_rhs);
                    break;
                case MOD:
                    result = e_lhs.mod(getLocation(), e_rhs);
                    break;
                case L: // `<`
                    result = e_lhs.less(getLocation(), e_rhs);
                    break;
                case LE: // `<=`
                    result = e_lhs.le(getLocation(), e_rhs);
                    break;
                case G: // `>`
                    result = e_lhs.greater(getLocation(), e_rhs);
                    break;
                case GE: // `>=`
                    result = e_lhs.ge(getLocation(), e_rhs);
                    break;
                case EQ: // `===`
                    if (lhs instanceof Symbol && rhs instanceof Symbol) {
                        Address a1 = ((Symbol)lhs).getAddress();
                        Address a2 = ((Symbol)rhs).getAddress();
                        result = a1.equals(a2) ? Bool.getT() : Bool.getNil();
                    } else {
                        result = Bool.getNil();
                    }
                    break;
                case EQL: // `==`
                    if (e_lhs.equals(e_rhs)) result = Bool.getT();
                    else result = Bool.getNil();
                    break;
                case NE: // `!=`
                    if (e_lhs.equals(e_rhs)) result = Bool.getNil();
                    else result = Bool.getT();
                    break;
            }
        }
        return result;
    }

    public TokenType getOperator() {
        return op;
    }

    public Node getLhs() {
        return lhs;
    }

    public Node getRhs() {
        return rhs;
    }
}
