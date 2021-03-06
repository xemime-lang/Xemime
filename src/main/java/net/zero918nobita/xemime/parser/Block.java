package net.zero918nobita.xemime.parser;

import net.zero918nobita.xemime.ast.BlockNode;
import net.zero918nobita.xemime.ast.Node;
import net.zero918nobita.xemime.lexer.Lexer;
import net.zero918nobita.xemime.resolver.Resolver;

import java.util.ArrayList;

import static net.zero918nobita.xemime.lexer.TokenType.BR;
import static net.zero918nobita.xemime.lexer.TokenType.RB;

/** ブロック式のパーサ */
class Block extends ParseUnit {
    Block(Lexer lexer, Resolver resolver) {
        super(lexer, resolver);
    }

    @Override
    protected Node parse() throws Exception {
        ArrayList<Node> list;
        getToken();
        resolver.addScope();
        skipLineBreaks();
        Node node = new Expr(lexer, resolver).parse();

        // Syntax Error - 不明なトークン [value] が発見されました。
        if (!current(BR) && !current(RB)) throw new SyntaxError(lexer.getLocation(), 31, "不明なトークン `" + lexer.value() + "` が発見されました。");

        list = new ArrayList<>();
        list.add(node);
        while (!current(RB)) {
            skipLineBreaks();
            if (current(RB)) break;
            node = new Expr(lexer, resolver).parse();
            if (current(BR) || current(RB)) {
                list.add(node);
            } else {
                // Syntax Error - ブロック式内のステートメントにセミコロンが付いていません。
                throw new SyntaxError(lexer.getLocation(), 7, "ブロック式内のステートメントにセミコロンが付いていません。");
            }
            getToken();
        }
        resolver.removeScope();
        getToken();
        return new BlockNode(lexer.getLocation(), list);
    }
}
