package net.zero918nobita.xemime.parser;

import net.zero918nobita.xemime.ast.ExprNode;
import net.zero918nobita.xemime.ast.Node;
import net.zero918nobita.xemime.lexer.Lexer;
import net.zero918nobita.xemime.lexer.TokenType;
import net.zero918nobita.xemime.resolver.Resolver;

import static net.zero918nobita.xemime.lexer.TokenType.ADD;
import static net.zero918nobita.xemime.lexer.TokenType.OR;
import static net.zero918nobita.xemime.lexer.TokenType.SUB;

/**
 * 単純式の構文解析器
 * @author Kodai Matsumoto
 */

class ArithmeticExpr extends ParseUnit {
    /**
     * @param lexer 字句解析器
     * @param resolver 意味解析器
     */
    ArithmeticExpr(Lexer lexer, Resolver resolver) {
        super(lexer, resolver);
    }

    /**
     * 単純式の構文解析と意味解析を行います。
     * @return 生成された AST
     */
    @Override
    protected Node parse() throws Exception {
        Node node = new Term(lexer, resolver).parse();
        switch (lexer.tokenType()) {
            case ADD:
            case SUB:
            case OR:
                node = simpleExpr(node);
                break;
        }
        return node;
    }

    private Node simpleExpr(Node node) throws Exception {
        ExprNode result = null;
        while ((current(ADD)) ||
                (current(SUB)) ||
                (current(OR))) {
            TokenType op = lexer.tokenType();
            getToken();
            skipLineBreaks();
            Node term = new Term(lexer, resolver).parse();
            if (result == null) {
                result = new ExprNode(lexer.getLocation(), op, node, term);
                resolver.getTypeOfNode(result);
            } else {
                result = new ExprNode(lexer.getLocation(), op, result, term);
                resolver.getTypeOfNode(result);
            }
        }
        return result;
    }
}
