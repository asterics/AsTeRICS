##

## Audio Selector

# Audio Selector

### Component Type: Processor (Subcategory: Audio and Voice)

This plug-in manages the audio tracks present in the data/music folder and different external request working as an interface with the wavefileplayer plug-in

![Screenshot: AudioSelector plugin](./img/AudioSelector.jpg "Screenshot: AudioSelector plugin")  
AudioSelector plugin

## Requirements

To work along with wavefileplayer plug-in.

## Output Port Description

- **TrackName \[string\]:** of the Track to be played. **Supports value suggestions from ARE (dynamic property)**

## Event Listener Description

- **StartStop:** Togle between play stop state request.
- **NextTrack:** Play next track request.
- **VolumeUp:** Put the volume up request.
- **VolumeDown:** Put the volume down request.

## Event Trigger Description

- **Play:** Play Track Request.
- **Pause:** Stop Track Requests.
- VolumeUp: Volume Up request.
- **VolumeDown:** Volume Down Request.
