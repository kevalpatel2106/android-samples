Steps to create watchface:
- Create `CanvasWatchFaceService`. This is the base class of the watch face service.
- Create `CanvasWatchFaceService.Engine` class. This is the actual implementation of the watch face.
- Include meta data for your custom `CanvasWatchFaceService` in `AndroidManifest.xml`. This contains the preview images to display in watch face picker and intent filters.
- Add `android.permission.WAKE_LOCK"` permission in wear and mobile projects.
- Set watch face style.
- Handle all the callbacks.