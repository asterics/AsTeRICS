# Environmental Control

AsTeRICS has many plugins for environmental control like **KNX**, **EnOcean**, **IrTrans**, **Phillips Hue** or **OpenHAB**. Check the [Plugins](/plugins/) page and filter by ```Home Control```.

On this page you will find some tutorials of how to use such plugins to control your environment and how to create and accessible user interface for it.

## OpenHAB

OpenHAB is an Open Source Home Automation SW that let's you control light, heating and other appliances of your Smart Home. Such appliances or devices are called **things** which are represented as **items** in a user interface. OpenHAB provides several user interfaces and means of voice control.

<!-- ![Screenshot: openHAB demo with different options (./light, heating, temperature, ...)](/plugins/processors/img/openhab_overview.png "Screenshot: openHAB demo with different options (light, heating, temperature, ...)") -->

::: tip Accessible User Interface
This tutorial explains how to control an OpenHAB instance and it's configured items by an AsTeRICS model and how to create an **accessible user interface** for it using AsTeRICS Grid.
:::

::: tip
This tutorial is based on OpenHAB v3.x but should also work with OpenHAB v2.x
:::

1. Read the [OpenHAB concept page](https://www.openhab.org/docs/)
2. [Install OpenHAB](https://www.openhab.org/download/)
3. Download the [openhab2 demo configuration zip file](https://github.com/asterics/AsTeRICS/releases/download/v4.1.0/demo-conf-openhab2.zip).
4. Restore the demo configuration using openhab-cli (Linux) by entering ```sudo openhab-cli restore demo-conf-openhab2.zip``` in the command line. On other systems extract the .zip file and copy the ```conf/items``` and the ```conf/sitemaps``` folder to your OpenHAB config folder.
6. Start OpenHAB
7. [Download OpenHAB model](https://raw.githubusercontent.com/asterics/AsTeRICS/master/bin/ARE/models/componentTests/processors/openHAB_simple_test.acs) or [Open OpenHAB model in WebACS](http://webacs.asterics.eu/?areBaseURI=http://127.0.0.1:8081&openFile=https://raw.githubusercontent.com/asterics/AsTeRICS/master/bin/ARE/models/componentTests/processors/openHAB_simple_test.acs)
9. Start the ARE (```ARE.exe``` or ```start.bat``` or ```start.sh```)
10. Open model in ARE or Upload model from ACS
11. Start model in ARE
12. Open the [OpenHAB Basic UI of the Kitchen](http://localhost:8080/basicui/app?w=GF_Kitchen&sitemap=demo)
<!-- ![Screenshot: OpenHAB Basic UI of Kitchen, showing light and roller shutter items](./img/openhab-basic-ui-kitchen.png) -->
11. In the ARE GUI: Click on ```Item Light_GF_Kitchen_Ceiling ON``` or ```Item Light_GF_Kitchen_Ceiling OFF```. You should see the switching of the item in the basic UI accordingly.
    ![Screenshot: OpenHAB Basic UI of Kitchen and ARE GUI with buttons to control OpenHAB items. Animation showing light and roller shutter items switched on and off and the temperature slider changing the temperature](./img/openhab-show-synced-control.gif)
12. Change a value in the basic UI, you should get an event in the event visualizer of the ARE GUI.

### Accessible UI

You can use AsTeRICS Grid to create an accessible UI for controlling your Smart Home using OpenHAB.

1. Open [AsTeRICS Grid](https://grid.asterics.eu)
2. See [AsTeRICS Action](../../manuals/asterics-grid/05_actions.html#asterics-action): To know how to execute an action of an AsTeRICS model in a running ARE instance.
3. Click ```Download from ARE```
4. In ```Component```, select ```openHAB.1_c```
5. In ```Send data```
   1. Select ```actionString```
   2. Set item name and state value, e.g. ```@OPENHAB:Light_GF_Kitchen_Ceiling,ON``` or ```Light_GF_Kitchen_Ceiling,ON``` ([See plugin documentation](../../plugins/processors/OpenHAB.html#input-port-description))
6. Test action by clicking on ```Test Action```
7. Click ```OK``` to save the action.

<!--
## KNX

## Enocean

## IrTrans

-->
