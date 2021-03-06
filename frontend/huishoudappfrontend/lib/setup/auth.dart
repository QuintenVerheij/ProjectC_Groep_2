import 'package:firebase_auth/firebase_auth.dart';
import 'package:google_sign_in/google_sign_in.dart';

abstract class BaseAuth {
  Stream<String> get onAuthStateChanged;
  Future<String> signInWithEmailAndPassword(String email, String password);
  Future<String> createUserWithEmailAndPassword(String email, String password);

  Future<void> sendResetPasswordEmail(String email);
  Future<String> currentUser();
  Future<String> getEmailUser();
  Future<void> signOut();
  Future<String> signInWithGoogle();
  Future<String> getUserIdToken();
}

class Auth implements BaseAuth {
  //bron:
  // https://www.youtube.com/watch?v=OlcYP6UXlm8
  // https://github.com/tensor-programming/firebase_auth_example_stream/blob/master/lib/main.dart

  final FirebaseAuth _firebaseAuth = FirebaseAuth.instance;
  final GoogleSignIn _googleSignIn = GoogleSignIn();
  @override
  Stream<String> get onAuthStateChanged =>
      _firebaseAuth.onAuthStateChanged.map((FirebaseUser user) => user?.uid);

  @override
  Future<String> createUserWithEmailAndPassword(
      String email, String password) async {
    return (await _firebaseAuth.createUserWithEmailAndPassword(
            email: email, password: password))
        .uid;
  }

  @override
  Future<String> currentUser() async {
    return (await _firebaseAuth.currentUser()).uid;
  }

  @override
  Future<void> sendResetPasswordEmail(String email) async {
    return (await _firebaseAuth.sendPasswordResetEmail(email: email));
  }

  @override
  Future<String> getEmailUser() async {
    return (await _firebaseAuth.currentUser()).email;
  }

  @override
  Future<String> getUserIdToken() async {
    return (await _firebaseAuth.currentUser()).providerData[1].providerId;
  }

  @override
  Future<String> signInWithEmailAndPassword(
      String email, String password) async {
    return (await _firebaseAuth.signInWithEmailAndPassword(
            email: email, password: password))
        .uid;
  }

  @override
  Future<String> signInWithGoogle() async {
    final GoogleSignInAccount account = await _googleSignIn.signIn();
    final GoogleSignInAuthentication _auth = await account.authentication;
    final AuthCredential credential = GoogleAuthProvider.getCredential(
      accessToken: _auth.accessToken,
      idToken: _auth.idToken,
    );
    return (await _firebaseAuth.signInWithCredential(credential)).uid;
  }

  @override
  Future<void> signOut() {
    return _firebaseAuth.signOut();
  }
}
