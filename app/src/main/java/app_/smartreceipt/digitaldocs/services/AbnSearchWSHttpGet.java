package app_.smartreceipt.digitaldocs.services;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

public class AbnSearchWSHttpGet {
    private static final String	UTF_8	= "UTF-8";

    public static void main(String[] args) {
        try {
            String guid = "411e2117-1fe8-4876-a8a8-5e3150e22eda";
            String abn = "36004763526";

            ABNSearchObject result = searchByABN(guid, abn, false);

            if (!result.isException()) {
                result.getOrganisationName();
            } else {
                result.getExceptionDescription();
            }
        } catch (Exception e) {
            System.err.println("Caught exception : " + e);
            e.printStackTrace(System.err);
        }
    }

    public static ABNSearchObject searchByABN(String guid, String abn, boolean includeHistorical) throws URISyntaxException, IOException,
            SAXException, ParserConfigurationException, FactoryConfigurationError {
        ABNSearchObject results = null;

        String params = "";

        params += "&includeHistoricalDetails=" + encodeBooleanParam(includeHistorical);
        params += "&searchString=" + URLEncoder.encode(abn, UTF_8);

        results = doRequest(guid, "ABRSearchByABN", params);

        return results;
    }

    public static ABNSearchObject searchByACN(String guid, String acn, boolean includeHistorical) throws URISyntaxException, IOException,
            SAXException, ParserConfigurationException, FactoryConfigurationError {
        ABNSearchObject results = null;

        String params = "";

        params += "&includeHistoricalDetails=" + encodeBooleanParam(includeHistorical);
        params += "&searchString=" + URLEncoder.encode(acn, UTF_8);

        results = doRequest(guid, "ABRSearchByASIC", params);

        return results;
    }

    public static ABNSearchObject searchByABNv200506(String guid, String abn, boolean includeHistorical) throws URISyntaxException, IOException,
            SAXException, ParserConfigurationException, FactoryConfigurationError {
        ABNSearchObject results = null;

        String params = "";

        params += "&includeHistoricalDetails=" + encodeBooleanParam(includeHistorical);
        params += "&searchString=" + URLEncoder.encode(abn, UTF_8);

        results = doRequest(guid, "SearchByABNv200506", params);

        return results;
    }

    public static ABNSearchObject searchByACNv200506(String guid, String acn, boolean includeHistorical) throws URISyntaxException, IOException,
            SAXException, ParserConfigurationException, FactoryConfigurationError {
        ABNSearchObject results = null;

        String params = "";

        params += "&includeHistoricalDetails=" + encodeBooleanParam(includeHistorical);
        params += "&searchString=" + URLEncoder.encode(acn, UTF_8);

        results = doRequest(guid, "SearchByASICv200506", params);

        return results;
    }

    public static ABNSearchObject searchByNameSimpleProtocol(String guid, String name, boolean legal, boolean trading, boolean act, boolean nsw,
                                                             boolean nt, boolean qld, boolean sa, boolean tas, boolean vic, boolean wa, String postcode) throws URISyntaxException, IOException,
            SAXException, ParserConfigurationException, FactoryConfigurationError {
        ABNSearchObject results = null;

        String params = "";

        params += "&name=" + URLEncoder.encode(name, UTF_8);

        params += "&legalName=" + encodeBooleanParam(legal);
        params += "&tradingName=" + encodeBooleanParam(trading);

        params += "&ACT=" + encodeBooleanParam(act);
        params += "&NSW=" + encodeBooleanParam(nsw);
        params += "&NT=" + encodeBooleanParam(nt);
        params += "&QLD=" + encodeBooleanParam(qld);
        params += "&SA=" + encodeBooleanParam(sa);
        params += "&TAS=" + encodeBooleanParam(tas);
        params += "&VIC=" + encodeBooleanParam(vic);
        params += "&WA=" + encodeBooleanParam(wa);

        params += "&postcode=" + URLEncoder.encode(postcode, UTF_8);

        results = doRequest(guid, "ABRSearchByNameSimpleProtocol", params);

        return results;
    }

    public static ABNSearchObject searchByNameAdvancedSimpleProtocol(String guid, String name, boolean legal, boolean trading, boolean act,
                                                                     boolean nsw, boolean nt, boolean qld, boolean sa, boolean tas, boolean vic, boolean wa, String postcode, String width, int minScore)
            throws URISyntaxException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError {
        ABNSearchObject results = null;

        String params = "";

        params += "&name=" + URLEncoder.encode(name, UTF_8);

        params += "&legalName=" + encodeBooleanParam(legal);
        params += "&tradingName=" + encodeBooleanParam(trading);

        params += "&ACT=" + encodeBooleanParam(act);
        params += "&NSW=" + encodeBooleanParam(nsw);
        params += "&NT=" + encodeBooleanParam(nt);
        params += "&QLD=" + encodeBooleanParam(qld);
        params += "&SA=" + encodeBooleanParam(sa);
        params += "&TAS=" + encodeBooleanParam(tas);
        params += "&VIC=" + encodeBooleanParam(vic);
        params += "&WA=" + encodeBooleanParam(wa);

        params += "&postcode=" + URLEncoder.encode(postcode, UTF_8);

        params += "&searchWidth=" + width;

        params += "&minimumScore=" + minScore;

        results = doRequest(guid, "ABRSearchByNameAdvancedSimpleProtocol", params);

        return results;
    }

    private static ABNSearchObject doRequest(String guid, String service, String parameters) throws URISyntaxException, IOException, SAXException,
            ParserConfigurationException, FactoryConfigurationError {
        ABNSearchObject result = null;

        URL url = new URL("https://abr.business.gov.au/abrxmlsearch/ABRXMLSearch.asmx/" + service + "?authenticationGuid=" + URLEncoder.encode(guid, UTF_8) + parameters);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "text/xml; charset-utf-8");
        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            result = new ABNSearchObject(XMLUtils.DOMParseXML(connection.getInputStream()).getDocumentElement());

        connection.disconnect();

        return result;
    }

    private static String encodeBooleanParam(boolean value) {
        if (value)
            return "Y";
        else
            return "N";
    }

}