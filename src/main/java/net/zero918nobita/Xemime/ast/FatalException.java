package net.zero918nobita.Xemime.ast;

public class FatalException extends Exception {
    public FatalException(int location, int errorCode) {
        super(location + ": インタプリタ内部の深刻なエラーが発生しました。 [" + errorCode + "]");
    }
}
