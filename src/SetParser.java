import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;


public class SetParser extends ParseDaddy{

    //TODO: Add Arraylist of Sets and Hashmap
    // of sets as local variables so we can access them
    // That or add them as statics for the GameSet Class

    //TODO: Implement Second Neighbor pass once all
    // GameSet objects have been created

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

    public CastingSet ParseCastingSet(Document document) {
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


    // Still Needs to be implemented
    // I'm Thinking we do a double pass
    // Once to register every set
    // Second time to fill in the neighbors
    private ArrayList<GameSet> ParseNeighbors() {

        return new ArrayList<GameSet>();
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

    //Gus Start

    public ArrayList<ActingSet> FindActingSetData(Document document) {

        Element root = document.getDocumentElement();
        NodeList setNodes = root.getElementsByTagName("set");

        ArrayList<ActingSet> actingSets = new ArrayList<>();
        Map<String, ActingSet> setMap = new HashMap<>();
        Map<ActingSet, ArrayList<String>> neighborNameMap = new HashMap<>();

        // Loop through each set
        for (int i = 0; i < setNodes.getLength(); i++) {

            Node setNode = setNodes.item(i);

            String name = setNode.getAttributes().getNamedItem("name").getNodeValue(); //This acquires the name

            // Here we initialize variables for later
            Area area = null;
            ArrayList<String> neighborNames = new ArrayList<>();
            ArrayList<ActingRole> roles = new ArrayList<>();
            int maximumProgress = 0;

            NodeList children = setNode.getChildNodes();

            //for every element of the child nodes
            for (int j = 0; j < children.getLength(); j++) {

                Node child = children.item(j);

                //we use cases to determine the value type
                switch (child.getNodeName()) {

                    case "area":
                        area = ParseArea(child);
                        break;

                    case "neighbors":
                        neighborNames = ParseNeighborNames(child);
                        break;

                    case "takes":
                        maximumProgress = ParseMaxTakes(child);
                        break;

                    case "parts":
                        roles = ParseRoleList(child);
                        break;
                }
            }
            // We add all the elements to the acting set then add it to the Acting set array.
            ActingSet actingSet = new ActingSet(name, area, new ArrayList<>(), maximumProgress, roles);
            actingSets.add(actingSet);
            setMap.put(name, actingSet); // We also add it to the neighbor map!
            neighborNameMap.put(actingSet, neighborNames);
        }

        // Here we resolve the neighbors (may need to be fixed with trailer and casting office in mind)
        /**
         * Ok so I figure we can have a function like public ArrayList<gameset> LoadBoard(Document document)
         * that grabs all sets and adds the ActingSets Trailer and CastingSets to a hashmap.
         * Next we would loop through and resolve all neighbors.
         */
        for (ActingSet set : actingSets)
        {
            ArrayList<GameSet> resolvedNeighbors = new ArrayList<>();

            for (String neighborName : neighborNameMap.get(set))
            {

                if (setMap.containsKey(neighborName))
                {
                    resolvedNeighbors.add(setMap.get(neighborName));
                }
            }

            set.SetNeighbors(resolvedNeighbors);
        }

        return actingSets;
    }

    private ArrayList<String> ParseNeighborNames(Node neighborsNode)
    {

        ArrayList<String> neighborNames = new ArrayList<>();
        NodeList neighborList = neighborsNode.getChildNodes();
        //loop through the children and get the neighbors
        for (int i = 0; i < neighborList.getLength(); i++)
        {

            Node neighbor = neighborList.item(i);

            if ("neighbor".equals(neighbor.getNodeName()))
            {
                String name = neighbor.getAttributes().getNamedItem("name").getNodeValue();
                neighborNames.add(name);
            }
        }

        return neighborNames;
    }

    private int ParseMaxTakes(Node takesNode)
    {
        int max = 0;
        NodeList takeNodes = takesNode.getChildNodes();

        for (int i = 0; i < takeNodes.getLength(); i++)
        {
            Node take = takeNodes.item(i);

            if ("take".equals(take.getNodeName()))
            {
                int number = Integer.parseInt(take.getAttributes().getNamedItem("number").getNodeValue());

                if (number > max) {
                    max = number;
                }
            }
        }

        return max;
    }

    public GameSet FindTrailerSetData(Document document)
    {

        Element root = document.getDocumentElement();
        NodeList trailerList = root.getElementsByTagName("trailer");

        if (trailerList.getLength() != 1)
        {
            System.out.println("Could not find trailer");
            return null;
        }

        Node trailerNode = trailerList.item(0);

        Area area = null;
        ArrayList<String> neighborNames = new ArrayList<>();

        NodeList children = trailerNode.getChildNodes();

        for (int i = 0; i < children.getLength(); i++)
        {

            Node child = children.item(i);

            switch (child.getNodeName()) {

                case "area":
                    area = ParseArea(child);
                    break;

                case "neighbors":
                    neighborNames = ParseNeighborNames(child);
                    break;
            }
        }

        GameSet trailer = new GameSet("Trailer", new ArrayList<GameSet>(), area);

        return trailer;
    }






}
