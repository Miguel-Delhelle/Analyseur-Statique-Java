package um.ico.ingenierie.Analysis.Models;

import um.ico.ingenierie.Analysis.Models.SourceCode.AbstractSourceClass;
import um.ico.ingenierie.Analysis.Models.SourceCode.AbstractSourceMethods;
import um.ico.ingenierie.Analysis.Models.SourceCode.AbstractSourcePackage;
import um.ico.ingenierie.Common.results.SearchResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * La classe MetricsData a pour but de juste stocker les données d'un unique code Source.
 * Il y a un MetricsData par code Source.
 *
 * @author Miguel Delhelle
 * @version 1.0
 * */

public class MetricsData {

    List<AbstractSourcePackage> abstractSourcePackageList = new ArrayList<AbstractSourcePackage>();
    private List<AbstractSourceClass> abstractSourceClassList = new ArrayList<AbstractSourceClass>();

    public MetricsData() {
    }


    public int getNumberOfPackage() {
        return this.getAbstractPackageSourceList().size();
    }

    public int getNumberOfClass() {
        return getAbstractSourceClassList().size();
    }

    public int getTotalNumberOfMethods() {
        return this.abstractSourceClassList.stream()
                .mapToInt(AbstractSourceClass::getNumberOfMethods)
                .sum();
    }

    public int getTotalNumberOfLines() {
        return this.abstractSourceClassList.stream()
                .mapToInt(value -> value.getAbstractSourceMethodsList().stream().mapToInt(AbstractSourceMethods::getNumberOfLines).sum())
                .sum();
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
                ", numberOfClass=" + getNumberOfClass() +
                ", numberOfMethods=" + getTotalNumberOfMethods() +
                ", numberOfLines=" +  getTotalNumberOfLines() +
                ", abstractPackageSourceList=" + getAbstractPackageSourceList() +
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

    /**
     * Ajoute une classe à la collection de métriques, en s'assurant de ne pas créer de doublons
     * et en gérant la fusion des packages.
     * @param classe La classe à ajouter.
     */
    public void addClass(AbstractSourceClass classe) {
        if (classe == null || classe.getNameOfClass() == null) {
            return;
        }
        boolean alreadyExists = abstractSourceClassList.stream()
                .anyMatch(existingClass ->
                        existingClass.getNameOfClass().equals(classe.getNameOfClass()) &&
                                existingClass.getPackageParent().getName().equals(classe.getPackageParent().getName())
                );
        if (alreadyExists) {
            return;
        }
        this.abstractSourceClassList.add(classe);
        AbstractSourcePackage paquet = classe.getPackageParent();
        Optional<AbstractSourcePackage> existingPackage = abstractSourcePackageList.stream()
                .filter(p -> p.getName().equals(paquet.getName()))
                .findFirst();
        if (existingPackage.isPresent()) {
            existingPackage.get().addClassToPackage(classe);
        } else {
            paquet.addClassToPackage(classe);
            this.abstractSourcePackageList.add(paquet);
        }
    }

    public double getAverageNumberMethodsByClass(){
        if (this.getNumberOfClass() == 0){return 0.0;}
        return (double) this.getTotalNumberOfMethods() / this.getNumberOfClass();
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
     * @return Le nom du package de base (ex: "com.example.superAppli") ou une chaîne vide.
     */
    public String determineBasePackage() {
        List<String> packageNames = this.getAbstractPackageSourceList().stream()
                .map(AbstractSourcePackage::getName)
                .collect(Collectors.toList());

        if (packageNames.isEmpty()) {
            return "";
        }

        String longestCommonPrefix = packageNames.getFirst();
        for (int i = 1; i < packageNames.size(); i++) {
            String currentPackage = packageNames.get(i);
            while (currentPackage.indexOf(longestCommonPrefix) != 0) {
                longestCommonPrefix = longestCommonPrefix.substring(0, longestCommonPrefix.length() - 1);
                if (longestCommonPrefix.isEmpty()) {
                    return "";
                }
            }
        }
        if (longestCommonPrefix.endsWith(".")) {
            longestCommonPrefix = longestCommonPrefix.substring(0, longestCommonPrefix.length() - 1);
        }
        return longestCommonPrefix;
    }

}
