import 'package:flutter/material.dart';
import 'package:huishoudappfrontend/createaccount_widget.dart';
import 'package:huishoudappfrontend/services/permission_serivce.dart';
import 'login_widget.dart';
import 'page_container.dart';
import 'createaccount_widget.dart';
import 'package:huishoudappfrontend/setup/provider.dart';
import 'package:huishoudappfrontend/setup/auth.dart';
import 'package:huishoudappfrontend/setup/validators.dart';
import 'profile.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  final routes = <String, WidgetBuilder>{
    LoginPage.tag: (context) => LoginPage(),
    HomePage.tag: (context) => HomePage(),
    CreateAccount.tag: (context) => CreateAccount(),
    Profilepage.tag: (context) => Profilepage(),
  };

  @override
  Widget build(BuildContext context) {
    return Provider(
      auth: Auth(),
      perm: PermissionsService(),
      child: MaterialApp(
        title: 'Flutter Demo',
        theme: ThemeData(primarySwatch: Colors.blue),
        home: MyHomePage(),
        routes: routes,
      ),
    );
  }
}

class MyHomePage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final Auth auth = Provider.of(context).auth;
    return StreamBuilder<String>(
      stream: auth.onAuthStateChanged,
      builder: (context, AsyncSnapshot<String> snapshot) {
        if (snapshot.connectionState == ConnectionState.active) {
          final bool loggedIn = snapshot.hasData;
          
          if (loggedIn == true) {
            print('to the homepage');
            return HomePage();
          } else {
            print('to the loginpage');
            return LoginPage();
          }
        }
        return CircularProgressIndicator();
      },
    );
  }
}
