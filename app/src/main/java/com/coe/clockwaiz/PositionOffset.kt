package com.coe.clockwaiz

class PositionOffsetPref :Preferences(){

    var zRefValueFront:Float by floatPref("zRefValueFront")
    var xOffsetFront:Float by floatPref("xOffsetFront")
    var yOffsetFront:Float by floatPref("yOffsetFront")

    var zRefValueBack:Float by floatPref("zRefValueBack")
    var xOffsetBack:Float by floatPref("xOffsetBack")
    var yOffsetBack:Float by floatPref("yOffsetBack")

    var yRefValuePortraitUp:Float by floatPref("yRefValuePortraitUp")
    var xOffsetPortraitUp:Float by floatPref("xOffsetPortraitUp")
    var zOffsetPortraitUp:Float by floatPref("zOffsetPortraitUp")

    var yRefValuePortraitDown:Float by floatPref("yRefValuePortraitDown")
    var xOffsetPortraitDown:Float by floatPref("xOffsetPortraitDown")
    var zOffsetPortraitDown:Float by floatPref("zOffsetPortraitDown")

    var xRefValueLandscapeLeft:Float by floatPref("xRefValueLandscapeLeft")
    var yOffSetLandscapeLeft:Float by floatPref("yOffSetLandscapeLeft")
    var zOffSetLandscapeLeft:Float by floatPref("zOffSetLandscapeLeft")

    var xRefValueLandscapeRight:Float by floatPref("xRefValueLandscapeRight")
    var yOffsetLandscapeRight:Float by floatPref("yOffsetLandscapeRight")
    var zOffsetLandscapeRight:Float by floatPref("zOffsetLandscapeRight")

    var xOffset:Float by floatPref("xOffset")
    var yOffset:Float by floatPref("yOffset")
    var zOffset:Float by floatPref("zOffset")



}

