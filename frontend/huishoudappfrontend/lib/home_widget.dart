import 'dart:convert';
import 'Objects.dart';

import 'package:flutter/material.dart';
import 'package:huishoudappfrontend/beer_page.dart';

import 'package:huishoudappfrontend/setup/provider.dart';
import 'package:huishoudappfrontend/setup/auth.dart';

import 'package:http/http.dart';



class Home_widget extends StatefulWidget{
  static User currentUser;
  Home_widget({Key key, User currentUser = null}) : super(key: key);
  

  @override
  State<StatefulWidget> createState() {
    
    return new Home_widget_state();
  } 
}

class Home_widget_state extends State<Home_widget>{
  
  String _userinfo = Home_widget.currentUser.toString();
  

   Future<User> getUser() async {
    String uid = await Auth().currentUser();
    User currentUser;
    final Response res = await get("http://10.0.2.2:8080/authCurrent?uid=$uid",
        headers: {'Content-Type': 'application/json'});
    print(res.statusCode);
    if (res.statusCode == 200) {
      // If server returns an OK response, parse the JSON.
      currentUser = User.fromJson(json.decode(res.body));
    } else {
      print("Could not find user");
    }
    return currentUser;
  }

  void _changeUserInfo(String newinfo) {
    setState(() {
      _userinfo = newinfo;
    });
  }

  @override
  Widget build(BuildContext context) {
    return 
      Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            FutureBuilder<User>(
              future: getUser(),
              builder: (context, snapshot) {
                if (snapshot.hasData) {
                  return Text("Welcome, " + snapshot.data.displayName);
                } else if (snapshot.hasError) {
                  return Text("${snapshot.error}");
                }

                // By default, show a loading spinner.
                return CircularProgressIndicator();
              },
            ),
            (FlatButton(
                child: Text("Go to Beer"),
                onPressed: () async {
                  Auth auth = Provider.of(context).auth;
                  String uid = await auth.currentUser();
                  print(uid);
                  final Response res = await get(
                      "http://10.0.2.2:8080/authCurrent?uid=$uid",
                      headers: {'Content-Type': 'application/json'});
                  print(res.statusCode);
                  if (res.statusCode == 200) {
                    // If server returns an OK response, parse the JSON.
                    User currentUser = User.fromJson(json.decode(res.body));
                    Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) =>
                              BeerPage(currentUser: currentUser),
                        ));
                  }
                })),
          ],
        ),
      );
      
  }

  
}