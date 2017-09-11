package net.zero918nobita.Xemime.parser;

import net.zero918nobita.Xemime.ast.ExprNode;
import net.zero918nobita.Xemime.ast.Node;
import net.zero918nobita.Xemime.entity.Int;
import net.zero918nobita.Xemime.lexer.Lexer;
import net.zero918nobita.Xemime.lexer.TokenType;
import net.zero918nobita.Xemime.resolver.Resolver;

/**
 * 項を構文解析します。
 * @author Kodai Matsumoto
 */

class Term extends ParseUnit {
    Term(Lexer lexer, Resolver resolver) {
        super(lexer, resolver);
    }

    @Override
    Node parse() throws Exception {
        Factor factor = new Factor(lexer, resolver);
        Node node = factor.parse();
        switch (lexer.tokenType()) {
            case MUL:
            case DIV:
            case XOR:
                node = term(node);
                break;
        }
        return node;
    }

    private Node term(Node node) throws Exception {
        ExprNode result = null;
        while ((lexer.tokenType() == TokenType.MUL) ||
                (lexer.tokenType() == TokenType.DIV) ||
                (lexer.tokenType() == TokenType.AND) ||
                (lexer.tokenType() == TokenType.XOR)) {
            TokenType op = lexer.tokenType();
            getToken();
            Node term = new Term(lexer, resolver).parse();
            if (op == TokenType.DIV && term.equals(new Int(0, 0))) throw new DivideByZeroException(lexer.getLocation(), 1);
            if (result == null) {
                result = new ExprNode(lexer.getLocation(), op, node, term);
            } else {
                result = new ExprNode(lexer.getLocation(), op, result, term);
            }
        }
        return result;
    }
}