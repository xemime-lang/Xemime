package net.zero918nobita.xemime;

import net.zero918nobita.xemime.interpreter.Main;
import net.zero918nobita.xemime.resolver.SemanticError;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * net.zero918nobita.xemime.resolver.Scope のテストクラスです。
 * @author Kodai Matsumoto
 */

public class ScopeTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void test() throws Exception {
        expectedException.expect(SemanticError.class);
        expectedException.expectMessage("4: シンボル `a` の参照先を解決できません [2]");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        Main.exec(
                "{\n" +
                        "  let a = 2\n" +
                        "}\n" +
                        "println $ a\n"
        );
    }
}
