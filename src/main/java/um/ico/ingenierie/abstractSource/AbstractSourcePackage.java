package um.ico.ingenierie.abstractSource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AbstractSourcePackage implements Serializable {

    private String name;
    private List<AbstractSourceClass> lesClass = new ArrayList<AbstractSourceClass>();

    public AbstractSourcePackage() {
    }

    public AbstractSourcePackage(String name) {
        this.name = name;
    }

    public AbstractSourcePackage(String name, List<AbstractSourceClass> lesClass) {
        this.name = name;
        this.lesClass = lesClass;
    }

    public void addClassToPackage(AbstractSourceClass sourceClass){
        if (lesClass.contains(sourceClass)){return;}
        else {
            this.lesClass.add(sourceClass);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AbstractSourceClass> getLesClass() {
        return lesClass;
    }

    private void setLesClass(List<AbstractSourceClass> lesClass) {
        this.lesClass = lesClass;
    }

    @Override
    public String toString() {
        return "AbstractPackageSource{" +
                "name='" + name + '\'' +
                ", lesClass=" + lesClass +
                '}';
    }
}
