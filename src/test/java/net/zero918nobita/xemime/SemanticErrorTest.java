package net.zero918nobita.xemime;

import net.zero918nobita.xemime.ast.Symbol;
import net.zero918nobita.xemime.resolver.SemanticError;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * net.zero918nobita.xemime.resolver.SemanticError のテストクラスです。
 * @author Kodai Matsumoto
 */

public class SemanticErrorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void test() throws Exception {
        expectedException.expect(SemanticError.class);
        expectedException.expectMessage("1: シンボル `a` の参照先を解決できません [2]");

        throw new SemanticError(1, 2, Symbol.intern("a"));
    }
}
