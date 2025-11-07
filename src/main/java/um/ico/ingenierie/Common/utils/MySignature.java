package um.ico.ingenierie.Common.utils;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public final class MySignature {

    public static String createMethodSignature(IMethodBinding binding) {
        if (binding == null) return "unknown.binding";

        StringBuilder signature = new StringBuilder();
        ITypeBinding declaringClass = binding.getDeclaringClass();
        if (declaringClass == null || declaringClass.getQualifiedName().isEmpty()) {
            return "local.class#" + binding.getName();
        }
        signature.append(declaringClass.getErasure().getQualifiedName());
        signature.append("#");
        signature.append(binding.getName());
        signature.append("(");

        ITypeBinding[] parameters = binding.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            signature.append(parameters[i].getErasure().getQualifiedName());
            if (i < parameters.length - 1) {
                signature.append(",");
            }
        }
        signature.append(")");
        return signature.toString();
    }

    public static String simplifySignature(String signature) {
        String[] parts = signature.split("#");
        String classAndPackage = parts[0];
        String methodNameAndParams = parts.length > 1 ? parts[1] : "";

        String className = classAndPackage.substring(classAndPackage.lastIndexOf('.') + 1);

        // On remplace les longs noms de paramètres par "..." pour la clarté
        return className + "#" + methodNameAndParams.replaceAll("\\(.*\\)", "(...)");
    }

}
