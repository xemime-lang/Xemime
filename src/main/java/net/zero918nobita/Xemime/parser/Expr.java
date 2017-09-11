package net.zero918nobita.Xemime.parser;

import net.zero918nobita.Xemime.ast.ExprNode;
import net.zero918nobita.Xemime.ast.Node;
import net.zero918nobita.Xemime.lexer.Lexer;
import net.zero918nobita.Xemime.lexer.TokenType;
import net.zero918nobita.Xemime.resolver.Resolver;

class Expr extends ParseUnit {
    Expr(Lexer lexer, Resolver resolver) {
        super(lexer, resolver);
    }

    @Override
    Node parse() throws Exception {
        Node node = new SimpleExpr(lexer, resolver).parse();
        switch (lexer.tokenType()) {
            case L:
            case G:
            case EQ:
            case EQL:
            case NE:
            case LE:
            case GE:
                node = logicalExpr(node);
                break;
        }
        return node;
    }

    private Node logicalExpr(Node node) throws Exception {
        ExprNode result = null;
        while ((lexer.tokenType() == TokenType.L) ||
                (lexer.tokenType() == TokenType.G) ||
                (lexer.tokenType() == TokenType.EQ) ||
                (lexer.tokenType() == TokenType.EQL) ||
                (lexer.tokenType() == TokenType.NE) ||
                (lexer.tokenType() == TokenType.LE) ||
                (lexer.tokenType() == TokenType.GE)) {
            TokenType op = lexer.tokenType();
            getToken();
            Node simpleExpr = new SimpleExpr(lexer, resolver).parse();
            if (result == null) result = new ExprNode(lexer.getLocation(), op, node, simpleExpr);
            else result = new ExprNode(lexer.getLocation(), op, result, simpleExpr);
        }
        return result;
    }
}