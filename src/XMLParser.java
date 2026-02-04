import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;

public class XMLParser {

    /**
     * Attempts to open xml file by the given fileName
     * Throws an error if not possible
     * @param fileName
     * @return
     * @throws ParserConfigurationException
     */
    public Document GetDocumentFromFile(String fileName)
            throws ParserConfigurationException {
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = null;

            try {
                document = documentBuilder.parse(fileName);
            } catch (Exception ex) {
                System.out.println("XML parse failure");
                ex.printStackTrace();
            }
            return document;
        }
    }

    public ArrayList<ActingSet> FindActingSetData (Document document) {
        return new ArrayList<ActingSet>();
    }

    public CastingSet FindCastingSetData(Document document) {
        return new CastingSet();
    }

    public GameSet FindTrailerSetData(Document document){
        return new GameSet();
    }
}
