package dev.foltz.mooselang.typing;

import dev.foltz.mooselang.ast.ASTDefaultVisitor;
import dev.foltz.mooselang.ast.ASTNode;
import dev.foltz.mooselang.ast.expression.ASTExpr;
import dev.foltz.mooselang.ast.expression.ASTExprName;
import dev.foltz.mooselang.ast.expression.literals.ASTExprBool;
import dev.foltz.mooselang.ast.expression.literals.ASTExprInt;
import dev.foltz.mooselang.ast.expression.literals.ASTExprNone;
import dev.foltz.mooselang.ast.expression.literals.ASTExprString;
import dev.foltz.mooselang.ast.statement.ASTStmtLet;
import dev.foltz.mooselang.ast.typing.ASTType;
import dev.foltz.mooselang.ast.typing.ASTTypeLiteral;
import dev.foltz.mooselang.ast.typing.ASTTypeName;
import dev.foltz.mooselang.ast.typing.ASTTypeUnion;
import dev.foltz.mooselang.typing.type.Type;
import dev.foltz.mooselang.typing.type.TypeUnion;
import dev.foltz.mooselang.typing.type.builtin.BuiltinTypes;
import dev.foltz.mooselang.typing.type.builtin.TypeBool;
import dev.foltz.mooselang.typing.type.builtin.TypeInt;
import dev.foltz.mooselang.typing.type.builtin.TypeString;
import dev.foltz.mooselang.typing.type.literal.TypeLiteralBool;
import dev.foltz.mooselang.typing.type.literal.TypeLiteralInt;
import dev.foltz.mooselang.typing.type.literal.TypeLiteralString;

import java.util.*;

import static dev.foltz.mooselang.typing.type.builtin.BuiltinTypes.TYPE_UNKNOWN;

public class ASTTypeChecker extends ASTDefaultVisitor<Optional<Type>> {
    public final Map<String, Type> namedTypes;

    public ASTTypeChecker() {
        super(node -> { throw new UnsupportedOperationException("Cannot type check: " + node); });
        this.namedTypes = new HashMap<>();
    }

    public Optional<Type> evalType(ASTNode node) {
        return node.accept(this);
    }

    public void bindType(String name, Type type) {
        if (namedTypes.containsKey(name)) {
            throw new IllegalArgumentException("Cannot rebind type: " + name);
        }
        namedTypes.put(name, type);
    }

    public boolean equalTypes(Optional<Type> a, Optional<Type> b) {
        return a.isPresent() && b.isPresent() && equalTypes(a.get(), b.get());
    }

    public boolean equalTypes(Type a, Type b) {
        if (a == TYPE_UNKNOWN && b == TYPE_UNKNOWN) {
            return false;
        }
        else if (a == b) {
            return true;
        }
        else if (a.isEqual(b)) {
            return true;
        }
        else {
            // TODO: Structural equality.
            return false;
        }
    }

    public boolean isSubtype(Optional<Type> sub, Optional<Type> sup) {
        return sub.isPresent() && sup.isPresent() && isSubtype(sub.get(), sup.get());
    }

    public boolean isSubtype(Type sub, Type sup) {
//        System.out.println("IsSubType(" + sub + ", " + sup + ")");
        if (sub != TYPE_UNKNOWN && sup == TYPE_UNKNOWN) {
            return true;
        }
        else if (equalTypes(sub, sup)) {
            return true;
        }
        else if (sub instanceof TypeLiteralBool && sup instanceof TypeBool) {
            return true;
        }
        else if (sub instanceof TypeLiteralInt && sup instanceof TypeInt) {
            return true;
        }
        else if (sub instanceof TypeLiteralString && sup instanceof TypeString) {
            return true;
        }
        else {
            if (sup instanceof TypeUnion tu) {
                // Simple check, pairwise OR subtyping.
                boolean pairwise = tu.types.stream().anyMatch(t -> isSubtype(sub, t));
                if (pairwise) {
                    return true;
                }
                // TODO: Handle subtypes.
                return false;
            }
            else if (sub instanceof TypeLiteralBool && sup instanceof TypeBool) {
                return true;
            }
            else if (sub instanceof TypeLiteralInt && sup instanceof TypeInt) {
                return true;
            }
            else if (sub instanceof TypeLiteralString && sup instanceof TypeString) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    @Override
    public Optional<Type> visit(ASTTypeUnion node) {
        var types = node.types.stream().map(this::evalType).toList();
        if (types.stream().allMatch(Optional::isPresent)) {
            var qualTypes = types.stream().map(t -> t.get()).toList();
            return Optional.of(new TypeUnion(qualTypes));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Type> visit(ASTTypeName node) {
        return Optional.ofNullable(namedTypes.get(node.name));
    }

    @Override
    public Optional<Type> visit(ASTExprName node) {
        Optional<ASTType> typeHint = node.typeHint();
        return typeHint.isPresent() ? evalType(typeHint.get()) : Optional.of(TYPE_UNKNOWN);
    }

    @Override
    public Optional<Type> visit(ASTExprNone node) {
        return Optional.of(BuiltinTypes.TYPE_NONE);
    }

    @Override
    public Optional<Type> visit(ASTExprBool node) {
        return Optional.of(new TypeLiteralBool(node.value()));
    }

    @Override
    public Optional<Type> visit(ASTExprInt node) {
        return Optional.of(new TypeLiteralInt(node.value()));
    }

    @Override
    public Optional<Type> visit(ASTExprString node) {
        return Optional.of(new TypeLiteralString(node.value()));
    }

    @Override
    public Optional<Type> visit(ASTStmtLet node) {
        var name = node.name;
        var body = node.body;

        var typeName = evalType(name);
        var typeBody = evalType(body);

        if (isSubtype(typeBody, typeName)) {
            return typeName.equals(Optional.of(TYPE_UNKNOWN)) ? typeBody : typeName;
        }
        else if(equalTypes(typeName, typeBody)) {
            return typeName;
        }
        else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Type> visit(ASTTypeLiteral node) {
        ASTExpr lit = node.literal;
        if (lit instanceof ASTExprBool b) {
            return Optional.of(new TypeLiteralBool(b.value()));
        }
        else if (lit instanceof ASTExprInt i) {
            return Optional.of(new TypeLiteralInt(i.value()));
        }
        else if (lit instanceof ASTExprString s) {
            return Optional.of(new TypeLiteralString(s.value()));
        }
        else {
            return Optional.empty();
        }
    }
}
