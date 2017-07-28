/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.quakeml;

import gov.usgs.volcanoes.core.quakeml.Pick.Onset;
import gov.usgs.volcanoes.core.quakeml.Pick.Polarity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Comparator;
import java.util.Map;

/**
 * Holder for QuakeML arrival.
 *
 * @author Tom Parker
 *
 */
public class Arrival {
  private static final Logger LOGGER = LoggerFactory.getLogger(Arrival.class);

  /**
   * Sort arrival by distance from origin.
   *
   * @return compare value
   */
  public static Comparator<Arrival> distanceComparator() {
    return new Comparator<Arrival>() {
      @Override
      public int compare(final Arrival e1, final Arrival e2) {
        return Double.compare(e1.distance, e2.distance);
      }
    };
  }


  public String publicId;
  private Pick pick;
  private String phase;
  private double distance;
  private double timeResidual;
  private double timeWeight;

  /**
   * Constructor for newly created Arrival.
   * 
   * @param publicId resource identifier of the arrival
   * @param pick associated pick
   * @param phase phase identification 
   */
  public Arrival(String publicId, Pick pick, String phase) {
    this.publicId = publicId;
    this.pick = pick;
    this.phase = phase;
  }

  /**
   * Constructor from XML.
   *
   * @param arrivalElement XML element representing arrival
   * @param picks picks associated with event
   */
  public Arrival(Element arrivalElement, Map<String, Pick> picks) {
    publicId = arrivalElement.getAttribute("publicID");
    LOGGER.debug("new arrival {}", publicId);

    // this.phase = arrivalElement.getAttribute("phase");
    pick = picks.get(arrivalElement.getElementsByTagName("pickID").item(0).getTextContent());
    phase = arrivalElement.getElementsByTagName("phase").item(0).getTextContent();
    timeResidual = Double
        .parseDouble(arrivalElement.getElementsByTagName("timeResidual").item(0).getTextContent());
    NodeList distanceElement = arrivalElement.getElementsByTagName("distance");
    if (distanceElement.getLength() > 0) {
      distance = Double.parseDouble(distanceElement.item(0).getTextContent());
    }
    NodeList timeWeightElement = arrivalElement.getElementsByTagName("timeWeight");
    if (timeWeightElement.getLength() > 0) {
      timeWeight = Double.parseDouble(timeWeightElement.item(0).getTextContent());
    }
  }

  public String getPhase() {
    return phase;
  }

  public Pick getPick() {
    return pick;
  }

  public Double getTimeWeight() {
    return timeWeight;
  }

  /**
   * To XML Element.
   * @param doc xml document
   * @return xml element
   */
  public Element toElement(Document doc) {

    Element arrival = doc.createElement("arrival");
    arrival.setAttribute("publicID", publicId);
    arrival.setAttribute("pickID", pick.publicId);


    Element phaseElement = doc.createElement("phase");
    phaseElement.appendChild(doc.createTextNode(phase));
    arrival.appendChild(phaseElement);

    if (!Double.isNaN(distance)) {
      Element element = doc.createElement("distance");
      element.appendChild(doc.createTextNode(Double.toString(distance)));
      arrival.appendChild(element);
    }

    if (!Double.isNaN(timeResidual)) {
      Element element = doc.createElement("timeResidual");
      element.appendChild(doc.createTextNode(Double.toString(timeResidual)));
      arrival.appendChild(element);
    }

    if (!Double.isNaN(timeWeight)) {
      Element element = doc.createElement("timeWeight");
      element.appendChild(doc.createTextNode(Double.toString(timeWeight)));
      arrival.appendChild(element);
    }

    return arrival;
  }

  /**
   * Return a phase tag.
   *
   * @return phase tag
   */
  public String getTag() {
    final StringBuilder sb = new StringBuilder();

    sb.append(timeWeight);

    final Onset onset = pick.getOnset();
    if (onset == Pick.Onset.EMERGENT) {
      sb.append("e");
    } else if (onset == Pick.Onset.IMPULSIVE) {
      sb.append("i");
    }

    sb.append(phase.charAt(0));

    final Polarity polarity = pick.getPolarity();
    if (polarity == Pick.Polarity.NEGATIVE) {
      sb.append("-");
    } else if (polarity == Pick.Polarity.POSITIVE) {
      sb.append("+");
    }

    return sb.toString();
  }

  public double getTimeResidual() {
    return timeResidual;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();

    sb.append("PublicId: " + publicId + "\n");
    sb.append("Distance: " + distance + "°\n");
    sb.append("Phase: " + phase + "\n");
    sb.append("Pick: " + pick + "\n");

    return sb.toString();
  }

}
