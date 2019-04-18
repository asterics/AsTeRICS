# Discover Solutions

::: warning TODO
The solutions are listed statically at the moment, but should later be adapted to provide searching and tag-based filtering. The solutions could be automatically fetched from a blog similar to the [showcases page of openhab](https://www.openhab.org/about/showcase.html).
:::

Here you can find a selection of solutions that can be used directly or as a starting point for a customization. Please read the [Get Started](/get-started/) section first, to know more about AsTeRICS and solutions.

The solutions are grouped by **use cases** and can be directly started from this page. Some solutions depend on certain **input devices** or other hardware and software. Just click onto the ```Read More``` button to get a detailed step by step instruction of how to install and use it.

::: tip Start
1. Start the **ARE** program on your computer
2. Click onto `Start`
:::

If you want to customize a solution, click onto `Edit` or go to [**Customize**](/customize/) to learn how to do that.


## Computer Control

<Model left
    title="Camera Mouse"
    :tags='[{href:"#", text:"webcam"}, {href:"#", text:"feature"}, {href:"#", text:"world"}]'
    description="Mouse control according to your head movements with configurable settings."
    image="/assets/img/solutions/camera-mouse.1.jpg"
    model="https://raw.githubusercontent.com/asterics/AsTeRICS/gh-pages/webapps/asterics-camerainput-cameramouse/models/XFaceTrackerMouse(WLM).acs"
    webapp="http://asterics.github.io/AsTeRICS/webapps/startpage/#submenuSolutionDemos:asterics-camerainput-cameramouse"
    docs="/solutions/Camera-Mouse.html"
/>

<Model
    title="Eye Tracking Mouse"
    :tags='[{href:"https://gaming.tobii.com/product/tobii-eye-tracker-4c/", text:"eye-tracker"}, {href:"#", text:"windows"}]'
    description="Mouse control by eye tracking with configurable settings."
    image="/assets/img/solutions/eye-tracking-mouse.2.jpg"
    model="https://raw.githubusercontent.com/asterics/AsTeRICS/gh-pages/webapps/asterics-camerainput-eyecontrol/models/EyeControlledMouse(W).acs"
    webapp="http://asterics.github.io/AsTeRICS/webapps/startpage/#submenuSolutionDemos:asterics-camerainput-eyecontrol"
    docs="/solutions/Eye-Tracking-Mouse.html"
/>

<Model left
    title="Switch-controlled Mouse"
    :tags='[{href:"#", text:"mouse"}]'
    description="Provides mouse control using AT switches."
    image="/assets/img/solutions/switch-mouse.1.png"
    model="https://raw.githubusercontent.com/asterics/AsTeRICS/master/bin/ARE/models/useCaseDemos/mouseControl/crosshairCursorControl_2keys_wraparound.acs"
    docs="/solutions/Switch-Mouse.html"
/>

## AAC

<Model
    title="Basic AAC Grid"
    :tags='[{href:"#", text:"mouse"}]'
    description="Basic communication and simple keyboard with speech synthesis."
    image="/img/AsTeRICS-Ergo_Grid_en-1-768x592.jpg"
    grid="grid-data-1539356163042-54"
    docs="/solutions/AAC-Basic.html"
/>

## Occupational Therapy

<Model left
    title="Sounds by Head Movement"
    :tags='[{href:"#", text:"music"}]'
    description="Creates sounds according to head movement."
    image="/assets/img/solutions/head-sound.1.jpg"
    model="https://raw.githubusercontent.com/asterics/AsTeRICS/master/bin/ARE/models/HeadSound.acs"
    docs="/solutions/Head-Sound.html"
/>
