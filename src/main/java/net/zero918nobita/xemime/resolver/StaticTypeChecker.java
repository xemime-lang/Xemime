package net.zero918nobita.xemime.resolver;

import net.zero918nobita.xemime.NodeType;
import net.zero918nobita.xemime.ast.*;
import net.zero918nobita.xemime.entity.*;
import net.zero918nobita.xemime.entity.Double;
import net.zero918nobita.xemime.parser.FatalError;
import net.zero918nobita.xemime.type.*;

import java.util.LinkedHashMap;

/**
 * 意味解析中に、静的型チェックを行います。
 * @author Kodai Matsumoto
 */

class StaticTypeChecker {
    Type check(Resolver resolver, Node node) throws TypeError, SemanticError, FatalError {
        switch (node.recognize()) {
            case BOOL:
                return check(resolver, (Bool) node);
            case INT:
                return check(resolver, (Int) node);
            case DOUBLE:
                return check(resolver, (Double) node);
            case STR:
                return check(resolver, (Str) node);
            case ARRAY:
                return check(resolver, (Array) node);
            case ARRAY_REFERENCE:
                return check(resolver, (ArrayReferenceNode)node);
            case SYMBOL:
                return check(resolver, (Symbol) node);
            case MINUS:
                return check(resolver, (MinusNode) node);
            case EXPR:
                return check(resolver, (ExprNode) node);
            case LAMBDA_EXPR:
                return check(resolver, (LambdaExprNode) node);
            case FUNCALL:
                return check(resolver, (FuncallNode) node);
            case DOT_EXPR:
                return check(resolver, (DotExprNode) node);
            case DOT_CALL:
                return check(resolver, (DotCallNode) node);
            default:
                throw new TypeError(node.getLocation(), 64, "`" + node + "` の静的型チェックに失敗しました。");
        }
    }

    private Type check(Resolver resolver, Bool bool) {
        return BoolType.gen();
    }

    private Type check(Resolver resolver, Int num) {
        return IntType.gen();
    }

    private Type check(Resolver resolver, Double num) {
        return DoubleType.gen();
    }

    private Type check(Resolver resolver, Str str) {
        return StrType.gen();
    }

    private Type check(Resolver resolver, Array array) throws FatalError, SemanticError, TypeError {
        if (array.getElements().size() == 0) return new ArrayType(AnyType.gen());
        Type type = resolver.getTypeOfNode(array.getElement(0));
        for (int i = 1; i < array.getElements().size(); i++) {
            if (type instanceof IntType && resolver.getTypeOfNode(array.getElement(i)) instanceof DoubleType) {
                type = DoubleType.gen();
            } else if (!resolver.getTypeOfNode(array.getElement(i)).equals(type) &&
                    !(type instanceof DoubleType && resolver.getTypeOfNode(array.getElement(i)) instanceof IntType))
                type = AnyType.gen();
        }
        return new ArrayType(type);
    }

    private Type check(Resolver resolver, ArrayReferenceNode arrayReferenceNode) throws FatalError, SemanticError, TypeError {
        if (!(resolver.getTypeOfNode(arrayReferenceNode.getArray()) instanceof ArrayType))
            throw new TypeError(arrayReferenceNode.getLocation(), 139, "配列参照 `" + arrayReferenceNode + "` の対象となっている `" + arrayReferenceNode.getArray() + "` は配列ではありません。");
        return ((ArrayType)resolver.getTypeOfNode(arrayReferenceNode.getArray())).getType();
    }

    private Type check(Resolver resolver, Symbol symbol) throws SemanticError {
        return resolver.getTypeOfVariable(symbol);
    }

    /**
     * MinusNode ( 単項演算子 `-` ) の型チェックを行います。
     * @param resolver 意味解析器
     * @param minusNode 対象のノード
     * @return 戻り値の型
     * @throws TypeError 単項演算子 `-` を付与されているデータの型が整数型でも小数型でもない場合に発生させます。
     */
    private Type check(Resolver resolver, MinusNode minusNode) throws TypeError, SemanticError, FatalError {
        Type type = check(resolver, minusNode.getAbs());
        if (type instanceof IntType || type instanceof DoubleType) {
            return type;
        } else {
            throw new TypeError(minusNode.getLocation(), 63, "整数型または小数型以外の型のデータに単項演算子 `-` を付与することはできません。");
        }
    }

    private Type check(Resolver resolver, ExprNode exprNode) throws TypeError, SemanticError, FatalError {
        Node lhs = exprNode.getLhs();
        Node rhs = exprNode.getRhs();
        if (lhs instanceof ExprNode)
            while ((lhs = ((ExprNode) lhs).getLhs()) instanceof ExprNode) check(resolver, lhs);
        if (rhs instanceof ExprNode)
            while ((rhs = ((ExprNode) rhs).getRhs()) instanceof ExprNode) check(resolver, rhs);
        Type tLhs = check(resolver, lhs);
        Type tRhs = check(resolver, rhs);

        switch (exprNode.getOperator()) {
            case ADD:
                if (tLhs instanceof StrType && tRhs instanceof StrType) {
                    return StrType.gen();
                } else if (tLhs instanceof StrType || tRhs instanceof StrType) {
                    throw new TypeError(exprNode.getLocation(), 61, "文字列型データに他の型のデータを足すことはできません。");
                }
            case SUB:
            case MUL:
            case DIV:
                if (tLhs instanceof IntType) {
                    if (tRhs instanceof IntType) {
                        return IntType.gen();
                    } else if (tRhs instanceof DoubleType) {
                        return DoubleType.gen();
                    } else if (tRhs instanceof AnyType) {
                        return tRhs;
                    } else {
                        throw new TypeError(exprNode.getLocation(), 59, "演算子の右辺のデータの型が不正です。");
                    }
                } else if (tLhs instanceof DoubleType) {
                    if (tRhs instanceof IntType || tRhs instanceof DoubleType) {
                        return DoubleType.gen();
                    } else if (tRhs instanceof AnyType) {
                        return tRhs;
                    } else {
                        throw new TypeError(exprNode.getLocation(), 58, "演算子の右辺のデータの型が不正です。");
                    }
                } else if (tLhs instanceof AnyType) {
                    return tLhs;
                } else {
                    throw new TypeError(exprNode.getLocation(), 57, "演算子の左辺のデータの型が不正です。");
                }
            case MOD:
                if (tLhs instanceof IntType && tRhs instanceof IntType) return tLhs;
                else if (tLhs instanceof IntType) throw new TypeError(exprNode.getLocation(), 142, "剰余演算子の右辺が整数型データではありません。");
                else if (tRhs instanceof IntType) throw new TypeError(exprNode.getLocation(), 143, "剰余演算子の左辺が整数型データではありません。");
                else throw new TypeError(exprNode.getLocation(), 144, "剰余演算子の両辺が整数型データではありません。");
            case AND:
            case OR:
            case XOR:
                if ((tLhs instanceof BoolType || tLhs instanceof AnyType) &&
                        (tRhs instanceof BoolType || tRhs instanceof AnyType)) {
                    return BoolType.gen();
                } else if (tLhs instanceof BoolType || tLhs instanceof AnyType) {
                    throw new TypeError(exprNode.getLocation(), 129, "論理演算子の右辺のデータの型が不正です。");
                } else {
                    throw new TypeError(exprNode.getLocation(), 130, "論理演算子の左辺のデータの型が不正です。");
                }
            default:
                throw new FatalError(exprNode.getLocation(), 60);
        }
    }

    private Type check(Resolver resolver, LambdaExprNode lambdaExprNode) throws TypeError, SemanticError, FatalError {
        return new FuncType(AnyType.gen(), new LinkedHashMap<>());
    }

    private Type check(Resolver resolver, FuncallNode funcallNode) throws TypeError, SemanticError, FatalError {
        Node func = funcallNode.getFunc();
        Type type;
        if (func.is(NodeType.SYMBOL)) {
            type = resolver.getTypeOfVariable((Symbol)func);
            if (type instanceof FuncType) {
                type = ((FuncType)type).getReturnType();
            } else if (!(type instanceof AnyType)){
                throw new TypeError(func.getLocation(), 97, "指定されたシンボルの型が関数型ではありません。");
            }
        } else if (!(func instanceof Native)) {
            // Fatal Error - シンボルまたは組み込み関数を指定してください。
            throw new FatalError(funcallNode.getLocation(), 96);
        } else {
            type = ((Native) func).getReturnType();
        }
        return type;
    }

    private Type check(Resolver resolver, DotExprNode dotExprNode) throws TypeError, SemanticError, FatalError {
        return IntType.gen();
    }

    private Type check(Resolver resolver, DotCallNode dotCallNode) throws TypeError, SemanticError, FatalError {
        return IntType.gen();
    }
}
