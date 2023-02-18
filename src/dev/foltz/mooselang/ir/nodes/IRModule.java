package dev.foltz.mooselang.ir.nodes;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.ir.nodes.comp.IRComp;

import java.util.List;
import java.util.Map;

public class IRModule extends IRNode {
    public final Map<String, List<IRDefType>> topLevelTypeDefs;
    public final Map<String, List<IRDefValue>> topLevelDefs;
    public final List<IRComp> topLevelComps;

    public IRModule(Map<String, List<IRDefType>> topLevelTypeDefs, Map<String, List<IRDefValue>> topLevelDefs, List<IRComp> topLevelComps) {
        this.topLevelTypeDefs = Map.copyOf(topLevelTypeDefs);
        this.topLevelDefs = Map.copyOf(topLevelDefs);
        this.topLevelComps = List.copyOf(topLevelComps);
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRModule(" + topLevelTypeDefs + ", " + topLevelDefs + ", " + topLevelComps + ")";
    }
}
