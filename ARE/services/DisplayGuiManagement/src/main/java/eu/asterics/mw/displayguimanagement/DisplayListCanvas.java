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

package eu.asterics.mw.displayguimanagement;

import eu.asterics.mw.services.AstericsErrorHandling;

public class DisplayListCanvas extends DisplayCanvas {

    String title;

    int nextChildY = 16;
    final int listElementHeight = 16;

    int lastCanvasIndex = 0;
    int visibleCanvasIndex = 0;
    int fillOfLastCanvas = 0;
    int elementsPerCanvas = 0;

    public DisplayListCanvas(String title, int x, int y, int w, int h) {
        super(x, y, w, h);

        if (h < listElementHeight) {
            AstericsErrorHandling.instance.getLogger().warning("Warning: Canvas height does not fit a single element");
        }

        this.title = title;
        elementsPerCanvas = h / listElementHeight;
        DisplayCanvas canvas = new DisplayCanvas(x, y, w, h);
        canvas.addChild(new DisplayLabel("\37" + title + "                       \36", 0, 0, w, h));
        fillOfLastCanvas++;
        children.add(canvas);
    }

    @Override
    public void addChild(DisplayCanvas canvas) {
        DisplayGuiManager.debugMessage("DisplayListCanvas.addChild(): adding " + canvas.canvasName);
        DisplayCanvas parent = children.get(lastCanvasIndex);

        canvas.setPosition(0, nextChildY);
        nextChildY += listElementHeight;
        canvas.setSize(relPosition.width, listElementHeight);
        if (fillOfLastCanvas < elementsPerCanvas) {
            parent.addChild(canvas);
            fillOfLastCanvas++;
        } else {
            DisplayCanvas c = new DisplayCanvas(relPosition.x, relPosition.y, relPosition.width, relPosition.height);

            canvas.setPosition(0, 0);
            c.addChild(canvas);
            children.add(c);
            nextChildY = 16;
            fillOfLastCanvas = 1;
            lastCanvasIndex++;
        }
    }

    @Override
    public void draw() {
        DisplayGuiManager.debugMessage("DisplayListCanvas.draw()");
        setVisible();
        children.get(visibleCanvasIndex).draw();
    }

    @Override
    public void press(int x, int y) {
        DisplayGuiManager
                .debugMessage("DisplayListCanvas.press() on " + canvasName + " active canvas=" + visibleCanvasIndex);

        DisplayCanvas c = children.get(visibleCanvasIndex);
        if (pressOnCanvas(x, y)) {
            c.press(x, y);
        }
    }

    @Override
    public void navigate(NavigationDirection nav) {
        if (nav == NavigationDirection.DOWN) {
            if (visibleCanvasIndex < lastCanvasIndex) {
                visibleCanvasIndex++;
            }
        } else if (nav == NavigationDirection.UP) {

            if (visibleCanvasIndex > 0) {
                visibleCanvasIndex--;
            }
        }
    }
}
