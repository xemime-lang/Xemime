package net.zero918nobita.Xemime;

/**
 * 変数宣言
 * @author Kodai Matsumoto
 */

class X_Declare extends X_BinExpr {
    X_Declare(X_Symbol symbol, X_Object obj) {
        super(TokenType.DECLARE, symbol, obj);
    }

    X_Object run() throws Exception {
        X_Symbol sym = (X_Symbol)obj1;
        X_Object o = obj2.run();
        Main.defValue(sym, o);
        return o;
    }
}