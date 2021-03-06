package net.zero918nobita.xemime.ast;

import net.zero918nobita.xemime.NodeType;
import net.zero918nobita.xemime.Recognizable;
import net.zero918nobita.xemime.entity.Int;
import net.zero918nobita.xemime.interpreter.Main;

/**
 * 前置デクリメントを表すノードです。
 * @author Kodai Matsumoto
 */

public class PrefixDecrementNode extends Node implements Recognizable {
    private Symbol symbol;
    private static Int one = new Int(0, 1);

    public PrefixDecrementNode(int location, Symbol symbol) {
        super(location);
        this.symbol = symbol;
    }

    @Override
    public NodeType recognize() {
        return NodeType.PREFIX_DEC;
    }

    @Override
    public Node run() throws Exception {
        Node node = Main.getValueOfSymbol(symbol);

        // Fatal Exception - 前置デクリメントの評価に失敗しました。
        if (node == null) throw new FatalException(getLocation(), 34);

        node = node.run();

        // Fatal Exception - 前置デクリメントの評価に失敗しました。
        if (!(node instanceof Int)) throw new FatalException(getLocation(), 35);

        Main.setValue(symbol, node.sub(getLocation(), one));
        return Main.getValueOfSymbol(symbol);
    }
}
