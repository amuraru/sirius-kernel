/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.kernel.nls;

import sirius.kernel.commons.Explain;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

/**
 * Provides a {@link Properties} class with keys sorted lexicographically
 */
class SortedProperties extends Properties {

    private static final long serialVersionUID = -8585151811583014130L;

    @Override
    @SuppressWarnings({"unchecked", "rawtypes", "UseOfObsoleteCollectionType", "squid:S1149"})
    @Explain("We use an old Java API here, which requires those collections")
    public synchronized Enumeration<Object> keys() {
        Enumeration<Object> keysEnum = super.keys();
        Vector keyList = new Vector();
        while (keysEnum.hasMoreElements()) {
            keyList.add(keysEnum.nextElement());
        }
        Collections.sort(keyList);
        return keyList.elements();
    }
}
