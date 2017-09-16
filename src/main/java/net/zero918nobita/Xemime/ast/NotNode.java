package net.zero918nobita.Xemime.ast;

import net.zero918nobita.Xemime.entity.Bool;

/**
 * 否定演算子を表すノードです。<br>
 * 評価されると、真偽(Bool)型の T は NIL に、NIL は T に変換します。
 * @author Kodai Matsumoto
 */

public class NotNode extends Node {
    private Node node;

    public NotNode(int location, Node node) {
        super(location);
        this.node = node;
    }

    @Override
    public Node run() throws Exception {
        Node o = node.run();

        // Fatal Exception - 真偽値以外のデータには単項演算子 `!` を適用できません
        if (o.getClass() != Bool.class) throw new FatalException(getLocation(), 15);

        Bool p = (Bool)o;
        if (p.isTrue()) return Bool.Nil;
        else return Bool.T;
    }
}
