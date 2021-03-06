package net.zero918nobita.xemime.parser;

import net.zero918nobita.xemime.ast.Node;
import net.zero918nobita.xemime.ast.ReturnNode;
import net.zero918nobita.xemime.entity.Unit;
import net.zero918nobita.xemime.lexer.Lexer;
import net.zero918nobita.xemime.resolver.Resolver;

import static net.zero918nobita.xemime.lexer.TokenType.SEMICOLON;

/**
 * 式の構文解析器
 * @author Kodai Matsumoto
 */

class Expr extends ParseUnit{
    /**
     * @param lexer 字句解析器
     * @param resolver 意味解析器
     */
    Expr(Lexer lexer, Resolver resolver) {
        super(lexer, resolver);
    }

    /**
     * 式の構文解析と意味解析と意味解析を行います。
     * @return 生成された AST
     */
    @Override
    protected Node parse() throws Exception {
        Node node;
        skipLineBreaks();

        switch (lexer.tokenType()) {
            // if 文
            case IF:
                node = new If(lexer, resolver).parse();
                break;

            // switch 文
            case SWITCH:
                node = new Switch(lexer, resolver).parse();
                break;

            // for 文
            case FOR:
                node = new For(lexer, resolver).parse();
                break;

            // while 文
            case WHILE:
                node = new While(lexer, resolver).parse();
                break;

            // fn (関数定義)
            case FN:
                node = new Fn(lexer, resolver).parse();
                break;

            case RETURN:
                getToken(); // skip `return`
                Node return_value = new Expr(lexer, resolver).parse();
                node = new ReturnNode(lexer.getLocation(), return_value);
                break;

            // 論理式
            default:
                node = new LogicalExpr(lexer, resolver).parse();
                if (current(SEMICOLON)) {
                    getToken();
                    node = new Unit(lexer.getLocation(), node);
                }
        }

        return node;
    }
}
