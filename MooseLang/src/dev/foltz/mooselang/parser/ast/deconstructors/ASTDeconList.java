package dev.foltz.mooselang.parser.ast.deconstructors;

import dev.foltz.mooselang.interpreter.Scope;
import dev.foltz.mooselang.interpreter.runtime.RTList;
import dev.foltz.mooselang.interpreter.runtime.RTObject;
import dev.foltz.mooselang.parser.ast.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class ASTDeconList extends ASTDeconstructor {
    public List<ASTDeconstructor> decons;

    public ASTDeconList(List<ASTDeconstructor> decons) {
        this.decons = new ArrayList<>(decons);
    }

    @Override
    public RTObject deconstruct(RTObject rtObj, Scope scope) {
        if (!matches(rtObj)) {
            throw new IllegalStateException("List deconstructor cannot accept: " + rtObj);
        }

        return rtObj;
    }

    @Override
    public boolean matches(RTObject rtObj) {
        if (rtObj instanceof RTList rtList) {
            int listLen = rtList.elems.size();
            int deconLen = decons.size();

            if (listLen != deconLen) {
                return false;
            }

            for (int i = 0; i < listLen; i++) {
                RTObject elem = rtList.elems.get(i);
                ASTDeconstructor decon = decons.get(i);
                if (!decon.matches(elem)) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTDeconList{" +
                "decons=" + decons +
                '}';
    }
}
