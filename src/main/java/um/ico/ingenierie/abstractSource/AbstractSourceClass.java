package um.ico.ingenierie.abstractSource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AbstractSourceClass implements Serializable {

    private String nameOfClass;
    private AbstractSourcePackage packageParent;
    private List<AbstractSourceMethods> abstractSourceMethodsList = new ArrayList<AbstractSourceMethods>();
    private List<AbstractSourceAttributs> abstractAttributsSourceList = new ArrayList<AbstractSourceAttributs>();
    private int numberOfMethods = 0;
    //private int numberOfAttributs = 0;
    private int numberOfLinesInClass = 0;

    public AbstractSourceClass() {
    }

    public AbstractSourceClass(String nameOfClass, int numberOfMethods, int numberOfLinesInClass) {
        this.nameOfClass = nameOfClass;
        this.numberOfMethods = numberOfMethods;
        this.numberOfLinesInClass = numberOfLinesInClass;
    }

    public void setNameOfClass(String nameOfClass) {
        this.nameOfClass = nameOfClass;
    }

    public void setNumberOfMethods(int numberOfMethods) {
        this.numberOfMethods = numberOfMethods;
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
        boolean noMethods = (numberOfMethods == 0);
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
