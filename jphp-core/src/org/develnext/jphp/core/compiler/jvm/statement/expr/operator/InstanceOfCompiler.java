package org.develnext.jphp.core.compiler.jvm.statement.expr.operator;

import org.develnext.jphp.core.compiler.common.misc.StackItem;
import org.develnext.jphp.core.compiler.jvm.statement.ExpressionStmtCompiler;
import org.develnext.jphp.core.compiler.jvm.statement.expr.BaseExprCompiler;
import org.develnext.jphp.core.tokenizer.token.expr.operator.InstanceofExprToken;
import php.runtime.Memory;

public class InstanceOfCompiler extends BaseExprCompiler<InstanceofExprToken> {
    public InstanceOfCompiler(ExpressionStmtCompiler exprCompiler) {
        super(exprCompiler);
    }

    @Override
    public void write(InstanceofExprToken instanceOf, boolean returnValue) {
        if (expr.stackEmpty(true))
            expr.unexpectedToken(instanceOf);

        StackItem o = expr.stackPop();
        expr.writePush(o);

        if (expr.stackPeek().isConstant())
            expr.unexpectedToken(instanceOf);

        expr.writePopBoxing();

        if (instanceOf.isVariable()){
            expr.writePushVariable(instanceOf.getWhatVariable());
            expr.writePopString();
            expr.writePushDupLowerCase();
        } else if (instanceOf.isExpression()) {
            expr.writeExpression(instanceOf.getWhatExpr(), true, false);
            expr.writePopString();
            expr.writePushDupLowerCase();
        } else {
            expr.writePushConstString(instanceOf.getWhat().getName());
            expr.writePushConstString(instanceOf.getWhat().getName().toLowerCase());
        }

        expr.writeSysDynamicCall(Memory.class, "instanceOf", Boolean.TYPE, String.class, String.class);

        if (!returnValue)
            expr.writePopAll(1);
    }
}
