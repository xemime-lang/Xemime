package net.zero918nobita.Xemime;

/**
 * 否定演算子
 * @author Kodai Matsumoto
 */

class X_Not extends X_Code {
    private X_Code obj;

    X_Not(int n, X_Code o) {
        super(n);
        obj = o;
    }

    @Override
    X_Code run() throws Exception {
        X_Code o = obj.run();
        if (o.getClass() != X_Bool.class) throw new Exception(getLocation() + ": 真偽値以外のものには論理否定演算子を適用できません");
        X_Bool p = (X_Bool)o;
        if (p.isTrue()) return X_Bool.Nil;
        else return X_Bool.T;
    }
}
