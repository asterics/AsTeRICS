/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 *
 *
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b.
 *       d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
 *      d88P888          888           888    888   888  888    888 Y88b.    
 *     d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b. 
 *    d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
 *   d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
 *  d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
 * d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P"
 *
 *
 *                    homepage: http://www.asterics.org
 *
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */

package eu.asterics.mw.model.deployment.impl;

import java.awt.Dimension;
import java.awt.Point;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ModelGUIInfo {
    private boolean decoration = true;
    private boolean fullscreen = false;
    private boolean valwaysOnTop = false;
    private boolean toSysTray = false;
    private boolean shopControlPanel = true;
    private int posX;
    private int posY;
    private int width;
    private int height;

    public ModelGUIInfo(boolean decoration, boolean fullscreen, boolean valwaysOnTop, boolean toSysTray,
            boolean shopControlPanel, int posX, int posY, int width, int height) {
        super();
        this.decoration = decoration;
        this.fullscreen = fullscreen;
        this.valwaysOnTop = valwaysOnTop;
        this.toSysTray = toSysTray;
        this.shopControlPanel = shopControlPanel;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("decoration: ").append(decoration).append("\nfullscreen:").append(fullscreen)
                .append("\nshopControlPanel: ").append(shopControlPanel).append("\ntoSysTray: ").append(toSysTray)
                .append("\nvalwaysOnTop: ").append(valwaysOnTop).append("\nposX: ").append(posX).append("\nposY: ")
                .append(posY).append("\nwidth: ").append(width).append("\nheight: ").append(height);
        return buf.toString();
    }

    public ModelGUIInfo(int posX, int posY, int width, int height) {
        super();
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public Point getPosition() {
        return new Point(posX, posY);
    }

    public Dimension getDimension() {
        return new Dimension(width, height);
    }

    public void appendXMLElements(Document doc) {

        Element modelGUI = (Element) doc.getElementsByTagName("modelGUI").item(0);

        Element decoration = doc.createElement("Decoration");
        modelGUI.appendChild(decoration);
        decoration.setTextContent(Boolean.toString(this.decoration));

        Element fullscreen = doc.createElement("Fullscreen");
        modelGUI.appendChild(fullscreen);
        fullscreen.setTextContent(Boolean.toString(this.fullscreen));

        Element alwaysOnTop = doc.createElement("AlwaysOnTop");
        modelGUI.appendChild(alwaysOnTop);
        alwaysOnTop.setTextContent(Boolean.toString(this.valwaysOnTop));

        Element toSystemTray = doc.createElement("ToSystemTray");
        modelGUI.appendChild(toSystemTray);
        toSystemTray.setTextContent(Boolean.toString(this.toSysTray));

        Element shopControlPanel = doc.createElement("ShopControlPanel");
        modelGUI.appendChild(shopControlPanel);
        shopControlPanel.setTextContent(Boolean.toString(this.shopControlPanel));

        Element areGUIWindow = doc.createElement("AREGUIWindow");
        modelGUI.appendChild(areGUIWindow);

        Element posX = doc.createElement("posX");
        areGUIWindow.appendChild(posX);
        posX.setTextContent(Integer.toString(this.posX));

        Element posY = doc.createElement("posY");
        areGUIWindow.appendChild(posY);
        posY.setTextContent(Integer.toString(this.posY));

        Element width = doc.createElement("width");
        areGUIWindow.appendChild(width);
        width.setTextContent(Integer.toString(this.width));

        Element height = doc.createElement("height");
        areGUIWindow.appendChild(height);
        height.setTextContent(Integer.toString(this.height));
    }

    /**
     * @return the decoration
     */
    public boolean isDecoration() {
        return decoration;
    }

    /**
     * @return the fullscreen
     */
    public boolean isFullscreen() {
        return fullscreen;
    }

    /**
     * @return the valwaysOnTop
     */
    public boolean isValwaysOnTop() {
        return valwaysOnTop;
    }

    /**
     * @return the toSysTray
     */
    public boolean isToSysTray() {
        return toSysTray;
    }

    /**
     * @return the shopControlPanel
     */
    public boolean isShopControlPanel() {
        return shopControlPanel;
    }

    /**
     * @return the posX
     */
    public int getPosX() {
        return posX;
    }

    /**
     * @return the posY
     */
    public int getPosY() {
        return posY;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }
}
