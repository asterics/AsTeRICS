# Gaming, Toys & Music

AsTeRICS can be used to control computer games, music programs or RC toys or infrared-controlled lights and toys. The keyboard input of a game or a music program can be substituted by another input modality (e.g. head movement, eye gaze, switch press,...) which emulates a key press. Similarily a remote controlled toy can be controlled by adapting an input modality to an RC or infrared command.

## Head Movement to Smiley Tutorial

In a first tutorial, you will learn how to map up/down head movements to a discrete number between 1 and 4. The number will than be mapped to one of 4 image paths and the selected image will be shown in the ImageBox widget depending on the head movement.

![Demo showing selected smiley image (from angry to happy) depending on head movement](https://user-images.githubusercontent.com/4621810/137794267-4b207741-a95c-40ed-9237-cdd9473f159c.gif)

Later this tutorial can be used as a basis for other use cases as well, simply by exchanging the input signal (e.g. Use an air pressure or sip/puff value instead of head movements) or by exchanging the action (e.g. Send cursor keys [[UP]]/[[DOWN]] or send a certain Infrared command instead of showing an image).

### Model Description

Below you can see the model used for this tutorial. You can [download](https://raw.githubusercontent.com/asterics/AsTeRICS/master/bin/ARE/models/tutorial/B_HeadMovement_MappedTo_SmileyImage.acs) and open it in the **ACS program** or directly [open it in the **WebACS**](http://webacs.asterics.eu/?areBaseURI=http://127.0.0.1:8081&openFile=https://raw.githubusercontent.com/asterics/AsTeRICS/master/bin/ARE/models/tutorial/B_HeadMovement_MappedTo_SmileyImage.acs) web application.

![Head-Smiley-Control-Model](https://user-images.githubusercontent.com/4621810/137795484-07c6a282-25d6-44e0-912d-4bc4a37dde49.png)

1. The XFacetrackerLK plugin opens the first camera device with a resolution of 320x240 pixels, tracks the head movements and sends the relative movement in x-direction to the Integrate plugin.
2. The Integrate plugin integrates the relative movements with minimum and maximum values set to **0 and 240** respectively (same as camera resolution). ![Head-Smiley-Control-Integrate-Properties](https://user-images.githubusercontent.com/4621810/137796282-a2a080aa-d32a-4693-bfe8-af4a75dfadec.png)
3. The SignalTranslation plugin translates the values between **0 and 240** to values between **1.00 and 4.00**. ![Head-Smiley-Control-SignalTranslator-Properties](https://user-images.githubusercontent.com/4621810/137796551-1629aea6-a031-4143-9502-7d2c2862ccbd.png)
4. The Quantizer plugin creates a quantized (integer) number by rounding to the nearest integer number.
5. The StringDispatcher plugin has predefined Strings (paths to image files) which are sent to the output port depending on the input number (1-4). ![Head-Smiley-Control-StringDispatcher-Properties](https://user-images.githubusercontent.com/4621810/137797248-d663214b-8529-4493-858e-2bac85f129ae.png)
6. The TextDisplay widget shows the quantized number in a text box for debugging purposes only.
7. The ImageBox widget shows the image which path was received at the input port.

## Toys


The following tutorial documents a [toy helicopter controlled with muscle signals](https://www.ki-i.at/helicopter/).

<!--
## Gaming





-->
