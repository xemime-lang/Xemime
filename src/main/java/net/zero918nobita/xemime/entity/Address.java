package net.zero918nobita.xemime.entity;

import net.zero918nobita.xemime.NodeType;
import net.zero918nobita.xemime.Recognizable;
import net.zero918nobita.xemime.ast.Node;

/**
 * アドレス<br>
 * グローバルフレーム、ローカルフレームで管理され、シンボルとその値にあたる実体を結びつけます。
 * フレームの値であり、実体テーブルのキーでもあります。
 * @author Kodai Matsumoto
 */

public class Address extends Node implements Comparable, Recognizable {
    private int address;

    public Address(int location, int address) {
        super(location);
        this.address = address;
    }

    @Override
    public NodeType recognize() {
        return NodeType.ADDRESS;
    }

    /** アドレスを取得します。 */
    public int getAddress() {
        return address;
    }

    /** アドレスを文字列化します。 */
    @Override
    public String toString() {
        return "<#" + String.valueOf(address) + ">";
    }

    /** 参照先が一致していれば true 、そうでなければ false を返します。 */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Address && address == ((Address)obj).getAddress();
    }

    @Override
    public int hashCode() {
        return address;
    }

    @Override
    public int compareTo(Object obj) {
        return address - ((Address)obj).getAddress();
    }
}
