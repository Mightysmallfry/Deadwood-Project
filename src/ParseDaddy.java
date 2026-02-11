import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class ParseDaddy {

    protected Area ParseArea(Node areaTag)
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

    protected ActingRole ParseRole(Node detail)
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

    protected ArrayList<ActingRole> ParseRoleList(Node partsNode)
    {

        ArrayList<ActingRole> roles = new ArrayList<>();

        if (partsNode == null)
        {
            return roles;
        }

        NodeList partNodes = partsNode.getChildNodes();

        //loops through parts
        for (int i = 0; i < partNodes.getLength(); i++)
        {
            Node partNode = partNodes.item(i);

            if ("part".equals(partNode.getNodeName()))
            {
//                String roleName = partNode.getAttributes().getNamedItem("name").getNodeValue();
//
//                int roleLevel = Integer.parseInt(partNode.getAttributes().getNamedItem("level").getNodeValue());
//
//                String roleLine = "";
//                Area roleArea = null;
//
//
//                NodeList partDetails = partNode.getChildNodes();
//
//                for (int j = 0; j < partDetails.getLength(); j++)
//                {
//                    Node detail = partDetails.item(j);
//
//                    if ("area".equals(detail.getNodeName()))
//                    {
//                        roleArea = ParseArea(detail);
//                    }
//
//                    if ("line".equals(detail.getNodeName()))
//                    {
//                        roleLine = detail.getTextContent().trim();
//                    }
//                }

                ActingRole role = ParseRole(partNode);

                roles.add(role);
            }
        }
        return roles;
    }
}
