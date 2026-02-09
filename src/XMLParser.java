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

    public GameSet FindTrailerSetData(Document document){
        // Get all elements from the document
        Element root = document.getDocumentElement();

        // Get all scene Sets
        NodeList Sets = root.getElementsByTagName("set");


        return new GameSet();
    }

    public CastingSet FindCastingSetData(Document document) {
        // Get all sets from the document
        Element root = document.getDocumentElement();
        NodeList sets = root.getElementsByTagName("office");

        if (sets.getLength() != 1) {
            System.out.println("Could not find just one Casting Set");
            return null;
        }
        CastingSet castingSet = new CastingSet();
        Node set = sets.item(0);

        NodeList setDetails = set.getChildNodes();

        for (int i = 0; i < setDetails.getLength(); i++) {

            // Parse neighbors
//            if ("neighbors".equals(setDetails.item(i).getNodeName())) {
//                castingSet.SetNeighbors(ParseNeighbors());
//            }

            // Parse Area
            if ("area".equals(setDetails.item(i).getNodeName()))
            {
                Node areaTag = setDetails.item(i);
                Area area = ParseArea(areaTag);

                castingSet.SetArea(area);
            }

            // Parse Upgrades
            if ("upgrades".equals(setDetails.item(i).getNodeName())) {
                Node upgradesTag = setDetails.item(i);
                ArrayList<UpgradeData> upgradeData = ParseUpgrades(upgradesTag);
                castingSet.SetUpgrades(upgradeData);
            }
        }

        return castingSet;
    }


    public ArrayList<SceneCard> FindSceneCardData (Document document)
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
            foundCards.add(foundCard);
        }
        return foundCards;
    }

    private ActingRole FetchActingRole(Node detail)
    {
        // Get Basic role - Name, Level
        String roleName = detail.getAttributes().getNamedItem("name").getNodeValue();
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
                roleArea = ParseArea(partDetail);
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

    // Still Needs to be implemented
    // I'm Thinking we do a double pass
    // Once to register every set
    // Second time to fill in the neighbors
    private ArrayList<GameSet> ParseNeighbors() {

        return new ArrayList<GameSet>();
    }

    private Area ParseArea(Node areaTag)
    {
        if (areaTag == null) {
            return null;
        }

        return new Area(
                Integer.parseInt(areaTag.getAttributes().getNamedItem("x").getNodeValue()),
                Integer.parseInt(areaTag.getAttributes().getNamedItem("y").getNodeValue()),
                Integer.parseInt(areaTag.getAttributes().getNamedItem("w").getNodeValue()),
                Integer.parseInt(areaTag.getAttributes().getNamedItem("h").getNodeValue())
        );
    }

    private ArrayList<UpgradeData> ParseUpgrades(Node upgradesTag)
    {
        if (upgradesTag == null){
            return null;
        }
        ArrayList<UpgradeData> upgradeList = new ArrayList<>();

        int level = 0;
        int amount = 0;
        String currencyType = "";
        Area area = null;

        for (int i = 0; i < upgradesTag.getChildNodes().getLength(); i++){
            Node upgradeTag = upgradesTag.getChildNodes().item(i);

            if ("upgrade".equals(upgradeTag.getNodeName())) {
                level = Integer.parseInt(upgradeTag.getAttributes().getNamedItem("level").getNodeValue());
                amount = Integer.parseInt(upgradeTag.getAttributes().getNamedItem("amt").getNodeValue());
                currencyType = upgradeTag.getAttributes().getNamedItem("currency").getNodeValue();

                NodeList subUpgradeTags = upgradeTag.getChildNodes();
                for (int j = 0; j < subUpgradeTags.getLength(); j++) {
                    Node subUpgradeTag = subUpgradeTags.item(j);
                    if ("area".equals(subUpgradeTag.getNodeName())) {
                        area = ParseArea(subUpgradeTag);
                    }
                }
                upgradeList.add(new UpgradeData(level, amount, currencyType, area));
            }
        }

        return upgradeList;
    }

}
