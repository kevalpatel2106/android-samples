#Facebook Chat Heads

We all love the chat heads from the popular Facebook Messenger. This provides very handy and easy access to the chat conversation screen no matter on which screen you are.

In this sample project demonstrates how we can create simple chat heads and allow user to drag them across the screen. So that user can adjust the position of the chat head.

####Here is the tutorial on [medium](https://medium.com/@kevalpatel2106/create-chat-heads-like-facebook-messenger-32f7f1a62064#.v4bm57tj1).

##Summary steps:
- Create chat head layout.
- Create service for chat heads and add the chat head layout to the display window.
- Override the `OnTouch()`, to respond the drag and move event.
- Add `android.permission.SYSTEM_ALERT_WINDOW` permission to the `AndroidManifest.xml` and check if the permission available in runtime before starting chat head service.

##Screenshot:
![sample](https://github.com/kevalpatel2106/android-samples/blob/master/Facebook%20Chat%20Heads/assets/sample.gif)
