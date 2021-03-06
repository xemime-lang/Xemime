package net.zero918nobita.xemime;

import net.zero918nobita.xemime.entity.Double;
import net.zero918nobita.xemime.ast.MinusNode;
import net.zero918nobita.xemime.entity.Str;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * net.zero918nobita.xemime.ast.MinusNode のテストクラスです。
 * @author Kodai Matsumoto
 */

public class MinusNodeTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testRun() throws Exception {
        MinusNode minus = new MinusNode(0, new Double(0.2));
        assertThat(minus.run(), is(new Double(-0.2)));
    }

    @Test
    public void testRun2() throws Exception {
        expectedException.expect(Exception.class);
        expectedException.expectMessage("0: インタプリタ内部の深刻なエラーが発生しました。 [14]");
        MinusNode minus = new MinusNode(0, new Str("foo"));
        minus.run();
    }
}
