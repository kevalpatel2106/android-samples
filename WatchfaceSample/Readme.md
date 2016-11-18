#Watch face sample

This sample creates android wear watch face. This watch face only displays digital clock. 
This sample helps you to create the watch face that supports ambient mode, square or round watch layouts and configure watch face design with phone application using DataMap API.

*You can follow [this](https://catinean.com/2015/03/07/creating-a-watch-face-with-android-wear-api/) tutorial if you want to develop your own watchface.*

####[Summary] Steps to create watch face:
- Create `CanvasWatchFaceService`. This is the base class of the watch face service.
- Create `CanvasWatchFaceService.Engine` class. This is the actual implementation of the watch face.
- Include meta data for your custom `CanvasWatchFaceService` in `AndroidManifest.xml`. This contains the preview images to display in watch face picker and intent filters.
- Add `android.permission.WAKE_LOCK"` permission in wear and mobile projects.
- Set watch face style.
- Implement ticker to tick every second while watch is in interactive mode.
- Handle layout changes when watch enters in ambient mode.
- Draw canvas on `onDraw()`.

