package net.zero918nobita.Xemime.ast;

import net.zero918nobita.Xemime.interpreter.Main;

import java.util.ArrayList;

/**
 * 関数呼び出し
 * @author Kodai Matsumoto
 */

public class X_Funcall extends Node {
    private Node func;
    private ArrayList<Node> list;

    public X_Funcall(int n, Node code, ArrayList<Node> l) throws Exception {
        super(n);
        if (code instanceof X_Symbol || code instanceof X_Native || code instanceof X_Funcall) {
            func = code;
        } else {
            throw new Exception(getLocation() + ": 深刻なエラー: 関数呼び出しに失敗しました");
        }
        list = l;
    }

    @Override
    public Node run() throws Exception {
        if (func instanceof X_Native) {
            ArrayList<Node> params = new ArrayList<>();
            for (Node o : list) params.add(o.run());
            params.add(0, func);
            return ((X_Native) func).call(params, null);
        } else if (func instanceof X_Funcall) {
            Node c = func.run();
            if (c == null) throw new Exception(getLocation() + ": 存在しない関数です");
            if (!(c instanceof X_Function)) throw new Exception(getLocation() + ": 関数ではありません");
            X_Function f = (X_Function)c;
            ArrayList<Node> params = new ArrayList<>();
            for (Node o: list) params.add(o.run());
            params.add(0, f);
            return f.call(params, null);
        } else {
            X_Symbol symbol = (X_Symbol)func;
            Node c = Main.getValueOfSymbol(symbol);
            if (c == null) throw new Exception(getLocation() + ": 関数 `" + symbol.getName() + "` は存在しません");
            if (!(c instanceof X_Function)) throw new Exception(getLocation() + ": 変数 `" + symbol.getName() + "` には関数オブジェクトが代入されていません");
            X_Function func = (X_Function) c;
            ArrayList<Node> params = new ArrayList<>();
            for (Node o : list) params.add(o.run());
            params.add(0, func);
            return func.call(params, null);
        }
    }
}
