import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
        // Get all elements from the document
        Element root = document.getDocumentElement();

        // Get all scene Sets
        NodeList Sets = root.getElementsByTagName("set");

        return new ArrayList<ActingSet>();
    }

    public CastingSet FindCastingSetData(Document document) {
        return new CastingSet();
    }

    public GameSet FindTrailerSetData(Document document){
        return new GameSet();
    }

    public ArrayList<SceneCard> FindSceneCardData (Document document){
        Element root = document.getDocumentElement();
        NodeList sceneCards = root.getElementsByTagName("card");

        // If no cards are found, early return
        if (sceneCards.getLength() == 0){
            return null;
        }

        ArrayList<SceneCard> foundCards = new ArrayList<SceneCard>(sceneCards.getLength());

        String cardName;
        String imageName;
        int cardDifficulty;

        // Loop through each card creating it at the end
        for (int i = 0; i < sceneCards.getLength(); i++){


            // Get Card Framework - name, img, budget
            Node card = sceneCards.item(i);

            cardName = card.getAttributes().getNamedItem("name").getNodeValue();
            imageName = card.getAttributes().getNamedItem("img").getNodeValue();
            cardDifficulty = Integer.parseInt(card.getAttributes().getNamedItem("budget").getNodeValue());

            SceneCard foundCard = new SceneCard(cardDifficulty, cardName, imageName);


            // Get Card Details - scene details, roles
            NodeList cardDetails = card.getChildNodes();

            // Within a card, we have a scene and parts
            for (int j = 0; j < cardDetails.getLength(); j++){
                Node detail = cardDetails.item(j);

                // If the cardDetail is a scene, get number and description
                // Then set it for the found card
                if ("scene".equals(detail.getNodeName())) {
                    int sceneNumber = Integer.parseInt(detail.getAttributes().getNamedItem("number").getNodeValue());
                    String sceneDescription = detail.getTextContent();

                    foundCard.SetCardNumber(sceneNumber);
                    foundCard.SetDescription(sceneDescription);
                }

                // If the cardDetail is an acting role,
                // create and add it to the foundCard def
                if ("part".equals(detail.getNodeName())) {
                    ActingRole role = FetchActingRole(detail);

                    foundCard.AddRole(role);
                }
            }


            // Add the card into the array of possible cards
            foundCards.set(i, foundCard);
        }
        return foundCards;
    }

    private ActingRole FetchActingRole(Node detail)
    {
        // Get Basic role - Name, Level
        String roleName = detail.getAttributes().getNamedItem("role").getNodeValue();
        int roleRank = Integer.parseInt(detail.getAttributes().getNamedItem("level").getNodeValue());

        // Create for use in constructor
        String roleQuote  = "";
        Area roleArea = new Area();

        // Dive into the role, getting - Area, Line
        NodeList partDetails = detail.getChildNodes();
        for (int k = 0; k < partDetails.getLength(); k++)
        {
            Node partDetail = partDetails.item(k);

            if ("area".equals(partDetail.getNodeName())){
                roleArea = new Area(
                        Integer.parseInt(partDetail.getAttributes().getNamedItem("x").getNodeValue()),
                        Integer.parseInt(partDetail.getAttributes().getNamedItem("y").getNodeValue()),
                        Integer.parseInt(partDetail.getAttributes().getNamedItem("w").getNodeValue()),
                        Integer.parseInt(partDetail.getAttributes().getNamedItem("h").getNodeValue())
                );
            }
            if ("line".equals(partDetail.getNodeName())){
                roleQuote = partDetail.getTextContent();
            }
        }

        // Combine, construct and add
        // Our new Role to the card
        return new ActingRole(
            roleRank,
            roleName,
            roleQuote,
            roleArea);
    }



}
