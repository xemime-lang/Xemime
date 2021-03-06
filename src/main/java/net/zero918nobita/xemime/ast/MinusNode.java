package net.zero918nobita.xemime.ast;

import net.zero918nobita.xemime.NodeType;
import net.zero918nobita.xemime.Recognizable;
import net.zero918nobita.xemime.entity.Double;
import net.zero918nobita.xemime.entity.Int;
import net.zero918nobita.xemime.entity.Numeric;
import org.jetbrains.annotations.NotNull;

/**
 * 単項演算子 `-` を表すノードです。<br>
 * 評価されると、数値(IntType, Double)型の値の符号を反転させます。
 * @author Kodai Matsumoto
 */

public class MinusNode extends Node implements Recognizable {
    private Node node;

    public MinusNode(int location, Node node) {
        super(location);
        this.node = node;
    }

    @Override
    @NotNull
    public NodeType recognize() {
        return NodeType.MINUS;
    }

    @Override
    public String toString() {
        return (node instanceof Numeric) ? "-" + node : "-(" + node + ")";
    }

    @Override
    @NotNull
    public Node run() throws Exception {
        Node o = node.run();
        if (o.getClass() != Int.class && o.getClass() != Double.class)
            // Fatal Exception - 数値以外のデータには単項演算子 `-` を適用できません
            throw new FatalException(getLocation(), 14);
        if (o.getClass() == Int.class) return new Int(getLocation(), -((Int)o).getValue());
        return new Double(getLocation(), -((Double)o).getValue());
    }

    public Node getAbs() {
        return node;
    }
}
