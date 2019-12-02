import 'package:flutter/material.dart';
import 'package:syncfusion_flutter_charts/charts.dart';
import 'Objects.dart';
import 'design.dart';

Widget grafiek = FutureBuilder<List<ConsumeDataPerMonthPerUser>>(
      future: CurrentUser().getGroupConsumeData(),
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          return SfCartesianChart(
            primaryXAxis: CategoryAxis(),
            series: <ChartSeries>[
              AreaSeries<ConsumeDataPerMonthPerUser, String>(
                dataSource: snapshot.data,
                color: Design.orange2,
                borderMode: AreaBorderMode.excludeBottom,
                borderColor: Design.rood,
                borderWidth: 2,
                xValueMapper: (ConsumeDataPerMonthPerUser data, _) => data.uid,
                yValueMapper: (ConsumeDataPerMonthPerUser data, _) => data.amount,
                dataLabelSettings: DataLabelSettings(isVisible: true),
              )
            ]
          );
        }else if(snapshot.hasError) {
          print(snapshot.error);
          return Text("${snapshot.error}");

        }else{
          return CircularProgressIndicator();
        }
      },
  );

