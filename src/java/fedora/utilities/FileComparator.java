/*
 * Created on May 26, 2005
 *
 */
package fedora.utilities;

import java.io.File;
import java.util.Comparator;

/**
 * Compares one File object to another.
 * Sorts directories before files, otherwise alphabetically ignoring case.
 * 
 * @author Edwin Shin
 *
 */
public class FileComparator implements Comparator {

    public int compare(Object fileA, Object fileB) {
        if ( !(fileA instanceof File) || !(fileB instanceof File)) {
            throw new ClassCastException();
        }
        File a = (File)fileA;
        File b = (File)fileB;
        //... Sort directories before files,
        //    otherwise alphabetical ignoring case.
        if (a.isDirectory() && !b.isDirectory()) {
            return -1;

        } else if (!a.isDirectory() && b.isDirectory()) {
            return 1;

        } else {
            return a.getName().compareToIgnoreCase(b.getName());
        }
    }

}
