package net.zero918nobita.Xemime;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * エントリーポイント
 * @author Kodai Matsumoto
 */

public class Main {
    private static Parser parser;
    private static HashMap<X_Symbol, X_Address> globalSymbols = new HashMap<>();
    private static TreeMap<X_Address, X_Code> entities = new TreeMap<X_Address, X_Code>() {{
        put(new X_Address(0,0), X_Bool.T);
    }};
    static Frame frame = new Frame();

    static void loadLocalFrame(HashMap<X_Symbol, X_Address> table) {
        frame.loadLocalFrame(table);
    }

    static void unloadLocalFrame() {
        frame.unloadLocalFrame();
    }

    static boolean hasSymbol(X_Symbol sym) {
        return frame.hasSymbol(sym) || globalSymbols.containsKey(sym);
    }

    /**
     * シンボルの参照先アドレスを取得する
     * @param sym シンボル
     * @return 参照
     */
    static X_Address getAddressOfSymbol(X_Symbol sym) {
        return (frame.hasSymbol(sym)) ?
                frame.getAddressOfSymbol(sym) :
                globalSymbols.get(sym);
    }

    /**
     * シンボルの値を取得する
     * @param sym シンボル
     * @return 値
     */
    static X_Code getValueOfSymbol(X_Symbol sym) {
        if (frame.hasSymbol(sym)) {
            return frame.getValueOfSymbol(sym, entities);
        } else {
            return (globalSymbols.containsKey(sym)) ?
                    globalSymbols.get(sym).fetch(entities) : null;
        }
    }

    /**
     * 参照先の実体を取得する
     * @param address アドレス
     * @return 実体
     */
    static X_Code getValueOfReference(X_Address address) {
        return entities.get(address);
    }

    /**
     * シンボルに参照をセットする
     * @param sym シンボル
     * @param ref 参照
     */
    static void setAddress(X_Symbol sym, X_Address ref) throws Exception {
        if (frame.hasSymbol(sym)) { frame.setAddress(sym, ref); return; }
        if (!globalSymbols.containsKey(sym)) throw new Exception(parser.getLocation() + ": 変数 `" + sym.getName() + "` は宣言されていません");
        globalSymbols.put(sym, ref);
    }

    /**
     * 宣言済みの変数に値をセットします。
     * @param sym シンボル
     * @param obj 値
     * @throws Exception 変数が宣言されていなかった場合に例外を発生させます。
     */
    static void setValue(X_Symbol sym, X_Code obj) throws Exception {
        if (frame.hasSymbol(sym)) { frame.setValue(sym, obj); return; }
        X_Address ref = register(obj);
        if (!globalSymbols.containsKey(sym)) throw new Exception(parser.getLocation() + ": 変数 `" + sym.getName() + "` は宣言されていません");
        globalSymbols.put(sym, ref);
    }

    /**
     * 変数を参照で宣言します。
     * @param sym 変数
     * @param ref 参照
     */
    static void defAddress(X_Symbol sym, X_Address ref) throws Exception {
        if (frame.numberOfLayers() != 0) { frame.defAddress(sym, ref); return; }
        globalSymbols.put(sym, ref);
    }

    /**
     * 変数を値で宣言します。
     * @param sym 変数
     * @param obj 値
     */
    static void defValue(X_Symbol sym, X_Code obj) throws Exception {
        if (frame.numberOfLayers() != 0) { frame.defValue(sym, obj); return; }
        X_Address ref = register(obj);
        globalSymbols.put(sym, ref);
    }

    static X_Address register(X_Code obj) {
        entities.put(new X_Address(0,entities.lastKey().getAddress() + 1), obj);
        return new X_Address(0, entities.lastKey().getAddress());
    }

    public static void main(String[] args) {
        if (args.length >= 2) {
            usage();
            System.out.println(System.lineSeparator() + "Usage: java -jar Xemime.jar [source file name]");
            return;
        }

        globalSymbols.put(X_Symbol.intern(0, "Core"), register(new X_Core(0)));
        globalSymbols.put(X_Symbol.intern(0, "Object"), register(new X_Object(0)));

        try {
            parser = new Parser();
            BufferedReader in;
            if (args.length == 0) {
                usage();
                in = new BufferedReader(new InputStreamReader(System.in));
                System.out.print(System.lineSeparator() + "[1]> ");
                String line;
                while (true) {
                    line = in.readLine();
                    if (line != null && !line.equals("")) {
                        X_Code obj = parser.parse(line);
                        if (obj == null) break;
                        System.out.println(obj.run().toString());
                        System.out.print("[" + (obj.getLocation() + 1) + "]> ");
                        parser.goDown(1);
                    } else if (line == null) {
                        break;
                    }
                }
            } else {
                in = new BufferedReader(new FileReader(args[0]));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append('\n');
                }
                X_Code code = parser.parse("{" + stringBuilder.toString() + "};");
                code.run();
            }
            in.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void usage() {
        System.out.println("   _  __               _              \n" +
                "  | |/ /__  ____ ___  (_)___ ___  ___ \n" +
                "  |   / _ \\/ __ `__ \\/ / __ `__ \\/ _ \\\n" +
                " /   /  __/ / / / / / / / / / / /  __/\n" +
                "/_/|_\\___/_/ /_/ /_/_/_/ /_/ /_/\\___/ \n\n" +
                "Xemime Version 1.0.0 2017-08-07");
    }

    private static class X_Object extends X_Handler {
        X_Object(int n) {
            super(n);
            setMember(X_Symbol.intern(0, "clone"), new X_Clone());
        }

        private static class X_Clone extends X_Native {
            X_Clone() {
                super(0, 0);
            }

            @Override
            protected X_Address exec(ArrayList<X_Code> params) throws Exception {
                return Main.register(params.get(0).run());
            }
        }
    }

    private static class X_Core extends X_Handler {
        X_Core(int n) {
            super(n);
            setMember(X_Symbol.intern(0, "if"), new X_If());
            setMember(X_Symbol.intern(0, "print"), new X_Print());
            setMember(X_Symbol.intern(0, "println"), new X_Println());
            setMember(X_Symbol.intern(0, "exit"), new X_Exit());
        }

        private static class X_Exit extends X_Native {
            X_Exit() {
                super(0, 0);
            }

            @Override
            protected X_Code exec(ArrayList<X_Code> params) throws Exception {
                System.exit(0);
                return new X_Int(0, 0);
            }
        }

        private static class X_Print extends X_Native {
            X_Print() {
                super(0, 1);
            }

            @Override
            protected X_Code exec(ArrayList<X_Code> params) throws Exception {
                X_Code o = params.get(1).run();
                System.out.print(o);
                return o;
            }
        }

        private static class X_Println extends X_Native {
            X_Println() {
                super(0, 1);
            }

            @Override
            protected X_Code exec(ArrayList<X_Code> params) throws Exception {
                X_Code o = params.get(1).run();
                System.out.println(o);
                return o;
            }
        }

        private static class X_If extends X_Native {
            X_If(){
                super(0, 3);
            }

            @Override
            protected X_Code exec(ArrayList<X_Code> params) throws Exception {
                return (params.get(1).run().equals(X_Bool.Nil)) ? params.get(3).run() : params.get(2).run();
            }
        }
    }
}
