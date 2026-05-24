package fr.delweb.analyzer.Analysis.Models.SourceCode;

import org.eclipse.jdt.core.dom.Type;

import java.io.Serializable;
import java.util.List;


/**
 * Cette classe, est un modèle de répresentation abstraite
 * de la représentation d'une méthode trouvé dans le code source analysé.
 *
 * @author Miguel Delhelle
 * @version 1.0
 */

public class AbstractSourceMethods implements Serializable {

    private AbstractSourceClass parentClass;
    private boolean isConstructor;
    private String name;
    private List<String> parameters;
    private Type returnType;
    private int numberOfLines;

    public AbstractSourceMethods(AbstractSourceClass parentClass, boolean isConstructor, String name, List<String> parameters, Type returnType, int numberOfLines) {
        this.parentClass = parentClass;
        this.isConstructor = isConstructor;
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.numberOfLines = numberOfLines;
    }

    @Override
    public String toString() {
        return "AbstractSourceMethods{" +
                ", isConstructor=" + isConstructor +
                ", name='" + name + '\'' +
                ", parameters=" + parameters +
                ", returnType='" + returnType + '\'' +
                ", Number of Lines:"+numberOfLines;
    }

    public AbstractSourceClass getParentClass() {
        return parentClass;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public String getName() {
        return name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public Type getReturnType() {
        return returnType;
    }

    public int getNumberOfLines() {
        return numberOfLines;
    }
}
