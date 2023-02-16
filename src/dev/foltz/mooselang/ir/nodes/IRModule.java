package dev.foltz.mooselang.ir.nodes;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.ir.nodes.comp.IRComp;

import java.util.List;

public class IRModule extends IRNode {
    public final List<IRGlobalDef> topLevelDefs;
    public final List<IRComp> topLevelComps;

    public IRModule(List<IRGlobalDef> topLevelDefs, List<IRComp> topLevelComps) {
        this.topLevelDefs = List.copyOf(topLevelDefs);
        this.topLevelComps = List.copyOf(topLevelComps);
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRModule(" + topLevelDefs + ", " + topLevelComps + ")";
    }
}
