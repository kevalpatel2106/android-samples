#Floating widget

Floating widgets are the views that float over the screen. This view will remain floating on the screen no matter in whichever screen you are. They are very convenient for multitasking as a user can work on other application and control your application for the same time. That means if you are in the calculator application and a widget from the music player is floating over the screen, you can control your music at the same time.

In this sample project demonstrates how we can create simple floating views and allow user to drag them across the screen. So that user can adjust the position of the view in the screen.

##Summary steps:
- Create layout of the floating view.
- Create service for floating view and add the view layout to the display window.
- Override the `OnTouch()`, to respond the drag and move event.
- Add `android.permission.SYSTEM_ALERT_WINDOW` permission to the `AndroidManifest.xml` and check if the permission available in runtime before starting floating widget service.

##Screenshot:
![sample](https://github.com/kevalpatel2106/android-samples/blob/master/Foating%20Widget/assets/sample.gif)
