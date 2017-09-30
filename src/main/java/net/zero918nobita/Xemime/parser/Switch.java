package net.zero918nobita.Xemime.parser;

import net.zero918nobita.Xemime.ast.Node;
import net.zero918nobita.Xemime.lexer.Lexer;
import net.zero918nobita.Xemime.resolver.Resolver;

/**
 * switch 文の構文解析器
 * @author Kodai Matsumoto
 */

class Switch extends ParseUnit {
    /**
     * @param lexer 字句解析器
     * @param resolver 意味解析器
     */
    Switch(Lexer lexer, Resolver resolver) {
        super(lexer, resolver);
    }

    /**
     * switch 文の構文解析と意味解析を行います。
     * @return 生成された AST
     */
    @Override
    protected Node parse() throws Exception {
        return null;
    }
}
