package net.zero918nobita.Xemime;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

/**
 * net.zero918nobita.Xemime.X_Symbol のテストクラスです。
 * @author Kodai Matsumoto
 */

public class X_SymbolTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testRun() throws Exception {
        expectedException.expect(Exception.class);
        expectedException.expectMessage("シンボル `unknown` は定義されていません");
        X_Symbol symbol = X_Symbol.intern("unknown");
        symbol.run();
    }

    @Test
    public void testRun2() throws Exception {
        Main.defValue(X_Symbol.intern("variable"), new X_Symbol("value"));
        X_Symbol symbol = X_Symbol.intern("variable");
        assertThat(symbol.run().toString(), is("value"));
    }

    @Test
    public void testToString() {
        X_Symbol symbol = X_Symbol.intern("sample");
        assertThat(symbol.toString(), is("sample"));
    }
}
