import org.w3c.dom.Node;

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
}
