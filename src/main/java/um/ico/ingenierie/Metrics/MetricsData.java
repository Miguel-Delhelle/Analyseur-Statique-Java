package um.ico.ingenierie.Metrics;

import um.ico.ingenierie.abstractSource.AbstractSourceClass;
import um.ico.ingenierie.abstractSource.AbstractSourceMethods;
import um.ico.ingenierie.abstractSource.AbstractSourcePackage;
import um.ico.ingenierie.results.SearchResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MetricsData {

    public AtomicInteger numberOfClass = new AtomicInteger(0);
    public AtomicInteger numberOfMethods = new AtomicInteger(0);
    public AtomicInteger numberOfLines = new AtomicInteger(0);
    //public double averageNumberOfMethodsInClass = 0.0;
    //public double averageNumberOfLinesInMethods = 0.0;
    private List<AbstractSourcePackage> abstractSourcePackageList = new ArrayList<AbstractSourcePackage>();
    private List<AbstractSourceClass> abstractSourceClassList = new ArrayList<AbstractSourceClass>();


    public MetricsData() {
    }

    /*public String allClassString() {
        String str = "";
        for (AbstractSourceClass laClasse : this.abstractSourceClassList) {
            str += laClasse.toString();
        }
        return str;
    } */

    public int getNumberOfPackage() {
        return this.getAbstractPackageSourceList().size();
    }

    public AtomicInteger getNumberOfClass() {
        return numberOfClass;
    }

    public void setNumberOfClass(AtomicInteger numberOfClass) {
        this.numberOfClass = numberOfClass;
    }

    public AtomicInteger getNumberOfMethods() {
        return numberOfMethods;
    }

    public void setNumberOfMethods(AtomicInteger numberOfMethods) {
        this.numberOfMethods = numberOfMethods;
    }

    public AtomicInteger getNumberOfLines() {
        return numberOfLines;
    }

    public void setNumberOfLines(AtomicInteger numberOfLines) {
        this.numberOfLines = numberOfLines;
    }

    public List<AbstractSourcePackage> getAbstractPackageSourceList() {
        return abstractSourcePackageList;
    }

    public void setAbstractPackageSourceList(List<AbstractSourcePackage> abstractSourcePackageList) {
        this.abstractSourcePackageList = abstractSourcePackageList;
    }

    public List<AbstractSourceClass> getAbstractSourceClassList() {
        return abstractSourceClassList;
    }

    public void setAbstractSourceClassList(List<AbstractSourceClass> abstractSourceClassList) {
        this.abstractSourceClassList = abstractSourceClassList;
    }

    // Méthodes

    @Override
    public String toString() {
        return "MetricsData{" +
                "number of Package= " + getNumberOfPackage() +
                ", numberOfClass=" + numberOfClass +
                ", numberOfMethods=" + numberOfMethods +
                ", numberOfLines=" + numberOfLines +
                ", abstractPackageSourceList=" + abstractSourcePackageList +
                '}';
    }

    private SearchResult verifyPaquetExistence(String nomDuNouveauPaquet) {
        boolean bool = false;
        for (int i = 0; i<this.abstractSourcePackageList.size(); i++) {
            if (abstractSourcePackageList.get(i).getName().toString().equals(nomDuNouveauPaquet)) {
                return new SearchResult(true,i);
            }
        }
        return new SearchResult(false,-1);
    }

    public void addPackage(AbstractSourcePackage paquetSource) {
        if (!verifyPaquetExistence(paquetSource.getName()).isExist()) {
            this.abstractSourcePackageList.add(paquetSource);
        }
        ;
    }

    public void addClass(AbstractSourceClass classe){
        AbstractSourcePackage paquet = classe.getPackageParent();
        SearchResult searchResult = this.verifyPaquetExistence(paquet.getName());
        if (searchResult.isExist()){
            this.abstractSourcePackageList.get(searchResult.getPosition()).addClassToPackage(classe);
            this.abstractSourceClassList.add(classe);
        }else {
            paquet.addClassToPackage(classe);
            this.abstractSourcePackageList.add(paquet);
            this.abstractSourceClassList.add(classe);
        }
    }

    public double getAverageNumberMethodsByClass(){
        double averageMethod;
        int numberOfMethodTotal = 0;
        for (AbstractSourceClass sourceClass : this.getAbstractSourceClassList()){
            numberOfMethodTotal = numberOfMethodTotal+sourceClass.getAbstractSourceMethodsList().size();
        }
        averageMethod = numberOfMethodTotal / this.getAbstractSourceClassList().size();
        return averageMethod;
    }

    public int getAverageNumberOfLineByMethods(){
        int compteurOfMethods = 0;
        int numberOfLines = 0;
        for (AbstractSourceClass sourceClass : this.getAbstractSourceClassList()){
            for (AbstractSourceMethods sourceMethods : sourceClass.getAbstractSourceMethodsList()){
                compteurOfMethods++;
                numberOfLines = numberOfLines+sourceMethods.getNumberOfLines();
            }
        }
        return numberOfLines/compteurOfMethods;
    }
    public int getAverageNumberOfAttributsByClass(){
        int compteurAttribut = 0;
        for (AbstractSourceClass sourceClass : this.getAbstractSourceClassList()){
            compteurAttribut = compteurAttribut+sourceClass.getAbstractAttributsSourceList().size();
        }
        return compteurAttribut/this.getAbstractSourceClassList().size();
    }



    public List<AbstractSourceClass> get10percentClassWithMoreMethods() {
        long sizeOfList = this.getAbstractSourceClassList().size();
        return this.getAbstractSourceClassList()
                .stream()
                .sorted(Comparator.comparingInt(AbstractSourceClass::getNumberOfMethods).reversed())
                .limit((long) Math.ceil(sizeOfList*0.10))
                .collect(Collectors.toList());
    }

    public List<AbstractSourceClass> get10percentClassWithMoreAttributes() {
        long sizeOfList = this.getAbstractSourceClassList().size();
        return this.getAbstractSourceClassList()
                .stream()
                .sorted(Comparator.comparingInt(AbstractSourceClass::getNumberOfAttributs).reversed())
                .limit((long) Math.ceil(sizeOfList*0.10))
                .collect(Collectors.toList());
    }

    public List<AbstractSourceClass> getImportantClass(){
        List<AbstractSourceClass> classWithLotMethod = this.get10percentClassWithMoreMethods();
        List<AbstractSourceClass> classWithLotAttribut = this.get10percentClassWithMoreAttributes();

        List<AbstractSourceClass> importantClass = new ArrayList<AbstractSourceClass>();

        for (AbstractSourceClass sourceClass : classWithLotAttribut){
            if (classWithLotMethod.contains(sourceClass)) {importantClass.add(sourceClass);};
        }
        return importantClass;
    }

    public List<AbstractSourceClass> getClassesWithMoreThanXMethods(int numberOfMethods){
        List<AbstractSourceClass> classWithXMethods = new ArrayList<AbstractSourceClass>();
        for (AbstractSourceClass sourceClass : this.getAbstractSourceClassList()){
            if (sourceClass.getNumberOfMethods() > numberOfMethods){classWithXMethods.add(sourceClass);}
        }
        return classWithXMethods;
    }

    public List<AbstractSourceMethods> get10PercentOfLargeMethodsByClass(AbstractSourceClass sourceClass){
        long sizeOfList = sourceClass.getAbstractSourceMethodsList().size();
        return sourceClass.getAbstractSourceMethodsList()
                .stream()
                .sorted(Comparator.comparingInt(AbstractSourceMethods::getNumberOfLines).reversed())
                .limit((long) Math.ceil(sizeOfList*0.10))
                .collect(Collectors.toList());
    }

    public int getMaxNumberOfArgs(){
        int tmpMax = 0;
        for (AbstractSourceClass sourceClass : this.getAbstractSourceClassList()){
            for (AbstractSourceMethods sourceMethods : sourceClass.getAbstractSourceMethodsList()){
                if (sourceMethods.getParameters().size() > tmpMax){
                    tmpMax = sourceMethods.getParameters().size();
                }
            }
        }
        return tmpMax;
    }


    /**
     * Détermine le package de base du projet analysé en trouvant
     * le plus long préfixe commun à tous les noms de packages.
     * @return Le nom du package de base (ex: "um.ico.ingenierie") ou une chaîne vide.
     */
    public String determineBasePackage() {
        List<String> packageNames = this.abstractSourcePackageList.stream()
                .map(AbstractSourcePackage::getName)
                .collect(Collectors.toList());

        if (packageNames.isEmpty()) {
            return "";
        }

        String longestCommonPrefix = packageNames.get(0);
        for (int i = 1; i < packageNames.size(); i++) {
            String currentPackage = packageNames.get(i);
            while (currentPackage.indexOf(longestCommonPrefix) != 0) {
                longestCommonPrefix = longestCommonPrefix.substring(0, longestCommonPrefix.length() - 1);
                if (longestCommonPrefix.isEmpty()) {
                    return "";
                }
            }
        }

        // On s'assure de ne pas couper un nom de package au milieu (ex: um.ico.ing)
        // On retourne le préfixe jusqu'au dernier point.
        if (longestCommonPrefix.endsWith(".")) {
            longestCommonPrefix = longestCommonPrefix.substring(0, longestCommonPrefix.length() - 1);
        }

        return longestCommonPrefix;
    }

}
