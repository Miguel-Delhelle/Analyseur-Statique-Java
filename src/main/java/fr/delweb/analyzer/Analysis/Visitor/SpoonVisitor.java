package fr.delweb.analyzer.Analysis.Visitor;

import fr.delweb.analyzer.Analysis.Models.SourceCode.*;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import fr.delweb.analyzer.Analysis.Models.Graph.MicroGraph.Edge;
import fr.delweb.analyzer.Analysis.Models.Graph.MicroGraph.EdgeType;
import um.ico.ingenierie.Analysis.Models.SourceCode.*;
import fr.delweb.analyzer.Analysis.Result.SingleFileAnalysisResult;

import java.util.*;
import java.util.stream.Collectors;

public class SpoonVisitor extends CtScanner implements IVisitor {

    private String currentMethodSignature = null;
    private final AbstractSourcePackage localPackage = new AbstractSourcePackage();
    private final AbstractSourceClass localClass = new AbstractSourceClass();
    private final Map<String, Set<Edge>> localEdges = new HashMap<>();

    public SpoonVisitor() {
        super();
    }

    @Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
        // Visite les éléments internes (méthodes, champs) avant de finaliser la classe
        super.visitCtClass(ctClass);

        localClass.setNameOfClass(ctClass.getSimpleName());
        if (ctClass.getPackage() != null) {
            localPackage.setName(ctClass.getPackage().getQualifiedName());
        }
        localClass.setPackageParent(localPackage);

        if (ctClass.getPosition().isValidPosition()) {
            int startLine = ctClass.getPosition().getLine();
            int endLine = ctClass.getPosition().getEndLine();
            localClass.setNumberOfLinesInClass(endLine - startLine);
        }

        localClass.setTypeOfClass(determineNodeType(ctClass));
    }
    @Override
    public <T> void visitCtInterface(CtInterface<T> ctInterface) {
        super.visitCtInterface(ctInterface);
        localClass.setNameOfClass(ctInterface.getSimpleName());
        if (ctInterface.getPackage() != null) {
            localPackage.setName(ctInterface.getPackage().getQualifiedName());
        }
        localClass.setPackageParent(localPackage);
        if (ctInterface.getPosition().isValidPosition()) {
            int startLine = ctInterface.getPosition().getLine();
            int endLine = ctInterface.getPosition().getEndLine();
            localClass.setNumberOfLinesInClass(endLine - startLine);
        }
        localClass.setTypeOfClass(TypeOfClass.INTERFACE);
    }
    @Override
    public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
        super.visitCtEnum(ctEnum);
        localClass.setNameOfClass(ctEnum.getSimpleName());
        if (ctEnum.getPackage() != null) {
            localPackage.setName(ctEnum.getPackage().getQualifiedName());
        }
        localClass.setPackageParent(localPackage);
        if (ctEnum.getPosition().isValidPosition()) {
            int startLine = ctEnum.getPosition().getLine();
            int endLine = ctEnum.getPosition().getEndLine();
            localClass.setNumberOfLinesInClass(endLine - startLine);
        }
        localClass.setTypeOfClass(TypeOfClass.ENUM);
    }

    @Override
    public <T> void visitCtField(CtField<T> f) {
        super.visitCtField(f);
        localClass.addAttributs(new AbstractSourceAttributs(localClass, f.getType().toString(), f.getSimpleName()));
    }
    @Override
    public <T> void visitCtMethod(CtMethod<T> m) {
        this.currentMethodSignature = createSpoonMethodSignature(m.getReference());

        int numberOfLines = 0;
        if (m.getBody() != null && m.getBody().getPosition().isValidPosition()) {
            numberOfLines = m.getBody().getPosition().getEndLine() - m.getBody().getPosition().getLine();
        }

        List<String> params = m.getParameters().stream()
                .map(p -> p.getType().toString() + " " + p.getSimpleName())
                .collect(Collectors.toList());

        localClass.addMethod(new AbstractSourceMethods(
                localClass,
                false, // isConstructor est faux pour CtMethod
                m.getSimpleName(),
                params,
                null, // JDT-spécifique, mis à null pour Spoon
                numberOfLines
        ));

        // Scanner le corps de la méthode
        super.visitCtMethod(m);

        // Réinitialiser la signature après avoir visité la méthode
        this.currentMethodSignature = null;
    }
    @Override
    public <T> void visitCtConstructor(CtConstructor<T> c) {
        this.currentMethodSignature = createSpoonMethodSignature(c.getReference());

        int numberOfLines = 0;
        if (c.getBody() != null && c.getBody().getPosition().isValidPosition()) {
            numberOfLines = c.getBody().getPosition().getEndLine() - c.getBody().getPosition().getLine();
        }

        List<String> params = c.getParameters().stream()
                .map(p -> p.getType().toString() + " " + p.getSimpleName())
                .collect(Collectors.toList());

        localClass.addMethod(new AbstractSourceMethods(
                localClass,
                true, // isConstructor est vrai pour CtConstructor
                localClass.getNameOfClass(), // Le nom d'un constructeur est celui de la classe
                params,
                null, // JDT-spécifique, mis à null pour Spoon
                numberOfLines
        ));
        // Scanner le corps du constructeur
        super.visitCtConstructor(c);
        // Réinitialiser la signature
        this.currentMethodSignature = null;
    }
    @Override
    public <T> void visitCtInvocation(CtInvocation<T> invocation) {
        if (currentMethodSignature != null && invocation.getExecutable() != null) {
            String calleeSignature = createSpoonMethodSignature(invocation.getExecutable());
            localEdges.computeIfAbsent(currentMethodSignature, k -> new HashSet<>())
                    .add(new Edge(calleeSignature, EdgeType.CALL));
        }
        super.visitCtInvocation(invocation);
    }
    @Override
    public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
        if (currentMethodSignature != null && ctConstructorCall.getExecutable() != null) {
            String calleeSignature = createSpoonMethodSignature(ctConstructorCall.getExecutable());
            localEdges.computeIfAbsent(currentMethodSignature, k -> new HashSet<>())
                    .add(new Edge(calleeSignature, EdgeType.INSTANTIATION));
        }
        super.visitCtConstructorCall(ctConstructorCall);
    }
    @Override
    public void visitCtThrow(CtThrow throwStatement) {
        if (currentMethodSignature != null && throwStatement.getThrownExpression() instanceof CtConstructorCall) {
            CtConstructorCall<?> constructorCall = (CtConstructorCall<?>) throwStatement.getThrownExpression();
            if (constructorCall.getExecutable() != null) {
                String calleeSignature = createSpoonMethodSignature(constructorCall.getExecutable());
                localEdges.computeIfAbsent(currentMethodSignature, k -> new HashSet<>())
                        .add(new Edge(calleeSignature, EdgeType.THROWS));
            }
        }
        super.visitCtThrow(throwStatement);
    }

    @Override
    public SingleFileAnalysisResult getResult() {
        return new SingleFileAnalysisResult(this.localClass, this.localEdges);
    }

    /**
     * Crée une signature de méthode unique à partir d'une référence Spoon.
     */
    private String createSpoonMethodSignature(CtExecutableReference<?> ref) {
        if (ref == null) return "unknown.binding";

        StringBuilder signature = new StringBuilder();
        CtTypeReference<?> declaringType = ref.getDeclaringType();

        if (declaringType == null || declaringType.getQualifiedName() == null) {
            return "local.class#" + ref.getSimpleName();
        }

        signature.append(declaringType.getQualifiedName());
        signature.append("#");
        if (ref.isConstructor()) {
            signature.append(declaringType.getSimpleName());
        } else {
            signature.append(ref.getSimpleName());
        }

        signature.append("(");

        signature.append(
                ref.getParameters().stream()
                        .map(p -> p.getQualifiedName())
                        .collect(Collectors.joining(","))
        );

        signature.append(")");
        return signature.toString();
    }
    /**
     * Détermine le type de noeud (Classe, Exception, etc.) à partir d'un CtType Spoon.
     */
    private TypeOfClass determineNodeType(CtType<?> type) {
        if (type.isInterface()) return TypeOfClass.INTERFACE;
        if (type.isEnum()) return TypeOfClass.ENUM;
        // Spoon n'a pas de concept direct de 'record' avant Java 16, donc cette détection est basique.
        if (type.getSuperclass() != null && type.getSuperclass().getQualifiedName().equals("java.lang.Record")) {
            return TypeOfClass.RECORD;
        }

        // Vérification pour les exceptions en remontant l'arbre d'héritage
        CtTypeReference<?> superclass = type.getSuperclass();
        while (superclass != null) {
            if ("java.lang.Throwable".equals(superclass.getQualifiedName())) {
                return TypeOfClass.EXCEPTION;
            }
            try {
                superclass = superclass.getSuperclass();
            } catch (Exception e){
                // On a atteint la fin de la hiérarchie pour les classes externes
                break;
            }
        }

        // Par défaut, c'est une classe standard.
        return TypeOfClass.CLASS;
    }
}