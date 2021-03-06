package app_.smartreceipt.digitaldocs.services;

import org.w3c.dom.Node;

import java.lang.reflect.InvocationTargetException;

/**
 * Searches for the ABN in the receipt
 */

public class ABNSearchObject {
    private Node searchResultsPayload;

    public ABNSearchObject(Node searchResultsPayload) {
        this.searchResultsPayload = searchResultsPayload;
    }

    public boolean isException() {
        return XMLUtils.getNode(searchResultsPayload, "/ABRPayloadSearchResults/response/exception") != null;
    }

    public String getOrganisationName() throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        return XMLUtils.getNodeText(searchResultsPayload, "/ABRPayloadSearchResults/response/businessEntity/mainName/organisationName");
    }

    public String getExceptionDescription() throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        return XMLUtils.getNodeText(searchResultsPayload, "/ABRPayloadSearchResults/response/exception/exceptionDescription");
    }

}