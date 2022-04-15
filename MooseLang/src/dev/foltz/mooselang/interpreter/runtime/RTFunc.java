package dev.foltz.mooselang.interpreter.runtime;

import dev.foltz.mooselang.parser.ast.deconstructors.*;
import dev.foltz.mooselang.parser.ast.expressions.ASTExpr;
import dev.foltz.mooselang.parser.ast.statements.ASTStmt;

import java.util.ArrayList;
import java.util.List;

public class RTFunc extends RTObject {
    public String name;
    public List<RTFuncBranch> branches;

    public RTFunc(String name) {
        this.name = name;
        this.branches = new ArrayList<>();
    }

    public void addBranch(RTFuncBranch branch) {
        for (RTFuncBranch b : branches) {
            if (b.paramDecons.size() != branch.paramDecons.size()) {
                throw new IllegalStateException("Cannot add function path with " + branch.paramDecons.size()
                        + " arguments, expected " + b.paramDecons.size());
            }
        }
        branches.add(branch);
    }

    public RTFuncBranch dispatch(List<RTObject> params) {
//        System.out.println("Dispatch " + name + "(" + params + ")");
        for (RTFuncBranch branch : branches) {
            List<ASTDeconstructor> decons = branch.paramDecons;
//            System.out.println("Comparing branch: " + decons);
            if (decons.size() != params.size()) {
                System.out.println("Argument count mismatch");
                continue;
            }
            boolean match = true;
            for (int i = 0; i < decons.size(); i++) {
                ASTDeconstructor decon = decons.get(i);
                RTObject param = params.get(i);
                if (decon instanceof ASTDeconInt deconInt && param instanceof RTInt rtInt) {
                    if (deconInt.literal.value != rtInt.value) {
                        match = false;
                        break;
                    }
                }
                else if (decon instanceof ASTDeconString deconStr && param instanceof RTString rtStr) {
                    if (!deconStr.value.value.equals(rtStr.value)) {
                        match = false;
                        break;
                    }
                }
                else if (decon instanceof ASTDeconList deconList && param instanceof RTList rtList) {
                    if (!(deconList.decons.isEmpty() && rtList.elems.isEmpty())) {
                        match = false;
                        break;
                    }
                }
                else if (decon instanceof ASTDeconName) {
                    // Nothing to do here
                }
                else {
                    match = false;
                    break;
                }
            }

            if (match) {
                return branch;
            }
        }

        throw new IllegalStateException("Could not dispatch function call " + name + " with params " + params);
    }

    public static class RTFuncBranch {
        public List<ASTDeconstructor> paramDecons;
        public ASTStmt body;

        public RTFuncBranch(List<ASTDeconstructor> paramDecons, ASTStmt body) {
            this.paramDecons = paramDecons;
            this.body = body;
        }
    }
}
