package um.ico.ingenierie.Analysis.Models.SourceCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Cette classe, est un modèle de répresentation abstraite
 * de la représentation d'une Class dans le code source analysé.
 *
 * @author Miguel Delhelle
 * @version 1.0
 */

public class AbstractSourceClass implements Serializable {

    private String nameOfClass;
    private AbstractSourcePackage packageParent;
    private List<AbstractSourceMethods> abstractSourceMethodsList = new ArrayList<AbstractSourceMethods>();
    private List<AbstractSourceAttributs> abstractAttributsSourceList = new ArrayList<AbstractSourceAttributs>();
    private int numberOfLinesInClass = 0;
    private TypeOfClass typeOfClass;

    public AbstractSourceClass() {
    }

    public AbstractSourceClass(String nameOfClass, int numberOfLinesInClass) {
        this.nameOfClass = nameOfClass;
        //this.numberOfMethods = numberOfMethods;
        this.numberOfLinesInClass = numberOfLinesInClass;
    }

    public void setNameOfClass(String nameOfClass) {
        this.nameOfClass = nameOfClass;
    }

    public void setNumberOfLinesInClass(int numberOfLinesInClass) {
        this.numberOfLinesInClass = numberOfLinesInClass;
    }

    public AbstractSourcePackage getPackageParent() {
        return packageParent;
    }

    public void setPackageParent(AbstractSourcePackage packageParent) {
        this.packageParent = packageParent;
    }

    public boolean isEmpty() {
        boolean noName = (nameOfClass == null || nameOfClass.isEmpty());
        boolean noMethods = (getNumberOfMethods() == 0);
        //boolean noAttributes = (numberOfAttributs == 0);
        boolean noLines = (numberOfLinesInClass == 0);

        return noName && noMethods && noLines;
    }

    public void addAttributs(AbstractSourceAttributs aas){
        this.abstractAttributsSourceList.add(aas);
    }

    public int getNumberOfAttributs(){
        return this.abstractAttributsSourceList.size();
    }



    public void addMethod(AbstractSourceMethods abstractSourceMethods){
        this.abstractSourceMethodsList.add(abstractSourceMethods);
    }

    public List<AbstractSourceMethods> getAbstractSourceMethodsList() {
        return abstractSourceMethodsList;
    }

    public void setAbstractSourceMethodsList(List<AbstractSourceMethods> abstractSourceMethodsList) {
        this.abstractSourceMethodsList = abstractSourceMethodsList;
    }

    public List<AbstractSourceAttributs> getAbstractAttributsSourceList() {
        return abstractAttributsSourceList;
    }

    public void setAbstractAttributsSourceList(List<AbstractSourceAttributs> abstractAttributsSourceList) {
        this.abstractAttributsSourceList = abstractAttributsSourceList;
    }

    /**
     * Cette méthode compte toutes les méthodes SAUF les constructeurs.
     *
     * @author Miguel Delhelle
     * @version 1.0
     */
    public int getNumberOfMethods(){
        return this.getAbstractSourceMethodsList()
                .stream()
                .filter(abstractSourceMethods -> !abstractSourceMethods.isConstructor()).toList().size();
    }

    public TypeOfClass getTypeOfClass() {
        return typeOfClass;
    }

    public void setTypeOfClass(TypeOfClass typeOfClass) {
        this.typeOfClass = typeOfClass;
    }

    public String getNameOfClass() {
        return nameOfClass;
    }

    public int getNumberOfLinesInClass() {
        return numberOfLinesInClass;
    }

    @Override
    public String toString() {
        return "\nAbstractSourceClass: \n" +
                "\n --- nameOfClass='" + nameOfClass + '\'' +
                "\n --- numberOfMethods=" + getNumberOfMethods() +
                "\n --- numberOfAttributs=" + getNumberOfAttributs() +
                "\n --- numberOfLinesInClass=" + numberOfLinesInClass +
                "\n ------ Methods: "+abstractSourceMethodsList.toString();
    }
}
