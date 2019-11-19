import 'package:flutter/material.dart';
import 'package:liquid_progress_indicator/liquid_progress_indicator.dart';

class Loading_Indicator extends StatelessWidget{
  @override
  Widget build(BuildContext context) {
    return LiquidCircularProgressIndicator(
                value: 0.25, // Defaults to 0.5.
                valueColor: AlwaysStoppedAnimation(Colors
                    .yellow), // Defaults to the current Theme's accentColor.
                backgroundColor: Colors
                    .yellow, // Defaults to the current Theme's backgroundColor.
                borderColor: Colors.yellow,
                borderWidth: 5.0,
                direction: Axis
                    .vertical, // The direction the liquid moves (Axis.vertical = bottom to top, Axis.horizontal = left to right). Defaults to Axis.vertical.
                center: Text("Loading..."),
              );
  }
  
}