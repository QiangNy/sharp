1.use command

start app
adb shell am start -a action.picture.calculate

stop app
adb shell am broadcast -a action.broadstart.picture.stop

start tashID 1：后摄  2：后摄2  3：前摄  4：前摄2  100：后摄和前摄组合
open single cameraID  1 , 2, 3, 4 or 100
adb shell am broadcast -a action.broadstart.picture.start --ei mCameraID 1 --ez isWhite false --ez isFlash true

----目前只需1和2
