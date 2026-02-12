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

    // Methods
    private HashMap<String, GameSet> _allSets;


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
                throw new RuntimeException("Failed to parse XML:" + fileName, ex);
            }
            return document;
        }
    }

    private void Set_AllSets(HashMap<String,GameSet> sets){this._allSets = sets;}

    private HashMap<String,GameSet> Get_AllSets(){return this._allSets;}

    private GameSet ParseCastingSet(Node set) {

        CastingSet castingSet = new CastingSet();

        NodeList setDetails = set.getChildNodes();

        for (int i = 0; i < setDetails.getLength(); i++) {
            Node currentNode = setDetails.item(i);

            if(currentNode.getNodeType() != Node.ELEMENT_NODE)
                continue;

            switch (currentNode.getNodeName()) {

                case "area":
                    Area area = ParseArea(currentNode);
                    castingSet.SetArea(area);
                    break;

                case "upgrades":
                    ArrayList<UpgradeData> upgradeData = ParseUpgrades(currentNode);
                    castingSet.SetUpgrades(upgradeData);
                    break;
            }
        }
        return castingSet;
    }

    private void ParseBoard(Document document) {
        Element root = document.getDocumentElement();
        NodeList children = root.getChildNodes();
        HashMap<String,GameSet> setHolder = new HashMap<>(children.getLength());
        HashMap<String,ArrayList<String>> neighborHolder = new HashMap<>(children.getLength());
        for(int i = 0; i < children.getLength(); i++)
        {
            Node currentNode = children.item(i);

            if (currentNode.getNodeType() != Node.ELEMENT_NODE)
                continue;

            String nodeType = currentNode.getNodeName();
            String name;

            switch (nodeType)
            {
                case "set":
                    name = currentNode.getAttributes().getNamedItem("name").getNodeValue();
                    setHolder.put(name,FindActingSetData(currentNode,name));
                    break;

                case "trailer":
                    name = "trailer";
                    setHolder.put(name,FindTrailerSetData(currentNode));
                    break;

                case "office":
                    name = "office";
                    setHolder.put(name,ParseCastingSet(currentNode));
                    break;

                default:
                    continue;
            }

            if(name != null)
            {
                Node neighborsNode = getNeighborsNode(currentNode);
                neighborHolder.put(name, neighborsNode != null ? ParseNeighborNames(neighborsNode) : new ArrayList<>());
            }

        }   //everything should be in the hashmap now! so we need to go back through and parse neighbors.

        for(Map.Entry<String,GameSet> entry : setHolder.entrySet())
        {
            String key = entry.getKey();
            GameSet value = entry.getValue();
            ArrayList<String> stringNeighbors = neighborHolder.get(key);
            HashMap<String,GameSet> newNeighbors = new HashMap<>(stringNeighbors.size());

            for (String stringNeighbor : stringNeighbors) {
                GameSet neighbor = setHolder.get(stringNeighbor);
                if(neighbor == null)
                {
                    throw new IllegalArgumentException("Unknown neighbor:  " + stringNeighbor);
                }
                newNeighbors.put(stringNeighbor,neighbor);
            }
            value.SetNeighbors(newNeighbors);
        }

        Set_AllSets(setHolder);
    }

    public CastingSet FindCastingSet()
    {
        if (_allSets == null)
        {
            throw new IllegalStateException("Board not parsed yet.");
        }
            GameSet set = Get_AllSets().get("office");
        return (set instanceof CastingSet) ? (CastingSet) set : null;
    }

    public GameSet FindTrailer()
    {
        if (_allSets == null)
        {
            throw new IllegalStateException("Board not parsed yet.");
        }
        return Get_AllSets().get("trailer");
    }

    public ArrayList<ActingSet> FindActingSets()
    {
        ArrayList<ActingSet> actingSets = new ArrayList<>();

        if (_allSets == null)
        {
            throw new IllegalStateException("Board not parsed yet.");
        }
        for(GameSet set : Get_AllSets().values())
        {
            if (set instanceof ActingSet)
            {
                actingSets.add(((ActingSet) set));
            }

        }
        return actingSets;
    }


    //Helper to get the neighbor node name.
    private Node getNeighborsNode(Node daddy)
    {
        NodeList nodes = daddy.getChildNodes();
        for(int i = 0; i < nodes.getLength(); i++)
        {
            Node child = nodes.item(i);

            if(child.getNodeType() != Node.ELEMENT_NODE)
                continue;

            if(child.getNodeName().equals("neighbors"))
            {
                return child;
            }
        }
        return null;
    }


    //Upgrades moment
    private ArrayList<UpgradeData> ParseUpgrades(Node upgradesTag)
    {
        if (upgradesTag == null){
            return new ArrayList<>();
        }
        ArrayList<UpgradeData> upgradeList = new ArrayList<>();

        int level = 0;
        int amount = 0;
        String currencyType = "";


        for (int i = 0; i < upgradesTag.getChildNodes().getLength(); i++){
            Node upgradeTag = upgradesTag.getChildNodes().item(i);

            if (upgradeTag.getNodeType() != Node.ELEMENT_NODE)
                continue;

            if ("upgrade".equals(upgradeTag.getNodeName())) {
                Area area = null;
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


    private GameSet FindActingSetData(Node setNode,String name) {

        Area area = null;
        ArrayList<ActingRole> roles = new ArrayList<>();
        int maximumProgress = 0;

        NodeList children = setNode.getChildNodes();

        //for every element of the child nodes
        for (int j = 0; j < children.getLength(); j++) {
            Node child = children.item(j);

            if(child.getNodeType() != Node.ELEMENT_NODE)
                continue;

            //we use cases to determine the value type
            switch (child.getNodeName()) {

                case "area":
                    area = ParseArea(child);
                    break;

                case "takes":
                    maximumProgress = ParseMaxTakes(child);
                    break;

                case "parts":
                    roles = ParseRoleList(child);
                    break;
                }
            }
        return new ActingSet(name, area, new HashMap<>(), maximumProgress, roles);
    }



    private ArrayList<String> ParseNeighborNames(Node neighborsNode)
    {

        ArrayList<String> neighborNames = new ArrayList<>();
        NodeList neighborList = neighborsNode.getChildNodes();
        //loop through the children and get the neighbors
        for (int i = 0; i < neighborList.getLength(); i++)
        {
            Node neighbor = neighborList.item(i);

            if(neighbor.getNodeType() != Node.ELEMENT_NODE)
                continue;

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

            if (take.getNodeType() == Node.ELEMENT_NODE && "take".equals(take.getNodeName()))
            {
                int number = Integer.parseInt(take.getAttributes().getNamedItem("number").getNodeValue());

                if (number > max) {
                    max = number;
                }
            }
        }

        return max;
    }

    private GameSet FindTrailerSetData(Node trailerNode)
    {

        Area area = null;

        NodeList children = trailerNode.getChildNodes();

        for (int i = 0; i < children.getLength(); i++)
        {
            Node child = children.item(i);

            if(child.getNodeType() != Node.ELEMENT_NODE)
                continue;

            if (child.getNodeName().equals("area"))
            {
                area = ParseArea(child);
            }
        }

        return new GameSet("trailer", new HashMap<>(), area);
    }


}
