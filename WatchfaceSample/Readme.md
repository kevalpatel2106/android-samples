#Watch face sample

####Steps to create watch face:
- Create `CanvasWatchFaceService`. This is the base class of the watch face service.
- Create `CanvasWatchFaceService.Engine` class. This is the actual implementation of the watch face.
- Include meta data for your custom `CanvasWatchFaceService` in `AndroidManifest.xml`. This contains the preview images to display in watch face picker and intent filters.
- Add `android.permission.WAKE_LOCK"` permission in wear and mobile projects.
- Set watch face style.
- Implement ticker to tick every second while watch is in interactive mode.
- Handle layout changes when watch enters in ambient mode.
- Draw canvas on `onDraw()`.