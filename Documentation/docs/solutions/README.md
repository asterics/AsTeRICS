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

<TileGroup>
<ModelTile target="./Camera-Mouse" v-bind:buttons='[{ href: "", text: "Start" }, { href: "http://asterics.github.io/AsTeRICS/webapps/startpage/#submenuSolutionDemos:asterics-camerainput-cameramouse", text: "Settings" },{ href: "http://asterics.github.io/AsTeRICS/webapps/WebACS/?areBaseURI=http://localhost:8081&openFile=http://asterics.github.io/AsTeRICS/webapps/asterics-camerainput-cameramouse/models/XFaceTrackerMouse(WLM).acs", text: "Edit" }]' title="Camera Mouse" image-url="/img/stock-photo-biometric-verification-woman-face-recognition-security-613853963.jpg" shortDesc="Mouse control according to your head movements with configurable settings." v-bind:tags='[{ href: "#", text: "webcam" }]'></ModelTile>

<ModelTile target="./Eye-Tracking-Mouse" v-bind:buttons='[{ href: "", text: "Start" }, { href: "http://asterics.github.io/AsTeRICS/webapps/startpage/#submenuSolutionDemos:asterics-camerainput-eyecontrol", text: "Settings" },{ href: "http://asterics.github.io/AsTeRICS/webapps/WebACS/?areBaseURI=http://localhost:8081&openFile=http://asterics.github.io/AsTeRICS/webapps/asterics-camerainput-eyecontrol/models/EyeControlledMouse(W).acs", text: "Edit" }]' title="Eye Tracking Mouse" image-url="/img/stock-photo-eye-monitoring-virtual-reality-700122865.jpg" shortDesc="Mouse control by eye tracking with configurable settings." v-bind:tags='[{ href: "https://gaming.tobii.com/product/tobii-eye-tracker-4c/", text: "eye-tracker" },{ href: "", text: "windows" }]'></ModelTile>

<ModelTile target="./Switch-Mouse" title="Switch-controlled Mouse" image-url="/img/fabi-switches.jpg" shortDesc="Provides mouse control using AT switches."></ModelTile>
</TileGroup>

## AAC

<TileGroup>
<ModelTile target="./AAC-Basic" v-bind:buttons='[{ href: "https://asterics.github.io/AsTeRICS-Grid/package/static/#grid/grid-data-1539356163042-54?date=1551382911842", text: "Start" },{ href: "https://asterics.github.io/AsTeRICS-Grid/package/static/#grid/edit/grid-data-1539356163042-54", text: "Edit" }]' title="Basic AAC Grid" image-url="/img/AsTeRICS-Ergo_Grid_en-1-768x592.jpg" shortDesc="Basic communication and simple keyboard with speech synthesis."></ModelTile>
</TileGroup>

## Occupational Therapy

<ModelTile target="./Head-Sound" title="Sounds by Head Movement" image-url="/img/stock-photo--d-illustration-of-musical-notes-and-musical-signs-of-abstract-music-sheet-songs-and-melody-concept-761313844.jpg" shortDesc="Creates sounds according to head movement."></ModelTile>