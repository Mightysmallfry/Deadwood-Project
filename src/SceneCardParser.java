import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;

public class SceneCardParser extends ParseDaddy{
    private ArrayList<SceneCard> _parsedCards = new ArrayList<SceneCard>();

    public SceneCardParser() {}
    public SceneCardParser(String fileName)
    {
        try {
            Document document = GetDocumentFromFile(fileName);
            _parsedCards = ParseSceneCardData(document);
        } catch (Exception e) {
            System.out.println("Something went wrong parsing : [" + fileName + "] : " + e);
        }
    }

    public ArrayList<SceneCard> GetParsedList() {
        return _parsedCards;
    }



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


    public ArrayList<SceneCard> ParseSceneCardData(Document document)
    {
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

                switch (detail.getNodeName())
                {
                    // Get Scene metadata
                    case "scene":
                        int sceneNumber = Integer.parseInt(detail.getAttributes().getNamedItem("number").getNodeValue());
                        String sceneDescription = detail.getTextContent();

                        foundCard.SetCardNumber(sceneNumber);
                        foundCard.SetDescription(sceneDescription);
                        break;

                    // Get Role metadata
                    case "part":
                        ActingRole role = ParseRole(detail);
                        foundCard.AddRole(role);
                        break;
                }
            }
            // Add the card into the array of possible cards
            foundCards.add(foundCard);
        }
        _parsedCards = foundCards;
        return foundCards;
    }
}
